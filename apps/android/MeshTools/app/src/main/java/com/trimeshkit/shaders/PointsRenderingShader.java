package com.trimeshkit.shaders;

import android.content.Context;
import android.opengl.GLES31;

import com.trimeshkit.meshtools.R;

/**
 * Created by wahmed on 07/11/2017.
 */

public class PointsRenderingShader extends RenderingShader {


    public PointsRenderingShader(Context _context) {
        super(_context);
    }

    public void initShader() {
        super.initShader();

        // load vertex shader
        mVertexShader = ShaderUtils.loadShader(GLES31.GL_VERTEX_SHADER, ShaderUtils.readShaderSourceCode(mContext, R.raw.main_vertex));

        // load fragment shader
        mFragmentShader = ShaderUtils.loadShader(GLES31.GL_FRAGMENT_SHADER, ShaderUtils.readShaderSourceCode(mContext, R.raw.main_fragment));

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

    public void render() {
        GLES31.glUseProgram(mShaderProgram);
        super.render(GLES31.GL_POINTS);
    }

}
