package com.example.texttile.presentation.ui.lib.snackUtil;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.texttile.R;
import com.google.android.material.snackbar.Snackbar;

public class CustomSnackUtil {


    public void showSnack(Activity activity, String msg, int image_id) {
        View view = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        LayoutInflater inflater = activity.getLayoutInflater();

        final Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT);
        View customSnackView = inflater.inflate(R.layout.custom_snackbar_view, null);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setPadding(0, 0, 0, 0);

        TextView txt_title = customSnackView.findViewById(R.id.txt_title);
        ImageView imageView = customSnackView.findViewById(R.id.imageView);

        txt_title.setText(msg);
        imageView.setImageResource(image_id);
        snackbarLayout.addView(customSnackView, 0);
        snackbar.show();
    }

    public void showSnack(Dialog dialog, String msg, int image_id) {
        LayoutInflater inflater = dialog.getLayoutInflater();
        View view = dialog.getWindow().getDecorView().findViewById(android.R.id.content);

        final Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT);
        View customSnackView = inflater.inflate(R.layout.custom_snackbar_view, null);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setPadding(0, 0, 0, 0);

        TextView txt_title = customSnackView.findViewById(R.id.txt_title);
        ImageView imageView = customSnackView.findViewById(R.id.imageView);

        txt_title.setText(msg);
        imageView.setImageResource(image_id);
        snackbarLayout.addView(customSnackView, 0);
        snackbar.show();
    }


}
