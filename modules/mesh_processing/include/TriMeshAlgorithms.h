/**
 * @Date:   2017-12-20T20:42:46+00:00
 * @Last modified time: 2017-12-21T19:35:39+00:00
 */



#ifndef MESH_PROCESSING_TRIMESHALGORITHMS_H
#define MESH_PROCESSING_TRIMESHALGORITHMS_H

#include <vector>
#include "OpenMesh\Core\Geometry\VectorT.hh"
#include <map>
#include "TriMesh.h"

namespace TriMeshKit
{
    namespace MeshProcessing
    {
        typedef std::vector<OpenMesh::Vec2d> Points2DList;

        class TriMeshAlgorithms
        {
        public:
            static bool smooth(TriMesh& _mesh);

            static void triangulate(TriMesh& _mesh, const std::vector<OpenMesh::Vec2d>& _pointList,
                const std::vector<OpenMesh::Vec2ui>& _segmentList, const std::vector<OpenMesh::Vec2d>& _holeList,
                const std::vector<int>& _pointMarkerList, const std::vector<int>& _segmentMarkerList,
                const std::string& flags, std::map<TriMesh::VertexHandle, int>& _markedVertices);

            static void bendSketch(TriMesh& _mesh, const std::vector<Points2DList>& _boundryList,
                const std::vector<Points2DList>& _convexList, const std::vector<Points2DList>& _concaveList);
        };
    }
}

#endif // MESH_PROCESSING_TRIMESHALGORITHMS_H
