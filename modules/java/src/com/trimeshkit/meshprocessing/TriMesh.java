package com.trimeshkit.meshprocessing;

import com.trimeshkit.utility.BufferUtility;
import java.nio.Buffer;

public class TriMesh
{	
    private long nativeHandle;
    private Buffer mNormals;
    private Buffer mPositions;
    private Buffer mFacesIndices;
	
    public boolean mDirty = true;

    public TriMesh()
	{
		initialise();
	}
	
    public Buffer getVerticesPoints()
    {
		if(mDirty)
		{
			mPositions = BufferUtility.getFloatBuffer(getInternalVerticesPoints());
		}
		
		return mPositions;
    }

    public Buffer getVerticesNormals()
    {
		if(mDirty)
		{
			mNormals = BufferUtility.getFloatBuffer(getInternalVerticesNormals());
		}
		
		return mNormals;  
    }

    public Buffer getFacesIndices()
    {
		if(mDirty)
		{
			mFacesIndices = BufferUtility.getIntBuffer(getInternalFacesIndices());
			mDirty = false;
		}
		return mFacesIndices;
    }

	public float getLargetLength()
    {
        float[] bb = getBoundingBox();
        return (float)Math.sqrt(Math.pow((bb[0]-bb[3]),2) +
                Math.pow((bb[2]-bb[4]),2) + Math.pow((bb[3]-bb[5]),2));
    }
	
    public native float[] getCenter();
    public native float[] getBoundingBox();
    public native void updateVerticesNormals();
	public native int getNumberOfFaces();
    public native void refresh(boolean _updateNormals);
    public native void setDirty(boolean _isDirty);
	public native boolean isDirty();

	private native void initialise();
    private native float[] getInternalVerticesPoints();
    private native float[] getInternalVerticesNormals();
    private native int[] getInternalFacesIndices();

};
