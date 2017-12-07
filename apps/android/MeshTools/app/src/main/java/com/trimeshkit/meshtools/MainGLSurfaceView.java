package com.trimeshkit.meshtools;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.trimeshkit.meshprocessing.TriMesh;
import com.trimeshkit.state.ApplicationState;

import java.util.ArrayList;

/**
 * Created by wahmed on 21/10/2017.
 */

public class MainGLSurfaceView extends GLSurfaceView {

    private Context mContext;
    private final MainGLRenderer mRenderer;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    // Touch Events
    private float mPreviousX;
    private float mPreviousY;
    private float mDisplayDenisty;
    private float mDisplayWidth;
    private float mDisplayHeight;

    private boolean mDrawing = false;

    public MainGLSurfaceView(Context _context, AttributeSet _attributeSet) {
        super(_context, _attributeSet);

        mContext = _context;

        // Create an OpenGL ES 3.1 context
        setEGLContextClientVersion(3);

        mRenderer = new MainGLRenderer(mContext);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        mScaleDetector = new ScaleGestureDetector(mContext, new ScaleListener());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mDisplayDenisty = displayMetrics.density;
        mDisplayWidth = displayMetrics.widthPixels;
        mDisplayHeight = displayMetrics.heightPixels;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getPointerCount() == 2) {
            // Let the ScaleGestureDetector inspect all events.
            mScaleDetector.onTouchEvent(event);
        } else if (event.getPointerCount() == 1) {

            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDrawing = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(ApplicationState.getApplicationState() == ApplicationState.ApplicationStateEnum.GENERAL) {
                        float deltaX = (x - mPreviousX) / mDisplayDenisty / 2.0f;
                        float deltaY = (y - mPreviousY) / mDisplayDenisty / 2.0f;
                        mRenderer.setRotationDelta(deltaX, deltaY);
                    }
                    else if (mDrawing && ApplicationState.getApplicationState() == ApplicationState.ApplicationStateEnum.SKETCH)
                    {
                        double  xNormalized = (2 * x) / mDisplayWidth -1.0;
                        double  yNormalized = 1.0 - (2 * y) / mDisplayHeight ;

                        mRenderer.addLassoPoints(xNormalized, yNormalized);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    mDrawing = false;
                    break;
            }

            mPreviousX = x;
            mPreviousY = y;
        }

        requestRender();
        return true;
    }

    public boolean onDragEvent(DragEvent event) {
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            mRenderer.setScaleFactor(mScaleFactor);

            invalidate();
            return true;
        }
    }

    public void changeRenderingType(Definations.RenderingModeType _renderingTypeMode) {
        mRenderer.changeRenderingType(_renderingTypeMode);
        requestRender();
    }

    public void changeSketchType(Definations.SketchModeType _sketchTypeMode) {
        mRenderer.changeSketchType(_sketchTypeMode);
    }

    public void loadMesh(TriMesh _triMesh) {
        mRenderer.loadMesh(_triMesh);
        requestRender();
    }

    public  ArrayList<Float> getBoundryPoits()
    {
       return mRenderer.getBoundryPoits();
    }
}
