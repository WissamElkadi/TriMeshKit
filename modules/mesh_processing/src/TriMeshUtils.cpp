#define _USE_MATH_DEFINES
#include "TriMeshUtils.h"

#define REAL double
#define ANSI_DECLARATORS
#define VOID int

//Triangle
extern "C"
{
#include <triangle.h>
}

//OpenMesh
#include <OpenMesh/Core/IO/MeshIO.hh>

//TriMesh
#include "TriMesh.h"
#include <iostream>

#include <algorithm>


using namespace TriMeshKit::MeshProcessing;

bool TriMeshUtils::readMesh(TriMesh& _mesh, const std::string& _path, bool _requestNormals)
{
    _mesh.setDirty(true);
    OpenMesh::IO::Options option;

    if (_requestNormals)
    {
        // Add vertex normals as default property
        _mesh.request_vertex_normals();

        // assure we have vertex normals
        if (!_mesh.has_vertex_normals())
        {
            std::cerr << "ERROR: Standard vertex property 'Normals' not available!\n";
            return false;
        }

        // Add face normals as default property
        _mesh.request_face_normals();
    }

    bool result = OpenMesh::IO::read_mesh(_mesh, _path, option);

    // If the file did not provide vertex normals, then calculate them
    if (!option.check(OpenMesh::IO::Options::VertexNormal) &&
        _mesh.has_face_normals() && _mesh.has_vertex_normals())
    {
        // let the mesh update the normals
        _mesh.update_normals();

        // dispose the face normals, as we don't need them anymore
        _mesh.release_face_normals();
    }

    _mesh.refresh(false);

    return result;
}

void TriMeshUtils::writeMesh(TriMesh& _mesh, const std::string& _path, bool _binary /*= true*/)
{
    if(_binary)
        OpenMesh::IO::write_mesh(_mesh, _path, OpenMesh::IO::Options::Binary);
    else
        OpenMesh::IO::write_mesh(_mesh, _path);
}

void TriMeshKit::MeshProcessing::TriMeshUtils::triangulate(TriMesh& _mesh,
    const std::vector<OpenMesh::Vec2d>& _pointList, const std::vector<OpenMesh::Vec2ui>& _segmentList, const std::vector<OpenMesh::Vec2d>& _holeList,
    const std::vector<int>& _pointMarkerList, const std::vector<int>& _segmentMarkerList, const std::string& flags)
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
