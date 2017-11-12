#ifndef MESH_PROCESSING_TRIMESHUTILS_H
#define MESH_PROCESSING_TRIMESHUTILS_H

#define _USE_MATH_DEFINES

#include <string>

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
		};
	}
}

#endif // MESH_PROCESSING_TRIMESHUTILS_H
