package com.example.texttile.presentation.screen.authority.list;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isAdmin;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import javax.annotation.Nonnull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.PemissionItemBinding;
import com.example.texttile.data.dao.DaoAuthority;
import com.example.texttile.presentation.screen.authority.user.AuthorityActivity;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.data.model.ItemState;
import com.example.texttile.data.model.UserDataModel;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.ViewBinderHelper;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;

import java.util.ArrayList;
import java.util.Collections;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    Activity activity;
    EmptyDataListener emptyDataListener;
    ArrayList<UserDataModel> userDataList;

    ArrayList<ItemState> itemModels = new ArrayList<>();
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    public boolean isSelectionMode = false;
    OnSelectionCountListeners onSelectionCountListeners;
    DaoAuthority daoAuthority;

    public UserAdapter(ArrayList<UserDataModel> userDataList, Activity activity, EmptyDataListener emptyDataListener, OnSelectionCountListeners onSelectionCountListeners, DaoAuthority daoAuthority) {
        this.userDataList = userDataList;
        this.activity = activity;
        this.emptyDataListener = emptyDataListener;
        this.onSelectionCountListeners = onSelectionCountListeners;
        this.daoAuthority = daoAuthority;

        emptyDataListener.onDataChanged(true, getItemCount());
        itemModels.clear();
        for (int i = 0; i < this.userDataList.size(); i++) {
            itemModels.add(new ItemState(false, false, false, i));
        }
        binderHelper.setOpenOnlyOne(true);
    }

    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {

        PemissionItemBinding binding = PemissionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@Nonnull ViewHolder holder, int position) {
        UserDataModel userDataModel = userDataList.get(position);

        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.scale_up);
        holder.binding.layoutMain.startAnimation(animation);

        binderHelper.bind(holder.binding.swipeRevealLayout, String.valueOf(position));


        holder.binding.textEmployeeName.setText(userDataModel.getF_name());
        holder.binding.textType.setText(userDataModel.getType());
        holder.binding.txtUsername.setText(userDataModel.getU_name());
        holder.binding.textPhoneNo.setText(userDataModel.getPhone_number());

        holder.itemView.setOnClickListener(view -> {
        });

        holder.binding.btnEdit.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("tracker", "edit");
            bundle.putString("position", "Admin");
            bundle.putSerializable(Const.USER_DATA, userDataList.get(position));
            activity.startActivity(new Intent(activity, AuthorityActivity.class).putExtras(bundle));
        });

        holder.binding.btnDelete.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.delete_conform);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            CardView btn_cancel = dialog.findViewById(R.id.btn_cancel);
            CardView btn_delete = dialog.findViewById(R.id.btn_delete);

            btn_cancel.setOnClickListener(v1 -> {
                dialog.dismiss();
            });
            btn_delete.setOnClickListener(v1 -> {
                new DaoAuthority(activity).delete(userDataList.get(position).getId()).addOnSuccessListener(runnable -> {
                    dialog.dismiss();
                    notifyItemRemoved(position);
                });
            });
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return userDataList.size();
    }

    public void selectionModeUpdate() {
        int count = 0;
        for (int i = 0; i < itemModels.size(); i++) {
            if (itemModels.get(i).isSelected()) {
                count++;
            }
        }

        if (count == 0) {

            isSelectionMode = false;
            unlockAll();
            onSelectionCountListeners.onCountChanged(isSelectionMode, count, count == itemModels.size() ? true : false);
        } else {
            isSelectionMode = true;
            lockAll();
            onSelectionCountListeners.onCountChanged(isSelectionMode, count, count == itemModels.size() ? true : false);
        }
    }


    public void lockAll() {
        for (int i = 0; i < itemModels.size(); i++) {
            if (itemModels.get(i).isSwiped()) {
                binderHelper.closeLayout(String.valueOf(i));
            }
            binderHelper.lockSwipe(String.valueOf(i));
        }
    }

    public void unlockAll() {
        for (int i = 0; i < itemModels.size(); i++) {
            binderHelper.unlockSwipe(String.valueOf(i));
        }
    }

    public void select_all() {
        for (int i = 0; i < itemModels.size(); i++) {
            itemModels.get(i).setSelected(true);

            notifyItemChanged(i);
        }
        selectionModeUpdate();
    }

    public void unSelect_all() {
        for (int i = 0; i < itemModels.size(); i++) {
            itemModels.get(i).setSelected(false);
            notifyItemChanged(i);
        }
        selectionModeUpdate();
    }

    public void delete_all() {
        if (isAdmin()) {
            final Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.delete_conform);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            CardView btn_cancel = dialog.findViewById(R.id.btn_cancel);
            CardView btn_delete = dialog.findViewById(R.id.btn_delete);

            btn_cancel.setOnClickListener(v1 -> {
                dialog.dismiss();
            });
            btn_delete.setOnClickListener(v1 -> {
                dialog.dismiss();

                ArrayList<Integer> selected_item = new ArrayList<>();
                for (int i = 0; i < userDataList.size(); i++) {
                    if (itemModels.get(i).isSelected()) {
                        selected_item.add(itemModels.get(i).getPosition());
                    }
                }
                Collections.sort(selected_item);

                daoAuthority.removeSelectedItem(selected_item);

            });
            dialog.show();

        } else {
            DialogUtil.showAccessDeniedDialog(activity);
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        PemissionItemBinding binding;

        public ViewHolder(@Nonnull PemissionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
