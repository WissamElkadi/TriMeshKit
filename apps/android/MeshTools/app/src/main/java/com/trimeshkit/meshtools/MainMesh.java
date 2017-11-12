package com.trimeshkit.meshtools;


import android.util.Log;

import com.trimeshkit.NativeTriMeshKit;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by wahmed on 21/10/2017.
 */

public class MainMesh {

    // Used to load the 'MeshTools' library on application startup.
    static {
        System.loadLibrary("TriMeshKit-java");
    }

    Buffer mNormals;
    Buffer mPositions;
    Buffer mFacesVertices;

    boolean mDirty = true;

    public MainMesh(String _path)
    {
        boolean loaded = NativeTriMeshKit.loadMesh(_path);
        if(!loaded)
            Log.d("MainMesh","Couldn't load the mesh");
        else {
            mPositions = getFloatBuffer(NativeTriMeshKit.getVerticesPoints());
            mNormals = getFloatBuffer(NativeTriMeshKit.getVerticesNormals());
            mFacesVertices = getIntBuffer(NativeTriMeshKit.getFacesIndices());
            mDirty = false;
        }
    }

    public Buffer getPoints()
    {
        if(mDirty)
        {
            float[] points = NativeTriMeshKit.getVerticesPoints();
            mPositions = getFloatBuffer(points);

            mDirty = false;
        }

        return mPositions;
    }


    public Buffer getTexCoords()
    {
        return null;
    }


    public Buffer getNormals()
    {
        if(mDirty)
        {
            float[] normals = NativeTriMeshKit.getVerticesNormals();
            mNormals = getFloatBuffer(normals);

            mDirty = false;
        }

        return mNormals;
    }


    public Buffer getFacesVertexIndices()
    {
        if(mDirty)
        {
            int[] facesIndicess = NativeTriMeshKit.getFacesIndices();
            mFacesVertices = getIntBuffer(facesIndicess);

            mDirty = false;
        }

        return mFacesVertices;
    }

    private Buffer getFloatBuffer(float[] array)
    {
        // Each float takes 4 bytes
        ByteBuffer bb = ByteBuffer.allocateDirect((Float.SIZE / Byte.SIZE) * array.length);
        bb.order(ByteOrder.nativeOrder());
        for (float d : array)
            bb.putFloat(d);
        bb.position(0);

        return bb;

    }

    private Buffer getIntBuffer(int[] array)
    {
        // Each int takes 4 bytes
        ByteBuffer bb = ByteBuffer.allocateDirect((Integer.SIZE / Byte.SIZE) * array.length);
        bb.order(ByteOrder.nativeOrder());
        for (int s : array)
            bb.putInt(s);
        bb.rewind();

        return bb;
    }


    public int getNumberOfFaces()
    {
        return NativeTriMeshKit.getNumberOfFaces();
    }

    public float[] getCenter()
    {
        return NativeTriMeshKit.getCenter();
    }

    public float[] getBoundingBox()
    {
        return NativeTriMeshKit.getBoundingBox();
    }

    public float getLargetLength()
    {
        float[] bb = getBoundingBox();
        return (float)Math.sqrt(Math.pow((bb[0]-bb[3]),2) +
                Math.pow((bb[2]-bb[4]),2) + Math.pow((bb[3]-bb[5]),2));
    }
}
