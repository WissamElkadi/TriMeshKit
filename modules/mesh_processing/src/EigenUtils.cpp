#include "EigenUtils.h"

void TriMeshKit::MeshProcessing::EigenUtils::removeRowSparse(Eigen::SparseMatrix<double>& matrix, unsigned int rowToRemove)
{
    unsigned int numRows = matrix.rows() - 1;
    unsigned int numCols = matrix.cols();

    for (unsigned int i = rowToRemove; i < numRows; i++) {
      // matrix.row(i) = matrix.row(i + 1);
    }

    matrix.conservativeResize(numRows, numCols);
}

void TriMeshKit::MeshProcessing::EigenUtils::removeColumnSparse(Eigen::SparseMatrix<double >& matrix, unsigned int colToRemove)
{
    unsigned int numRows = matrix.rows();
    unsigned int numCols = matrix.cols() - 1;

    for (unsigned int i = colToRemove; i < numCols; i++) {
        matrix.col(i) = matrix.col(i + 1);
    }

    matrix.conservativeResize(numRows, numCols);
}

void TriMeshKit::MeshProcessing::EigenUtils::slice(
    const Eigen::SparseMatrix<double>& X, const Eigen::Matrix<int, Eigen::Dynamic, 1> & R,
    const Eigen::Matrix<int, Eigen::Dynamic, 1> & C, Eigen::SparseMatrix<double>& Y)
{
    int xm = X.rows();
    int xn = X.cols();
    int ym = R.size();
    int yn = C.size();

    // special case when R or C is empty
    if (ym == 0 || yn == 0)
    {
        Y.resize(ym, yn);
        return;
    }

    // initialize row and col permutation vectors
    Eigen::VectorXi rowIndexVec = Eigen::VectorXi::LinSpaced(xm, 0, xm);
    Eigen::VectorXi rowPermVec = Eigen::VectorXi::LinSpaced(xm, 0, xm);
    for (int i = 0; i < ym; i++)
    {
        int pos = rowIndexVec.coeffRef(R(i));
        if (pos != i)
        {
            int& val = rowPermVec.coeffRef(i);
            std::swap(rowIndexVec.coeffRef(val), rowIndexVec.coeffRef(R(i)));
            std::swap(rowPermVec.coeffRef(i), rowPermVec.coeffRef(pos));
        }
    }
    Eigen::PermutationMatrix<Eigen::Dynamic, Eigen::Dynamic, int> rowPerm(rowIndexVec);

    Eigen::VectorXi colIndexVec = Eigen::VectorXi::LinSpaced(xn, 0, xn);
    Eigen::VectorXi colPermVec = Eigen::VectorXi::LinSpaced(xn, 0, xn);
    for (int i = 0; i < yn; i++)
    {
        int pos = colIndexVec.coeffRef(C(i));
        if (pos != i)
        {
            int& val = colPermVec.coeffRef(i);
            std::swap(colIndexVec.coeffRef(val), colIndexVec.coeffRef(C(i)));
            std::swap(colPermVec.coeffRef(i), colPermVec.coeffRef(pos));
        }
    }
    Eigen::PermutationMatrix<Eigen::Dynamic, Eigen::Dynamic, int> colPerm(colPermVec);

    Eigen::SparseMatrix<double> M = (rowPerm * X);
    Y = (M * colPerm).block(0, 0, ym, yn);
}
