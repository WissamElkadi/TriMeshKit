#define _USE_MATH_DEFINES

#include <OpenMesh/Core/IO/MeshIO.hh>

#include "TriMesh.h"
#include "TriMeshLinearSystem.h"
#include "LaplaceBeltramiOperator.h"

using namespace TriMeshKit::MeshProcessing;

TriMeshKit::MeshProcessing::TriMesh::TriMesh() :
    mIsDirty(true)
{
    for (auto& minCorr : mBoundingBox.at(0))
    {
        minCorr = std::numeric_limits<float>::max();
    }

    for (auto& maxCorr : mBoundingBox.at(1))
    {
        maxCorr = -std::numeric_limits<float>::max();
    }

    for (auto& centerCoord : mCenter)
    {
        centerCoord = 0.0f;
    }
}

std::vector<float> TriMesh::getVerticesPoints()
{
    return mVerticesPoints;
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

std::array<std::array<float, 3>, 2> TriMeshKit::MeshProcessing::TriMesh::getBoundingBox()
{
    return mBoundingBox;
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

void TriMeshKit::MeshProcessing::TriMesh::smooth()
{
    TriMeshLinearSystem triMeshLinearSystem(*this, DifferentialOperator::LAPLACIAN);
    triMeshLinearSystem.addZeroRightHandSide();
    triMeshLinearSystem.solve();
    refresh();
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

        if (vertexPoint[0] < mBoundingBox.at(0).at(0))
            mBoundingBox.at(0).at(0) = vertexPoint[0];
        else if((vertexPoint[0] > mBoundingBox.at(1).at(0)))
            mBoundingBox.at(1).at(0) = vertexPoint[0];

        if (vertexPoint[1] < mBoundingBox.at(0).at(1))
            mBoundingBox.at(0).at(1) = vertexPoint[1];
        else if(vertexPoint[0] > mBoundingBox.at(1).at(1))
            mBoundingBox.at(1).at(1) = vertexPoint[1];

        if (vertexPoint[2] < mBoundingBox.at(0).at(2))
            mBoundingBox.at(0).at(2) = vertexPoint[2];
        else if(vertexPoint[2] > mBoundingBox.at(1).at(2))
            mBoundingBox.at(1).at(2) = vertexPoint[2];

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

    auto min = mBoundingBox.at(0);
    auto max = mBoundingBox.at(1);
    mCenter[0] = float (min.at(0) + max.at(0)) / 2.0f;
    mCenter[1] = float(min.at(1) + max.at(1)) / 2.0f;
    mCenter[2] = float(min.at(2) + max.at(2)) / 2.0f;

    setDirty(false);
}

void TriMeshKit::MeshProcessing::TriMesh::setDirty(bool _isDirty)
{
    mIsDirty = _isDirty;
}

bool TriMeshKit::MeshProcessing::TriMesh::isDirty()
{
    return mIsDirty;
}
