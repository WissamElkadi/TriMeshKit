package com.trimeshkit.meshtools;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.trimeshkit.meshprocessing.TriMesh;
import com.trimeshkit.shaders.NormalsRenderingShader;
import com.trimeshkit.shaders.PointsRenderingShader;
import com.trimeshkit.shaders.SoildWireframeRenderingShader;
import com.trimeshkit.shaders.SolidRenderingShader;
import com.trimeshkit.shaders.WireframeRenderingShader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by wahmed on 21/10/2017.
 */

public class MainGLRenderer implements GLSurfaceView.Renderer {

    Definations.RenderingModeType mCurrentRenderingModeType;

    private SolidRenderingShader mSolidRenderingShader;
    private SoildWireframeRenderingShader mSoildWireframeRenderingShader;
    private NormalsRenderingShader mNormalsRenderingShader;
    private WireframeRenderingShader mWireframeRenderingShader;
    private PointsRenderingShader mPointsRenderingShader;

    private boolean mIsRendering = false;

    public MainGLRenderer(Context _context)
    {
        mSoildWireframeRenderingShader = new SoildWireframeRenderingShader(_context);
        mSolidRenderingShader = new SolidRenderingShader(_context);
        mWireframeRenderingShader = new WireframeRenderingShader(_context);
        mNormalsRenderingShader = new NormalsRenderingShader(_context);
        mPointsRenderingShader = new PointsRenderingShader(_context);
        mCurrentRenderingModeType = Definations.RenderingModeType.SOLID;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        mSoildWireframeRenderingShader.initShader();
        mSolidRenderingShader.initShader();
        mWireframeRenderingShader.initShader();
        mNormalsRenderingShader.initShader();
        mPointsRenderingShader.initShader();
    }

    public void onDrawFrame(GL10 unused) {
        if(!mIsRendering)
            return;
        if(mCurrentRenderingModeType == Definations.RenderingModeType.WIREFRAME)
        {
            mWireframeRenderingShader.render();
        }
        else if(mCurrentRenderingModeType == Definations.RenderingModeType.SOLID_WIREFRAME)
        {
            mSoildWireframeRenderingShader.render();
        }
        else if(mCurrentRenderingModeType == Definations.RenderingModeType.NORMALS)
        {
            mNormalsRenderingShader.render();
        }
        else if(mCurrentRenderingModeType == Definations.RenderingModeType.POINTS)
        {
            mPointsRenderingShader.render();
        }
        else
        {
            mSolidRenderingShader.render();
        }
    }

    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        mSoildWireframeRenderingShader.updateViewPortAndProjection(width, height);
        mSolidRenderingShader.updateViewPortAndProjection(width, height);
        mWireframeRenderingShader.updateViewPortAndProjection(width, height);
        mNormalsRenderingShader.updateViewPortAndProjection(width, height);
        mPointsRenderingShader.updateViewPortAndProjection(width, height);
    }

    public void setRotationDelta(float deltaX, float deltaY) {

        mSoildWireframeRenderingShader.setRotationDelta(deltaX, deltaY);
        mSolidRenderingShader.setRotationDelta(deltaX, deltaY);
        mWireframeRenderingShader.setRotationDelta(deltaX, deltaY);
        mNormalsRenderingShader.setRotationDelta(deltaX, deltaY);
        mPointsRenderingShader.setRotationDelta(deltaX, deltaY);
    }

    public void setScaleFactor(float _scaleFactor)
    {
        mSoildWireframeRenderingShader.setScaleFactor(_scaleFactor);
        mSolidRenderingShader.setScaleFactor(_scaleFactor);
        mWireframeRenderingShader.setScaleFactor(_scaleFactor);
        mNormalsRenderingShader.setScaleFactor(_scaleFactor);
        mPointsRenderingShader.setScaleFactor(_scaleFactor);
    }

    public void changeRenderingType(Definations.RenderingModeType _renderingTypeMode)
    {
        mCurrentRenderingModeType = _renderingTypeMode;
    }

    public void loadMesh(TriMesh _triMesh)
    {
        mIsRendering = false;

        mSoildWireframeRenderingShader.setMesh(_triMesh);
        mSolidRenderingShader.setMesh(_triMesh);
        mWireframeRenderingShader.setMesh(_triMesh);
        mNormalsRenderingShader.setMesh(_triMesh);
        mPointsRenderingShader.setMesh(_triMesh);

        mIsRendering = true;
    }
}
