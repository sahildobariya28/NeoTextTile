package com.example.texttile.presentation.screen.master.machine_master;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isDeletePermissionGranted;
import static com.example.texttile.presentation.ui.util.PermissionUtil.isReadPermissionGranted;
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
import com.example.texttile.presentation.ui.activity.PrintActivity;
import com.example.texttile.databinding.MachineItemBinding;
import com.example.texttile.data.dao.DAOMachineMaster;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.LoadFragmentChangeListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.data.model.ItemState;
import com.example.texttile.data.model.MachineMasterModel;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.SwipeRevealLayout;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.ViewBinderHelper;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class MachineMasterAdapter extends RecyclerView.Adapter<MachineMasterAdapter.ViewHolder> {

    Activity activity;
    LoadFragmentChangeListener loadFragmentChangeListener;
    EmptyDataListener emptyDataListener;
    DAOMachineMaster daoMachineMaster;
    ArrayList<MachineMasterModel> machineMasterList;
    int old_item = -1, new_item = -1;
    ArrayList<ItemState> itemModels = new ArrayList<>();
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public boolean isSelectionMode = false;
    OnSelectionCountListeners onSelectionCountListeners;

    public MachineMasterAdapter(ArrayList<MachineMasterModel> machineMasterList, Activity activity, LoadFragmentChangeListener loadFragmentChangeListener, EmptyDataListener emptyDataListener, OnSelectionCountListeners onSelectionCountListeners, DAOMachineMaster daoMachineMaster) {
        this.machineMasterList = machineMasterList;
        this.activity = activity;
        this.loadFragmentChangeListener = loadFragmentChangeListener;
        this.emptyDataListener = emptyDataListener;
        this.onSelectionCountListeners = onSelectionCountListeners;
        this.daoMachineMaster = daoMachineMaster;

        emptyDataListener.onDataChanged(true, getItemCount());
        itemModels.clear();
        for (int i = 0; i < machineMasterList.size(); i++) {
            itemModels.add(new ItemState(false, false, false, i));
        }
        binderHelper.setOpenOnlyOne(true);

    }

    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        MachineItemBinding binding = MachineItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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


        MachineMasterModel machineMasterModel = machineMasterList.get(position);
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


        holder.binding.txtMachineName.setText(machineMasterModel.getMachine_name());
        holder.binding.textJala.setText(machineMasterModel.getJala_name());
        holder.binding.textWrapColor.setText(machineMasterModel.getWrap_color());
        holder.binding.textWrapThread.setText(machineMasterModel.getWrap_thread());
        holder.binding.textReed.setText(machineMasterModel.getReed());
        holder.binding.textLabourCharge.setText(String.valueOf(machineMasterModel.getLabour_charge()));

        if (machineMasterModel.isStatus()) {
            holder.binding.switchMachine.setImageResource(R.drawable.switch_on);
        } else {
            holder.binding.switchMachine.setImageResource(R.drawable.switch_off);
        }
        holder.binding.switchMachine.setOnClickListener(view -> {

        });

        holder.itemView.setOnClickListener(view -> {
            expand_item(position, holder.binding.btnDown);
        });

        holder.binding.btnPrint.setOnClickListener(view -> {
            if (isReadPermissionGranted(PermissionState.MACHINE_MASTER.getValue())){
                activity.startActivity(new Intent(activity, PrintActivity.class).putExtra("data", machineMasterList.get(position)).putExtra("tracker", "machine"));
            }else {
                DialogUtil.showAccessDeniedDialog(activity);
            }
        });
        holder.binding.btnEdit.setOnClickListener(view -> {
            if (isUpdatePermissionGranted(PermissionState.MACHINE_MASTER.getValue())){
                Bundle bundle = new Bundle();
                bundle.putString("tracker", "edit");
                bundle.putSerializable("machine_data", machineMasterList.get(position));
                activity.startActivity(new Intent(activity, MachineMasterActivity.class).putExtras(bundle));
            }else {
                DialogUtil.showAccessDeniedDialog(activity);
            }
        });

        holder.binding.btnDelete.setOnClickListener(view -> {
            if (isDeletePermissionGranted(PermissionState.MACHINE_MASTER.getValue())){
                Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.delete_conform);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                CardView btn_cancel = dialog.findViewById(R.id.btn_cancel);
                CardView btn_delete = dialog.findViewById(R.id.btn_delete);

                btn_cancel.setOnClickListener(v1 -> {
                    dialog.dismiss();
                    DialogUtil.hideProgressDialog();
                });
                btn_delete.setOnClickListener(v1 -> {
                    dialog.dismiss();
                    DialogUtil.showProgressDialog(activity);
                    daoMachineMaster.delete(machineMasterList.get(position).getId()).addOnSuccessListener(runnable -> {
                        DialogUtil.showDeleteDialog(activity);
                    });
                });

                dialog.show();
            }else {
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
        if (isDeletePermissionGranted(PermissionState.MACHINE_MASTER.getValue())){
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
                for (int i = 0; i < machineMasterList.size(); i++) {
                    if (itemModels.get(i).isSelected()) {
                        selected_item.add(itemModels.get(i).getPosition());
                    }
                }

                daoMachineMaster.removeSelectedItem(selected_item);
            });

            dialog.show();
        }else {
            DialogUtil.showAccessDeniedDialog(activity);
        }

    }

    @Override
    public int getItemCount() {
        return machineMasterList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        MachineItemBinding binding;

        public ViewHolder(@Nonnull MachineItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

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

}
