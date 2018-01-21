#ifndef MESH_PROCESSING_EIGENUTILS_H
#define MESH_PROCESSING_EIGENUTILS_H

#include <eigen/Sparse>

namespace TriMeshKit
{
    namespace MeshProcessing
    {
        class TriMesh;
        class EigenUtils
        {
        public:

            static void slice(const Eigen::SparseMatrix<double>& X, const Eigen::Matrix<int, Eigen::Dynamic, 1> & R,
                const Eigen::Matrix<int, Eigen::Dynamic, 1> & C, Eigen::SparseMatrix<double>& Y);

            static void removeRowSparse(Eigen::SparseMatrix<double>& matrix, unsigned int rowToRemove);
            static void removeColumnSparse(Eigen::SparseMatrix<double >& matrix, unsigned int colToRemove);

            template<typename MatrixType>
            static void removeRow(Eigen::PlainObjectBase<MatrixType>& matrix, unsigned int rowToRemove);

            template<typename MatrixType>
            static void removeColumn(Eigen::PlainObjectBase<MatrixType>& matrix, unsigned int colToRemove);
        };
    }
}

#include "../src/EigenUtilsTemp.cpp"

#endif // MESH_PROCESSING_EIGENUTILS_H
