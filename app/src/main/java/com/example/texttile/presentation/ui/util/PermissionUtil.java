package com.example.texttile.presentation.ui.util;

import static com.example.texttile.core.MyApplication.getUSERMODEL;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.texttile.data.model.PermissionCategory;

import java.util.ArrayList;

public class PermissionUtil {


    public static boolean isReadPermissionGranted(int id) {

        if (getUSERMODEL() != null) {
            ArrayList<PermissionCategory> permissionList = getUSERMODEL().getPermissionList();

            for (int i = 0; i < permissionList.size(); i++) {
                if (permissionList.get(i).getId() == id) {
                    if (getUSERMODEL().getType().equals(Const.ADMIN)) {
                        return true;
                    } else {
                        return permissionList.get(i).isRead();
                    }
                }
            }
        }
        return false;
    }

    public static boolean isInsertPermissionGranted(int id) {


        if (getUSERMODEL().getPermissionList() != null) {
            ArrayList<PermissionCategory> permissionList = getUSERMODEL().getPermissionList();

            for (int i = 0; i < permissionList.size(); i++) {
                if (permissionList.get(i).getId() == id) {
                    if (getUSERMODEL().getType().equals(Const.ADMIN)) {
                        return true;
                    } else {
                        return permissionList.get(i).isInsert();
                    }
                }
            }
        }
        return false;
    }

    public static boolean isUpdatePermissionGranted(int id) {

        ArrayList<PermissionCategory> permissionList = getUSERMODEL().getPermissionList();

        for (int i = 0; i < permissionList.size(); i++) {
            if (permissionList.get(i).getId() == id) {
                if (getUSERMODEL().getType().equals(Const.ADMIN)) {
                    return true;
                } else {
                    return permissionList.get(i).isUpdate();
                }
            }
        }
        return false;

    }

    public static boolean isDeletePermissionGranted(int id) {
        ArrayList<PermissionCategory> permissionList = getUSERMODEL().getPermissionList();

        for (int i = 0; i < permissionList.size(); i++) {
            if (permissionList.get(i).getId() == id) {
                if (getUSERMODEL().getType().equals(Const.ADMIN)) {
                    return true;
                } else {
                    return permissionList.get(i).isDelete();
                }
            }
        }
        return false;
    }

    public static boolean isAdmin() {

        return getUSERMODEL().getType().equals(Const.ADMIN);
    }

    public static boolean isPricePermissionGranted(int id) {
        ArrayList<PermissionCategory> permissionList = getUSERMODEL().getPermissionList();

        for (int i = 0; i < permissionList.size(); i++) {
            if (permissionList.get(i).getId() == id) {
                if (getUSERMODEL().getType().equals(Const.ADMIN)) {
                    return true;
                } else {
                    return permissionList.get(i).isPrice();
                }
            }
        }
        return false;
    }

//    public static void isInsertPermissionGranted(int id, View btn_add_item, Activity activity) {
//        USER_DATA.observe((LifecycleOwner) activity, userDataModel -> {
//
//            if (userDataModel != null) {
//                ArrayList<PermissionCategory> permissionList = userDataModel.getPermissionList();
//
//                for (int i = 0; i < permissionList.size(); i++) {
//                    if (permissionList.get(i).getId() == id) {
//                        if (userDataModel.getType().equals(Const.ADMIN)) {
//                            btn_add_item.setVisibility(View.VISIBLE);
//                        } else {
//                            if (permissionList.get(i).isInsert()) {
//                                btn_add_item.setVisibility(View.VISIBLE);
//                                break;
//                            } else {
//                                btn_add_item.setVisibility(View.GONE);
//                            }
//                        }
//                    }
//                }
//            }
//        });
//    }


    public static boolean isPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int readExtStorage = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return readExtStorage == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static void takePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivityForResult(intent, 101);
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivityForResult(intent, 101);
            }
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        }
    }
}


