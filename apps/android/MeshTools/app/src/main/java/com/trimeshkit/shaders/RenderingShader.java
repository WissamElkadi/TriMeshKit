package com.trimeshkit.shaders;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLES31;
import android.opengl.Matrix;

import com.trimeshkit.meshprocessing.TriMesh;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wahmed on 07/11/2017.
 */

public class RenderingShader {

    protected Activity mContext;

    // Mesh
    protected TriMesh mTriMesh;

    // shader
    protected int mShaderProgram;
    protected int mVertexShader;
    protected int mGeometryShader;
    protected int mFragmentShader;

    protected int mVertexHandle;
    protected int mNormalHandle;
    protected int mModelViewMatrixHandle;
    protected int mProjectionMatrixHandle;
    protected int mCameraPositionHandle;
    protected int mCameraDirectionHandle;


    // Matrices
    protected float[] mModelViewMatrix = new float[16];
    protected float[] mProjectionMatrix = new float[16];

    // Camera
    protected float[] mCameraPosition = new float[3];
    protected float[] mCameraDirection = new float[3];


    // Motion
    public  float mSurfaceAspectRatio;

    public RenderingShader(Context _context)
    {
        mContext = (Activity) _context;
    }

    public void initShader() {
        // Set the background frame color
        GLES31.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES31.glClearDepthf(1.0f);

        GLES31.glEnable(GLES31.GL_CULL_FACE);
        GLES31.glCullFace(GLES31.GL_BACK);
        GLES31.glFrontFace(GLES31.GL_CCW);
        GLES31.glEnable(GLES31.GL_DEPTH_TEST);
    }

    protected String readShaderSourceCode(int rawFileIndex) {
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


    protected void render(int renderingMode)
    {
        // Redraw background color
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT | GLES31.GL_DEPTH_BUFFER_BIT);

        GLES31.glEnableVertexAttribArray(mVertexHandle);
        GLES31.glVertexAttribPointer(mVertexHandle, 3, GLES31.GL_FLOAT, false, 3 * 4, mTriMesh.getVerticesPoints());

//        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, mBuffers[0]);
//        GLES31.glEnableVertexAttribArray(mVertexHandle);
//        GLES31.glVertexAttribPointer(mVertexHandle, 3, GLES31.GL_FLOAT, false, 0, 0);

        GLES31.glEnableVertexAttribArray(mNormalHandle);
        GLES31.glVertexAttribPointer(mNormalHandle, 3, GLES31.GL_FLOAT, false, 3 * 4, mTriMesh.getVerticesNormals());

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

        // Then, we issue the render call
        //GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, mBuffers[2]);
        GLES31.glDrawElements(renderingMode, mTriMesh.getNumberOfFaces() * 3, GLES31.GL_UNSIGNED_INT, mTriMesh.getFacesIndices());

        // Finally, we disable the vertex, normal arrays
        GLES31.glDisableVertexAttribArray(mNormalHandle);
        GLES31.glDisableVertexAttribArray(mVertexHandle);
        //GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, 0);

        ShaderUtils.checkGLError("Rendering of the 3D mesh failed");
    }

    public void updateViewPortAndProjection(int width, int height)
    {
        GLES31.glViewport(0, 0, width, height);
        mSurfaceAspectRatio = (float) width / height;

        // this projection matrix is applied to object coordinates
        Matrix.perspectiveM(mProjectionMatrix, 0, 45.0f, mSurfaceAspectRatio, 0.1f, 30.0f);
    }

    public void setRotationDelta(float deltaX, float deltaY) {

        float[] centerVector = new float[4];
        float[] center = mTriMesh.getCenter();
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
        Matrix.perspectiveM(mProjectionMatrix, 0, 45.0f / _scaleFactor, mSurfaceAspectRatio, 0.1f, 30.0f);
    }

    public void setMesh(TriMesh _mesh)
    {
        mTriMesh = _mesh;

        float[] center = mTriMesh.getCenter();
        mCameraPosition[0] = center[0];
        mCameraPosition[1] = center[1] + mTriMesh.getLargetLength() / 2;
        mCameraPosition[2] = mTriMesh.getLargetLength() + center[2];

        mCameraDirection[0] = 0.0f;
        mCameraDirection[1] = mTriMesh.getLargetLength() / 2;
        mCameraDirection[2] = -mTriMesh.getLargetLength();

        Matrix.setLookAtM(mModelViewMatrix, 0, mCameraPosition[0], mCameraPosition[1], mCameraPosition[2],
                center[0], center[1], center[2], 0.0f, 1.0f, 0.0f);
    }

}
