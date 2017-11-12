#define _USE_MATH_DEFINES
#include "TriMeshUtils.h"

//OpenMesh
#include <OpenMesh/Core/IO/MeshIO.hh>

//TriMesh
#include "TriMesh.h"
#include <iostream>

using namespace TriMeshKit::MeshProcessing;

bool TriMeshUtils::readMesh( TriMesh& _mesh, const std::string& _path ,bool _requestNormals)
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

void TriMeshUtils::writeMesh( TriMesh& _mesh, const std::string& _path, bool _binary /*= true*/ )
{
	if(_binary)
		OpenMesh::IO::write_mesh(_mesh, _path, OpenMesh::IO::Options::Binary);
	else
		OpenMesh::IO::write_mesh(_mesh, _path);
}
