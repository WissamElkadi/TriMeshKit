#ifndef MESH_PROCESSING_DIFFERENTIALOPERATOR_CPP
#define MESH_PROCESSING_DIFFERENTIALOPERATOR_CPP

#include "LaplaceBeltramiOperator.h"
#include "TriMesh.h"

template<int Order /*= 1*/>
void TriMeshKit::MeshProcessing::LaplaceBeltramiOperator<Order>::build(const TriMesh& _triMesh, Eigen::SparseMatrix<double>& _laplaceMatix)
{
    std::vector<Eigen::Triplet<double>> tripletList;
    tripletList.reserve(_triMesh.n_vertices());

    for (const auto& vh_i : _triMesh.vertices())
    {
        double dualArea = 0.0;
        /*int faceCount = 0;
        for (const auto & fh : _triMesh.vf_range(vh_i))
        {
            ++faceCount;
            dualArea += _triMesh.calc_face_normal(fh).length();
        }
        dualArea /= faceCount;*/

        double sum_w_ij = 0.0;
        for (const auto& heh : _triMesh.voh_range(vh_i))
        {
            double cotAlpha = _triMesh.cotan(heh);
            double cotBeta = _triMesh.cotan(_triMesh.opposite_halfedge_handle(heh));
            double w_ij = (double)((cotAlpha + cotBeta) / 2.);
            sum_w_ij -= w_ij;
            tripletList.emplace_back(vh_i.idx(), _triMesh.to_vertex_handle(heh).idx(), w_ij);
        }
        tripletList.emplace_back(vh_i.idx(), vh_i.idx(), sum_w_ij);
    }

    _laplaceMatix.resize(_triMesh.n_vertices(), _triMesh.n_vertices());
    _laplaceMatix.setFromTriplets(tripletList.begin(), tripletList.end());

    if (Order > 1)
    {
        Eigen::SparseMatrix<double> tempMatrix = _laplaceMatix;
        for (int i = 1; i < Order; ++i)
        {
            tempMatrix = (tempMatrix * _laplaceMatix).eval();
        }
        _laplaceMatix = tempMatrix.eval();
    }
}

#endif //MESH_PROCESSING_DIFFERENTIALOPERATOR_CPP