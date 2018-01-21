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
            LAPLACIAN,
            MASS
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

            void addToRightMatrix(MatrixOperator _matrixOperator, double factor = 1.0,  OperationType _operationType = ADD, int _order = 1);
            void addToLeftMatrix(MatrixOperator _matrixOperator, double factor = 1.0, OperationType _operationType = ADD, int order = 1);

            void addDirichletBoundryCondition(const TriMesh::VertexHandle& _vh, const VPropType& _pValue);

            bool solve();

        private:
            void build();

        private:
            TriMesh&                                                              mTriMesh;
            OpenMesh::VPropHandleT<VPropType>                                     mVPropertyHandle;

            std::map<OpenMesh::VertexHandle, VPropType>                           mDirichletBoundryCondition;
            Eigen::VectorXi                                                       mUnknowns;

            Eigen::SparseMatrix<double>                                           mLeftMatrix;
            Eigen::Matrix<double, Eigen::Dynamic, VPropType::size_>               mRightMatrix;
            Eigen::SparseMatrix<double>                                           mLaplaceMatrix;
            Eigen::SparseMatrix<double>                                           mMassMatrix;
            Eigen::Matrix<double, Eigen::Dynamic, VPropType::size_>               mPropertyMatrix;
            Eigen::Matrix<double, Eigen::Dynamic, VPropType::size_>               mDiffrentialPropertyMatrix;
            Eigen::Matrix<double, Eigen::Dynamic, VPropType::size_>               mMassPropertyMatrix;

            Eigen::SparseMatrix<double>                                           mA;
            Eigen::Matrix<double, Eigen::Dynamic, VPropType::size_>               mB;

            Eigen::SimplicialCholesky<Eigen::SparseMatrix<double>>  mSparseSolver;

            bool mIsDirtySystem = true;
        };//TriMeshLinearSystem

    } //MeshProcessing
} //TriMeshKit

#include "../src/TriMeshLinearSystem.cpp"

#endif //MESH_PROCESSING_MESHLINEARSYSTEM_H
