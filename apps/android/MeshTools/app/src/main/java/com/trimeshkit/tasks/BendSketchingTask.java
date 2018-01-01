package com.trimeshkit.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.trimeshkit.meshprocessing.TriMesh;
import com.trimeshkit.meshprocessing.TriMeshAlgorithms;
import com.trimeshkit.meshtools.MainSurfaceActivity;
import com.trimeshkit.state.ApplicationState;

import java.util.ArrayList;

/**
 * Created by wahmed on 31/12/2017.
 */

public class BendSketchingTask extends AsyncTask<ArrayList<ArrayList<Float>>, Integer, Boolean> {

    private MainSurfaceActivity activity;
    private ProgressDialog progressBar;

    public BendSketchingTask(MainSurfaceActivity context) {

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

    protected Boolean doInBackground(ArrayList<ArrayList<Float>>... parms) {
        activity.mTriMesh = new TriMesh();

        // Boundray
        double[][] boundrayDoubleArrayList = new double[parms[0].size()][];
        for(int j = 0; j < parms[0].size(); j++) {
            boundrayDoubleArrayList[j] = new double[parms[0].get(j).size()];
            int i = 0;

            for (Float f : parms[0].get(j)) {
                boundrayDoubleArrayList[j][i++] = (f != null ? f : Float.NaN);
            }
        }

        // Convex
        double[][] convexDoubleArrayList = new double[parms[1].size()][];
        for(int j = 0; j < parms[1].size(); j++) {
            convexDoubleArrayList[j] = new double[parms[1].get(j).size()];
            int i = 0;

            for (Float f : parms[1].get(j)) {
                convexDoubleArrayList[j][i++] = (f != null ? f : Float.NaN);
            }
        }

        // Concave
        double[][] concaveDoubleArrayList = new double[parms[2].size()][];
        for(int j = 0; j < parms[2].size(); j++) {
            concaveDoubleArrayList[j] = new double[parms[2].get(j).size()];
            int i = 0;

            for (Float f : parms[2].get(j)) {
                concaveDoubleArrayList[j][i++] = (f != null ? f : Float.NaN);
            }
        }

        // Ridge
        double[][] ridgeDoubleArrayList = new double[parms[3].size()][];
        for(int j = 0; j < parms[3].size(); j++) {
            ridgeDoubleArrayList[j] = new double[parms[3].get(j).size()];
            int i = 0;

            for (Float f : parms[3].get(j)) {
                ridgeDoubleArrayList[j][i++] = (f != null ? f : Float.NaN);
            }
        }

        // Valley
        double[][] valleyDoubleArrayList = new double[parms[4].size()][];
        for(int j = 0; j < parms[4].size(); j++) {
            valleyDoubleArrayList[j] = new double[parms[4].get(j).size()];
            int i = 0;

            for (Float f : parms[4].get(j)) {
                valleyDoubleArrayList[j][i++] = (f != null ? f : Float.NaN);
            }
        }

        TriMeshAlgorithms.bendSketch(activity.mTriMesh, boundrayDoubleArrayList,
                convexDoubleArrayList, concaveDoubleArrayList,
                ridgeDoubleArrayList, valleyDoubleArrayList);

        ApplicationState.setApplicationState(ApplicationState.ApplicationStateEnum.GENERAL);

        activity.runOnUiThread(new Runnable() {
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

