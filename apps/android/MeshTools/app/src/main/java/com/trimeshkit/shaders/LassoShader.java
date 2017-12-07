package com.trimeshkit.shaders;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLES31;

import com.trimeshkit.meshtools.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Created by wahmed on 06/12/2017.
 */

public class LassoShader {

    private Activity mContext;
    private float[] mColor;

    // shader
    private int mShaderProgram;
    private int mVertexShader;
    private int mFragmentShader;

    private int mVertexHandle;
    private int mColorHandle;

    private ByteBuffer mPoints;
    private ArrayList<Float> mPointsArray = new ArrayList<>(0);
    private int mPointsCount = 0;

    public LassoShader(Context _context, float[] _color) {
        mContext = (Activity) _context;
        mColor = _color;
    }

    public void initShader() {
        // load vertex shader
        mVertexShader = ShaderUtils.loadShader(GLES31.GL_VERTEX_SHADER, ShaderUtils.readShaderSourceCode(mContext, R.raw.lasso_vertex));

        // load fragment shader
        mFragmentShader = ShaderUtils.loadShader(GLES31.GL_FRAGMENT_SHADER, ShaderUtils.readShaderSourceCode(mContext, R.raw.lasso_fragment));

        //create program
        mShaderProgram = ShaderUtils.createProgram(mVertexShader, mFragmentShader, -1);

        // Setup the shader variable handles only if we successfully created the shader program
        if (mShaderProgram > 0) {
            // Activate the shader program
            GLES31.glUseProgram(mShaderProgram);

            mVertexHandle = GLES31.glGetAttribLocation(mShaderProgram, "v_Position");
            mColorHandle = GLES31.glGetUniformLocation(mShaderProgram, "u_Color");

            // Stop using the shader program
            GLES31.glUseProgram(0);
        }
    }

    public synchronized void addPoints(double _x, double _y)
    {
        mPointsCount++;

        mPointsArray.add((float)_x);
        mPointsArray.add((float)_y);

        mPoints = ByteBuffer.allocateDirect((Float.SIZE / Byte.SIZE) * mPointsArray.size());
        mPoints.order(ByteOrder.nativeOrder());

        for (float d : mPointsArray)
            mPoints.putFloat(d);

        mPoints.rewind();
    }

    public void render() {
        GLES31.glUseProgram(mShaderProgram);

        if(mPointsCount > 0) {
            GLES31.glLineWidth(1.0f);
            GLES31.glEnableVertexAttribArray(mVertexHandle);
            GLES31.glVertexAttribPointer(mVertexHandle, 2, GLES31.GL_FLOAT, false, 2 * 4, mPoints);

            // Color Position
            GLES31.glUniform4f(mColorHandle, mColor[0], mColor[1], mColor[2], mColor[3]);

            GLES31.glDrawArrays(GLES31.GL_LINES, 0, mPointsCount);

            GLES31.glDisableVertexAttribArray(mVertexHandle);
        }
        GLES31.glUseProgram(0);
    }

    public ArrayList<Float> getPointsSet()
    {
        return mPointsArray;
    }
}
