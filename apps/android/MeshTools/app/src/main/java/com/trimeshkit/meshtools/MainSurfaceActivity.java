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
import com.trimeshkit.meshprocessing.TriMeshAlgorithms;
import com.trimeshkit.meshprocessing.TriMeshUtils;

public class MainSurfaceActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    // Used to load the 'TriMeshKit' library on application startup.
    static {
        System.loadLibrary("TriMeshKit-JNI");
    }

    private TriMesh mTriMesh;
    private MainGLSurfaceView mGLView;
    private Boolean mIsFABOpen = false;
    private FloatingActionButton mRenderingFAAB, mSolidRenderingFAB, mSolidWireframeRenderingFAB, mWireframeRenderingFAB, mNormalRenderingFAB, mPointRenderingFAB;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    private static final int PICK_MESH_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply screen orientation
        //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

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

//        final FloatingActionButton renderingFAB = (FloatingActionButton) findViewById(R.id.renderingFAB);
//        renderingFAB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mGLView.changeRenderingType();
////                renderingFAB.setImageResource(R.drawable.status_green);
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        /// FAB
        mRenderingFAAB = (FloatingActionButton) findViewById(R.id.renderingFAB);
        mSolidRenderingFAB = (FloatingActionButton) findViewById(R.id.solidRenderingFAB);
        mSolidWireframeRenderingFAB = (FloatingActionButton) findViewById(R.id.solidWireframeRenderingFAB);
        mWireframeRenderingFAB = (FloatingActionButton) findViewById(R.id.wireframeRenderingFAB);
        mNormalRenderingFAB = (FloatingActionButton) findViewById(R.id.normalRenderingFAB);
        mPointRenderingFAB = (FloatingActionButton) findViewById(R.id.pointRenderingFAB);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);

        mRenderingFAAB.setOnClickListener(this);
        mSolidRenderingFAB.setOnClickListener(this);
        mSolidWireframeRenderingFAB.setOnClickListener(this);
        mWireframeRenderingFAB.setOnClickListener(this);
        mNormalRenderingFAB.setOnClickListener(this);
        mPointRenderingFAB.setOnClickListener(this);
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

        } else if (id == R.id.nav_sketch_modeling) {

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
                animateFAB();
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
        }
    }

    public void animateFAB() {
        if (mIsFABOpen) {
            mRenderingFAAB.startAnimation(rotate_backward);
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
            mIsFABOpen = false;
        } else {
            mRenderingFAAB.startAnimation(rotate_forward);
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
            mIsFABOpen = true;
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
            return TriMeshAlgorithms.smoothMesh(activity.mTriMesh);
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
                        activity.mGLView.loadMesh(activity.mTriMesh);
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
}
