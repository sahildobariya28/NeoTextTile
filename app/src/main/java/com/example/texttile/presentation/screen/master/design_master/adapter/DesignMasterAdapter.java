package com.example.texttile.presentation.screen.master.design_master.adapter;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isDeletePermissionGranted;
import static com.example.texttile.presentation.ui.util.PermissionUtil.isUpdatePermissionGranted;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.DesignMasterItemBinding;
import com.example.texttile.data.dao.DAODesignMaster;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.presentation.screen.master.design_master.activity.design_master.DesignMasterActivity;
import com.example.texttile.data.model.DesignMasterModel;
import com.example.texttile.data.model.ItemState;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.SwipeRevealLayout;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.ViewBinderHelper;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;

import java.util.ArrayList;
import java.util.Collections;

import javax.annotation.Nonnull;

public class DesignMasterAdapter extends RecyclerView.Adapter<DesignMasterAdapter.ViewHolder> {

    Activity activity;
    int old_item, new_item;
    EmptyDataListener emptyDataListener;
    ArrayList<DesignMasterModel> designMasterList;
    ArrayList<ItemState> itemModels = new ArrayList<>();
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public boolean isSelectionMode = false;
    OnSelectionCountListeners onSelectionCountListeners;
    DAODesignMaster daoDesignMaster;

    public DesignMasterAdapter(ArrayList<DesignMasterModel> designMasterModels, Activity activity, EmptyDataListener emptyDataListener, OnSelectionCountListeners onSelectionCountListeners, DAODesignMaster daoDesignMaster) {

        this.designMasterList = designMasterModels;
        this.activity = activity;
        this.emptyDataListener = emptyDataListener;
        this.onSelectionCountListeners = onSelectionCountListeners;
        this.daoDesignMaster = daoDesignMaster;

        emptyDataListener.onDataChanged(true, getItemCount());
        itemModels.clear();
        for (int i = 0; i < this.designMasterList.size(); i++) {
            itemModels.add(new ItemState(false, false, false, i));
        }
        binderHelper.setOpenOnlyOne(true);
    }

    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
//        DesignMasterItemBinding designMasterItemBinding = DesignMasterItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        DesignMasterItemBinding designMasterItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.design_master_item, parent, false);
        return new ViewHolder(designMasterItemBinding);
    }

    @Override
    public void onBindViewHolder(@Nonnull ViewHolder holder, int position) {
        holder.binding.setDesignModel(designMasterList.get(position));

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
//
        DesignMasterModel designMasterModel = designMasterList.get(position);
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

        holder.binding.btnShowJalaList.setOnClickListener(view -> {

            final Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.design_jala_pattern_item);
            dialog.setCancelable(false);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ListView simpleListView = dialog.findViewById(R.id.simpleListView);
            ImageView btn_close = dialog.findViewById(R.id.btn_close);

            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < designMasterModel.getJalaWithFileModelList().size(); i++) {
                list.add(designMasterModel.getJalaWithFileModelList().get(i).getJala_name());
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, R.layout.white_simple_spinner_dropdown_item, R.id.edit_name, list);
            simpleListView.setAdapter(arrayAdapter);

            btn_close.setOnClickListener(v1 -> {
                dialog.dismiss();
            });


            dialog.show();
        });


        holder.binding.btnEdit.setOnClickListener(view -> {
            if (isUpdatePermissionGranted(PermissionState.DESIGN_MASTER.getValue())) {
                Bundle bundle = new Bundle();
                bundle.putString("tracker", "edit");
                bundle.putSerializable("design_data", designMasterList.get(position));
                activity.startActivity(new Intent(activity, DesignMasterActivity.class).putExtras(bundle));
            } else {
                DialogUtil.showAccessDeniedDialog(activity);
            }
        });

        holder.binding.btnDelete.setOnClickListener(view -> {
            if (isDeletePermissionGranted(PermissionState.DESIGN_MASTER.getValue())) {
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

                    daoDesignMaster.removeSingleItem(position);
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
            if (isDeletePermissionGranted(PermissionState.DESIGN_MASTER.getValue())) {
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
                    for (int i = 0; i < designMasterList.size(); i++) {
                        if (itemModels.get(i).isSelected()) {
                            selected_item.add(itemModels.get(i).getPosition());
                        }
                    }
                    Collections.sort(selected_item);

                    daoDesignMaster.removeSelectedItem(selected_item);

                });
                dialog.show();
            } else {
                DialogUtil.showAccessDeniedDialog(activity);
            }
    }


    @Override
    public int getItemCount() {
        return designMasterList.size();
    }

    public void showF_list(int size, ViewHolder holder, ArrayList<Integer> fList) {
        for (int i = 0; i < size; i++) {
            show(i + 1, holder, fList.get(i));
        }
    }

    public void show(int position, ViewHolder holder, int f_value) {
        if (position == 1) {
            holder.binding.viewF1.setVisibility(View.VISIBLE);
            holder.binding.textF1.setText(String.valueOf(f_value));
        } else if (position == 2) {
            holder.binding.viewF2.setVisibility(View.VISIBLE);
            holder.binding.textF2.setText(String.valueOf(f_value));
        } else if (position == 3) {
            holder.binding.viewF3.setVisibility(View.VISIBLE);
            holder.binding.textF3.setText(String.valueOf(f_value));
        } else if (position == 4) {
            holder.binding.viewF4.setVisibility(View.VISIBLE);
            holder.binding.textF4.setText(String.valueOf(f_value));
        } else if (position == 5) {
            holder.binding.viewF5.setVisibility(View.VISIBLE);
            holder.binding.textF5.setText(String.valueOf(f_value));
        } else if (position == 6) {
            holder.binding.viewF6.setVisibility(View.VISIBLE);
            holder.binding.textF6.setText(String.valueOf(f_value));
        } else if (position == 7) {
            holder.binding.viewF7.setVisibility(View.VISIBLE);
            holder.binding.textF7.setText(String.valueOf(f_value));
        } else if (position == 8) {
            holder.binding.viewF8.setVisibility(View.VISIBLE);
            holder.binding.textF8.setText(String.valueOf(f_value));
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


//    public void deleteImage(DesignMasterModel model) {
//
//        if (model != null) {
//            if (model.getJalaWithFileModelList().get(deletePosition).getFile_uri() != null && !model.getJalaWithFileModelList().get(deletePosition).getFile_uri().isEmpty()) {
//                daoDesignMaster.deleteFile(model.getJalaWithFileModelList().get(deletePosition).getFile_uri()).addOnSuccessListener(unused -> {
//
//                    model.getJalaWithFileModelList().remove(deletePosition);
//                    if (deletePosition < (model.getJalaWithFileModelList().size() - 1)) {
//                        deleteImage(model);
//                    } else {
//                        daoDesignMaster.remove(model.getId()).addOnSuccessListener(runnable -> {
//                            DialogUtil.showDeleteDialog(activity);
//                        });
//                    }
//                });
//            } else {
//                model.getJalaWithFileModelList().remove(deletePosition);
//                if (deletePosition < (model.getJalaWithFileModelList().size() - 1)) {
//                    deleteImage(model);
//                } else {
//                    daoDesignMaster.remove(model.getId()).addOnSuccessListener(runnable -> {
//                        DialogUtil.showDeleteDialog(activity);
//                    });
//                }
//            }
//        }
//    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        DesignMasterItemBinding binding;

        public ViewHolder(@Nonnull DesignMasterItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
