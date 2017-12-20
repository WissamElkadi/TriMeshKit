#include "TriMeshAlgorithms.h"
#include "TriMeshLinearSystem.h"

bool TriMeshKit::MeshProcessing::TriMeshAlgorithms::smooth(TriMesh& _mesh)
{
    // Heat Equation
    // (M - hC) Pn = M * Po
    TriMeshLinearSystem<TriMesh::Point> triMeshLinearSystem(_mesh, _mesh.points_pph());

    triMeshLinearSystem.addToLeftMatrix(MASS, 1.0, INIT);
    triMeshLinearSystem.addToLeftMatrix(LAPLACIAN, 0.1, SUB);

    triMeshLinearSystem.addToRightMatrix(MASS, 1.0, INIT);

    auto result = triMeshLinearSystem.solve();

    if(result)
        _mesh.refresh();

    return result;
}
