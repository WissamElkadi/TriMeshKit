#include "TriMeshLinearSystem.h"
#include "TriMesh.h"

TriMeshKit::MeshProcessing::TriMeshLinearSystem::TriMeshLinearSystem(TriMesh& _triMesh, DifferentialOperator _operator) :
    mTriMesh(_triMesh),
    mDifferentialOperator(_operator)
{
    mLeftMatrix.setIdentity();
}


void TriMeshKit::MeshProcessing::TriMeshLinearSystem::addZeroRightHandSide()
{
    mRightMatrix.resize(mTriMesh.n_vertices(), 3);
    mRightMatrix.setZero();
}


void TriMeshKit::MeshProcessing::TriMeshLinearSystem::build()
{
    if(mDifferentialOperator == LAPLACIAN)
        LaplaceBeltramiOperator<>::build(mTriMesh, mLeftMatrix);

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

    // save
    for (const auto& vh : mTriMesh.vertices())
    {
        mTriMesh.set_point(vh, OpenMesh::Vec3f(leftUnknown(vh.idx(), 0), leftUnknown(vh.idx(), 1), leftUnknown(vh.idx(), 2)));
        mTriMesh.setDirty(true);
    }
}
