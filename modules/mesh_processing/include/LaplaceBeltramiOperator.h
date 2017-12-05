#ifndef MESH_PROCESSING_DIFFERENTIALOPERATOR_H
#define MESH_PROCESSING_DIFFERENTIALOPERATOR_H

#include <eigen/Sparse>

namespace TriMeshKit
{
    namespace MeshProcessing
    {
        class TriMesh;
        template<int Order = 1>
        class LaplaceBeltramiOperator
        {
        public:
            static void build(const TriMesh& _triMesh, Eigen::SparseMatrix<double>& _laplaceMatix);

        };
    } //MeshProcessing
} //TriMeshKit

#include "../src/LaplaceBeltramiOperator.cpp"

#endif //MESH_PROCESSING_DIFFERENTIALOPERATOR_H
