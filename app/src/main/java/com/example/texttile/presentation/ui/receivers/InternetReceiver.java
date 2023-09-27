package com.example.texttile.presentation.ui.receivers;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.WindowManager;

import androidx.cardview.widget.CardView;

import com.example.texttile.core.MyApplication;
import com.example.texttile.R;

public class InternetReceiver extends BroadcastReceiver {
    private Dialog dialog;
    public static boolean firstTime = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (isOnline(context)) {
                Log.d("fdsfsdfsd", "onReceive: " + "connected");
            } else {
                Log.d("fdsfsdfsd", "onReceive: " + "disconnected");
                showInternetNotAvailableDialog(((MyApplication) context.getApplicationContext()).getContext());
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void showInternetNotAvailableDialog(Context context) {

        if (firstTime) {
            firstTime = false;
            dialog = new Dialog(context);
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
            dialog.setContentView(R.layout.internet_dialog);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            CardView btn_try_again = dialog.findViewById(R.id.btn_try_again);
            dialog.setOnDismissListener(dialogInterface -> {
                firstTime = true;
            });

            btn_try_again.setOnClickListener(v1 -> {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                //should check null because in airplane mode it will be null
                if (netInfo != null && netInfo.isConnected() && netInfo.isAvailable()) {
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    dialog.show();
                }

            });
            dialog.show();
        }

    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            return (netInfo != null && netInfo.isConnected() && netInfo.isAvailable());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}
