package com.trimeshkit.meshtools;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.trimeshkit.meshprocessing.TriMesh;
import com.trimeshkit.meshprocessing.TriMeshUtils;
import com.trimeshkit.state.ApplicationState;

import java.util.ArrayList;

public class MainSurfaceActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    // Used to load the 'MeshTools' library on application startup.
    static {
        System.loadLibrary("TriMeshKit-JNI");
    }

    private TriMesh mTriMesh;
    private MainGLSurfaceView mGLView;
    private Boolean mIsRenderingFABOpen = false, mIsSketchingFABOpen = false;
    private FloatingActionButton mRenderingFAB, mSolidRenderingFAB, mSolidWireframeRenderingFAB, mWireframeRenderingFAB, mNormalRenderingFAB, mPointRenderingFAB;
    private FloatingActionButton mSketchingFAB, mBoundrySketchingFAB, mFlatSketchingFAB, mFeatureSketchingFAB, mConvexSketchingFAB, mConcaveSketchingFAB, mValleySketchingFAB,
            mRidgeSketchingFAB, mEreaseSketchingFAB, mApplySketchingFAB;

    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    private static final int PICK_MESH_REQUEST = 1;


    private class SmoothMeshTask extends AsyncTask<Void, Integer, Boolean> {

        private MainSurfaceActivity activity;
        private ProgressDialog progressBar;

        public SmoothMeshTask(MainSurfaceActivity context) {

            activity = context;
            progressBar = new ProgressDialog(context);
            progressBar.setMessage("Smoothing Mesh ...");
            progressBar.setIndeterminate(true);
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        protected void onPreExecute() {
            progressBar.show();
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected Boolean doInBackground(Void... parms) {
//           / activity.mTriMesh.smoothMesh();

            return true;
        }

        protected void onPostExecute(Boolean result) {
            progressBar.dismiss();
        }
    }

    private class LoadMeshTask extends AsyncTask<String, Integer, Boolean> {

        private MainSurfaceActivity activity;
        private ProgressDialog progressBar;

        public LoadMeshTask(MainSurfaceActivity context) {

            activity = context;
            progressBar = new ProgressDialog(context);
            progressBar.setMessage("Loading Mesh ...");
            progressBar.setIndeterminate(true);
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        protected void onPreExecute() {
            progressBar.show();
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected Boolean doInBackground(String... parms) {
            activity.mTriMesh = new TriMesh();
            boolean loaded = TriMeshUtils.readMesh(activity.mTriMesh, parms[0], true);

            if (loaded) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.mGLView.loadMesh(activity.mTriMesh, true);
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "Couldn't Load mesh", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
            progressBar.dismiss();
        }
    }

    private class TriangulatePointsTask extends AsyncTask<ArrayList<Float>, Integer, Boolean> {

        private MainSurfaceActivity activity;
        private ProgressDialog progressBar;

        public TriangulatePointsTask(MainSurfaceActivity context) {

            activity = context;
            progressBar = new ProgressDialog(context);
            progressBar.setMessage("triangulate ...");
            progressBar.setIndeterminate(true);
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        protected void onPreExecute() {
            progressBar.show();
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected Boolean doInBackground(ArrayList<Float>... parms) {
            activity.mTriMesh = new TriMesh();

            double[] doubleArray = new double[parms[0].size()];
            int i = 0;

            for (Float f : parms[0]) {
                doubleArray[i++] = (f != null ? f : Float.NaN);
            }

            TriMeshUtils.triangulate(activity.mTriMesh, doubleArray);

            ApplicationState.setApplicationState(ApplicationState.ApplicationStateEnum.GENERAL);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.mGLView.loadMesh(activity.mTriMesh, false);
                }
            });

            return true;
        }

        protected void onPostExecute(Boolean result) {
            progressBar.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply screen orientation
        //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        ApplicationState.setApplicationState(ApplicationState.ApplicationStateEnum.GENERAL);

        setContentView(R.layout.main_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActivityManager activityManager
                = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

        final boolean supportsEs3_1 =
                configurationInfo.reqGlEsVersion >= 0x30001;

        if (supportsEs3_1) {
            // Create a GLSurfaceView instance and set it
            // as the ContentView for this Activity.
            mGLView = (MainGLSurfaceView) findViewById(R.id.glSurfaceViewID);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // FAB
        initializeRenderingFAB();
        initializeSketchingFAB();
        initializeFABAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_import_mesh) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("file/*");
            startActivityForResult(intent, PICK_MESH_REQUEST);
        } else if (id == R.id.nav_export_mesh) {
            ApplicationState.setApplicationState(ApplicationState.ApplicationStateEnum.GENERAL);
        } else if (id == R.id.nav_sketch_modeling) {
            ApplicationState.setApplicationState(ApplicationState.ApplicationStateEnum.SKETCH);
        } else if (id == R.id.nav_deformation) {

        } else if (id == R.id.nav_decimate) {

        } else if (id == R.id.nav_smooth) {
            SmoothMeshTask smoothMeshTask = new SmoothMeshTask(this);
            smoothMeshTask.execute();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.renderingFAB:
                animateRenderingFAB();
                break;
            case R.id.solidRenderingFAB:
                    mGLView.changeRenderingType(Definations.RenderingModeType.SOLID);
                break;
            case R.id.solidWireframeRenderingFAB:
                    mGLView.changeRenderingType(Definations.RenderingModeType.SOLID_WIREFRAME);
                break;
            case R.id.wireframeRenderingFAB:
                    mGLView.changeRenderingType(Definations.RenderingModeType.WIREFRAME);
                break;
            case R.id.pointRenderingFAB:
                    mGLView.changeRenderingType(Definations.RenderingModeType.POINTS);
                break;
            case R.id.normalRenderingFAB:
                    mGLView.changeRenderingType(Definations.RenderingModeType.NORMALS);
                break;
            case  R.id.sketchingFAB:
                animateSketchingFAB();
                break;
            case R.id.boundrySketchingFAB:
                mGLView.changeSketchType(Definations.SketchModeType.BOUNDARY);
                break;
            case R.id.flatSketchingFAB:
                mGLView.changeSketchType(Definations.SketchModeType.FLAT);
                break;
            case R.id.featureSketchingFAB:
                mGLView.changeSketchType(Definations.SketchModeType.FEATURE);
                break;
            case R.id.convexSketchingFAB:
                mGLView.changeSketchType(Definations.SketchModeType.CONVEX);
                break;
            case R.id.concaveSketchingFAB:
                mGLView.changeSketchType(Definations.SketchModeType.CONCAVE);
                break;
            case R.id.valleySketchingFAB:
                mGLView.changeSketchType(Definations.SketchModeType.VALLEY);
                break;
            case R.id.ridgeSketchingFAB:
                mGLView.changeSketchType(Definations.SketchModeType.RIDGE);
                break;
            case R.id.ereaseSketching:
                mGLView.ereaseSketching();
                break;
            case R.id.applyBendSketching:
                ArrayList<Float> boundrayList = mGLView.getBoundryPoits();
                TriangulatePointsTask triangulatePointsTask = new TriangulatePointsTask(this);
                triangulatePointsTask.execute(boundrayList);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_MESH_REQUEST) {
            if (resultCode == RESULT_OK) {
                String meshPath = data.getData().getPath();
                LoadMeshTask loadMeshTask = new LoadMeshTask(this);
                loadMeshTask.execute(meshPath);
            }
        }
    }

    private void initializeRenderingFAB()
    {
        /// Rendering FAB
        mRenderingFAB = (FloatingActionButton) findViewById(R.id.renderingFAB);
        mSolidRenderingFAB = (FloatingActionButton) findViewById(R.id.solidRenderingFAB);
        mSolidWireframeRenderingFAB = (FloatingActionButton) findViewById(R.id.solidWireframeRenderingFAB);
        mWireframeRenderingFAB = (FloatingActionButton) findViewById(R.id.wireframeRenderingFAB);
        mNormalRenderingFAB = (FloatingActionButton) findViewById(R.id.normalRenderingFAB);
        mPointRenderingFAB = (FloatingActionButton) findViewById(R.id.pointRenderingFAB);

        mRenderingFAB.setOnClickListener(this);
        mSolidRenderingFAB.setOnClickListener(this);
        mSolidWireframeRenderingFAB.setOnClickListener(this);
        mWireframeRenderingFAB.setOnClickListener(this);
        mNormalRenderingFAB.setOnClickListener(this);
        mPointRenderingFAB.setOnClickListener(this);
    }

    private void initializeSketchingFAB()
    {
        mSketchingFAB = (FloatingActionButton) findViewById(R.id.sketchingFAB);
        mBoundrySketchingFAB = (FloatingActionButton) findViewById(R.id.boundrySketchingFAB);
        mFlatSketchingFAB = (FloatingActionButton) findViewById(R.id.flatSketchingFAB);
        mFeatureSketchingFAB = (FloatingActionButton) findViewById(R.id.featureSketchingFAB);
        mConvexSketchingFAB = (FloatingActionButton) findViewById(R.id.convexSketchingFAB);
        mConcaveSketchingFAB = (FloatingActionButton) findViewById(R.id.concaveSketchingFAB);
        mValleySketchingFAB = (FloatingActionButton) findViewById(R.id.valleySketchingFAB);
        mRidgeSketchingFAB = (FloatingActionButton) findViewById(R.id.ridgeSketchingFAB);
        mEreaseSketchingFAB = (FloatingActionButton) findViewById(R.id.ereaseSketching);
        mApplySketchingFAB = (FloatingActionButton) findViewById(R.id.applyBendSketching);

        mSketchingFAB.setOnClickListener(this);
        mBoundrySketchingFAB.setOnClickListener(this);
        mFlatSketchingFAB.setOnClickListener(this);
        mFeatureSketchingFAB.setOnClickListener(this);
        mConvexSketchingFAB.setOnClickListener(this);
        mConcaveSketchingFAB.setOnClickListener(this);
        mValleySketchingFAB.setOnClickListener(this);
        mRidgeSketchingFAB.setOnClickListener(this);
        mEreaseSketchingFAB.setOnClickListener(this);
        mApplySketchingFAB.setOnClickListener(this);
    }

    private void initializeFABAnimation()
    {
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
    }

    private void animateRenderingFAB()
    {
        if (mIsRenderingFABOpen) {
            mRenderingFAB.startAnimation(rotate_backward);
            mSolidRenderingFAB.startAnimation(fab_close);
            mSolidWireframeRenderingFAB.startAnimation(fab_close);
            mWireframeRenderingFAB.startAnimation(fab_close);
            mNormalRenderingFAB.startAnimation(fab_close);
            mPointRenderingFAB.startAnimation(fab_close);

            mSolidRenderingFAB.setClickable(false);
            mSolidWireframeRenderingFAB.setClickable(false);
            mWireframeRenderingFAB.setClickable(false);
            mNormalRenderingFAB.setClickable(false);
            mPointRenderingFAB.setClickable(false);
            mIsRenderingFABOpen = false;
        } else {
            mRenderingFAB.startAnimation(rotate_forward);
            mSolidRenderingFAB.startAnimation(fab_open);
            mSolidWireframeRenderingFAB.startAnimation(fab_open);
            mWireframeRenderingFAB.startAnimation(fab_open);
            mNormalRenderingFAB.startAnimation(fab_open);
            mPointRenderingFAB.startAnimation(fab_open);

            mSolidRenderingFAB.setClickable(true);
            mSolidWireframeRenderingFAB.setClickable(true);
            mWireframeRenderingFAB.setClickable(true);
            mNormalRenderingFAB.setClickable(true);
            mPointRenderingFAB.setClickable(true);
            mIsRenderingFABOpen = true;
        }
    }

    private void animateSketchingFAB()
    {
        if (mIsSketchingFABOpen) {
            mSketchingFAB.startAnimation(rotate_backward);
            mBoundrySketchingFAB.startAnimation(fab_close);
            mFlatSketchingFAB.startAnimation(fab_close);
            mFeatureSketchingFAB.startAnimation(fab_close);
            mConvexSketchingFAB.startAnimation(fab_close);
            mConcaveSketchingFAB.startAnimation(fab_close);
            mValleySketchingFAB.startAnimation(fab_close);
            mRidgeSketchingFAB.startAnimation(fab_close);
            mEreaseSketchingFAB.startAnimation(fab_close);
            mApplySketchingFAB.startAnimation(fab_close);

            mBoundrySketchingFAB.setClickable(false);
            mFlatSketchingFAB.setClickable(false);
            mFeatureSketchingFAB.setClickable(false);
            mConvexSketchingFAB.setClickable(false);
            mConcaveSketchingFAB.setClickable(false);
            mValleySketchingFAB.setClickable(false);
            mRidgeSketchingFAB.setClickable(false);
            mEreaseSketchingFAB.setClickable(false);
            mApplySketchingFAB.setClickable(false);

            mIsSketchingFABOpen = false;
        } else {
            mSketchingFAB.startAnimation(rotate_forward);
            mBoundrySketchingFAB.startAnimation(fab_open);
            mFlatSketchingFAB.startAnimation(fab_open);
            mFeatureSketchingFAB.startAnimation(fab_open);
            mConvexSketchingFAB.startAnimation(fab_open);
            mConcaveSketchingFAB.startAnimation(fab_open);
            mValleySketchingFAB.startAnimation(fab_open);
            mRidgeSketchingFAB.startAnimation(fab_open);
            mEreaseSketchingFAB.startAnimation(fab_open);
            mApplySketchingFAB.startAnimation(fab_open);

            mBoundrySketchingFAB.setClickable(true);
            mFlatSketchingFAB.setClickable(true);
            mFeatureSketchingFAB.setClickable(true);
            mConvexSketchingFAB.setClickable(true);
            mConcaveSketchingFAB.setClickable(true);
            mValleySketchingFAB.setClickable(true);
            mRidgeSketchingFAB.setClickable(true);
            mEreaseSketchingFAB.setClickable(true);
            mApplySketchingFAB.setClickable(true);

            mIsSketchingFABOpen = true;
        }
    }
}
