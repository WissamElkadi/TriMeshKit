package com.trimeshkit.shaders;

import android.content.Context;
import android.opengl.GLES31;
import android.opengl.GLES31Ext;
import android.util.DisplayMetrics;
import com.trimeshkit.meshtools.R;

/**
 * Created by wahmed on 07/11/2017.
 */

public class SoildWireframeRenderingShader extends RenderingShader{


    private int mScreenSizeHandle;
    //Screen Size
    private float[] mScreenSize = new float[2];


    public SoildWireframeRenderingShader(Context _context)
    {
        super(_context);
    }

    public void initShader()
    {
        super.initShader();

        // load vertex shader
        mVertexShader = ShaderUtils.loadShader(GLES31.GL_VERTEX_SHADER, readShaderSourceCode(R.raw.surface_solid_wirframe_vertex));
        //load Geometry shader
        mGeometryShader = ShaderUtils.loadShader(GLES31Ext.GL_GEOMETRY_SHADER_EXT, readShaderSourceCode(R.raw.surface_solid_wirframe_geometry));
        // load fragment shader
        mFragmentShader = ShaderUtils.loadShader(GLES31.GL_FRAGMENT_SHADER, readShaderSourceCode(R.raw.surface_solid_wirframe_fragment));

        //create program
        mShaderProgram = ShaderUtils.createProgram(mVertexShader, mFragmentShader, mGeometryShader);

        // Setup the shader variable handles only if we successfully created the shader program
        if (mShaderProgram > 0) {
            // Activate the shader program
            GLES31.glUseProgram(mShaderProgram);

            mVertexHandle = GLES31.glGetAttribLocation(mShaderProgram, "v_Position");
            mNormalHandle = GLES31.glGetAttribLocation(mShaderProgram, "v_Normal");
            mModelViewMatrixHandle = GLES31.glGetUniformLocation(mShaderProgram, "u_ModelViewMatrix");
            mProjectionMatrixHandle = GLES31.glGetUniformLocation(mShaderProgram, "u_ProjectionMatrix");
            mCameraPositionHandle = GLES31.glGetUniformLocation(mShaderProgram, "u_CameraPosition");
            mCameraDirectionHandle = GLES31.glGetUniformLocation(mShaderProgram, "u_CameraDirection");
            mScreenSizeHandle = GLES31.glGetUniformLocation(mShaderProgram, "u_ScreenSize");

            // Stop using the shader program
            GLES31.glUseProgram(0);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            mContext.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            mScreenSize[0] = displayMetrics.widthPixels;
            mScreenSize[1] = displayMetrics.heightPixels;

            // Combine the rotation matrix with the projection and camera view
            // Note that the mMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
//            /Matrix.multiplyMM(mModelViewMatrix, 0, mModelViewMatrix, 0, rotationMatrix, 0);

//            GLES31.glGenBuffers(3, mBuffers, 0);
//
//            GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, mBuffers[0]);
//            Buffer points = mMesh.getPoints();
//            GLES31.glBufferData(GLES31.GL_ARRAY_BUFFER, points.capacity() * 4, points, GLES31.GL_STATIC_DRAW);
//
//            GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, mBuffers[1]);
//            Buffer normals = mMesh.getNormals();
//            GLES31.glBufferData(GLES31.GL_ARRAY_BUFFER, normals.capacity() * 4, normals, GLES31.GL_STATIC_DRAW);
//
//            GLES31.glBindBuffer(GLES31.GL_ELEMENT_ARRAY_BUFFER, mBuffers[2]);
//            Buffer faces = mMesh.getFacesVertexIndices();
//            GLES31.glBufferData(GLES31.GL_ELEMENT_ARRAY_BUFFER, faces.capacity() * 4, faces, GLES31.GL_STATIC_DRAW);
//
//            // IMPORTANT: Unbind from the buffer when we're done with it.
//            GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, 0);
        }
    }

    public void render()
    {
        GLES31.glUseProgram(mShaderProgram);

        // Screen Size
        GLES31.glUniform2f(mScreenSizeHandle, mScreenSize[0], mScreenSize[1]);

        super.render(GLES31.GL_TRIANGLES);
    }

}
