#ifndef MESH_PROCESSING_EIGENUTILS_TEMPL_CPP
#define MESH_PROCESSING_EIGENUTILS_TEMPL_CPP

#include "EigenUtils.h"
#include <Eigen/Core>
template<typename MatrixType>
void TriMeshKit::MeshProcessing::EigenUtils::removeRow(Eigen::PlainObjectBase<MatrixType>& matrix, unsigned int rowToRemove)
{
    unsigned int numRows = matrix.rows() - 1;
    unsigned int numCols = matrix.cols();

    if (rowToRemove < numRows)
        matrix.block(rowToRemove, 0, numRows - rowToRemove, numCols) = matrix.block(rowToRemove + 1, 0, numRows - rowToRemove, numCols);

    matrix.conservativeResize(numRows, numCols);
}

template<typename MatrixType>
void TriMeshKit::MeshProcessing::EigenUtils::removeColumn(Eigen::PlainObjectBase<MatrixType>& matrix, unsigned int colToRemove)
{
    unsigned int numRows = matrix.rows();
    unsigned int numCols = matrix.cols() - 1;

    if (colToRemove < numCols)
        matrix.block(0, colToRemove, numRows, numCols - colToRemove) = matrix.block(0, colToRemove + 1, numRows, numCols - colToRemove);

    matrix.conservativeResize(numRows, numCols);
}


#endif
