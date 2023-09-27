package com.example.texttile.presentation.screen.master.party_master;

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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.PartyMasterItemBinding;
import com.example.texttile.data.dao.DAOPartyMaster;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.LoadFragmentChangeListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.data.model.ItemState;
import com.example.texttile.data.model.PartyMasterModel;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.SwipeRevealLayout;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.ViewBinderHelper;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Collections;

public class PartyMasterAdapter extends RecyclerView.Adapter<PartyMasterAdapter.ViewHolder> {

    Activity activity;
    LoadFragmentChangeListener loadFragmentChangeListener;
    EmptyDataListener emptyDataListener;
    ArrayList<PartyMasterModel> partyMasterList;
    DAOPartyMaster daoPartyMaster;

    ArrayList<ItemState> itemModels = new ArrayList<>();
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public boolean isSelectionMode = false;
    OnSelectionCountListeners onSelectionCountListeners;

    public PartyMasterAdapter(ArrayList<PartyMasterModel> partyMasterList, Activity activity, LoadFragmentChangeListener loadFragmentChangeListener, EmptyDataListener emptyDataListener, OnSelectionCountListeners onSelectionCountListeners, DAOPartyMaster daoPartyMaster) {

        this.partyMasterList = partyMasterList;
        this.activity = activity;
        this.loadFragmentChangeListener = loadFragmentChangeListener;
        this.emptyDataListener = emptyDataListener;
        this.daoPartyMaster = daoPartyMaster;
        this.onSelectionCountListeners = onSelectionCountListeners;

        emptyDataListener.onDataChanged(true, getItemCount());
        itemModels.clear();
        for (int i = 0; i < partyMasterList.size(); i++) {
            itemModels.add(new ItemState(false, false, false, i));
        }
        binderHelper.setOpenOnlyOne(true);
    }

    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        PartyMasterItemBinding binding = PartyMasterItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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

        PartyMasterModel partyMasterModel = partyMasterList.get(position);
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.scale_up);
        holder.binding.layoutMain.startAnimation(animation);

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

        holder.binding.txtPartyName.setText(partyMasterModel.getParty_Name());
        holder.binding.textAddress.setText(partyMasterModel.getAddress());

        holder.binding.btnEdit.setOnClickListener(view -> {
            if (isUpdatePermissionGranted(PermissionState.PARTY_MASTER.getValue())) {
                Bundle bundle = new Bundle();
                bundle.putString("tracker", "edit");
                bundle.putSerializable("party_data", partyMasterList.get(position));
                activity.startActivity(new Intent(activity, PartyMasterActivity.class).putExtras(bundle));

            } else {
                DialogUtil.showAccessDeniedDialog(activity);
            }
        });

        holder.binding.btnDelete.setOnClickListener(view -> {
            if (isDeletePermissionGranted(PermissionState.PARTY_MASTER.getValue())) {
                deleteItem(partyMasterList.get(position).getId());
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
        if (isDeletePermissionGranted(PermissionState.PARTY_MASTER.getValue())) {
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
                for (int i = 0; i < partyMasterList.size(); i++) {
                    if (itemModels.get(i).isSelected()) {
                        selected_item.add(itemModels.get(i).getPosition());
                    }
                }

                Collections.sort(selected_item);
                daoPartyMaster.removeSelectedItem(selected_item);
            });

            dialog.show();
        } else {
            DialogUtil.showAccessDeniedDialog(activity);
        }
    }

    @Override
    public int getItemCount() {
        return partyMasterList.size();
    }

    public void deleteItem(String child) {
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
            daoPartyMaster.delete(child).addOnSuccessListener(runnable -> {
                DialogUtil.showDeleteDialog(activity);
            });
        });

        dialog.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        PartyMasterItemBinding binding;

        public ViewHolder(@Nonnull PartyMasterItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
