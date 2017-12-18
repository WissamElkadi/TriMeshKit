#ifndef MESH_PROCESSING_TRIMESHLINEARSYSTEM_CPP
#define MESH_PROCESSING_TRIMESHLINEARSYSTEM_CPP

#include "TriMeshLinearSystem.h"
#include "TriMesh.h"
#include "TriMeshUtils.h"
#include <omp.h>
#include <iostream>

template <typename VPropType>
TriMeshKit::MeshProcessing::TriMeshLinearSystem<VPropType>::TriMeshLinearSystem(TriMesh& _triMesh, OpenMesh::VPropHandleT<VPropType> _vPropoHandle) :
    mTriMesh(_triMesh), mVPropertyHandle(_vPropoHandle)
{
    LaplaceBeltramiOperator<>::build(mTriMesh, mLaplaceMatrix);

    mPropertyMatrix.resize(mTriMesh.n_vertices(), VPropType::size_);

#pragma omp parallel for
    for (int i = 0; i < mTriMesh.n_vertices(); ++i)
    {
        auto& propertyValue = mTriMesh.property(mVPropertyHandle, mTriMesh.vertex_handle(i));
        for (int j = 0; j < VPropType::size_; ++j)
            mPropertyMatrix(i, j) = propertyValue[j];
    }

    mDiffrentialPropertyMatrix = (mLaplaceMatrix * mPropertyMatrix).eval();
}

template <typename VPropType>
void TriMeshKit::MeshProcessing::TriMeshLinearSystem<VPropType>::build()
{
    mA = mLeftMatrix;
    mB = mRightMatrix;

    if (mDirichletBoundryCondition.size() == 0)
    {
        Eigen::SparseMatrix<double> identityEqualityMatrix;
        Eigen::Matrix<double, Eigen::Dynamic, VPropType::size_>  bEqualityMatrix;
        bEqualityMatrix.resize(mDirichletBoundryCondition.size(), VPropType::size_);

        std::vector<Eigen::Triplet<double>> tripletList;
        tripletList.reserve(mDirichletBoundryCondition.size());

        int i = 0;
        for (const auto & condition : mDirichletBoundryCondition)
        {
            tripletList.emplace_back(i, condition.first.idx(), 1.0);

            for (int j = 0; j < bEqualityMatrix.cols(); ++j)
            {
                bEqualityMatrix(i, j) = condition.second[j];
            }
            ++i;
        }

        identityEqualityMatrix.resize(mDirichletBoundryCondition.size(), mLeftMatrix.cols());
        identityEqualityMatrix.setFromTriplets(tripletList.begin(), tripletList.end());

        mA.conservativeResize(mLeftMatrix.rows() + mDirichletBoundryCondition.size(), mLeftMatrix.cols());
        mA = mA.transpose();
        mA.rightCols(mDirichletBoundryCondition.size()) = identityEqualityMatrix;
        mA = mA.transpose();

        mB.conservativeResize(mRightMatrix.rows() + mDirichletBoundryCondition.size(), mRightMatrix.cols());
        mB.bottomRows(mDirichletBoundryCondition.size()) = bEqualityMatrix;

    }

    std::cout << "WISSAM 1" << std::endl;
    mSparseSolver.analyzePattern(-mA);
    mSparseSolver.factorize(-mA);
    std::cout << "WISSAM 2" << std::endl;
    mIsDirtySystem = false;
}

template <typename VPropType>
void TriMeshKit::MeshProcessing::TriMeshLinearSystem<VPropType>::solve()
{
    if (mIsDirtySystem)
        build();

    std::cout << "WISSAM 3" << std::endl;
    // solve Ax = b
    if (mSparseSolver.info() != Eigen::Success) {
        // decomposition failed
        std::cout << "WISSAM 4" << std::endl;
        return;
    }
    auto solution = mSparseSolver.solve(mB);
    if (mSparseSolver.info() != Eigen::Success) {
        std::cout << "WISSAM 5" << std::endl;
        // solving failed
        return;
    }

#pragma omp parallel for
    for (int i = 0; i < mTriMesh.n_vertices(); ++i)
    {
        auto& propertyValue = mTriMesh.property(mVPropertyHandle, mTriMesh.vertex_handle(i));
        for (int j = 0; j < VPropType::size_; ++j)
        {
            propertyValue[j] = solution(i, j);
        }
    }

   /* for (const auto& value : mDirichletBoundryCondition)
    {
        auto& propertyValue = mTriMesh.property(mVPropertyHandle, value.first);

        for (int j = 0; j < solution.cols(); ++j)
        {
            propertyValue[j] = value.second[j];
        }
    }*/

    mTriMesh.setDirty(true);
}

template <typename VPropType>
void TriMeshKit::MeshProcessing::TriMeshLinearSystem<VPropType>::addToRightMatrix(MatrixOperator _matrixOperator, double factor, OperationType _operationType /*= ADD*/)
{
    Eigen::Matrix<double, Eigen::Dynamic, VPropType::size_> tempMatrix;
    tempMatrix.resize(mTriMesh.n_vertices(), VPropType::size_);

    if (_matrixOperator == ZEROS)
    {
        tempMatrix.setZero();
    }
    else if (_matrixOperator == IDENTITY)
    {
        tempMatrix = mPropertyMatrix.eval();
    }
    else if (_matrixOperator == LAPLACIAN)
    {
        tempMatrix = mDiffrentialPropertyMatrix.eval();
    }

    if (_operationType == INIT)
    {
        mRightMatrix = (tempMatrix * factor).eval();
    }
    else if (_operationType == ADD)
    {
        mRightMatrix += (tempMatrix * factor).eval();
    }
    else if (_operationType == SUB)
    {
        mRightMatrix -= (tempMatrix * factor).eval();
    }
}

template <typename VPropType>
void TriMeshKit::MeshProcessing::TriMeshLinearSystem<VPropType>::addToLeftMatrix(MatrixOperator _matrixOperator, double factor, OperationType _operationType /*= ADD*/)
{
    Eigen::SparseMatrix<double> tempMatrix;
    tempMatrix.resize(mTriMesh.n_vertices(), mTriMesh.n_vertices());

    if (_matrixOperator == ZEROS)
    {
        tempMatrix.setZero();
    }
    else if (_matrixOperator == IDENTITY)
    {
        tempMatrix.setIdentity();
    }
    else if (_matrixOperator == LAPLACIAN)
    {
        tempMatrix = mLaplaceMatrix.eval();
    }

    if (_operationType == INIT)
    {
        mLeftMatrix = (tempMatrix * factor).eval();
    }
    else if (_operationType == ADD)
    {
        mLeftMatrix += (tempMatrix * factor).eval();
    }
    else if (_operationType == SUB)
    {
        mLeftMatrix -= (tempMatrix * factor).eval();
    }
}

template <typename VPropType>
void TriMeshKit::MeshProcessing::TriMeshLinearSystem<VPropType>::addDirichletBoundryCondition(const TriMesh::VertexHandle& _vh, const VPropType& _pValue)
{
    mIsDirtySystem = true;
    mDirichletBoundryCondition.emplace(_vh, _pValue);
}


#endif //MESH_PROCESSING_TRIMESHLINEARSYSTEM_CPP
