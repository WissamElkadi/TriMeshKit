#ifndef MESH_PROCESSING_TRIMESH_H
#define MESH_PROCESSING_TRIMESH_H

#define _USE_MATH_DEFINES

#include <OpenMesh/Core/Mesh/TriMesh_ArrayKernelT.hh>
#include <vector>
#include <array>

namespace TriMeshKit
{
    namespace MeshProcessing
    {
        class TriMesh : public OpenMesh::TriMesh_ArrayKernelT<>
        {
        public:
            TriMesh();
            std::vector<float> getVerticesPoints();
            std::vector<float> getVerticesNormals();
            std::vector<int> getFacesIndices();
            std::array<float, 3> getCenter();
            std::array<std::array<float, 3>, 2> getBoundingBox();
            void updateVerticesNormals();
            double cotan(const HalfedgeHandle& _he) const;

            void refresh(bool _updateNormals = true);
            void setDirty(bool _isDirty);
            bool isDirty();

        private:
            std::vector<float>                   mVerticesPoints;
            std::vector<float>                   mVerticesNormals;
            std::vector<int>                     mFacesIndices;
            std::array<float, 3>                 mCenter;
            std::array<std::array<float, 3>, 2>  mBoundingBox;
            bool                                 mIsDirty;
        };
    }
}
#endif
