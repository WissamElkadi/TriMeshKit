package com.trimeshkit.meshtools;

import android.app.Activity;
import android.opengl.GLES31;
import android.opengl.GLES31Ext;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.DisplayMetrics;

import com.trimeshkit.shaders.ShaderUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by wahmed on 21/10/2017.
 */

public class MainRenderer implements GLSurfaceView.Renderer {

    private Activity mContext;

    // Mesh
    private MainMesh mMesh;

    // shader
    private int mShaderProgram;
    private int mVertexShader;
    private int mGeometryShader;
    private int mFragmentShader;

    private int mVertexHandle;
    private int mNormalHandle;
    private int mModelViewMatrixHandle;
    private int mProjectionMatrixHandle;
    private int mCameraPositionHandle;
    private int mCameraDirectionHandle;
    private int mScreenSizeHandle;

    // Matrices
    private float[] mModelViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];

    // Camera
    private float[] mCameraPosition = new float[3];
    private float[] mCameraDirection = new float[3];

    //Screen Size
    private float[] mScreenSize = new float[2];

    // Motion
    public  float mSurfaceAspectRatio;

    //final int mBuffers[] = new int[3];

    public MainRenderer(Activity _context)
    {
        mContext = _context;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES31.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES31.glClearDepthf(1.0f);

        GLES31.glEnable(GLES31.GL_CULL_FACE);
        GLES31.glCullFace(GLES31.GL_BACK);
        GLES31.glFrontFace(GLES31.GL_CCW);
        GLES31.glEnable(GLES31.GL_DEPTH_TEST);

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

            float[] center = mMesh.getCenter();
            mCameraPosition[0] = center[0];
            mCameraPosition[1] = center[1] + mMesh.getLargetLength() / 2;
            mCameraPosition[2] = mMesh.getLargetLength() + center[2];

            mCameraDirection[0] = 0.0f;
            mCameraDirection[1] = mMesh.getLargetLength() / 2;
            mCameraDirection[2] = -mMesh.getLargetLength();

            Matrix.setLookAtM(mModelViewMatrix, 0, mCameraPosition[0], mCameraPosition[1], mCameraPosition[2],
                    center[0], center[1], center[2], 0.0f, 1.0f, 0.0f);

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

    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT | GLES31.GL_DEPTH_BUFFER_BIT);

        GLES31.glUseProgram(mShaderProgram);

        GLES31.glEnableVertexAttribArray(mVertexHandle);
        GLES31.glVertexAttribPointer(mVertexHandle, 3, GLES31.GL_FLOAT, false, 3 * 4, mMesh.getPoints());

//        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, mBuffers[0]);
//        GLES31.glEnableVertexAttribArray(mVertexHandle);
//        GLES31.glVertexAttribPointer(mVertexHandle, 3, GLES31.GL_FLOAT, false, 0, 0);

        GLES31.glEnableVertexAttribArray(mNormalHandle);
        GLES31.glVertexAttribPointer(mNormalHandle, 3, GLES31.GL_FLOAT, false, 3 * 4, mMesh.getNormals());

        // GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, mBuffers[1]);
        // GLES31.glEnableVertexAttribArray(mNormalHandle);
        // GLES31.glVertexAttribPointer(mNormalHandle, 3, GLES31.GL_FLOAT, false, 0, 0);

        // Pass the projection matrix to OpenGL
        GLES31.glUniformMatrix4fv(mProjectionMatrixHandle, 1, false, mProjectionMatrix, 0);
        GLES31.glUniformMatrix4fv(mModelViewMatrixHandle, 1, false, mModelViewMatrix, 0);

        // Camera Position
        GLES31.glUniform3f(mCameraPositionHandle, mCameraPosition[0], mCameraPosition[1], mCameraPosition[2]);

        // Camera Direction
        GLES31.glUniform3f(mCameraDirectionHandle, mCameraDirection[0], mCameraDirection[1], mCameraDirection[2]);

        // Screen Size
        GLES31.glUniform2f(mScreenSizeHandle, mScreenSize[0], mScreenSize[1]);

        // Then, we issue the render call
        //GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, mBuffers[2]);
        GLES31.glDrawElements(GLES31.GL_TRIANGLES, mMesh.getNumberOfFaces() * 3, GLES31.GL_UNSIGNED_INT, mMesh.getFacesVertexIndices());

        // Finally, we disable the vertex, normal arrays
        GLES31.glDisableVertexAttribArray(mNormalHandle);
        GLES31.glDisableVertexAttribArray(mVertexHandle);
        //GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, 0);

        ShaderUtils.checkGLError("Rendering of the 3D mesh failed");
    }

    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        GLES31.glViewport(0, 0, width, height);
        mSurfaceAspectRatio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.perspectiveM(mProjectionMatrix, 0, 50.0f, mSurfaceAspectRatio, 0.0f, 50.0f);
    }

    private String readShaderSourceCode(int rawFileIndex) {
        InputStream shaderInputStream = mContext.getResources().openRawResource(rawFileIndex);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = shaderInputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            shaderInputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }

    public void setRotationDelta(float deltaX, float deltaY) {

        float[] centerVector = new float[4];
        float[] center = mMesh.getCenter();
        centerVector[0] = center[0];
        centerVector[1] = center[1];
        centerVector[2] = center[2];
        centerVector[3] = 1.0f;

        //convert vector to be in camera space
        Matrix.multiplyMV(centerVector, 0, mModelViewMatrix, 0, centerVector, 0);

        float[] translationMatrix = new float[16];
        float[] rotationMatrix = new float[16];
        float[] reversedTranslationMatrix = new float[16];

        Matrix.setIdentityM(translationMatrix, 0);
        Matrix.translateM(translationMatrix,0, -centerVector[0], -centerVector[1], -centerVector[2]);

        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.rotateM(rotationMatrix, 0, deltaX, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(rotationMatrix, 0, deltaY, 1.0f, 0.0f, 0.0f);

        Matrix.setIdentityM(reversedTranslationMatrix, 0);
        Matrix.translateM(reversedTranslationMatrix, 0, centerVector[0], centerVector[1], centerVector[2]);

        Matrix.multiplyMM(mModelViewMatrix, 0, translationMatrix, 0, mModelViewMatrix, 0);

        Matrix.multiplyMM(mModelViewMatrix, 0, rotationMatrix, 0, mModelViewMatrix, 0);

        Matrix.multiplyMM(mModelViewMatrix, 0, reversedTranslationMatrix, 0, mModelViewMatrix, 0);
    }

    public void setScaleFactor(float _scaleFactor)
    {
        Matrix.perspectiveM(mProjectionMatrix, 0, 45.0f / _scaleFactor, mSurfaceAspectRatio, 0.1f, 10.0f);
    }

    public void changeRenderingType()
    {
    }
}
