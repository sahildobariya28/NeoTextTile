package com.example.texttile.presentation.screen.master.shade_master.sublist;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isDeletePermissionGranted;
import static com.example.texttile.presentation.ui.util.PermissionUtil.isUpdatePermissionGranted;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.ShadeMasterItemBinding;
import com.example.texttile.data.dao.DAOShadeMaster;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.LoadFragmentChangeListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.presentation.screen.master.shade_master.shade_master.ShadeMultipleActivity;
import com.example.texttile.data.model.ItemState;
import com.example.texttile.data.model.ShadeMasterModel;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.SwipeRevealLayout;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.ViewBinderHelper;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class ShadeMasterAdapter extends RecyclerView.Adapter<ShadeMasterAdapter.ViewHolder>{

    Activity activity;

    int old_item = -1, new_item = -1;
    LoadFragmentChangeListener loadFragmentChangeListener;
    DAOShadeMaster daoShadeMaster;
    EmptyDataListener emptyDataListener;
    ArrayList<ShadeMasterModel> shadeMasterList = new ArrayList<>();

    ArrayList<ItemState> itemModels = new ArrayList<>();
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public boolean isSelectionMode = false;
    OnSelectionCountListeners onSelectionCountListeners;


    public ShadeMasterAdapter(ArrayList<ShadeMasterModel> shadeMasterList, Activity activity, LoadFragmentChangeListener loadFragmentChangeListener, EmptyDataListener emptyDataListener, OnSelectionCountListeners onSelectionCountListeners, DAOShadeMaster daoShadeMaster) {

        this.shadeMasterList = shadeMasterList;
        this.activity = activity;
        this.emptyDataListener = emptyDataListener;
        this.loadFragmentChangeListener = loadFragmentChangeListener;
        this.onSelectionCountListeners = onSelectionCountListeners;
        this.daoShadeMaster = daoShadeMaster;


        emptyDataListener.onDataChanged(true, getItemCount());
        itemModels.clear();
        for (int i = 0; i < shadeMasterList.size(); i++) {
            itemModels.add(new ItemState(false, false, false, i));
        }
        binderHelper.setOpenOnlyOne(true);

    }

    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        ShadeMasterItemBinding binding = ShadeMasterItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@Nonnull ViewHolder holder, int position) {

        holder.binding.swipeRevealLayout.setSwipeListener(new SwipeRevealLayout.SwipeListener() {
            @Override
            public void onClosed(SwipeRevealLayout view) {
                itemModels.get(holder.getAdapterPosition()).setSwiped(false);
            }

            @Override
            public void onOpened(SwipeRevealLayout view) {
                itemModels.get(holder.getAdapterPosition()).setSwiped(true);

            }

            @Override
            public void onSlide(SwipeRevealLayout view, float slideOffset) {
                itemModels.get(holder.getAdapterPosition()).setSwiped(false);
                Log.d("jkdksdf324", "onSlide: " + "yes view is slide");
            }
        });

        holder.binding.viewMain.setOnClickListener(view -> {
            if (holder.binding.swipeRevealLayout.isOpened()) {
                binderHelper.closeLayout(String.valueOf(holder.getAdapterPosition()));
                itemModels.get(holder.getAdapterPosition()).setSwiped(false);
            } else {
                if (isSelectionMode) {
                    if (itemModels.get(holder.getAdapterPosition()).isSelected()) {
                        holder.binding.viewMain.setBackground(activity.getDrawable(R.drawable.border_bg_unselected));
                        itemModels.get(holder.getAdapterPosition()).setSelected(false);
                        selectionModeUpdate();
                    } else {
                        holder.binding.viewMain.setBackground(activity.getDrawable(R.drawable.border_bg_selected));
                        itemModels.get(holder.getAdapterPosition()).setSelected(true);
                        selectionModeUpdate();
                    }
                } else {
                    expand_item(holder.getAdapterPosition(), holder.binding.btnDown);
                }
            }
        });
        holder.binding.viewMain.setOnLongClickListener(view -> {
            if (holder.binding.swipeRevealLayout.isOpened()) {
                binderHelper.closeLayout(String.valueOf(holder.getAdapterPosition()));
                holder.binding.viewMain.setBackground(activity.getDrawable(R.drawable.border_bg_selected));
                itemModels.get(holder.getAdapterPosition()).setSelected(true);
                selectionModeUpdate();
            } else {
                if (itemModels.get(holder.getAdapterPosition()).isSelected()) {
                    holder.binding.viewMain.setBackground(activity.getDrawable(R.drawable.border_bg_unselected));
                    itemModels.get(holder.getAdapterPosition()).setSelected(false);
                    selectionModeUpdate();
                } else {
                    binderHelper.closeLayout(String.valueOf(holder.getAdapterPosition()));
                    holder.binding.viewMain.setBackground(activity.getDrawable(R.drawable.border_bg_selected));
                    itemModels.get(holder.getAdapterPosition()).setSelected(true);
                    selectionModeUpdate();
                }
            }
            return true;
        });

        binderHelper.bind(holder.binding.swipeRevealLayout, String.valueOf(position));

        ShadeMasterModel shadeMasterModel = shadeMasterList.get(position);
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.scale_up);
        holder.binding.layoutMain.startAnimation(animation);


        if (itemModels.get(position).isExpanded()) {
            holder.binding.detailContainer.setVisibility(View.VISIBLE);
            holder.binding.btnDown.setImageResource(R.drawable.up);
        } else {
            holder.binding.detailContainer.setVisibility(View.GONE);
            holder.binding.btnDown.setImageResource(R.drawable.down);
        }
        if (itemModels.get(position).isSelected()) {
            holder.binding.viewMain.setBackground(activity.getDrawable(R.drawable.border_bg_selected));
            itemModels.get(holder.getAdapterPosition()).setSelected(true);
        } else {
            holder.binding.viewMain.setBackground(activity.getDrawable(R.drawable.border_bg_unselected));
            itemModels.get(holder.getAdapterPosition()).setSelected(false);
        }
        if (itemModels.get(position).isSwiped()) {
            binderHelper.openLayout(String.valueOf(holder.getAdapterPosition()));
        } else {
            binderHelper.closeLayout(String.valueOf(holder.getAdapterPosition()));
        }

        holder.binding.textDesignNo.setText(shadeMasterModel.getDesign_no());
        holder.binding.textShadeNo.setText(shadeMasterModel.getShade_no());
        holder.binding.textWarpColor.setText(shadeMasterModel.getWrap_color());

        showF_list(shadeMasterModel.getF_list().size(), holder, shadeMasterList.get(position).getF_list());

        holder.binding.btnEdit.setOnClickListener(view -> {

            if (isUpdatePermissionGranted(PermissionState.SHADE_MASTER.getValue())) {
                Bundle bundle = new Bundle();
                bundle.putString("tracker", "edit");
                bundle.putSerializable("shade_data", shadeMasterList.get(position));
                activity.startActivity(new Intent(activity, ShadeMultipleActivity.class).putExtras(bundle));

            } else {
                DialogUtil.showAccessDeniedDialog(activity);
            }

        });

        holder.binding.btnDelete.setOnClickListener(view -> {

            if (isDeletePermissionGranted(PermissionState.SHADE_MASTER.getValue())) {
                Dialog dialog = new Dialog(activity);
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
                    DialogUtil.showProgressDialog(activity);
                    daoShadeMaster.delete(shadeMasterList.get(position).getId()).addOnSuccessListener(runnable -> {
                        DialogUtil.showDeleteDialog(activity);
                    });
                });

                dialog.show();

            } else {
                DialogUtil.showAccessDeniedDialog(activity);
            }
        });
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

        if (isDeletePermissionGranted(PermissionState.SHADE_MASTER.getValue())) {
            final Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.delete_conform);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            CardView btn_cancel = dialog.findViewById(R.id.btn_cancel);
            CardView btn_delete = dialog.findViewById(R.id.btn_delete);

            btn_cancel.setOnClickListener(v1 -> dialog.dismiss());
            btn_delete.setOnClickListener(v1 -> {
                dialog.dismiss();

                ArrayList<Integer> selected_item = new ArrayList<>();
                for (int i = 0; i < shadeMasterList.size(); i++) {
                    if (itemModels.get(i).isSelected()) {
                        selected_item.add(itemModels.get(i).getPosition());
                    }
                }

                daoShadeMaster.removeSelectedItem(selected_item);
            });

            dialog.show();
        } else {
            DialogUtil.showAccessDeniedDialog(activity);
        }
    }

    public void deleteItem(ShadeMasterModel shadeMasterModel, boolean isLast, int i) {
        daoShadeMaster.delete(shadeMasterModel.getId()).addOnSuccessListener(runnable -> {
            if (isLast) {

                itemModels.remove(i);
                selectionModeUpdate();
            } else {
                itemModels.remove(i);
                selectionModeUpdate();
            }
        });
    }

    @Override
    public int getItemCount() {
        return shadeMasterList.size();
    }

    public void showF_list(int size, ViewHolder holder, ArrayList<String> fList) {
        for (int i = 0; i < size; i++) {
            show(i + 1, holder, fList.get(i));
        }
    }

    public void show(int position, ViewHolder holder, String f_value) {
        if (position == 1) {
            holder.binding.viewF1.setVisibility(View.VISIBLE);
            holder.binding.textF1.setText(f_value);
        } else if (position == 2) {
            holder.binding.viewF2.setVisibility(View.VISIBLE);
            holder.binding.textF2.setText(f_value);
        } else if (position == 3) {
            holder.binding.viewF3.setVisibility(View.VISIBLE);
            holder.binding.textF3.setText(f_value);
        } else if (position == 4) {
            holder.binding.viewF4.setVisibility(View.VISIBLE);
            holder.binding.textF4.setText(f_value);
        } else if (position == 5) {
            holder.binding.viewF5.setVisibility(View.VISIBLE);
            holder.binding.textF5.setText(f_value);
        } else if (position == 6) {
            holder.binding.viewF6.setVisibility(View.VISIBLE);
            holder.binding.textF6.setText(f_value);
        } else if (position == 7) {
            holder.binding.viewF7.setVisibility(View.VISIBLE);
            holder.binding.textF7.setText(f_value);
        } else if (position == 8) {
            holder.binding.viewF8.setVisibility(View.VISIBLE);
            holder.binding.textF8.setText(f_value);
        }
    }

    public void expand_item(int current_item, ImageView btn_down) {
        if (new_item != -1) {
            if (new_item != current_item) {
                old_item = new_item;

                itemModels.get(old_item).setExpanded(false);
                btn_down.setImageResource(R.drawable.down);
                new_item = current_item;
                itemModels.get(new_item).setExpanded(true);
                btn_down.setImageResource(R.drawable.up);
                notifyItemChanged(old_item);
                notifyItemChanged(new_item);

            } else {
                old_item = new_item;
                if (itemModels.get(new_item).isExpanded()) {
                    itemModels.get(new_item).setExpanded(false);
                    btn_down.setImageResource(R.drawable.down);
                } else {
                    itemModels.get(new_item).setExpanded(true);
                    btn_down.setImageResource(R.drawable.up);

                }
                new_item = current_item;
                notifyItemChanged(old_item);
                notifyItemChanged(new_item);
            }

        } else {
            new_item = current_item;
            itemModels.get(new_item).setExpanded(true);
            btn_down.setImageResource(R.drawable.up);
            notifyItemChanged(old_item);
            notifyItemChanged(new_item);
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ShadeMasterItemBinding binding;

        public ViewHolder(@Nonnull ShadeMasterItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
