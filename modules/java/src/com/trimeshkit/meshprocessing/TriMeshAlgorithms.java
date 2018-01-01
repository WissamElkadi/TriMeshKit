/**
 * @Date:   2017-12-20T20:42:46+00:00
 * @Last modified time: 2017-12-31T23:03:41+00:00
 */



package com.trimeshkit.meshprocessing;

import com.trimeshkit.meshprocessing.TriMesh;
import java.util.ArrayList;

public class TriMeshAlgorithms
{
	public static boolean smoothMesh(TriMesh _mesh)
	{
		boolean result = smooth(_mesh);
		_mesh.mDirty = result;
		return result;
	}

  private native static boolean smooth (TriMesh _mesh);
	public native static void triangulate(TriMesh _mesh, double[] _pointList);
	public native static void bendSketch(TriMesh _mesh,  double[][] _boundrayLists,
	     double[][] _convexLists, double[][] _concaveLists,
			 double[][] _ridgePointsList, double[][] _valleyPointsList);
};
