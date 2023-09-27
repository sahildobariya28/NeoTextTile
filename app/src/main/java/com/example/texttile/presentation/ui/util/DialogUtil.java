package com.example.texttile.presentation.ui.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.texttile.R;
import com.example.texttile.data.model.XSSFSheetModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;

import java.util.ArrayList;

public class DialogUtil {

    private static Dialog progress_dialog;

    public static void showSuccessDialog(Activity activity, boolean isback) {
        if (activity != null) {
            final Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.success_dialog);
            dialog.setCancelable(false);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            CardView btn_done = dialog.findViewById(R.id.btn_done);

            btn_done.setOnClickListener(v1 -> {
                if (isback) {
                    dialog.dismiss();
                    activity.onBackPressed();
                } else {
                    dialog.dismiss();
                }
            });
            hideProgressDialog();
            dialog.show();
        }
    }

    public static void show_printer_dialog(Context activity, String title, String description, int image_id) {
        if (activity != null) {
            final Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.printer_dialog);
            dialog.setCancelable(true);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            CardView btn_done = dialog.findViewById(R.id.btn_done);
            TextView textTitle = dialog.findViewById(R.id.text_title);
            TextView text_description = dialog.findViewById(R.id.text_description);
            ImageView img_main = dialog.findViewById(R.id.img_main);

            textTitle.setText(title);
            text_description.setText(description);
            img_main.setImageResource(image_id);

            btn_done.setOnClickListener(v1 -> {

                dialog.dismiss();

            });
            hideProgressDialog();
            dialog.show();
        }
    }

    private static Dialog updateDialog;

    public static void showUpdateSuccessDialog(Activity activity, boolean isback) {

        if (activity != null) {
            if (updateDialog == null || !updateDialog.isShowing()) {
                updateDialog = new Dialog(activity);
                updateDialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
                updateDialog.setContentView(R.layout.update_success_dialog);
                updateDialog.setCancelable(true);
                updateDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);
                updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                CardView btn_done = updateDialog.findViewById(R.id.btn_done);

                btn_done.setOnClickListener(v1 -> {
                    if (isback) {
                        updateDialog.dismiss();
                        activity.onBackPressed();
                    } else {
                        updateDialog.dismiss();
                    }
                });
                hideProgressDialog();
                updateDialog.show();
            }
        }
    }

    public static void showErrorDialog(Activity activity) {

        if (activity != null) {
            final Dialog dialog = new Dialog(activity);
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
            dialog.setContentView(R.layout.error_dialog);
            dialog.setCancelable(true);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            CardView btn_try_again = dialog.findViewById(R.id.btn_try_again);

            btn_try_again.setOnClickListener(v1 -> {
                dialog.dismiss();
                activity.onBackPressed();
            });
            hideProgressDialog();
            dialog.show();

        }
    }

    public static void emptyRecordDialog(Activity activity) {
        if (activity != null) {
            final Dialog dialog = new Dialog(activity);
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
            dialog.setContentView(R.layout.dialog_empty_record);
            dialog.setCancelable(true);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            CardView btn_try_again = dialog.findViewById(R.id.btn_try_again);

            btn_try_again.setOnClickListener(v1 -> {
                dialog.dismiss();
            });


            dialog.show();
        }
    }

    public static void showDeleteDialog(Activity activity) {
        if (activity != null) {
            final Dialog dialog = new Dialog(activity);
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
            dialog.setContentView(R.layout.dialog_delete);
            dialog.setCancelable(true);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            CardView btn_done = dialog.findViewById(R.id.btn_done);

            btn_done.setOnClickListener(v1 -> {
                dialog.dismiss();
            });

            hideProgressDialog();
            dialog.show();
        }
    }

    public static void showExitDialog(Activity activity) {
        if (activity != null) {
            final Dialog dialog = new Dialog(activity);
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
            dialog.setContentView(R.layout.exit_screen);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            CardView btn_cancel = dialog.findViewById(R.id.btn_cancel);
            CardView btn_exit = dialog.findViewById(R.id.btn_exit);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                }
            });

            btn_cancel.setOnClickListener(v1 -> {
                dialog.dismiss();
            });
            btn_exit.setOnClickListener(v1 -> {
                activity.finish();
                activity.finishAffinity();
            });

            dialog.show();
        }
    }

    public static void showExcelDataRead(Activity activity, ArrayList<XSSFSheetModel> sheetList, ReadExcelUtils readExcelUtils, int position) {
        if (activity != null) {
            final Dialog dialog = new Dialog(activity);
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
            dialog.setContentView(R.layout.read_excel_data_dialog);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            CardView btn_yes = dialog.findViewById(R.id.btn_yes);
            CardView btn_no = dialog.findViewById(R.id.btn_no);

            btn_yes.setOnClickListener(v1 -> {
                dialog.dismiss();

                switch (sheetList.get(position).getXssfSheet().getSheetName()) {
                    case Const.PARTY_MASTER:
                        readExcelUtils.setPartyMasterData(sheetList.get(position).getXssfSheet());
                        break;
                    case Const.JALA_MASTER:
                        readExcelUtils.setJalaMasterData(sheetList.get(position).getXssfSheet());
                        break;
                    case Const.EMPLOYEE_MASTER:
                        readExcelUtils.setEmployeeMasterData(sheetList.get(position).getXssfSheet());
                        break;
                    case Const.YARN_MASTER:
                        readExcelUtils.setYarnMasterData(sheetList.get(position).getXssfSheet(), sheetList.get(position + 1).getXssfSheet());
                        break;
                    case Const.MACHINE_MASTER:
                        readExcelUtils.setMachineMasterData(sheetList.get(position).getXssfSheet());
                        break;
                    case Const.DESIGN_MASTER:
                        readExcelUtils.setDesignMasterData(activity, sheetList.get(position).getXssfSheet(), sheetList.get(position + 1).getXssfSheet(), sheetList.get(position + 2).getXssfSheet());
                        break;
                    case Const.SHADE_MASTER:
                        readExcelUtils.setShadeMasterData(sheetList.get(position).getXssfSheet());
                        break;
                    default:
//                        Log.d("kdkfsdfsdf", "showExcelDataRead: " + sheetList.get(position).getXssfSheet().getSheetName());
                        new CustomSnackUtil().showSnack(activity, "Invalid Sheet (" + sheetList.get(position).getXssfSheet().getSheetName() + ")", R.drawable.error_msg_icon);
                        break;
                }

            });
            btn_no.setOnClickListener(v1 -> {
                dialog.dismiss();
            });

            dialog.show();
        }
    }

    public static void showAccessDeniedDialog(Activity activity) {
        if (activity != null) {
            final Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.access_denied_dialog);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            CardView btn_okay = dialog.findViewById(R.id.btn_okay);

            btn_okay.setOnClickListener(v1 -> {
                dialog.dismiss();
            });

            dialog.show();
        }
    }


    public static void showDeleteConformDialog(Activity activity) {
        if (activity != null) {
            final Dialog dialog = new Dialog(activity);
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
            dialog.setContentView(R.layout.delete_conform);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            CardView btn_cancel = dialog.findViewById(R.id.btn_cancel);
            CardView btn_exit = dialog.findViewById(R.id.btn_exit);

            btn_cancel.setOnClickListener(v1 -> {
                dialog.dismiss();
            });
            btn_exit.setOnClickListener(v1 -> {
                dialog.dismiss();
            });

            dialog.show();
        }
    }

    public static void showProgressDialog(Activity activity) {
        if (activity != null) {
            progress_dialog = new Dialog(activity);
            progress_dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
            progress_dialog.setContentView(R.layout.dialog_progress);
            progress_dialog.setCancelable(true);
            progress_dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            progress_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            progress_dialog.show();
        }
    }

    public static boolean isProgressDialogShowing() {
        if (progress_dialog != null && progress_dialog.isShowing()) {
            return progress_dialog.isShowing();
        } else {
            return false;
        }
    }

    public static void hideProgressDialog() {

        if (progress_dialog != null && progress_dialog.isShowing()) {
            progress_dialog.dismiss();
        }
    }


}
