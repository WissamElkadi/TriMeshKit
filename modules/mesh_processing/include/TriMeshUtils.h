#ifndef MESH_PROCESSING_TRIMESHUTILS_H
#define MESH_PROCESSING_TRIMESHUTILS_H

#define _USE_MATH_DEFINES

#include <string>
#include "OpenMesh\Core\Geometry\VectorT.hh"

namespace TriMeshKit
{
    namespace MeshProcessing
    {
        class TriMesh;
        class TriMeshUtils
        { 
        public:
            static bool readMesh (TriMesh& _mesh, const std::string& _path, bool _requestNormals = true);
            static void writeMesh(TriMesh& _mesh, const std::string& _path, bool _binary = true);
            static void triangulate(TriMesh& _mesh, const std::vector<OpenMesh::Vec2d>& _pointList,
                const std::vector<OpenMesh::Vec2ui>& _segmentList, const std::vector<OpenMesh::Vec2d>& _holeList,
                const std::vector<int>& _pointMarkerList, const std::vector<int>& _segmentMarkerList,
                const std::string& flags);
        };
    }
}

#endif // MESH_PROCESSING_TRIMESHUTILS_H
