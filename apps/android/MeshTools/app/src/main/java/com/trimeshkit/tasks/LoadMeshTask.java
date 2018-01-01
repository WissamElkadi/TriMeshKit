package com.trimeshkit.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.trimeshkit.meshprocessing.TriMesh;
import com.trimeshkit.meshprocessing.TriMeshUtils;
import com.trimeshkit.meshtools.MainSurfaceActivity;

/**
 * Created by wahmed on 31/12/2017.
 */

public class LoadMeshTask extends AsyncTask<String, Integer, Boolean> {

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
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.mGLView.loadMesh(activity.mTriMesh, true);
                }
            });
        } else {
            activity.runOnUiThread(new Runnable() {
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
