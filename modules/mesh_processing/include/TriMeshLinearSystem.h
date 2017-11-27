#ifndef MESH_PROCESSING_TRIMESHLINEARSYSTEM_H
#define MESH_PROCESSING_TRIMESHLINEARSYSTEM_H

#include <eigen/Sparse>
#include "LaplaceBeltramiOperator.h"

namespace TriMeshKit
{
    namespace MeshProcessing
    {
        class TriMesh;

        enum DifferentialOperator
        {
            LAPLACIAN
        };
        class TriMeshLinearSystem
        {
        public:
            TriMeshLinearSystem(TriMesh& _triMesh, DifferentialOperator _operator);
            void solve();
            void addZeroRightHandSide();

        private:
            void build();

        private:
            TriMesh&                                     mTriMesh;
            DifferentialOperator                         mDifferentialOperator;
            Eigen::SparseMatrix<float>                   mLeftMatrix;
            Eigen::Matrix<float, Eigen::Dynamic, 3>      mRightMatrix;
            Eigen::SparseLU<Eigen::SparseMatrix<float>>  mSparseSolver;
        };//TriMeshLinearSystem

    } //MeshProcessing
} //TriMeshKit

#endif //MESH_PROCESSING_MESHLINEARSYSTEM_H
