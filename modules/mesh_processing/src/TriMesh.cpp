#define _USE_MATH_DEFINES

#include <OpenMesh/Core/IO/MeshIO.hh>

#include "TriMesh.h"

using namespace TriMeshKit::MeshProcessing;

std::vector<float> TriMesh::getVerticesPoints()
{
    return mVerticesPoints;
}

TriMeshKit::MeshProcessing::TriMesh::TriMesh() :
    mIsDirty(true)
{
}

std::vector<float> TriMeshKit::MeshProcessing::TriMesh::getVerticesNormals()
{
    return mVerticesNormals;
}

std::vector<int> TriMeshKit::MeshProcessing::TriMesh::getFacesIndices()
{
    return mFacesIndices;
}

std::array<float, 3> TriMeshKit::MeshProcessing::TriMesh::getCenter()
{
    return mCenter;
}

void TriMeshKit::MeshProcessing::TriMesh::updateVerticesNormals()
{
    // Add vertex normals as default property
    request_vertex_normals();

    // Add face normals as default property
    request_face_normals();

    // let the mesh update the normals
    update_normals();

    // dispose the face normals, as we don't need them anymore
    release_face_normals();
}

void TriMeshKit::MeshProcessing::TriMesh::refresh(bool _updateNormals /*= true*/)
{
    if (!mIsDirty)
        return;

    mFacesIndices.resize(0);
    mVerticesPoints.resize(0);
    mVerticesNormals.resize(0);

    // Faces Loop
    for (const auto& fh : faces())
    {
        for (const auto& fvh : fv_range(fh))
        {
            mFacesIndices.push_back(fvh.idx());
        }
    }

    if(_updateNormals)
        updateVerticesNormals();

    OpenMesh::Vec3f center(0.0f, 0.0f, 0.0f);

    // Vertices Loop
    for (const auto& vh : vertices())
    {
        auto vertexPoint = point(vh);
        center += vertexPoint;

        for (int i = 0; i < vertexPoint.size(); ++i)
        {
            mVerticesPoints.push_back(vertexPoint[i]);
        }

        auto vertexNormal = normal(vh);
        for (int i = 0; i < vertexNormal.size(); ++i)
        {
            mVerticesNormals.push_back(vertexNormal[i]);
        }
    }

    center = center / n_vertices();
    mCenter[0] = center[0];
    mCenter[1] = center[1];
    mCenter[2] = center[2];

    setDirty(false);
}

void TriMeshKit::MeshProcessing::TriMesh::setDirty(bool _isDirty)
{
    mIsDirty = _isDirty;
}
