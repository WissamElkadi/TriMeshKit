#ifndef MESH_PROCESSING_TRIMESHLINEARSYSTEM_H
#define MESH_PROCESSING_TRIMESHLINEARSYSTEM_H

#include <eigen/Sparse>
#include "LaplaceBeltramiOperator.h"

namespace TriMeshKit
{
    namespace MeshProcessing
    {
        class TriMesh;

        enum MatrixOperator
        {
            ZEROS,
            IDENTITY,
            LAPLACIAN
        };

        enum OperationType
        {
            INIT,
            ADD,
            SUB
        };

        class TriMeshLinearSystem
        {
        public:
            TriMeshLinearSystem(TriMesh& _triMesh);
            void solve();
            void addToRightMatrix(MatrixOperator _matrixOperator, double factor = 1.0,  OperationType _operationType = ADD);
            void addToLeftMatrix(MatrixOperator _matrixOperator, double factor = 1.0, OperationType _operationType = ADD);

        private:
            void build();

        private:
            TriMesh&                                               mTriMesh;
            Eigen::SparseMatrix<double>                            mLeftMatrix;
            Eigen::Matrix<double, Eigen::Dynamic, 3>               mRightMatrix;
            Eigen::SparseMatrix<double>                            mLaplaceMatrix;
            Eigen::Matrix<double, Eigen::Dynamic, 3>               mPointsMatrix;
            Eigen::Matrix<double, Eigen::Dynamic, 3>               mDiffrentialPointsMatrix;

            //Eigen::SparseLU<Eigen::SparseMatrix<double>>           mSparseSolver;
            //Eigen::SimplicialLDLT<Eigen::SparseMatrix<double>>      mSparseSolver;
            Eigen::SimplicialCholesky<Eigen::SparseMatrix<double>>  mSparseSolver;
        };//TriMeshLinearSystem

    } //MeshProcessing
} //TriMeshKit

#endif //MESH_PROCESSING_MESHLINEARSYSTEM_H
