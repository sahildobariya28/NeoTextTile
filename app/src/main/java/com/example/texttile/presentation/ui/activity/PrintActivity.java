package com.example.texttile.presentation.ui.activity;

import static com.example.texttile.presentation.ui.util.PrinterUtils.isBluetoothPermission;
import static com.example.texttile.presentation.ui.util.PrinterUtils.requestBluetoothPermission;
import static com.example.texttile.presentation.ui.util.Util.generateQrCode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.texttile.core.MyApplication;
import com.example.texttile.R;
import com.example.texttile.data.dao.DAODesignMaster;
import com.example.texttile.data.dao.DAOOrder;
import com.example.texttile.data.dao.DAOShadeMaster;
import com.example.texttile.data.model.AllotProgramModel;
import com.example.texttile.data.model.DesignMasterModel;
import com.example.texttile.data.model.MachineMasterModel;
import com.example.texttile.data.model.OrderMasterModel;
import com.example.texttile.data.model.ShadeMasterModel;
import com.example.texttile.databinding.ActivityPrintBinding;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PrinterUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

public class PrintActivity extends AppCompatActivity {

    ActivityPrintBinding binding;
    OrderMasterModel orderMasterModel;
    AllotProgramModel allotProgramModel;
    MachineMasterModel machineMasterModel;
    Bitmap image;
    ArrayList<OrderMasterModel> orderList = new ArrayList<>();
    List<TextView> txt_F_list = new ArrayList<>();
    String tracker;
    String qr_code_name;
    ArrayList<String> f_WarpColorList = new ArrayList<>();

    DAOShadeMaster daoShadeMaster;

    public PrintActivity(){
        daoShadeMaster = new DAOShadeMaster(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrintBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tracker = getIntent().getStringExtra("tracker");
        initToolbar();

        if (tracker.equals("allot")) {
            if (getIntent().getSerializableExtra("data") != null) {

                binding.viewSleep.setVisibility(View.VISIBLE);
                binding.viewMachineSleep.setVisibility(View.GONE);

                allotProgramModel = (AllotProgramModel) getIntent().getSerializableExtra("data");

                txt_F_list.add(binding.textF1);
                txt_F_list.add(binding.textF2);
                txt_F_list.add(binding.textF3);
                txt_F_list.add(binding.textF4);
                txt_F_list.add(binding.textF5);
                txt_F_list.add(binding.textF6);
                txt_F_list.add(binding.textF7);
                txt_F_list.add(binding.textF8);

                String order_no = allotProgramModel.getOrder_no();
                qr_code_name = "@slip:" + order_no;
                get_order_list(order_no);
                binding.imgBarcode.setImageBitmap(generateQrCode(qr_code_name));
            }
        } else {
            if (getIntent().getSerializableExtra("data") != null) {

                binding.viewSleep.setVisibility(View.GONE);
                binding.viewMachineSleep.setVisibility(View.VISIBLE);

                machineMasterModel = (MachineMasterModel) getIntent().getSerializableExtra("data");

                String machine_name = machineMasterModel.getMachine_name();
                qr_code_name = "@MachineName:" + machine_name;

                if (TextUtils.isEmpty(machineMasterModel.getMachine_name())) {
                    machineMasterModel.setMachine_name("null");
                } else if (TextUtils.isEmpty(machineMasterModel.getWrap_color())) {
                    machineMasterModel.setWrap_color("null");
                } else if (TextUtils.isEmpty(machineMasterModel.getWrap_thread())) {
                    machineMasterModel.setWrap_thread("null");
                } else if (TextUtils.isEmpty(machineMasterModel.getJala_name())) {
                    machineMasterModel.setJala_name("null");
                } else if (TextUtils.isEmpty(machineMasterModel.getReed())) {
                    machineMasterModel.setReed("null");
                } else {
                    binding.textMachineNameM.setText(machineMasterModel.getMachine_name());
                    binding.textWarpColorM.setText(machineMasterModel.getWrap_color());
                    binding.textWarpThread.setText(machineMasterModel.getWrap_thread());
                    binding.textJala.setText(machineMasterModel.getJala_name());
                    binding.textReed.setText(machineMasterModel.getReed());
                    binding.imgMachineBarcode.setImageBitmap(generateQrCode(qr_code_name));
                }
            }
        }
        binding.btnAddItem.setOnClickListener(view -> {
            if (tracker.equals("allot")) {
                image = getBitmapFromView(binding.viewSleep);
            } else {
                image = getBitmapFromView(binding.viewMachineSleep);
            }

            if (image != null) {
                storeImage(image);
            }
        });

        binding.btnPrint.setOnClickListener(view -> {

            if (isBluetoothPermission(this)) {
                setupPrinter();
            } else {
                requestBluetoothPermission(this);
            }

        });

    }

    private void get_order_list(String orderNo) {
        DialogUtil.showProgressDialog(this);

        new DAOOrder(this).getReference().whereEqualTo("sub_order_no", orderNo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            orderList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                OrderMasterModel orderMasterModel1 = document.toObject(OrderMasterModel.class);
                                orderMasterModel = orderMasterModel1;
                                orderList.add(orderMasterModel1);
                                setShadeMasterData(orderMasterModel1);
                                get_design_list(orderMasterModel1.getDesign_no());
                                binding.textDate.setText(orderMasterModel1.getDate());
                                binding.textDesignNo.setText(orderMasterModel1.getDesign_no());
                                binding.textOrderNo.setText(orderMasterModel1.getSub_order_no());
                                binding.textPartyName.setText(orderMasterModel1.getParty_name());
                                binding.textShadeNo.setText(orderMasterModel1.getShade_no());
                                binding.textQty.setText(String.valueOf(orderMasterModel1.getQuantity()));
                                binding.textMachineName.setText(allotProgramModel.getMachine_name());
                            }
                            DialogUtil.hideProgressDialog();
                        } else {
                            DialogUtil.hideProgressDialog();
                            DialogUtil.emptyRecordDialog(PrintActivity.this);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        DialogUtil.showErrorDialog(PrintActivity.this);
                    }
                });

//        new DAOOrder(this).getReference().orderByChild("sub_order_no").equalTo(orderNo).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@Nonnull DataSnapshot dataSnapshot) {
//
//                if (dataSnapshot.exists()) {
//                    orderList.clear();
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                        OrderMasterModel orderMasterModel1 = postSnapshot.getValue(OrderMasterModel.class);
//                        orderMasterModel = orderMasterModel1;
//                        orderList.add(orderMasterModel1);
//                        setShadeMasterData(orderMasterModel1);
//                        get_design_list(orderMasterModel1.getDesign_no());
//                        binding.textDate.setText(orderMasterModel1.getDate());
//                        binding.textDesignNo.setText(orderMasterModel1.getDesign_no());
//                        binding.textOrderNo.setText(orderMasterModel1.getSub_order_no());
//                        binding.textPartyName.setText(orderMasterModel1.getParty_name());
//                        binding.textShadeNo.setText(orderMasterModel1.getShade_no());
//                        binding.textQty.setText(String.valueOf(orderMasterModel1.getQuantity()));
//                        binding.textMachineName.setText(allotProgramModel.getMachine_name());
//                    }
//                    DialogUtil.hideProgressDialog();
//                } else {
//                    DialogUtil.hideProgressDialog();
//                    DialogUtil.emptyRecordDialog(PrintActivity.this);
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@Nonnull DatabaseError databaseError) {
//                DialogUtil.showErrorDialog(PrintActivity.this);
//            }
//        });
    }

    private void get_design_list(String design_no) {

        new DAODesignMaster(this).getReference()
                .whereEqualTo("design_no", design_no)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if (!querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                DesignMasterModel designMasterModel = documentSnapshot.toObject(DesignMasterModel.class);
                                binding.textBasePick.setText("" + designMasterModel.getBase_pick());
                                binding.textQty.setText(binding.textQty.getText().toString() + "/" + designMasterModel.getType());
                                binding.textMachineName.setText(allotProgramModel.getMachine_name());
                            }
                        } else {
                            new CustomSnackUtil().showSnack(PrintActivity.this, "Design no is not match", R.drawable.error_msg_icon);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@Nonnull Exception e) {
                        DialogUtil.showErrorDialog(PrintActivity.this);
                    }
                });

//        new DAODesignMaster(this).getReference().orderByChild("design_no").equalTo(design_no).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@Nonnull DataSnapshot dataSnapshot) {
//
//                if(dataSnapshot.exists()){
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                        DesignMasterModel designMasterModel = postSnapshot.getValue(DesignMasterModel.class);
//
//                        binding.textBasePick.setText("" + designMasterModel.getBase_pick());
//                        binding.textQty.setText(binding.textQty.getText().toString() + "/" + designMasterModel.getType());
//                        binding.textMachineName.setText(allotProgramModel.getMachine_name());
//                    }
//                }else {
//                    new CustomSnackUtil().showSnack(PrintActivity.this, "Design no is not match", R.drawable.error_msg_icon);
//                }
//            }
//
//            @Override
//            public void onCancelled(@Nonnull DatabaseError databaseError) {
//                DialogUtil.showErrorDialog(PrintActivity.this);
//            }
//        });
    }


    public void setShadeMasterData(OrderMasterModel orderMasterModel) {

        daoShadeMaster.getReference().addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                // Handle error
                return;
            }
            if (snapshot != null && !snapshot.isEmpty()) {
                for (QueryDocumentSnapshot documentSnapshot : snapshot) {
                    ShadeMasterModel shadeMasterModel = documentSnapshot.toObject(ShadeMasterModel.class);
                    if (shadeMasterModel != null) {
                        if (shadeMasterModel.getShade_no().equals(orderMasterModel.getShade_no())) {
                            binding.textWarpColor.setText(shadeMasterModel.getWrap_color());

                            f_WarpColorList.addAll(shadeMasterModel.getF_list());
                            for (int i = 0; i < shadeMasterModel.getF_list().size(); i++) {
                                txt_F_list.get(i).setText(shadeMasterModel.getF_list().get(i));
                            }
                            break;
                        }
                    }
                }
                // Handle shadeMasterList
            }
        });
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.parseColor("#00000000"));
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (IOException ignored) {
        }
    }

    private File getOutputMediaFile() {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Download");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "MI_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public void initToolbar() {
        if (tracker.equals("allot")) {
            binding.textTitle.setText("Program Allotment Sleep");
        } else {
            binding.textTitle.setText("Machine Sleep");
        }
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    /*==============================================================================================
    ======================================BLUETOOTH PART============================================
    ==============================================================================================*/

    public void setupPrinter() {
        StringBuilder stringBuilder = new StringBuilder();
        if (tracker.equals("allot")) {
            String machine_name = binding.textMachineName.getText().toString().trim();
            String warp_color = binding.textWarpColor.getText().toString().trim();
            String designNO = binding.textDesignNo.getText().toString().trim();
            String shadeNO = binding.textShadeNo.getText().toString().trim();
            String qty = binding.textQty.getText().toString().trim();
            String base_pick = binding.textBasePick.getText().toString().trim();
            String order_no = binding.textOrderNo.getText().toString().trim();
            String party_name = binding.textPartyName.getText().toString().trim();


            stringBuilder.append("[C]<b><font size='big-2'>" + machine_name + "</font></b>\n");
            stringBuilder.append("[C]<font size='normal'>WARP: " + warp_color + "</font>\n");
            stringBuilder.append("[C]<font size='normal'>DATE: " + new SimpleDateFormat("dd/MM/yy ss:mm:hh").format(new Date()) + "</font>\n");
            stringBuilder.append("[C]<font size='big-2'>DESIGN NO: " + designNO + "</font>\n\n");
            stringBuilder.append("[C]------------------------------------------------\n\n");
            stringBuilder.append("[L]<font size='normal'>BASE PICK: " + "<u><b>" + base_pick + "</b></u></font>\n");
            stringBuilder.append("[L]<font size='normal'>SHADE NO: " + "<u><b>" + shadeNO + "</b></u></font>\n");


            for (int i = 0; i < f_WarpColorList.size(); i++) {
                stringBuilder.append("[L]<font size='big-2'>F" + (i+1) + ": " + f_WarpColorList.get(i) + "</font>\n");
            }


            stringBuilder.append("[C]<font size='big-2'>QTY: " + "<u><b>" + qty + "</b></u></font>\n");
            stringBuilder.append("[L]<font size='normal'>ORDER NO: " + order_no + "</font>\n");
            stringBuilder.append("[L]<font size='normal'>PARTY NAME: " + party_name + "</font>\n\n");

        } else {
            String machine_name = binding.textMachineNameM.getText().toString().trim();
            String warp_color = binding.textWarpColorM.getText().toString().trim();
            String warp_thread = binding.textWarpThread.getText().toString().trim();
            String jala = binding.textJala.getText().toString().trim();
            String reed = binding.textReed.getText().toString().trim();
            if (TextUtils.isEmpty(machine_name)) {
                machine_name = "null";
            } else if (TextUtils.isEmpty(warp_color)) {
                warp_color = "null";
            } else if (TextUtils.isEmpty(warp_thread)) {
                warp_thread = "null";
            } else if (TextUtils.isEmpty(jala)) {
                jala = "null";
            } else if (TextUtils.isEmpty(reed)) {
                reed = "null";
            } else {
                stringBuilder.append("[C]<b><font size='big'>" + machine_name + "</font></b>\n");
                stringBuilder.append("[C]------------------------------------------------\n\n");
                stringBuilder.append("[L]Warp Color: ");
                stringBuilder.append("[R]" + warp_color + "\n");
                stringBuilder.append("[L]Warp Thread: ");
                stringBuilder.append("[R]" + warp_thread + "\n");
                stringBuilder.append("[L]Jala: ");
                stringBuilder.append("[R]<font size='medium'><b>" + jala + "</b></font>\n");
                stringBuilder.append("[L]Reed: ");
                stringBuilder.append("[R]" + reed + "\n");
            }
        }
        stringBuilder.append("[C]------------------------------------------------\n\n");
        stringBuilder.append("[C]<qrcode size='20'>" + qr_code_name + "</qrcode>\n");
        stringBuilder.append("[C]\n");
        stringBuilder.append("[C]\n");
        stringBuilder.append("[C]\n");
        stringBuilder.append("[C]\n");
        stringBuilder.append("[C]\n");
        stringBuilder.append("[C]\n");
        PrinterUtils.printBluetooth(this, stringBuilder.toString());

    }

    @Override
    protected void onResume() {
        ((MyApplication) getApplicationContext()).setActivity(this, this);
        super.onResume();
    }
}