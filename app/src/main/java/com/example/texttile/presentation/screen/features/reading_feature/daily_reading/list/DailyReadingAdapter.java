package com.example.texttile.presentation.screen.features.reading_feature.daily_reading.list;

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

import javax.annotation.Nonnull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.ReadingItemBinding;
import com.example.texttile.data.dao.DAOReading;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.presentation.screen.features.reading_feature.daily_reading.reading.DailyReadingActivity;
import com.example.texttile.data.model.DailyReadingModel;
import com.example.texttile.data.model.ItemState;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.SwipeRevealLayout;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.ViewBinderHelper;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;

import java.util.ArrayList;

public class DailyReadingAdapter extends RecyclerView.Adapter<DailyReadingAdapter.ViewHolder> {

    Activity activity;
    EmptyDataListener emptyDataListener;
    ArrayList<DailyReadingModel> dailyReadingList;

    ArrayList<ItemState> itemModels = new ArrayList<>();
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public boolean isSelectionMode = false;
    OnSelectionCountListeners onSelectionCountListeners;
    DAOReading daoReading;

    public DailyReadingAdapter(ArrayList<DailyReadingModel> dailyReadingList, Activity activity, EmptyDataListener emptyDataListener, OnSelectionCountListeners onSelectionCountListeners, DAOReading daoReading) {
        this.dailyReadingList = dailyReadingList;
        this.activity = activity;
        this.emptyDataListener = emptyDataListener;
        this.onSelectionCountListeners = onSelectionCountListeners;
        this.daoReading = daoReading;

        emptyDataListener.onDataChanged(true, getItemCount());
        itemModels.clear();
        for (int i = 0; i < dailyReadingList.size(); i++) {
            itemModels.add(new ItemState(false, false, false, i));
        }
        binderHelper.setOpenOnlyOne(true);
    }

    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        ReadingItemBinding binding = ReadingItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
//                    expand_item(holder.getAdapterPosition(), holder.binding.btnDown);
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

        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.scale_up);
        holder.binding.viewMain.startAnimation(animation);


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

        DailyReadingModel dailyReadingModel = dailyReadingList.get(position);
        if (position == 0) {
            holder.binding.viewDate.setVisibility(View.VISIBLE);
            holder.binding.textTimeLine.setText(dailyReadingModel.getDate());
//            holder.binding.textTimeLine.startAnimation(animation);
        } else if (position > 0) {
            if (!dailyReadingList.get(position - 1).getDate().equals(dailyReadingList.get(position).getDate())) {
                holder.binding.viewDate.setVisibility(View.VISIBLE);
                holder.binding.textTimeLine.setText(dailyReadingModel.getDate());
//                holder.binding.textTimeLine.startAnimation(animation);
            } else {
                holder.binding.viewDate.setVisibility(View.GONE);
            }

        }

        holder.binding.textOrderNo.setText(dailyReadingModel.getOrder_no());
        holder.binding.textManagerName.setText(dailyReadingModel.getManager_name());
        holder.binding.textDate.setText(dailyReadingModel.getDate());
        holder.binding.textQuantity.setText("\\" + dailyReadingModel.getQty());

        holder.binding.btnEdit.setOnClickListener(view -> {
            if (isUpdatePermissionGranted(PermissionState.DAILY_READING.getValue())) {
                Bundle bundle = new Bundle();
                bundle.putString("tracker", "edit");
                bundle.putSerializable("reading_data", dailyReadingList.get(position));
                activity.startActivity(new Intent(activity, DailyReadingActivity.class).putExtras(bundle));
            } else {
                DialogUtil.showAccessDeniedDialog(activity);
            }

        });

        holder.binding.btnDelete.setOnClickListener(view -> {

                if (isDeletePermissionGranted(PermissionState.DAILY_READING.getValue())) {
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
                        daoReading.delete(dailyReadingList.get(position).getId()).addOnSuccessListener(runnable -> {
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
            if (isDeletePermissionGranted(PermissionState.DAILY_READING.getValue())) {
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
                    for (int i = 0; i < dailyReadingList.size(); i++) {
                        if (itemModels.get(i).isSelected()) {
                            selected_item.add(itemModels.get(i).getPosition());
                        }
                    }

                    daoReading.removeSelectedItem(selected_item);
                    onSelectionCountListeners.onCountChanged(true, 0, false);
                });

                dialog.show();
            } else {
                DialogUtil.showAccessDeniedDialog(activity);
            }
    }

    @Override
    public int getItemCount() {
        return dailyReadingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ReadingItemBinding binding;

        public ViewHolder(@Nonnull ReadingItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
