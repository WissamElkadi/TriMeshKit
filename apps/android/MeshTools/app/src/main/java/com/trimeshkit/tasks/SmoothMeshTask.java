package com.trimeshkit.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.trimeshkit.meshprocessing.TriMeshAlgorithms;
import com.trimeshkit.meshtools.MainSurfaceActivity;

/**
 * Created by wahmed on 31/12/2017.
 */

public class SmoothMeshTask extends AsyncTask<Void, Integer, Boolean> {

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
