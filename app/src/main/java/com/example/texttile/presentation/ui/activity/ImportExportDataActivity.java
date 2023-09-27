package com.example.texttile.presentation.ui.activity;

import static com.example.texttile.presentation.ui.util.FileUtils.getPath;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.core.MyApplication;
import com.example.texttile.R;
import com.example.texttile.databinding.ActivityImportExportDataBinding;
import com.example.texttile.databinding.SheetitemBinding;
import com.example.texttile.presentation.ui.interfaces.AddOnReadySheet;
import com.example.texttile.data.model.XSSFSheetModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.lib.snackUtil.DownloadSnackUtil;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.NewWriteExcelUtil;
import com.example.texttile.presentation.ui.util.ReadExcelUtils;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;

public class ImportExportDataActivity extends AppCompatActivity implements AddOnReadySheet {

    ArrayList<XSSFSheetModel> sheetList = new ArrayList<>();
    String tracker;
    ReadExcelUtils readExcelUtils;
    NewWriteExcelUtil writeExcelUtil;
    private static final int PICK_FOLDER_REQUEST_CODE = 100;
    private static final int PICK_EXCEL_FILE_REQUEST_CODE = 101;
    SheetAdapter sheetAdapter;


    ActivityImportExportDataBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImportExportDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getStringExtra("tracker") != null) {
            tracker = getIntent().getStringExtra("tracker");
        }

        initToolbar();

        readExcelUtils = new ReadExcelUtils(this, this);


        if (tracker.equals("import")) {
            binding.btnAdd.setVisibility(View.GONE);
//            this.position = position;

//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("*/*");
//            intent.putExtra(DocumentsContract.EXTRA_EXCLUDE_SELF, true);
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
//
//            String[] mimetypes = {"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.ms-excel"};
//            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//
//            startActivityForResult(intent, PICK_EXCEL_FILE_REQUEST_CODE);

//            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.setType("*/*");
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.getExternalStorageDirectory().toString());
//            startActivityForResult(intent, REQUEST_PICK_FILE);
//
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.getExternalStorageDirectory().toString());
            Intent i = Intent.createChooser(intent, "File");
            startActivityForResult(i, PICK_EXCEL_FILE_REQUEST_CODE);
        } else if (tracker.equals("export")) {
            binding.btnAdd.setVisibility(View.VISIBLE);
            writeExcelUtil = new NewWriteExcelUtil(this, this);
            writeExcelUtil.setUpExport();
        }


        binding.btnAdd.setOnClickListener(view -> {
            boolean isAllTrue = true;
            for (int i = 0; i < sheetList.size(); i++) {

                if (sheetList.get(i).isSelected() == false) {
                    isAllTrue = false;
                    break;
                }
            }
            if (isAllTrue) {
                if (tracker.equals("import")) {
                    final Dialog dialog = new Dialog(this);
                    dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
                    dialog.setContentView(R.layout.read_excel_data_dialog);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    CardView btn_yes = dialog.findViewById(R.id.btn_yes);
                    CardView btn_no = dialog.findViewById(R.id.btn_no);
                    TextView text_title = dialog.findViewById(R.id.text_title);
                    TextView text_description = dialog.findViewById(R.id.text_description);
                    TextView text_no = dialog.findViewById(R.id.text_no);
                    TextView text_yes = dialog.findViewById(R.id.text_yes);
                    LinearLayout view_file_name = dialog.findViewById(R.id.view_file_name);

                    text_title.setText("Import Firebase ?");
                    text_description.setVisibility(View.VISIBLE);
                    text_description.setText("Are you sure!\nYou want to import the data\nfrom excel to firebase ?");
                    view_file_name.setVisibility(View.GONE);
                    text_yes.setText("Import");
                    text_no.setText("Cancel");


                    btn_yes.setOnClickListener(v1 -> {
                        for (int i = 0; i < sheetList.size(); i++) {
                            Log.d("kdkfsdfsdf", "showExcelDataRead: " + sheetList.get(i).getXssfSheet().getSheetName());
                            if (sheetList.get(i).isSelected()) {


                                switch (sheetList.get(i).getXssfSheet().getSheetName()) {
                                    case Const.PARTY_MASTER:
                                        readExcelUtils.setPartyMasterData(sheetList.get(i).getXssfSheet());
                                        break;
                                    case Const.JALA_MASTER:
                                        readExcelUtils.setJalaMasterData(sheetList.get(i).getXssfSheet());
                                        break;
                                    case Const.EMPLOYEE_MASTER:
                                        readExcelUtils.setEmployeeMasterData(sheetList.get(i).getXssfSheet());
                                        break;
                                    case Const.YARN_MASTER:
                                        readExcelUtils.setYarnMasterData(sheetList.get(i).getXssfSheet(), sheetList.get(i + 1).getXssfSheet());
                                        break;
                                    case Const.MACHINE_MASTER:
                                        readExcelUtils.setMachineMasterData(sheetList.get(i).getXssfSheet());
                                        break;
                                    case Const.DESIGN_MASTER:
                                        readExcelUtils.setDesignMasterData(this, sheetList.get(i).getXssfSheet(), sheetList.get(i + 1).getXssfSheet(), sheetList.get(i + 2).getXssfSheet());
                                        break;
                                    case Const.SHADE_MASTER:
                                        readExcelUtils.setShadeMasterData(sheetList.get(i).getXssfSheet());
                                        break;
                                    default:

                                        new CustomSnackUtil().showSnack(ImportExportDataActivity.this, "Invalid Sheet (" + sheetList.get(i).getXssfSheet().getSheetName() + ")", R.drawable.error_msg_icon);
                                        break;
                                }
                            }
                        }
                        dialog.dismiss();
                    });
                    btn_no.setOnClickListener(v1 -> {
                        dialog.dismiss();
                    });

                    dialog.show();


                } else if (tracker.equals("export")) {

                    final Dialog dialog = new Dialog(this);
                    dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
                    dialog.setContentView(R.layout.read_excel_data_dialog);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    CardView btn_yes = dialog.findViewById(R.id.btn_yes);
                    CardView btn_no = dialog.findViewById(R.id.btn_no);
                    TextView text_title = dialog.findViewById(R.id.text_title);
                    TextView text_description = dialog.findViewById(R.id.text_description);
                    TextView text_no = dialog.findViewById(R.id.text_no);
                    TextView text_yes = dialog.findViewById(R.id.text_yes);
                    LinearLayout view_file_name = dialog.findViewById(R.id.view_file_name);
                    EditText edit_file_name = dialog.findViewById(R.id.edit_file_name);

                    text_title.setText("Export Firebase ?");
                    text_description.setVisibility(View.GONE);
                    view_file_name.setVisibility(View.VISIBLE);
                    text_yes.setText("Export");
                    text_no.setText("Cancel");

                    btn_yes.setOnClickListener(v1 -> {
                        DownloadSnackUtil downloadSnackUtil = new DownloadSnackUtil();

                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        executorService.execute(() -> {

                            runOnUiThread(() -> {
                                downloadSnackUtil.showSnack(ImportExportDataActivity.this, "Please Wait...");
                                downloadSnackUtil.updateProgress(0);
                            });

                            writeExcelUtil.storeExcelInStorage(ImportExportDataActivity.this, edit_file_name.getText().toString().trim() + ".xlsx");

                            runOnUiThread(() -> {
                                dialog.dismiss();
                                startCountdown(downloadSnackUtil, executorService);
//                            showDownloadProgress(0, downloadSnackUtil, executorService);
                            });
                        });


                    });
                    btn_no.setOnClickListener(v1 -> {
                        dialog.dismiss();
                    });

                    dialog.show();

                }
            } else {
                new CustomSnackUtil().showSnack(ImportExportDataActivity.this, "Please Wait", R.drawable.icon_warning);
            }
        });

    }

    private void startCountdown(DownloadSnackUtil downloadSnackUtil, ExecutorService executorService) {
        int msecondsToRun = 2000;

        new CountDownTimer(2000, 10) {
            // The millisUntilFinished thing is for a "pausing" function I implemented.
            public void onTick(long millisUntilFinished) {

                long timerProgress;

                timerProgress = msecondsToRun - millisUntilFinished;
                Log.d("dfsdfsdf", "onTick: " + millisUntilFinished + "    " + timerProgress + "    " + ((timerProgress / 10) / 2));
                downloadSnackUtil.updateProgress(((timerProgress / 10) / 2));
            }

            public void onFinish() {
                downloadSnackUtil.updateProgress(100);
                downloadSnackUtil.dismissSnack();
                new CustomSnackUtil().showSnack(ImportExportDataActivity.this, "Success", R.drawable.img_true);
                executorService.shutdown();
            }
        }.start();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FOLDER_REQUEST_CODE) {
            if (data != null) {
                if (data.getData() != null) {
                    writeExcelUtil.storeExcelInStorage(ImportExportDataActivity.this, "MyData.xls");
                }
            }else {
                onBackPressed();
            }
            return;
        } else if (requestCode == PICK_EXCEL_FILE_REQUEST_CODE) {
            if (data != null) {
                if (data.getData() != null) {
                    DownloadSnackUtil downloadSnackUtil = new DownloadSnackUtil();

                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.execute(() -> {

                        runOnUiThread(() -> {
                            downloadSnackUtil.showSnack(ImportExportDataActivity.this, "Please Wait...");
                            downloadSnackUtil.updateProgress(0);
                        });

                        Log.d("dfsdfsdfwer23", "onActivityResult: " + data.getData().toString());
                        if (getPath(ImportExportDataActivity.this, data.getData()) != null) {
                            sheetList = readExcelUtils.getSheet(getPath(ImportExportDataActivity.this, data.getData()));
                        }


                        runOnUiThread(() -> {
                            downloadSnackUtil.updateProgress(100);
                            downloadSnackUtil.dismissSnack();
                            if (sheetList != null && sheetList.size() != 0) {
                                sheetAdapter = new SheetAdapter();
                                binding.recyclerView.setLayoutManager(new LinearLayoutManager(ImportExportDataActivity.this));
                                binding.recyclerView.setAdapter(sheetAdapter);
                            }
                        });
                    });
                    executorService.shutdown();
                }
            }else {
                onBackPressed();
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onReadySheet(XSSFSheet sheet, boolean ischecked) {


        if (sheetList.size() == 0) {
            Log.d("fsdfsdfsdf", "onReadySheet: size 0 :" + sheet.getSheetName());
            sheetList.add(new XSSFSheetModel(sheet, false));
            setAdapter(0, true);
        } else {
            boolean ismatch = false;
            for (int i = 0; i < sheetList.size(); i++) {


                if (sheet.getSheetName().equals(sheetList.get(i).getXssfSheet().getSheetName())) {
                    Log.d("fsdfsdfsdf", "onReadySheet: ismatch :" + sheet.getSheetName());
                    sheetList.get(i).setSelected(ischecked);
                    setAdapter(i, false);
                    ismatch = true;
                    break;

                } else {
                    if (i == (sheetList.size() - 1)) {

                        if (!ismatch) {
                            Log.d("fsdfsdfsdf", "onReadySheet: last_pos ismatch false :" + sheet.getSheetName());
                            sheetList.add(new XSSFSheetModel(sheet, false));
                            sheetAdapter.notifyDataSetChanged();
                            setAdapter(i, true);
                        } else if (ismatch) {
                            Log.d("fsdfsdfsdf", "onReadySheet: last_pos ismatch true :" + sheet.getSheetName());
                            sheetList.get(i).setSelected(ischecked);
                            setAdapter(i, false);
                        }
                    }
                }
            }
        }

    }

    public void setAdapter(int position, boolean is_add) {


        if (sheetAdapter != null) {
            if (is_add) {
                sheetAdapter.notifyDataSetChanged();
            } else {
                sheetAdapter.notifyItemChanged(position);
            }
        } else {
            sheetAdapter = new SheetAdapter();
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerView.setAdapter(sheetAdapter);
        }
    }

    private class SheetAdapter extends RecyclerView.Adapter<SheetAdapter.ViewHolder> {

        @Nonnull
        @Override
        public SheetAdapter.ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
            SheetitemBinding binding = SheetitemBinding.inflate(LayoutInflater.from(ImportExportDataActivity.this), parent, false);
            return new SheetAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@Nonnull SheetAdapter.ViewHolder holder, int position) {
            XSSFSheetModel xssfSheetModel = sheetList.get(position);

            if (xssfSheetModel.isSelected()) {
                holder.binding.imgTrue.setVisibility(View.VISIBLE);
                holder.binding.progress.setVisibility(View.GONE);
            } else {
                holder.binding.imgTrue.setVisibility(View.GONE);
                holder.binding.progress.setVisibility(View.VISIBLE);
            }

            if (xssfSheetModel.getXssfSheet().getSheetName().equals("yarnCompanyList") || xssfSheetModel.getXssfSheet().getSheetName().equals("f_list") || xssfSheetModel.getXssfSheet().getSheetName().equals("jalaWithFileModelList") || xssfSheetModel.getXssfSheet().getSheetName().equals("shade_f_list") || xssfSheetModel.getXssfSheet().getSheetName().equals("Sheet14")) {
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }
            switch (xssfSheetModel.getXssfSheet().getSheetName()) {
                case Const.PARTY_MASTER:
                    holder.binding.textSheetName.setText("Party Master");
                    break;
                case Const.JALA_MASTER:
                    holder.binding.textSheetName.setText("Jala Master");
                    break;
                case Const.EMPLOYEE_MASTER:
                    holder.binding.textSheetName.setText("Employee Master");
                    break;
                case Const.YARN_MASTER:
                    holder.binding.textSheetName.setText("Yarn Master");
                    break;
                case Const.MACHINE_MASTER:
                    holder.binding.textSheetName.setText("Machine Master");
                    break;
                case Const.DESIGN_MASTER:
                    holder.binding.textSheetName.setText("Design Master");
                    break;
                case Const.SHADE_MASTER:
                    holder.binding.textSheetName.setText("Shade Master");
                    break;
                default:
                    holder.binding.textSheetName.setText("Null");
                    break;

            }

            holder.itemView.setOnClickListener(view -> {
                final Dialog dialog = new Dialog(ImportExportDataActivity.this);
                dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
                dialog.setContentView(R.layout.read_excel_data_dialog);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                CardView btn_yes = dialog.findViewById(R.id.btn_yes);
                CardView btn_no = dialog.findViewById(R.id.btn_no);
                TextView text_title = dialog.findViewById(R.id.text_title);
                TextView text_description = dialog.findViewById(R.id.text_description);
                TextView text_no = dialog.findViewById(R.id.text_no);
                TextView text_yes = dialog.findViewById(R.id.text_yes);
                LinearLayout view_file_name = dialog.findViewById(R.id.view_file_name);

                text_title.setText("Import Firebase ?");
                text_description.setVisibility(View.VISIBLE);
                text_description.setText("Are you sure!\nYou want to import the data\nfrom excel to firebase ?");
                view_file_name.setVisibility(View.GONE);
                text_yes.setText("Import");
                text_no.setText("Cancel");


                btn_yes.setOnClickListener(v1 -> {

                    if (sheetList.get(position).isSelected()) {

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
                                readExcelUtils.setDesignMasterData(ImportExportDataActivity.this, sheetList.get(position).getXssfSheet(), sheetList.get(position + 1).getXssfSheet(), sheetList.get(position + 2).getXssfSheet());
                                break;
                            case Const.SHADE_MASTER:
                                readExcelUtils.setShadeMasterData(sheetList.get(position).getXssfSheet());
                                break;
                            default:
                                new CustomSnackUtil().showSnack(ImportExportDataActivity.this, "Invalid Sheet (" + sheetList.get(position).getXssfSheet().getSheetName() + ")", R.drawable.error_msg_icon);
                                break;
                        }
                    }
                    dialog.dismiss();
                });
                btn_no.setOnClickListener(v1 -> {
                    dialog.dismiss();
                });

                dialog.show();
            });


        }

        @Override
        public int getItemCount() {
            return sheetList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            SheetitemBinding binding;

            public ViewHolder(@Nonnull SheetitemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }

    }

    public void initToolbar() {
        if (tracker.equals("import")) {
            binding.textTitle.setText("Import");
        } else {
            binding.textTitle.setText("Export");
        }
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    @Override
    protected void onResume() {
        ((MyApplication) getApplicationContext()).setActivity(this, this);
        super.onResume();
    }
}