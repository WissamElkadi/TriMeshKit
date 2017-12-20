#ifndef MESH_PROCESSING_TRIMESHALGORITHMS_H
#define MESH_PROCESSING_TRIMESHALGORITHMS_H

#include <vector>
#include "OpenMesh\Core\Geometry\VectorT.hh"

namespace TriMeshKit
{
    namespace MeshProcessing
    {
        class TriMesh;
        class TriMeshAlgorithms
        {
        public:
            static bool smooth(TriMesh& _mesh);

            static void triangulate(TriMesh& _mesh, const std::vector<OpenMesh::Vec2d>& _pointList,
                const std::vector<OpenMesh::Vec2ui>& _segmentList, const std::vector<OpenMesh::Vec2d>& _holeList,
                const std::vector<int>& _pointMarkerList, const std::vector<int>& _segmentMarkerList,
                const std::string& flags);
        };
    }
}

#endif // MESH_PROCESSING_TRIMESHALGORITHMS_H
