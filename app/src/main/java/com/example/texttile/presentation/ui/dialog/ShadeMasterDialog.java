package com.example.texttile.presentation.ui.dialog;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isInsertPermissionGranted;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.FItemBinding;
import com.example.texttile.databinding.ShadeMasterDialogBinding;
import com.example.texttile.data.dao.DAODesignMaster;
import com.example.texttile.data.dao.DAOShadeMaster;
import com.example.texttile.data.dao.DaoYarnMaster;
import com.example.texttile.presentation.ui.interfaces.AddOnSearchedItemSelectListener;
import com.example.texttile.presentation.screen.master.design_master.dialog.DesignMasterDialogFragment;
import com.example.texttile.data.model.DesignMasterModel;
import com.example.texttile.data.model.ShadeMasterModel;
import com.example.texttile.data.model.YarnMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.KeyboardUtils;
import com.example.texttile.presentation.ui.util.ListShowDialog;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ShadeMasterDialog extends DialogFragment implements AddOnSearchedItemSelectListener {

    Dialog dialog;
    ShadeMasterDialogBinding binding;

    ArrayList<String> design_no_list = new ArrayList<>();
    ArrayList<DesignMasterModel> designMasterModelsList = new ArrayList<>();
    ArrayList<String> warpColorList = new ArrayList<>();
    ArrayList<String> weftColorList = new ArrayList<>();
    ArrayList<String> list = new ArrayList<>();
    ShadeMasterModel shadeMasterModel;
    FAdapter fAdapter;
    int adapterSize = 0;
    DAOShadeMaster dao;

    ListShowDialog designListDialog, warpListDialog, f1_dialog, f2_dialog, f3_dialog, f4_dialog, f5_dialog, f6_dialog, f7_dialog, f8_dialog;
    final int DESIGN_REQUEST = 1, WARP_REQUEST = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        fAdapter = new FAdapter();
        super.onCreate(savedInstanceState);
        dao = new DAOShadeMaster(getActivity());
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = new Dialog(getActivity());
        binding = ShadeMasterDialogBinding.inflate(getLayoutInflater());
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (getArguments().getString("shade_no") != null) {
            String shade_no = getArguments().getString("shade_no");
            binding.textShadeNo.setText(shade_no);
        }

        KeyboardUtils.addKeyboardToggleListener(getActivity(), new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                if (isVisible) {
                    binding.guideline3.setGuidelinePercent(0.10f);
                    binding.guideline4.setGuidelinePercent(0.90f);
                } else {
                    binding.guideline3.setGuidelinePercent(0.25f);
                    binding.guideline4.setGuidelinePercent(0.75f);
                }
            }
        });

        binding.recyclerView.setAdapter(fAdapter);

        setAutoAdapter();

        shadeMasterModel = new ShadeMasterModel();

        if (shadeMasterModel != null) {
            for (int j = 0; j < designMasterModelsList.size(); j++) {
                if (designMasterModelsList.get(j).getDesign_no().equals(binding.textDesignNo.getText().toString().trim())) {
                    adapterSize = designMasterModelsList.get(j).getF_list().size();
                    list.clear();
                    for (int i = 0; i < adapterSize; i++) {
                        list.add(null);
                        fAdapter.notifyDataSetChanged();
                    }
                }
            }
            binding.textDesignNo.requestFocus();
        }


        binding.textDesignNo.setOnClickListener(view -> {
            if (designListDialog != null) {
                designListDialog.show_dialog();
            } else {
                designListDialog = new ListShowDialog(getActivity(), design_no_list, ShadeMasterDialog.this, DESIGN_REQUEST);
                designListDialog.show_dialog();
            }
        });

        binding.editWrapColor.setOnClickListener(view -> {
            if (warpListDialog != null) {
                warpListDialog.show_dialog();
            } else {
                warpListDialog = new ListShowDialog(getActivity(), warpColorList, ShadeMasterDialog.this, WARP_REQUEST);
                warpListDialog.show_dialog();
            }
        });


        binding.btnAdd.setOnClickListener(view -> {
            addArtist();
        });
        binding.btnClose.setOnClickListener(view -> {
            dialog.dismiss();
        });
        return dialog;
    }

    private void setAutoAdapter() {
        getDesignNoList();
        getYarnMaster();
    }

    private void getDesignNoList() {
        new DAODesignMaster(getActivity()).getReference().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                design_no_list.clear();
                designMasterModelsList.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    DesignMasterModel designMasterModel = documentSnapshot.toObject(DesignMasterModel.class);

                    if (designMasterModel != null && designMasterModel.getDesign_no() != null) {
                        design_no_list.add(designMasterModel.getDesign_no());
                        designMasterModelsList.add(designMasterModel);
                        fAdapter.notifyDataSetChanged();
                    }
                }
                if (designListDialog != null) {
                    designListDialog.update_list(design_no_list);
                } else {
                    designListDialog = new ListShowDialog(getActivity(), design_no_list, ShadeMasterDialog.this, DESIGN_REQUEST);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@Nullable Exception e) {
                Log.e("Firestore", "Error retrieving data", e);
            }
        });

//        new DAODesignMaster(getActivity()).getReference().addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                design_no_list.clear();
//                designMasterModelsList.clear();
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    DesignMasterModel designMasterModel = postSnapshot.getValue(DesignMasterModel.class);
//
//                    if (designMasterModel != null && designMasterModel.getDesign_no() != null) {
//                        design_no_list.add(designMasterModel.getDesign_no());
//                        designMasterModelsList.add(designMasterModel);
//                        fAdapter.notifyDataSetChanged();
//                    }
//                }
//                if (designListDialog != null) {
//                    designListDialog.update_list(design_no_list);
//                } else {
//                    designListDialog = new ListShowDialog(getActivity(), design_no_list, ShadeMasterDialog.this, DESIGN_REQUEST);
//                }
//
////                if (design_no_list.size() == 0) {
////                    binding.btnAddDesign.setVisibility(View.VISIBLE);
////                } else {
////                    new Util().setAutoAdapter(getContext(), design_no_list, binding.autoDesignNo);
////                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    private void getYarnMaster() {
        new DaoYarnMaster(getActivity()).getReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle error
                    return;
                }

                warpColorList.clear();
                weftColorList.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    YarnMasterModel yarnMasterModel = documentSnapshot.toObject(YarnMasterModel.class);
                    if (yarnMasterModel.getYarn_type().equals("WARP")) {
                        warpColorList.add(yarnMasterModel.getYarn_name());
                    } else {
                        weftColorList.add(yarnMasterModel.getYarn_name());

                    }
                }

                if (warpListDialog != null) {
                    warpListDialog.update_list(warpColorList);
                } else {
                    warpListDialog = new ListShowDialog(getActivity(), warpColorList, ShadeMasterDialog.this, WARP_REQUEST);
                }
            }
        });

//        new DaoYarnMaster(getActivity()).getReference().addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                warpColorList.clear();
//                weftColorList.clear();
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    YarnMasterModel yarnMasterModel = postSnapshot.getValue(YarnMasterModel.class);
//
//                    if (yarnMasterModel.getYarn_type().equals("WARP")) {
//                        warpColorList.add(yarnMasterModel.getYarn_name());
//                    } else {
//                        weftColorList.add(yarnMasterModel.getYarn_name());
//
//                    }
//
//                }
//                if (warpListDialog != null) {
//                    warpListDialog.update_list(warpColorList);
//                } else {
//                    warpListDialog = new ListShowDialog(getActivity(), warpColorList, ShadeMasterDialog.this, WARP_REQUEST);
//                }
//
////                if (warpColorList.size() == 0) {
////                    binding.btnAddYarn.setVisibility(View.VISIBLE);
////                } else {
////                    fAdapter.setAutoEditAdapter(weftColorList);
////                    new Util().setAutoAdapter(getActivity(), warpColorList, binding.editWrapColor);
////                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    private void addArtist() {

        String design_no = binding.textDesignNo.getText().toString().trim();
        String shade_no = binding.textShadeNo.getText().toString().trim();
        String wrap_color = binding.editWrapColor.getText().toString().trim();

        boolean is_valid = true;
        for (int i = 0; i < list.size(); i++) {
            if (TextUtils.isEmpty(list.get(i))) {
                new CustomSnackUtil().showSnack(dialog, "Please Enter Jala" + i, R.drawable.error_msg_icon);
                is_valid = false;
                break;
            }
        }
        if (is_valid) {
            if (TextUtils.isEmpty(design_no)) {
                new CustomSnackUtil().showSnack(dialog, "Please enter a Design no", R.drawable.error_msg_icon);
            } else if (TextUtils.isEmpty(shade_no)) {
                new CustomSnackUtil().showSnack(dialog, "Please enter a Shade no", R.drawable.error_msg_icon);
            } else if (TextUtils.isEmpty(wrap_color)) {
                new CustomSnackUtil().showSnack(dialog, "Please enter a Wrap color", R.drawable.error_msg_icon);
            } else {

                new DAODesignMaster(getActivity()).getReference().whereEqualTo("design_no", design_no)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (!querySnapshot.isEmpty()) {
                                    String id = dao.getId();
                                    DialogUtil.showProgressDialog(getActivity());

                                    ShadeMasterModel shadeMasterModel = new ShadeMasterModel();
                                    shadeMasterModel.setId(id);
                                    shadeMasterModel.setShade_no(shade_no);
                                    shadeMasterModel.setDesign_no(design_no);
                                    shadeMasterModel.setWrap_color(wrap_color);
                                    shadeMasterModel.setF_list(list);

                                    dao.insert(id, shadeMasterModel).addOnSuccessListener(runnable -> {
                                        DialogUtil.showSuccessDialog(getActivity(), false);
                                        dialog.dismiss();
                                    });
                                } else {
                                    new CustomSnackUtil().showSnack(dialog, "Warp Color not match", R.drawable.error_msg_icon);
                                }
                            } else {
                                Log.d("TAG", "Error getting documents: ", task.getException());
                            }
                        });

            }
        }
    }

    @Override
    public void onSelected(int position, String name, int request_code) {
        if (request_code == DESIGN_REQUEST) {

            binding.textDesignNo.setText(name);
            adapterSize = designMasterModelsList.get(position).getF_list().size();
            list.clear();


            for (int i = 0; i < adapterSize; i++) {
                list.add(null);
            }

            fAdapter.notifyDataSetChanged();
        } else if (request_code == WARP_REQUEST) {
            binding.editWrapColor.setText(name);
        } else if (request_code == 100) {

            list.set(0, name);

            fAdapter.notifyDataSetChanged();
        } else if (request_code == 101) {
            list.set(1, name);
            fAdapter.notifyItemChanged(1);
        } else if (request_code == 102) {
            list.set(2, name);
            fAdapter.notifyItemChanged(2);
        } else if (request_code == 103) {
            list.set(3, name);
            fAdapter.notifyItemChanged(3);
        } else if (request_code == 104) {
            list.set(4, name);
            fAdapter.notifyItemChanged(4);
        } else if (request_code == 105) {
            list.set(5, name);
            fAdapter.notifyItemChanged(5);
        } else if (request_code == 106) {
            list.set(6, name);
            fAdapter.notifyItemChanged(6);
        } else if (request_code == 107) {
            list.set(7, name);
            fAdapter.notifyItemChanged(7);
        }
    }

    @Override
    public void onAddButtonClicked(int position, String name, int request_code) {
        if (request_code == DESIGN_REQUEST) {

            if (isInsertPermissionGranted(PermissionState.DESIGN_MASTER.getValue())) {
                DesignMasterDialogFragment dialogFragment = new DesignMasterDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("design_no", name);
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getChildFragmentManager(), "My Fragment");
            } else {
                DialogUtil.showAccessDeniedDialog(getActivity());
            }
        } else if (request_code == WARP_REQUEST) {
            YarnMasterDialog yarnMasterDialog = new YarnMasterDialog();
            Bundle bundle = new Bundle();
            bundle.putString("yarn_name", name);
            bundle.putInt("yarn_type", 1);
            yarnMasterDialog.setArguments(bundle);
            yarnMasterDialog.show(getChildFragmentManager(), "Yarn Dialog");

//            YarnMasterDialog yarnMasterDialog = new YarnMasterDialog();
//            yarnMasterDialog.showDialog(getActivity(), binding.editWrapColor.getText().toString().trim(), 1);
        } else if (request_code == 100 || request_code == 101 || request_code == 102 || request_code == 103 || request_code == 104 || request_code == 105 || request_code == 106 || request_code == 107) {
            YarnMasterDialog yarnMasterDialog = new YarnMasterDialog();
            Bundle bundle = new Bundle();
            bundle.putString("yarn_name", name);
            bundle.putInt("yarn_type", 2);
            yarnMasterDialog.setArguments(bundle);
            yarnMasterDialog.show(getChildFragmentManager(), "Yarn Dialog");

//            YarnMasterDialog yarnMasterDialog = new YarnMasterDialog();
//            yarnMasterDialog.showDialog(getActivity(), name, 2);
        }
    }

    @Override
    public void onMultiSelected(ArrayList<String> shadeList, int request_code) {

    }

    private class FAdapter extends RecyclerView.Adapter<ShadeMasterDialog.FAdapter.ViewHolder> {

        ArrayList<String> weft_color_list = new ArrayList<>();

        @Override
        public FAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FItemBinding fBinding = FItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new FAdapter.ViewHolder(fBinding);
        }

        @Override
        public void onBindViewHolder(FAdapter.ViewHolder holder, int position) {
            if (list.get(position) != null) {
                holder.fBinding.autoF1.setText(list.get(position).trim());
            } else {
                if (list.get(position) != null) {
                    holder.fBinding.autoF1.setText(list.get(position));
                } else {
                    holder.fBinding.autoF1.setText("");
                }
            }

            holder.fBinding.autoF1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    int result = 0;
                    for (int j = 0; j < weft_color_list.size(); j++) {
                        if (weft_color_list.get(j).trim().toLowerCase().contains(charSequence.toString().trim().toLowerCase())) {
                            result++;
                        }
                    }
                    if (result == 0) {
                        holder.fBinding.btnAddYarnItem.setVisibility(View.VISIBLE);
                    } else {
                        holder.fBinding.btnAddYarnItem.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


            holder.fBinding.fTitle.setVisibility(View.VISIBLE);
            holder.fBinding.autoF1.setVisibility(View.VISIBLE);
            holder.fBinding.btnAddYarnItem.setVisibility(View.GONE);
            holder.fBinding.mainView.setBackgroundColor(getContext().getColor(R.color.white));
            holder.fBinding.fTitle.setText("F" + (int) (position + 1));
            holder.fBinding.autoF1.setHint("Enter F" + (int) (position + 1));

            holder.fBinding.btnAddYarnItem.setOnClickListener(view -> {

                if (isInsertPermissionGranted(PermissionState.DESIGN_MASTER.getValue())) {
                    YarnMasterDialog yarnMasterDialog = new YarnMasterDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("yarn_name", holder.fBinding.autoF1.getText().toString().trim());
                    bundle.putInt("yarn_type", 2);
                    yarnMasterDialog.setArguments(bundle);
                    yarnMasterDialog.show(getChildFragmentManager(), "Yarn Dialog");
                } else {
                    DialogUtil.showAccessDeniedDialog(getActivity());
                }

//                YarnMasterDialog yarnMasterDialog = new YarnMasterDialog();
//                yarnMasterDialog.showDialog(getActivity(), holder.fBinding.autoF1.getText().toString().trim(), 2);
            });
//            holder.auto_f1.setOnItemClickListener((adapterView, view, i, l) -> {
//
//                list.set(holder.getAdapterPosition(), holder.auto_f1.getText().toString());
//            });
            holder.fBinding.autoF1.setOnClickListener(view -> {
                if (position == 0) {
                    if (f1_dialog != null) {
                        f1_dialog.show_dialog();
                    } else {
                        f1_dialog = new ListShowDialog(getActivity(), weftColorList, ShadeMasterDialog.this, 100);
                        f1_dialog.show_dialog();
                    }
                } else if (position == 1) {
                    if (f2_dialog != null) {
                        f2_dialog.show_dialog();
                    } else {
                        f2_dialog = new ListShowDialog(getActivity(), weftColorList, ShadeMasterDialog.this, 101);
                        f2_dialog.show_dialog();
                    }
                } else if (position == 2) {
                    if (f3_dialog != null) {
                        f3_dialog.show_dialog();
                    } else {
                        f3_dialog = new ListShowDialog(getActivity(), weftColorList, ShadeMasterDialog.this, 102);
                        f3_dialog.show_dialog();
                    }
                } else if (position == 3) {
                    if (f4_dialog != null) {
                        f4_dialog.show_dialog();
                    } else {
                        f4_dialog = new ListShowDialog(getActivity(), weftColorList, ShadeMasterDialog.this, 103);
                        f4_dialog.show_dialog();
                    }
                } else if (position == 4) {
                    if (f5_dialog != null) {
                        f5_dialog.show_dialog();
                    } else {
                        f5_dialog = new ListShowDialog(getActivity(), weftColorList, ShadeMasterDialog.this, 104);
                        f5_dialog.show_dialog();
                    }
                } else if (position == 5) {
                    if (f6_dialog != null) {
                        f6_dialog.show_dialog();
                    } else {
                        f6_dialog = new ListShowDialog(getActivity(), weftColorList, ShadeMasterDialog.this, 105);
                        f6_dialog.show_dialog();
                    }
                } else if (position == 6) {
                    if (f7_dialog != null) {
                        f7_dialog.show_dialog();
                    } else {
                        f7_dialog = new ListShowDialog(getActivity(), weftColorList, ShadeMasterDialog.this, 106);
                        f7_dialog.show_dialog();
                    }
                } else if (position == 7) {
                    if (f8_dialog != null) {
                        f8_dialog.show_dialog();
                    } else {
                        f8_dialog = new ListShowDialog(getActivity(), weftColorList, ShadeMasterDialog.this, 107);
                        f8_dialog.show_dialog();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return adapterSize;
        }

        public void setAutoEditAdapter(ArrayList<String> warpColorList) {

            this.weft_color_list = warpColorList;
            notifyDataSetChanged();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            FItemBinding fBinding;

            public ViewHolder(FItemBinding fBinding) {
                super(fBinding.getRoot());
                this.fBinding = fBinding;

            }
        }
    }
}
