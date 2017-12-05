#include "TriMeshLinearSystem.h"
#include "TriMesh.h"
#include "TriMeshUtils.h"
#include <omp.h>

TriMeshKit::MeshProcessing::TriMeshLinearSystem::TriMeshLinearSystem(TriMesh& _triMesh) :
    mTriMesh(_triMesh)
{
    LaplaceBeltramiOperator<>::build(mTriMesh, mLaplaceMatrix);

    mPointsMatrix.resize(mTriMesh.n_vertices(), 3);
    for (const auto& vh : mTriMesh.vertices())
    {
        auto point = mTriMesh.point(vh);
        mPointsMatrix(vh.idx(), 0) = point[0];
        mPointsMatrix(vh.idx(), 1) = point[1];
        mPointsMatrix(vh.idx(), 2) = point[2];
    }

    mDiffrentialPointsMatrix = (mLaplaceMatrix * mPointsMatrix).eval();
}

void TriMeshKit::MeshProcessing::TriMeshLinearSystem::build()
{
    mSparseSolver.analyzePattern(mLeftMatrix);
    mSparseSolver.factorize(mLeftMatrix);
}


void TriMeshKit::MeshProcessing::TriMeshLinearSystem::solve()
{
    build();

    // solve Ax = b
    if (mSparseSolver.info() != Eigen::Success) {
        // decomposition failed
        return;
    }
    auto leftUnknown = mSparseSolver.solve(mRightMatrix);
    if (mSparseSolver.info() != Eigen::Success) {
        // solving failed
        return;
    }

#pragma omp parallel for
    for (int i = 0; i < mTriMesh.n_vertices(); ++i)
    {
        mTriMesh.set_point(mTriMesh.vertex_handle(i), OpenMesh::Vec3f(leftUnknown(i, 0), leftUnknown(i, 1), leftUnknown(i, 2)));
    }

    mTriMesh.setDirty(true);

    TriMeshKit::MeshProcessing::TriMeshUtils::writeMesh(mTriMesh, "/storage/emulated/0/cube3.stl");

}

void TriMeshKit::MeshProcessing::TriMeshLinearSystem::addToRightMatrix(MatrixOperator _matrixOperator, double factor, OperationType _operationType /*= ADD*/)
{
    Eigen::Matrix<double, Eigen::Dynamic, 3> tempMatrix;
    tempMatrix.resize(mTriMesh.n_vertices(), 3);

    if (_matrixOperator == ZEROS)
    {
        tempMatrix.setZero();
    }
    else if (_matrixOperator == IDENTITY)
    {
        tempMatrix = mPointsMatrix.eval();
    }
    else if (_matrixOperator == LAPLACIAN)
    {
        tempMatrix = mDiffrentialPointsMatrix.eval();
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

void TriMeshKit::MeshProcessing::TriMeshLinearSystem::addToLeftMatrix(MatrixOperator _matrixOperator, double factor, OperationType _operationType /*= ADD*/)
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
