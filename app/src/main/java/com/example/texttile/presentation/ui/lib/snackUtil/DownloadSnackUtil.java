package com.example.texttile.presentation.ui.lib.snackUtil;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.texttile.R;
import com.google.android.material.snackbar.Snackbar;

public class DownloadSnackUtil {
    Snackbar snackbar;
    TextView txt_download, txt_progress;
    ProgressBar progressBar;

    public Snackbar showSnack(Activity activity, String msg){
        View view = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        LayoutInflater inflater= activity.getLayoutInflater();

        snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);
        View customSnackView = inflater.inflate(R.layout.download_snackbar_view, null);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setPadding(0, 0, 0, 0);

        progressBar = customSnackView.findViewById(R.id.progress_bar);
        txt_download = customSnackView.findViewById(R.id.txt_title);
        txt_progress = customSnackView.findViewById(R.id.txt_download_progress);

        txt_download.setText(msg);
        snackbarLayout.addView(customSnackView, 0);
        snackbar.show();

        return snackbar;
    }

    public void updateProgress(double progress){
        Log.d("dfsdfsdf", "onTick: " + progress);
        progressBar.setProgress((int) progress);
        txt_progress.setText(String.valueOf((int)progress));
    }

    public void dismissSnack(){
        snackbar.dismiss();
    }

}
