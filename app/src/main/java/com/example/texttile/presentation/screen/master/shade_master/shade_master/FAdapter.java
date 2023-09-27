package com.example.texttile.presentation.screen.master.shade_master.shade_master;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isInsertPermissionGranted;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.FItemBinding;
import com.example.texttile.presentation.ui.dialog.YarnMasterDialog;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.ListShowDialog;
import com.example.texttile.presentation.ui.util.PermissionState;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class FAdapter extends RecyclerView.Adapter<FAdapter.ViewHolder> {

    ArrayList<String> weft_color_list = new ArrayList<>();
    Activity activity;
    FragmentManager fragmentManager;
    ShadeMultipleViewModel viewModel;

    public FAdapter(FragmentManager fragmentManager, Activity activity, ShadeMultipleViewModel viewModel){
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.viewModel = viewModel;
    }

    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        FItemBinding fBinding = FItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        View view = LayoutInflater.from(activity).inflate(R.layout.f_item, parent, false);
        return new ViewHolder(fBinding);
    }

    @Override
    public void onBindViewHolder(@Nonnull ViewHolder holder, int position) {

        if (viewModel.list.getValue().get(position) != null) {
            holder.fBinding.autoF1.setText(viewModel.list.getValue().get(position).trim());
        } else {
            if (viewModel.list.getValue().get(position) != null) {
                holder.fBinding.autoF1.setText(viewModel.list.getValue().get(position));
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
        holder.fBinding.mainView.setBackgroundColor(activity.getColor(R.color.white));
        holder.fBinding.fTitle.setText("F" + (int) (position + 1));
        holder.fBinding.autoF1.setHint("Enter F" + (int) (position + 1));

        holder.fBinding.btnAddYarnItem.setOnClickListener(view -> {

            if (isInsertPermissionGranted(PermissionState.DESIGN_MASTER.getValue())) {
                YarnMasterDialog yarnMasterDialog = new YarnMasterDialog();
                Bundle bundle = new Bundle();
                bundle.putString("yarn_name", holder.fBinding.autoF1.getText().toString().trim());
                bundle.putInt("yarn_type", 2);
                yarnMasterDialog.setArguments(bundle);
                yarnMasterDialog.show(fragmentManager, "Yarn Dialog");
            } else {
                DialogUtil.showAccessDeniedDialog(activity);
            }


        });
        holder.fBinding.autoF1.setOnClickListener(view -> {
            if (position == 0) {
                if (viewModel.f1_dialog != null) {
                    viewModel.f1_dialog.show_dialog();
                } else {
                    viewModel.f1_dialog = new ListShowDialog(activity, viewModel.weftColorList.getValue(), viewModel.getSelectDialogRef(), 100);
                    viewModel.f1_dialog.show_dialog();
                }
            } else if (position == 1) {
                if (viewModel.f2_dialog != null) {
                    viewModel.f2_dialog.show_dialog();
                } else {
                    viewModel.f2_dialog = new ListShowDialog(activity, viewModel.weftColorList.getValue(), viewModel.getSelectDialogRef(), 101);
                    viewModel.f2_dialog.show_dialog();
                }
            } else if (position == 2) {
                if (viewModel.f3_dialog != null) {
                    viewModel.f3_dialog.show_dialog();
                } else {
                    viewModel.f3_dialog = new ListShowDialog(activity, viewModel.weftColorList.getValue(), viewModel.getSelectDialogRef(), 102);
                    viewModel.f3_dialog.show_dialog();
                }
            } else if (position == 3) {
                if (viewModel.f4_dialog != null) {
                    viewModel.f4_dialog.show_dialog();
                } else {
                    viewModel.f4_dialog = new ListShowDialog(activity, viewModel.weftColorList.getValue(), viewModel.getSelectDialogRef(), 103);
                    viewModel.f4_dialog.show_dialog();
                }
            } else if (position == 4) {
                if (viewModel.f5_dialog != null) {
                    viewModel.f5_dialog.show_dialog();
                } else {
                    viewModel.f5_dialog = new ListShowDialog(activity, viewModel.weftColorList.getValue(), viewModel.getSelectDialogRef(), 104);
                    viewModel.f5_dialog.show_dialog();
                }
            } else if (position == 5) {
                if (viewModel.f6_dialog != null) {
                    viewModel.f6_dialog.show_dialog();
                } else {
                    viewModel.f6_dialog = new ListShowDialog(activity, viewModel.weftColorList.getValue(), viewModel.getSelectDialogRef(), 105);
                    viewModel.f6_dialog.show_dialog();
                }
            } else if (position == 6) {
                if (viewModel.f7_dialog != null) {
                    viewModel.f7_dialog.show_dialog();
                } else {
                    viewModel.f7_dialog = new ListShowDialog(activity, viewModel.weftColorList.getValue(), viewModel.getSelectDialogRef(), 106);
                    viewModel.f7_dialog.show_dialog();
                }
            } else if (position == 7) {
                if (viewModel.f8_dialog != null) {
                    viewModel.f8_dialog.show_dialog();
                } else {
                    viewModel.f8_dialog = new ListShowDialog(activity, viewModel.weftColorList.getValue(), viewModel.getSelectDialogRef(), 107);
                    viewModel.f8_dialog.show_dialog();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return viewModel.adapterSize;
    }

    public void setAutoEditAdapter(ArrayList<String> warpColorList) {

        this.weft_color_list = warpColorList;
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        FItemBinding fBinding;

        public ViewHolder(@Nonnull FItemBinding fBinding) {
            super(fBinding.getRoot());
            this.fBinding = fBinding;

        }
    }
}