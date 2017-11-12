package com.trimeshkit.shaders;

import android.content.Context;
import android.opengl.GLES31;
import com.trimeshkit.meshtools.R;

/**
 * Created by wahmed on 07/11/2017.
 */

public class NormalsRenderingShader extends RenderingShader{


    public NormalsRenderingShader(Context _context)
    {
        super(_context);
    }

    public void initShader()
    {
        super.initShader();

        // load vertex shader
        mVertexShader = ShaderUtils.loadShader(GLES31.GL_VERTEX_SHADER, readShaderSourceCode(R.raw.normals_vertex));

        // load fragment shader
        mFragmentShader = ShaderUtils.loadShader(GLES31.GL_FRAGMENT_SHADER, readShaderSourceCode(R.raw.normals_fragment));

        //create program
        mShaderProgram = ShaderUtils.createProgram(mVertexShader, mFragmentShader, -1);

        // Setup the shader variable handles only if we successfully created the shader program
        if (mShaderProgram > 0) {
            // Activate the shader program
            GLES31.glUseProgram(mShaderProgram);

            mVertexHandle = GLES31.glGetAttribLocation(mShaderProgram, "v_Position");
            mNormalHandle = GLES31.glGetAttribLocation(mShaderProgram, "v_Normal");
            mModelViewMatrixHandle = GLES31.glGetUniformLocation(mShaderProgram, "u_ModelViewMatrix");
            mProjectionMatrixHandle = GLES31.glGetUniformLocation(mShaderProgram, "u_ProjectionMatrix");

            // Stop using the shader program
            GLES31.glUseProgram(0);
        }
    }

    public void render()
    {
        GLES31.glUseProgram(mShaderProgram);

        // Redraw background color
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT | GLES31.GL_DEPTH_BUFFER_BIT);

        GLES31.glEnableVertexAttribArray(mVertexHandle);
        GLES31.glVertexAttribPointer(mVertexHandle, 3, GLES31.GL_FLOAT, false, 3 * 4, mMesh.getPoints());

        GLES31.glEnableVertexAttribArray(mNormalHandle);
        GLES31.glVertexAttribPointer(mNormalHandle, 3, GLES31.GL_FLOAT, false, 3 * 4, mMesh.getNormals());

        // Pass the projection matrix to OpenGL
        GLES31.glUniformMatrix4fv(mProjectionMatrixHandle, 1, false, mProjectionMatrix, 0);
        GLES31.glUniformMatrix4fv(mModelViewMatrixHandle, 1, false, mModelViewMatrix, 0);

        // Then, we issue the render call
        GLES31.glDrawElements(GLES31.GL_TRIANGLES, mMesh.getNumberOfFaces() * 3, GLES31.GL_UNSIGNED_INT, mMesh.getFacesVertexIndices());

        // Finally, we disable the vertex, normal arrays
        GLES31.glDisableVertexAttribArray(mNormalHandle);
        GLES31.glDisableVertexAttribArray(mVertexHandle);

        ShaderUtils.checkGLError("Rendering of the 3D mesh failed");
    }

}
