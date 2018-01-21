#define _USE_MATH_DEFINES
#include "TriMeshAlgorithms.h"
#include "TriMeshLinearSystem.h"

#include <iostream>
#include <algorithm>

//Triangle
#define REAL double
#define ANSI_DECLARATORS
#define VOID int
extern "C"
{
#include <triangle.h>
#include "OpenMesh\Core\Mesh\TriConnectivity.hh"
}

bool TriMeshKit::MeshProcessing::TriMeshAlgorithms::smooth(TriMesh& _mesh)
{
    // Heat Equation
    // (M - hC) Pn = M * Po
    TriMeshLinearSystem<TriMesh::Point> triMeshLinearSystem(_mesh, _mesh.points_pph());

    triMeshLinearSystem.addToLeftMatrix(MASS, 1.0, INIT);
    triMeshLinearSystem.addToLeftMatrix(LAPLACIAN, 0.1, SUB);

    triMeshLinearSystem.addToRightMatrix(MASS, 1.0, INIT);

    auto result = triMeshLinearSystem.solve();

    if (result)
        _mesh.refresh();

    return result;
}

void TriMeshKit::MeshProcessing::TriMeshAlgorithms::triangulate(TriMesh& _mesh,
    const std::vector<OpenMesh::Vec2d>& _pointList, const std::vector<OpenMesh::Vec2ui>& _segmentList, const std::vector<OpenMesh::Vec2d>& _holeList,
    const std::vector<int>& _pointMarkerList, const std::vector<int>& _segmentMarkerList, const std::string& flags,
    std::map<TriMesh::VertexHandle, int >& _markedVertices)
{
    // Prepare the flags
    std::string full_flags = flags + "pz" + (_segmentMarkerList.size() || _pointMarkerList.size() ? "" : "B");
    // Prepare the input struct
    triangulateio in;

    in.numberofpoints = _pointList.size();

    in.pointlist = (double*)calloc(_pointList.size() * 2, sizeof(double));
    std::copy_n(_pointList.data()->data(), _pointList.size() * 2, in.pointlist);

    in.numberofpointattributes = 0;

    in.pointmarkerlist = (int*)calloc(_pointList.size(), sizeof(int));
    for (unsigned i = 0; i < _pointList.size(); ++i)
    {
        in.pointmarkerlist[i] = _pointMarkerList.size() ? _pointMarkerList.at(i) : 1;
    }

    in.trianglelist = NULL;
    in.numberoftriangles = 0;
    in.numberofcorners = 0;
    in.numberoftriangleattributes = 0;
    in.triangleattributelist = NULL;

    in.numberofsegments = _segmentList.size();

    in.segmentlist = (int*)calloc(_segmentList.size() * 2, sizeof(int));
    std::copy_n(_segmentList.data()->data(), _segmentList.size() * 2, in.segmentlist);

    in.segmentmarkerlist = (int*)calloc(_segmentList.size(), sizeof(int));
    for (unsigned i = 0; i < _segmentList.size(); ++i) in.segmentmarkerlist[i] = _segmentMarkerList.size() ? _segmentMarkerList.at(i) : 1;


    in.numberofholes = _holeList.size();
    if (_holeList.size() > 0)
        std::copy_n(_holeList.data()->data(), _holeList.size() * 2, in.holelist);

    in.numberofregions = 0;

    // Prepare the output struct
    triangulateio out;
    out.pointlist = NULL;
    out.trianglelist = NULL;
    out.segmentlist = NULL;
    out.segmentmarkerlist = NULL;
    out.pointmarkerlist = NULL;

    // Call triangle
    ::triangulate(const_cast<char*>(full_flags.c_str()), &in, &out, 0);

    // Return the mesh
    _mesh.clear();
    std::vector<TriMesh::VertexHandle> vertices_handles(out.numberofpoints);

    int j = 0;
    for (int i = 0; i < out.numberofpoints; ++i)
    {
        auto& x = out.pointlist[j++];
        auto& y = out.pointlist[j++];
        vertices_handles[i] = _mesh.add_vertex(TriMesh::Point(x, y, 0.0f));
        _markedVertices.emplace(vertices_handles[i], out.pointmarkerlist[i]);
    }

    std::vector<TriMesh::VertexHandle> face_vhandles;

    int k = 0;
    for (int i = 0; i < out.numberoftriangles; ++i)
    {
        face_vhandles.clear();
        face_vhandles.push_back(vertices_handles[out.trianglelist[k++]]);
        face_vhandles.push_back(vertices_handles[out.trianglelist[k++]]);
        face_vhandles.push_back(vertices_handles[out.trianglelist[k++]]);
        _mesh.add_face(face_vhandles);
    }

    // Cleanup in
    free(in.pointlist);
    free(in.pointmarkerlist);
    free(in.segmentlist);
    free(in.segmentmarkerlist);
    //free(in.holelist);

    // Cleanup out
    free(out.pointlist);
    free(out.trianglelist);
    free(out.segmentlist);
    free(out.segmentmarkerlist);
    free(out.pointmarkerlist);

    _mesh.refresh(true);
}

void TriMeshKit::MeshProcessing::TriMeshAlgorithms::bendSketch(TriMesh& _mesh, const std::vector<Points2DList>& _boundryList, 
    const std::vector<Points2DList>& _convexList, const std::vector<Points2DList>& _concaveList,
    const std::vector<Points2DList>& _ridgeList, const std::vector<Points2DList>& _valleyList)
{
    std::vector<std::vector<Points2DList>> featurePointsLists;
    featurePointsLists.push_back(_boundryList);
    featurePointsLists.push_back(_convexList);
    featurePointsLists.push_back(_concaveList);
    featurePointsLists.push_back(_ridgeList);
    featurePointsLists.push_back(_valleyList);

    Points2DList featurePoints;

    std::vector<OpenMesh::Vec2ui> featureSegmentList;
    std::vector<int> inputMarkedVertices;

    int currentPointIndex = -1;

    int currentFeatureList = 0;
    int markedIndex = 1;
    std::map<int, float> markedIndexToZvalue;

    for (const auto& featurePointsList : featurePointsLists)
    {
        for (const auto& pointList : featurePointsList)
        {
            std::vector<float> zValueVector;

            // Convex , Concave interpolation
            if (currentFeatureList == 1 || currentFeatureList == 2)
            {
                float minValue = 0.2;
                float maxValue = minValue;

                if (currentFeatureList == 2)
                {
                    minValue = 0.01;
                    maxValue = minValue;
                }

                int middleIndex = (pointList.size() - 1) / 2;
                float zvalue = maxValue;

                for (int i = 0; i <= middleIndex; ++i)
                {
                    zvalue = minValue + (i / (float)middleIndex) * minValue;
                    zValueVector.push_back(zvalue);
                    maxValue = zvalue;
                }

                if (pointList.size() % 2 == 0)
                {
                    zValueVector.push_back(maxValue);
                }

                for (int j = middleIndex - 1; j >= 0; --j)
                {
                    zvalue = minValue + (j / (float)middleIndex) * minValue;
                    zValueVector.push_back(zvalue);
                }
            }
            else if(currentFeatureList == 3 || currentFeatureList == 4)
            {
                float minValue = 0.1;
                float maxValue = 0.2;

                if (currentFeatureList == 4)
                {
                    minValue = 0.05;
                    maxValue = 0.1;
                }

                float addedValue = (maxValue - minValue) / float(pointList.size() - 1);
                float currentValue = maxValue;
                for (int i = 0; i < pointList.size(); ++i)
                {
                    zValueVector.push_back(currentValue);
                    currentValue -= addedValue;
                }
            }

            int firstIndex = currentPointIndex + 1;

            for (int i = 0; i < pointList.size() - 1; ++i)
            {
                currentPointIndex++;
                featurePoints.push_back(pointList.at(i));
                featureSegmentList.push_back(OpenMesh::Vec2ui(currentPointIndex, currentPointIndex + 1));

                if (currentFeatureList != 0)
                {
                    inputMarkedVertices.push_back(markedIndex);
                    markedIndexToZvalue.emplace(markedIndex++, zValueVector.at(i));
                }
                else
                {
                    inputMarkedVertices.push_back(0);
                }
            }
            currentPointIndex++;

            featurePoints.push_back(pointList.at(pointList.size() - 1));
            if (currentFeatureList != 0)
            {
                inputMarkedVertices.push_back(markedIndex);
                markedIndexToZvalue.emplace(markedIndex++, zValueVector.at(pointList.size() - 1));
            }
            else
            {
                inputMarkedVertices.push_back(0);
                featureSegmentList.push_back(OpenMesh::Vec2ui(currentPointIndex, firstIndex));
            }
        }

        currentFeatureList++;
    }

    std::map<TriMesh::VertexHandle, int> markedVertices;
    triangulate(_mesh, featurePoints, featureSegmentList, std::vector<OpenMesh::Vec2d>(),
        inputMarkedVertices, std::vector<int>(), "a0.0005q", markedVertices);

    // Deformation Equation
    // (C ^2) Pn = (C) * Po

    TriMeshLinearSystem<TriMesh::Point> deformationLinearSystem(_mesh, _mesh.points_pph());

    deformationLinearSystem.addToLeftMatrix(LAPLACIAN, 1.0, INIT, 2);
    deformationLinearSystem.addToRightMatrix(LAPLACIAN, 1.0, INIT);

    for (const auto& vh : _mesh.vertices())
    {
        if (_mesh.is_boundary(vh))
        {
            deformationLinearSystem.addDirichletBoundryCondition(vh, _mesh.point(vh));
        }
        else if (markedVertices.at(vh) != 0)
        {
            deformationLinearSystem.addDirichletBoundryCondition(vh, _mesh.point(vh) + OpenMesh::Vec3f(0.0f, 0.0f, markedIndexToZvalue.at(markedVertices.at(vh))));
        }
    }

    auto result = deformationLinearSystem.solve();

    duplicateSymmetric(_mesh);

    if (result)
        _mesh.refresh(true);

    TriMeshUtils::writeMesh(_mesh, "sdcard/wissam.stl");
}

void TriMeshKit::MeshProcessing::TriMeshAlgorithms::duplicateSymmetric(TriMesh& _mesh)
{
    std::map<TriMesh::VertexHandle, TriMesh::VertexHandle> verticesMap;
    for (const auto& vh : _mesh.vertices())
    {
        if (_mesh.is_boundary(vh))
            continue;

        auto opp_vh = _mesh.add_vertex(_mesh.point(vh) * OpenMesh::Vec3d(1.0, 1.0, -1.0));
        verticesMap.emplace(vh, opp_vh);
    }

    std::vector<TriMesh::VertexHandle> face_vertices;

    for (const auto& fh : _mesh.faces())
    {
        face_vertices.clear();

        for (const auto& fvh : _mesh.fv_range(fh))
        {
            if (_mesh.is_boundary(fvh))
            {
                face_vertices.push_back(fvh);
            }
            else
            {
                face_vertices.push_back(verticesMap.at(fvh));
            }
        }
        std::swap(face_vertices[1], face_vertices[2]);
        _mesh.add_face(face_vertices);
    }

    _mesh.setDirty(true);
}
