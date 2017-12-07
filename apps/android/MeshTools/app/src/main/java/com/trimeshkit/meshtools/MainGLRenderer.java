package com.trimeshkit.meshtools;

import android.content.Context;
import android.opengl.GLES31;
import android.opengl.GLSurfaceView;

import com.trimeshkit.meshprocessing.TriMesh;
import com.trimeshkit.shaders.LassoShader;
import com.trimeshkit.shaders.NormalsRenderingShader;
import com.trimeshkit.shaders.PointsRenderingShader;
import com.trimeshkit.shaders.SoildWireframeRenderingShader;
import com.trimeshkit.shaders.SolidRenderingShader;
import com.trimeshkit.shaders.WireframeRenderingShader;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by wahmed on 21/10/2017.
 */

public class MainGLRenderer implements GLSurfaceView.Renderer {

    Definations.RenderingModeType mCurrentRenderingModeType;
    Definations.SketchModeType mCurrentSketchingModeType;

    private SolidRenderingShader mSolidRenderingShader;
    private SoildWireframeRenderingShader mSoildWireframeRenderingShader;
    private NormalsRenderingShader mNormalsRenderingShader;
    private WireframeRenderingShader mWireframeRenderingShader;
    private PointsRenderingShader mPointsRenderingShader;
    private LassoShader mBoundryLassoShader;
    private LassoShader mFlatLassoShader;
    private LassoShader mConvexLassoShader;
    private LassoShader mConcaveLassoShader;


    private boolean mIsRendering = false;

    public MainGLRenderer(Context _context) {
        mSoildWireframeRenderingShader = new SoildWireframeRenderingShader(_context);
        mSolidRenderingShader = new SolidRenderingShader(_context);
        mWireframeRenderingShader = new WireframeRenderingShader(_context);
        mNormalsRenderingShader = new NormalsRenderingShader(_context);
        mPointsRenderingShader = new PointsRenderingShader(_context);
        mCurrentRenderingModeType = Definations.RenderingModeType.SOLID;
        mCurrentSketchingModeType = Definations.SketchModeType.NONE;

        float[] boundryColor = {0.0f, 0.0f, 0.0f, 1.0f};
        mBoundryLassoShader = new LassoShader(_context, boundryColor);

        float[] flatColor = {0.0f, 1.0f, 0.0f, 1.0f};
        mFlatLassoShader = new LassoShader(_context, flatColor);

        float[] convexColor = {1.0f, 0.0f, 0.0f, 1.0f};
        mConvexLassoShader = new LassoShader(_context, convexColor);

        float[] concave = {0.0f, 0.0f, 1.0f, 1.0f};
        mConcaveLassoShader = new LassoShader(_context, concave);
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES31.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES31.glClearDepthf(1.0f);

        GLES31.glEnable(GLES31.GL_CULL_FACE);
        GLES31.glCullFace(GLES31.GL_BACK);
        GLES31.glFrontFace(GLES31.GL_CCW);
        GLES31.glEnable(GLES31.GL_DEPTH_TEST);

        mSoildWireframeRenderingShader.initShader();
        mSolidRenderingShader.initShader();
        mWireframeRenderingShader.initShader();
        mNormalsRenderingShader.initShader();
        mPointsRenderingShader.initShader();

        mBoundryLassoShader.initShader();
        mFlatLassoShader.initShader();
        mConvexLassoShader.initShader();
        mConcaveLassoShader.initShader();
    }

    public void onDrawFrame(GL10 unused) {
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT | GLES31.GL_DEPTH_BUFFER_BIT);

        mBoundryLassoShader.render();
        mBoundryLassoShader.render();
        mFlatLassoShader.render();
        mConvexLassoShader.render();
        mConcaveLassoShader.render();

        if(mIsRendering) {
            if (mCurrentRenderingModeType == Definations.RenderingModeType.WIREFRAME) {
                mWireframeRenderingShader.render();
            } else if (mCurrentRenderingModeType == Definations.RenderingModeType.SOLID_WIREFRAME) {
                mSoildWireframeRenderingShader.render();
            } else if (mCurrentRenderingModeType == Definations.RenderingModeType.NORMALS) {
                mNormalsRenderingShader.render();
            } else if (mCurrentRenderingModeType == Definations.RenderingModeType.POINTS) {
                mPointsRenderingShader.render();
            } else {
                mSolidRenderingShader.render();
            }
        }
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        mSoildWireframeRenderingShader.updateViewPortAndProjection(width, height);
        mSolidRenderingShader.updateViewPortAndProjection(width, height);
        mWireframeRenderingShader.updateViewPortAndProjection(width, height);
        mNormalsRenderingShader.updateViewPortAndProjection(width, height);
        mPointsRenderingShader.updateViewPortAndProjection(width, height);
    }

    public void setRotationDelta(float deltaX, float deltaY) {
        if (mIsRendering) {
            mSoildWireframeRenderingShader.setRotationDelta(deltaX, deltaY);
            mSolidRenderingShader.setRotationDelta(deltaX, deltaY);
            mWireframeRenderingShader.setRotationDelta(deltaX, deltaY);
            mNormalsRenderingShader.setRotationDelta(deltaX, deltaY);
            mPointsRenderingShader.setRotationDelta(deltaX, deltaY);
        }
    }

    public void setScaleFactor(float _scaleFactor) {
        if (mIsRendering) {
            mSoildWireframeRenderingShader.setScaleFactor(_scaleFactor);
            mSolidRenderingShader.setScaleFactor(_scaleFactor);
            mWireframeRenderingShader.setScaleFactor(_scaleFactor);
            mNormalsRenderingShader.setScaleFactor(_scaleFactor);
            mPointsRenderingShader.setScaleFactor(_scaleFactor);
        }
    }

    public void changeRenderingType(Definations.RenderingModeType _renderingTypeMode) {
        mCurrentRenderingModeType = _renderingTypeMode;
    }

    public void loadMesh(TriMesh _triMesh) {
        mIsRendering = false;

        mSoildWireframeRenderingShader.setMesh(_triMesh);
        mSolidRenderingShader.setMesh(_triMesh);
        mWireframeRenderingShader.setMesh(_triMesh);
        mNormalsRenderingShader.setMesh(_triMesh);
        mPointsRenderingShader.setMesh(_triMesh);

        mIsRendering = true;
    }

    public void addLassoPoints(double _x, double _y) {
        if (mCurrentSketchingModeType == Definations.SketchModeType.BOUNDARY)
            mBoundryLassoShader.addPoints(_x, _y);
        else if (mCurrentSketchingModeType == Definations.SketchModeType.FLAT)
            mFlatLassoShader.addPoints(_x, _y);
        else if (mCurrentSketchingModeType == Definations.SketchModeType.CONVEX)
            mConvexLassoShader.addPoints(_x, _y);
        else if (mCurrentSketchingModeType == Definations.SketchModeType.CONCAVE)
            mConcaveLassoShader.addPoints(_x, _y);
    }

    public void changeSketchType(Definations.SketchModeType _sketchTypeMode) {
        mCurrentSketchingModeType = _sketchTypeMode;
    }

    public ArrayList<Float> getBoundryPoits() {
        return mBoundryLassoShader.getPointsSet();
    }
}
