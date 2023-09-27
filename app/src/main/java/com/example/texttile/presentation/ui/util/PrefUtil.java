package com.example.texttile.presentation.ui.util;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;

import com.example.texttile.data.model.UserDataModel;
import com.google.gson.Gson;

public class PrefUtil {

    SharedPreferences sharedPreferences;
    Activity activity;
    private UserDataModel userDataModels;

    public PrefUtil(Activity activity) {
        this.sharedPreferences = activity.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        this.activity = activity;
    }

    public UserDataModel getUser() {
        if (!sharedPreferences.getString("isLogin", "null").equals("null")) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("isLogin", "");
            userDataModels = (UserDataModel) gson.fromJson(json, UserDataModel.class);
            return userDataModels;
        } else {
            return null;
        }
    }

    public void setUser(UserDataModel model) {
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(model);
        myEdit.putString("isLogin", json);
        myEdit.commit();
    }

    public void removeUser() {
        userDataModels = null;
        sharedPreferences.edit().remove("isLogin").commit();
    }

    public boolean isUserLogin(){
        if (getUser() == null){
            return false;
        }else {
            return true;
        }
    }
}
