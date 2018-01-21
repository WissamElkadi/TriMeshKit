#include "CurvatureFlowSmoother.h"

#include "Curve.h"

#include <eigen/Sparse>

TriMeshKit::CurveProcessing::CurvatureFlowSmoother::CurvatureFlowSmoother(Curve& _curve) :
    mTimeStep(100.0),
    mCurve(_curve)
{}

void TriMeshKit::CurveProcessing::CurvatureFlowSmoother::setTimeStep(double _timeStep)
{
    mTimeStep = _timeStep;
}

double TriMeshKit::CurveProcessing::CurvatureFlowSmoother::getTimeStep() const
{
    return mTimeStep;
}

bool TriMeshKit::CurveProcessing::CurvatureFlowSmoother::smoothCurve()
{
    auto pointsCount = mCurve.size();

    //Solve this System  (I − h∆) fh = f0

    //the Right Hand Side
    Eigen::MatrixX3d f0(pointsCount, 3);
    int row = 0;

    auto points = mCurve.getCurvePoints();

    for(const auto& point : points)
    {
        for (int i = 0; i < 3; ++i)
            f0(row, i) = point[i];
        ++row;
    }

    //Build the sparse System
    std::vector<Eigen::Triplet<double>> tripletList;
    tripletList.reserve(pointsCount);

    //Finite Difference method to discretized Laplace

    // ∆ = | -2  1  0  0  0  1  |
    //     |  1 -2  1  0  0  0  |
    //     |  0  1 -2  1  0  0  |
    //     |  0  0  1 -2  0  0  |
    //     |  0  0  0  1 -2  1  |
    //     |  1  0  0  0  1 -2  |
    // We assume that the curve is always closed
    tripletList.emplace_back(0, 0,    -2);
    tripletList.emplace_back(0, 1,    1);
    tripletList.emplace_back(0, pointsCount - 1,    1);

    for(int i = 1; i < pointsCount - 1; ++i)
    {
        tripletList.emplace_back(i, i,    -2);
        tripletList.emplace_back(i, i - 1, 1);
        tripletList.emplace_back(i, i + 1, 1);
    }

    tripletList.emplace_back(pointsCount - 1, pointsCount - 1,    -2);
    tripletList.emplace_back(pointsCount - 1, pointsCount - 2,     1);
    tripletList.emplace_back(pointsCount - 1, 0,     1);

    Eigen::SparseMatrix<double> laplaceMatrix(pointsCount, pointsCount);
    laplaceMatrix.setFromTriplets(tripletList.begin(), tripletList.end());

    Eigen::SparseMatrix<double> identity(pointsCount, pointsCount);
    identity.setIdentity();

    //Solve this System  (I − h∆) fh = f0
    Eigen::SparseMatrix<double> A = identity - mTimeStep * laplaceMatrix;

    // we use Cholesky factorization LDLT because matrix A is  Symmetric Positive Semi-Definite
    Eigen::SimplicialLDLT<Eigen::SparseMatrix<double>> solver;

    solver.compute(A);

    if (solver.info() != Eigen::ComputationInfo::Success)
    {
        // decomposition failed
        return false;
    }

    Eigen::Matrix<double , Eigen::Dynamic, 3> fh = solver.solve(f0);

    row = 0;
    for(int i = 0; i < points.size(); ++i, ++row)
    {
        mCurve.updatePoint(i, OpenMesh::Vec3d(fh(row, 0), fh(row, 1), fh(row, 2)));
    }

    return true;
}
