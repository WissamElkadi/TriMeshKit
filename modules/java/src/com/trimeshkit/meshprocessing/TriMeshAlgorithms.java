package com.trimeshkit.meshprocessing;

import com.trimeshkit.meshprocessing.TriMesh;

public class TriMeshAlgorithms
{
	public static boolean smoothMesh(TriMesh _mesh)
	{
		boolean result = smooth(_mesh);
		_mesh.mDirty = result;
		return result;
	}

	public native static void triangulate(TriMesh _mesh, double[] _pointList);

    private native static boolean smooth (TriMesh _mesh);
};
