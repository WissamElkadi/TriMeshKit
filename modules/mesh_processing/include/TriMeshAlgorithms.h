#ifndef MESH_PROCESSING_TRIMESHALGORITHMS_H
#define MESH_PROCESSING_TRIMESHALGORITHMS_H

namespace TriMeshKit
{
    namespace MeshProcessing
    {
        class TriMesh;
        class TriMeshAlgorithms
        {
        public:
            static bool smooth(TriMesh& _mesh);
        };
    }
}

#endif // MESH_PROCESSING_TRIMESHALGORITHMS_H
