#ifndef MESH_PROCESSING_DIFFERENTIALOPERATOR_CPP
#define MESH_PROCESSING_DIFFERENTIALOPERATOR_CPP

#include "LaplaceBeltramiOperator.h"
#include "TriMesh.h"

template<int Order /*= 1*/>
void TriMeshKit::MeshProcessing::LaplaceBeltramiOperator<Order>::build(const TriMesh& _triMesh, Eigen::SparseMatrix<float>& _laplaceMatix)
{
    std::vector<Eigen::Triplet<float>> tripletList;
    tripletList.reserve(_triMesh.n_vertices());

    for (const auto& vh_i : _triMesh.vertices())
    {
        float totalArea = 0.0;
        for (const auto& fh : _triMesh.vf_range(vh_i))
        {
            totalArea += _triMesh.calc_face_normal(fh).norm();
        }
        float dualArea = (float)totalArea / 3;

        float sum_w_ij = 0.0f;
        for (const auto& vh_j : _triMesh.vv_range(vh_i))
        {
            OpenMesh::HalfedgeHandle heh_ji;
            for (const auto& heh : _triMesh.voh_range(vh_j))
            {
                if (_triMesh.to_vertex_handle(heh) == vh_i)
                {
                    heh_ji = heh;
                }
            }
            float beta = _triMesh.calc_sector_angle(_triMesh.next_halfedge_handle(heh_ji));
            float alpha = _triMesh.calc_sector_angle(_triMesh.next_halfedge_handle(_triMesh.opposite_halfedge_handle(heh_ji)));
            float w_ij = 0.5 *(alpha + beta) / dualArea;
            sum_w_ij -= w_ij;
            tripletList.emplace_back(vh_i.idx(), vh_j.idx(), w_ij);
        }

        tripletList.emplace_back(vh_i.idx(), vh_i.idx(), sum_w_ij);
    }

    _laplaceMatix.resize(_triMesh.n_vertices(), _triMesh.n_vertices());
    _laplaceMatix.setFromTriplets(tripletList.begin(), tripletList.end());

    if (Order > 1)
    {
        Eigen::SparseMatrix<float> tempMatrix = _laplaceMatix;
        for (int i = 1; i < Order; ++i)
        {
            tempMatrix = (tempMatrix * _laplaceMatix).eval();
        }
        _laplaceMatix = tempMatrix.eval();
    }
}

#endif //MESH_PROCESSING_DIFFERENTIALOPERATOR_CPP