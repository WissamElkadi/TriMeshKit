package com.trimeshkit.meshprocessing;

import com.trimeshkit.meshprocessing.TriMesh;
import java.util.ArrayList;

public class TriMeshUtils
{
    public native static boolean readMesh (TriMesh _mesh, String _path, boolean _requestNormals);
    public native static void writeMesh(TriMesh _mesh, String _path, boolean _binary);				
};