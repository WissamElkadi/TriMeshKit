#ifndef MESH_PROCESSING_DIFFERENTIALOPERATOR_CPP
#define MESH_PROCESSING_DIFFERENTIALOPERATOR_CPP

#include "LaplaceBeltramiOperator.h"
#include "TriMesh.h"

template<int Order /*= 1*/>
void TriMeshKit::MeshProcessing::LaplaceBeltramiOperator<Order>::build(const TriMesh& _triMesh, Eigen::SparseMatrix<double>& _laplaceMatix, Eigen::SparseMatrix<double>& _massMatix)
{
    std::vector<Eigen::Triplet<double>> laplaceTripletList;
    laplaceTripletList.reserve(_triMesh.n_vertices());

    std::vector<Eigen::Triplet<double>> massTripletList;
    massTripletList.reserve(_triMesh.n_vertices());

    for (const auto& vh_i : _triMesh.vertices())
    {
        double dualArea = 0.0;
        for (const auto & fh : _triMesh.vf_range(vh_i))
        {
            dualArea += _triMesh.calc_face_normal(fh).length();
        }
        dualArea /= 3;
        massTripletList.emplace_back(vh_i.idx(), vh_i.idx(), dualArea);

        double sum_w_ij = 0.0;
        for (const auto& heh : _triMesh.voh_range(vh_i))
        {
            double cotAlpha = _triMesh.cotan(heh);
            double cotBeta = _triMesh.cotan(_triMesh.opposite_halfedge_handle(heh));
            double w_ij = (double)((cotAlpha + cotBeta) / (2.));
            sum_w_ij -= w_ij;
            laplaceTripletList.emplace_back(vh_i.idx(), _triMesh.to_vertex_handle(heh).idx(), w_ij);
        }
        laplaceTripletList.emplace_back(vh_i.idx(), vh_i.idx(), sum_w_ij);
    }

    _laplaceMatix.resize(_triMesh.n_vertices(), _triMesh.n_vertices());
    _laplaceMatix.setFromTriplets(laplaceTripletList.begin(), laplaceTripletList.end());

    _massMatix.resize(_triMesh.n_vertices(), _triMesh.n_vertices());
    _massMatix.setFromTriplets(massTripletList.begin(), massTripletList.end());

    if (Order > 1)
    {
        Eigen::SparseMatrix<double> laplaceTempMatrix = _laplaceMatix;
        Eigen::SparseMatrix<double> massTempMatrix = _massMatix;
        for (int i = 1; i < Order; ++i)
        {
            laplaceTempMatrix = (laplaceTempMatrix * _laplaceMatix).eval();
            massTempMatrix = (massTempMatrix * _massMatix).eval();
        }
        _laplaceMatix = laplaceTempMatrix.eval();
        _massMatix = massTempMatrix.eval();
    }
}

#endif //MESH_PROCESSING_DIFFERENTIALOPERATOR_CPP