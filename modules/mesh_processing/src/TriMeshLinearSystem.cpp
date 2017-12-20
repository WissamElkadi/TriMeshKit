#ifndef MESH_PROCESSING_TRIMESHLINEARSYSTEM_CPP
#define MESH_PROCESSING_TRIMESHLINEARSYSTEM_CPP

#include "TriMeshLinearSystem.h"
#include "TriMesh.h"
#include "TriMeshUtils.h"
#include <omp.h>
#include <iostream>
#include "EigenUtils.h"

template <typename VPropType>
TriMeshKit::MeshProcessing::TriMeshLinearSystem<VPropType>::TriMeshLinearSystem(TriMesh& _triMesh, OpenMesh::VPropHandleT<VPropType> _vPropoHandle) :
    mTriMesh(_triMesh), mVPropertyHandle(_vPropoHandle)
{
    LaplaceBeltramiOperator<>::build(mTriMesh, mLaplaceMatrix, mMassMatrix);

    mPropertyMatrix.resize(mTriMesh.n_vertices(), VPropType::size_);

#pragma omp parallel for
    for (int i = 0; i < mTriMesh.n_vertices(); ++i)
    {
        auto& propertyValue = mTriMesh.property(mVPropertyHandle, mTriMesh.vertex_handle(i));
        for (int j = 0; j < VPropType::size_; ++j)
            mPropertyMatrix(i, j) = propertyValue[j];
    }

    mDiffrentialPropertyMatrix = (mLaplaceMatrix * mPropertyMatrix).eval();
    mMassPropertyMatrix = (mMassMatrix * mPropertyMatrix).eval();
}

template <typename VPropType>
void TriMeshKit::MeshProcessing::TriMeshLinearSystem<VPropType>::build()
{
    if (mDirichletBoundryCondition.size() > 0)
    {
        mRightMatrix;
        int numberOfKnowns = mDirichletBoundryCondition.size();
        int numberOfUnKnowns = mTriMesh.n_vertices() - numberOfKnowns;
        mUnknowns.resize(numberOfUnKnowns);

        std::vector<bool> unknown_mask;
        unknown_mask.resize(mTriMesh.n_vertices(), true);

        for (const auto& condition : mDirichletBoundryCondition)
        {
#pragma omp parallel for
            for (int i = 0; i < mRightMatrix.rows(); ++i)
            {
                for (int j = 0; j < mRightMatrix.cols(); ++j)
                {
                    mRightMatrix(i, j) = mRightMatrix(i, j) - (condition.second[j] * mLeftMatrix.coeff(i, condition.first.idx()));
                }
            }
            unknown_mask[condition.first.idx()] = false;
        }

        int rowRemoved = 0;
        for (const auto& condition : mDirichletBoundryCondition)
        {
            EigenUtils::removeRow(mRightMatrix, condition.first.idx() - rowRemoved);
            rowRemoved++;
        }

        int u = 0;

#pragma omp parallel for
        for (int i = 0; i < mLeftMatrix.rows(); i++)
        {
            if (unknown_mask[i])
            {
                mUnknowns(u) = i;
                u++;
            }
        }

        EigenUtils::slice(mLeftMatrix, mUnknowns, mUnknowns, mA);
    }
    else
    {
        mA = mLeftMatrix;
    }

    mB = mRightMatrix;

    mSparseSolver.analyzePattern(mA);
    mSparseSolver.factorize(mA);
    mIsDirtySystem = false;
}

template <typename VPropType>
bool TriMeshKit::MeshProcessing::TriMeshLinearSystem<VPropType>::solve()
{
    if (mIsDirtySystem)
        build();

    // solve Ax = b
    if (mSparseSolver.info() != Eigen::Success) {
        // decomposition failed
        return false;
    }
    auto solution = mSparseSolver.solve(mB);
    if (mSparseSolver.info() != Eigen::Success) {
        // solving failed
        return false;
    }

    if (mDirichletBoundryCondition.size() > 0)
    {
#pragma omp parallel for
        for (int i = 0; i < solution.rows(); i++)
        {
            int vertexIndex = mUnknowns(i);
            auto& propertyValue = mTriMesh.property(mVPropertyHandle, mTriMesh.vertex_handle(vertexIndex));

            for (int j = 0; j < solution.cols(); j++)
            {
                propertyValue[j] = solution(i, j);
            }
        }

        for (const auto& value : mDirichletBoundryCondition)
        {
            auto& propertyValue = mTriMesh.property(mVPropertyHandle, value.first);

            for (int j = 0; j < solution.cols(); ++j)
            {
                propertyValue[j] = value.second[j];
            }
        }
    }

    else
    {
#pragma omp parallel for
        for (int i = 0; i < mTriMesh.n_vertices(); ++i)
        {
            auto& propertyValue = mTriMesh.property(mVPropertyHandle, mTriMesh.vertex_handle(i));
            for (int j = 0; j < VPropType::size_; ++j)
            {
                propertyValue[j] = solution(i, j);
            }
        }
    }
    mTriMesh.setDirty(true);

    return true;
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
    else if (_matrixOperator == MASS)
    {
        tempMatrix = mMassPropertyMatrix.eval();
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
    else if (_matrixOperator == MASS)
    {
        tempMatrix = mMassMatrix.eval();
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
