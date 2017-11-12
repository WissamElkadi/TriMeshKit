package com.trimeshkit;

/**
 * Created by wahmed on 03/11/2017.
 */

public class NativeTriMeshKit {

    // Used to load the 'MeshTools' library on application startup.
    static {
        System.loadLibrary("TriMeshKit-java");
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native static boolean loadMesh(String path);
    public native static float[] getVerticesPoints();
    public native static float[] getVerticesNormals();
    public native static int[] getFacesIndices();
    public native static int getNumVertexCoordinates();
    public native static int getNumberOfFaces();
    public native static float[] getCenter();
    public native static float[] getBoundingBox();
}
