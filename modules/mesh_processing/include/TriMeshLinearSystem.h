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

        template <typename VPropType>
        class TriMeshLinearSystem
        {
        public:
            TriMeshLinearSystem(TriMesh& _triMesh, OpenMesh::VPropHandleT<VPropType> _vPropoHandle);
            void solve();
            void addToRightMatrix(MatrixOperator _matrixOperator, double factor = 1.0,  OperationType _operationType = ADD);
            void addToLeftMatrix(MatrixOperator _matrixOperator, double factor = 1.0, OperationType _operationType = ADD);

            void addDirichletBoundryCondition(const TriMesh::VertexHandle& _vh, const VPropType& _pValue);
        private:
            void build();

        private:
            TriMesh&                                                              mTriMesh;
            OpenMesh::VPropHandleT<VPropType>                                     mVPropertyHandle;

            std::map<OpenMesh::VertexHandle, VPropType>                           mDirichletBoundryCondition;

            Eigen::SparseMatrix<double>                                           mLeftMatrix;
            Eigen::Matrix<double, Eigen::Dynamic, VPropType::size_>               mRightMatrix;
            Eigen::SparseMatrix<double>                                           mLaplaceMatrix;
            Eigen::Matrix<double, Eigen::Dynamic, VPropType::size_>               mPropertyMatrix;
            Eigen::Matrix<double, Eigen::Dynamic, VPropType::size_>               mDiffrentialPropertyMatrix;

            Eigen::SparseMatrix<double>                                           mA;
            Eigen::Matrix<double, Eigen::Dynamic, VPropType::size_>               mB;

            //Eigen::SparseLU<Eigen::SparseMatrix<double>>           mSparseSolver;
            //Eigen::SimplicialLDLT<Eigen::SparseMatrix<double>>      mSparseSolver;
            Eigen::SimplicialCholesky<Eigen::SparseMatrix<double>>  mSparseSolver;

            bool mIsDirtySystem = true;
        };//TriMeshLinearSystem

    } //MeshProcessing
} //TriMeshKit

#include "../src/TriMeshLinearSystem.cpp"

#endif //MESH_PROCESSING_MESHLINEARSYSTEM_H
