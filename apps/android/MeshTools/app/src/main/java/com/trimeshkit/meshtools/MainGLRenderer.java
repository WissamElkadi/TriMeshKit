package com.trimeshkit.meshtools;

import android.app.Activity;
import android.content.Context;
import android.drm.DrmRights;
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

    private Context mContext;

    private Definations.RenderingModeType mCurrentRenderingModeType;
    private Definations.SketchModeType mCurrentSketchingModeType;

    private SolidRenderingShader mSolidRenderingShader;
    private SoildWireframeRenderingShader mSoildWireframeRenderingShader;
    private NormalsRenderingShader mNormalsRenderingShader;
    private WireframeRenderingShader mWireframeRenderingShader;
    private PointsRenderingShader mPointsRenderingShader;

    private ArrayList<LassoShader> mBoundryLassoShaders = new ArrayList<>();
    private ArrayList<LassoShader> mFlatLassoShaders = new ArrayList<>();
    private ArrayList<LassoShader> mFeatureLassoShaders = new ArrayList<>();
    private ArrayList<LassoShader> mConvexLassoShaders = new ArrayList<>();
    private ArrayList<LassoShader> mConcaveLassoShaders = new ArrayList<>();
    private ArrayList<LassoShader> mValleyLassoShaders = new ArrayList<>();
    private ArrayList<LassoShader> mRidgeLassoShaders = new ArrayList<>();


    private boolean mIsRendering = false;

    public MainGLRenderer(Context _context) {
        mContext = _context;

        mSoildWireframeRenderingShader = new SoildWireframeRenderingShader(_context);
        mSolidRenderingShader = new SolidRenderingShader(_context);
        mWireframeRenderingShader = new WireframeRenderingShader(_context);
        mNormalsRenderingShader = new NormalsRenderingShader(_context);
        mPointsRenderingShader = new PointsRenderingShader(_context);
        mCurrentRenderingModeType = Definations.RenderingModeType.SOLID;
        mCurrentSketchingModeType = Definations.SketchModeType.NONE;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES31.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES31.glClearDepthf(1.0f);

        //GLES31.glEnable(GLES31.GL_CULL_FACE);
        //GLES31.glCullFace(GLES31.GL_BACK);
        GLES31.glEnable(GL10.GL_LINE_SMOOTH);
        GLES31.glHint(GL10.GL_LINE_SMOOTH_HINT, GLES31.GL_NICEST);
        GLES31.glFrontFace(GLES31.GL_CCW);
        GLES31.glEnable(GLES31.GL_DEPTH_TEST);

        mSoildWireframeRenderingShader.initShader();
        mSolidRenderingShader.initShader();
        mWireframeRenderingShader.initShader();
        mNormalsRenderingShader.initShader();
        mPointsRenderingShader.initShader();
    }

    public void onDrawFrame(GL10 unused) {
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT | GLES31.GL_DEPTH_BUFFER_BIT);

        for (LassoShader lassoShader : mBoundryLassoShaders) {
            if (!lassoShader.isInitialized())
                lassoShader.initShader();

            lassoShader.render();
        }

        for (LassoShader lassoShader : mFlatLassoShaders) {
            if (!lassoShader.isInitialized())
                lassoShader.initShader();

            lassoShader.render();
        }

        for (LassoShader lassoShader : mConvexLassoShaders) {
            if (!lassoShader.isInitialized())
                lassoShader.initShader();
            lassoShader.render();
        }

        for (LassoShader lassoShader : mConcaveLassoShaders) {
            if (!lassoShader.isInitialized())
                lassoShader.initShader();

            lassoShader.render();
        }

        for (LassoShader lassoShader : mFeatureLassoShaders) {
            if (!lassoShader.isInitialized())
                lassoShader.initShader();

            lassoShader.render();
        }

        for (LassoShader lassoShader : mValleyLassoShaders) {
            if (!lassoShader.isInitialized())
                lassoShader.initShader();

            lassoShader.render();
        }

        for (LassoShader lassoShader : mRidgeLassoShaders) {
            if (!lassoShader.isInitialized())
                lassoShader.initShader();

            lassoShader.render();
        }
        if (mIsRendering) {
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

    public void loadMesh(TriMesh _triMesh, boolean updateModelViewMatrix) {
        mIsRendering = false;

        mSoildWireframeRenderingShader.setMesh(_triMesh, updateModelViewMatrix);
        mSolidRenderingShader.setMesh(_triMesh, updateModelViewMatrix);
        mWireframeRenderingShader.setMesh(_triMesh, updateModelViewMatrix);
        mNormalsRenderingShader.setMesh(_triMesh, updateModelViewMatrix);
        mPointsRenderingShader.setMesh(_triMesh, updateModelViewMatrix);

        mIsRendering = true;
    }

    public void addLassoPoints(double _x, double _y, boolean _isFirstLassoPoint) {

        if (_isFirstLassoPoint) {
            if (mCurrentSketchingModeType == Definations.SketchModeType.BOUNDARY) {
                float[] boundryColor = {0.0f, 0.0f, 0.0f, 1.0f};
                LassoShader boundryShader = new LassoShader(mContext, boundryColor);
                mBoundryLassoShaders.add(boundryShader);
            } else if (mCurrentSketchingModeType == Definations.SketchModeType.FLAT) {
                float[] flatColor = {0.0f, 1.0f, 0.0f, 1.0f};
                LassoShader flatShader = new LassoShader(mContext, flatColor);
                mFlatLassoShaders.add(flatShader);
            } else if (mCurrentSketchingModeType == Definations.SketchModeType.CONVEX) {
                float[] convexColor = {1.0f, 0.0f, 0.0f, 1.0f};
                LassoShader convexShader = new LassoShader(mContext, convexColor);
                mConvexLassoShaders.add(convexShader);
            } else if (mCurrentSketchingModeType == Definations.SketchModeType.CONCAVE) {
                float[] concaveColor = {0.0f, 0.0f, 1.0f, 1.0f};
                LassoShader concaveShader = new LassoShader(mContext, concaveColor);
                mConcaveLassoShaders.add(concaveShader);
            } else if (mCurrentSketchingModeType == Definations.SketchModeType.FEATURE) {
                float[] featureColor = {0.0f, 0.392f, 0.0f, 1.0f};
                LassoShader featureShader = new LassoShader(mContext, featureColor);
                mFeatureLassoShaders.add(featureShader);
            } else if (mCurrentSketchingModeType == Definations.SketchModeType.VALLEY) {
                float[] valleyColor = {1.0f, 0.498f, 0.314f, 1.0f};
                LassoShader valleyShader = new LassoShader(mContext, valleyColor);
                mValleyLassoShaders.add(valleyShader);
            } else if (mCurrentSketchingModeType == Definations.SketchModeType.RIDGE) {
                float[] ridgeColor = {0.502f, 0.0f, 0.502f, 1.0f};
                LassoShader ridgeShader = new LassoShader(mContext, ridgeColor);
                mRidgeLassoShaders.add(ridgeShader);
            }

        }

        if (mCurrentSketchingModeType == Definations.SketchModeType.BOUNDARY)
            mBoundryLassoShaders.get(mBoundryLassoShaders.size() - 1).addPoints(_x, _y);
        else if (mCurrentSketchingModeType == Definations.SketchModeType.FLAT)
            mFlatLassoShaders.get(mFlatLassoShaders.size() - 1).addPoints(_x, _y);
        else if (mCurrentSketchingModeType == Definations.SketchModeType.CONVEX)
            mConvexLassoShaders.get(mConvexLassoShaders.size() - 1).addPoints(_x, _y);
        else if (mCurrentSketchingModeType == Definations.SketchModeType.CONCAVE)
            mConcaveLassoShaders.get(mConcaveLassoShaders.size() - 1).addPoints(_x, _y);
        else if (mCurrentSketchingModeType == Definations.SketchModeType.FEATURE)
            mFeatureLassoShaders.get(mFeatureLassoShaders.size() - 1).addPoints(_x, _y);
        else if (mCurrentSketchingModeType == Definations.SketchModeType.VALLEY)
            mValleyLassoShaders.get(mValleyLassoShaders.size() - 1).addPoints(_x, _y);
        else if (mCurrentSketchingModeType == Definations.SketchModeType.RIDGE)
            mRidgeLassoShaders.get(mRidgeLassoShaders.size() - 1).addPoints(_x, _y);
    }

    public void changeSketchType(Definations.SketchModeType _sketchTypeMode) {
        mCurrentSketchingModeType = _sketchTypeMode;
    }

    public ArrayList<Float> getBoundryPoits() {
        ArrayList<Float> result = new ArrayList<>();
        for (LassoShader lassoShader : mBoundryLassoShaders)
            result.addAll(lassoShader.getPointsSet());

        return result;
    }
}
