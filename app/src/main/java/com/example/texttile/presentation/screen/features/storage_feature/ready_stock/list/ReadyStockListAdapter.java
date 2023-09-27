package com.example.texttile.presentation.screen.features.storage_feature.ready_stock.list;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isDeletePermissionGranted;
import static com.example.texttile.presentation.ui.util.PermissionUtil.isUpdatePermissionGranted;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.ExtraOrderItemBinding;
import com.example.texttile.data.dao.DAOExtraOrder;
import com.example.texttile.presentation.screen.features.storage_feature.ready_stock.readystock.ReadyStockActivity;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.LoadFragmentChangeListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.data.model.ItemState;
import com.example.texttile.data.model.ReadyStockModel;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.SwipeRevealLayout;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.ViewBinderHelper;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class ReadyStockListAdapter extends RecyclerView.Adapter<ReadyStockListAdapter.ViewHolder> {

    Activity activity;
    LoadFragmentChangeListener loadFragmentChangeListener;
    EmptyDataListener emptyDataListener;
    DAOExtraOrder daoExtraOrder;
    ArrayList<ReadyStockModel> readyStockList;
    ArrayList<ItemState> itemModels = new ArrayList<>();
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public boolean isSelectionMode = false;
    OnSelectionCountListeners onSelectionCountListeners;


    public ReadyStockListAdapter(ArrayList<ReadyStockModel> readyStockList, Activity activity, LoadFragmentChangeListener loadFragmentChangeListener, EmptyDataListener emptyDataListener, OnSelectionCountListeners onSelectionCountListeners) {
        this.readyStockList = readyStockList;
        this.activity = activity;
        this.loadFragmentChangeListener = loadFragmentChangeListener;
        this.emptyDataListener = emptyDataListener;
        daoExtraOrder = new DAOExtraOrder(activity);
        this.onSelectionCountListeners = onSelectionCountListeners;


        emptyDataListener.onDataChanged(true, getItemCount());
        itemModels.clear();
        for (int i = 0; i < readyStockList.size(); i++) {
            itemModels.add(new ItemState(false, false, false, i));
        }
        binderHelper.setOpenOnlyOne(true);
    }

    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        ExtraOrderItemBinding binding = ExtraOrderItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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

        ReadyStockModel readyStockModel = readyStockList.get(position);
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

        if (position == 0) {
            holder.binding.viewDate.setVisibility(View.VISIBLE);
            holder.binding.textTimeLine.setText(readyStockModel.getCurrent_date());
//            holder.binding.textTimeLine.startAnimation(animation);
        } else if (position > 0) {
            if (!readyStockList.get(position - 1).getCurrent_date().equals(readyStockList.get(position).getCurrent_date())) {
                holder.binding.viewDate.setVisibility(View.VISIBLE);
                holder.binding.textTimeLine.setText(readyStockModel.getCurrent_date());
//                holder.binding.textTimeLine.startAnimation(animation);
            } else {
                holder.binding.viewDate.setVisibility(View.GONE);
            }
        }

        holder.binding.textDesignNo.setText(readyStockModel.getDesign_no());
        holder.binding.textShadeNo.setText(readyStockModel.getShade_no());
//        holder.binding.textDate.setText(readyStockModel.getCurrent_date());


        holder.binding.textQty.setOnClickListener(view -> {

            if (holder.binding.qtyEditContainer.getVisibility() == View.VISIBLE) {
                holder.binding.qtyEditContainer.setVisibility(View.GONE);
            } else {
                holder.binding.qtyEditContainer.setVisibility(View.VISIBLE);
                holder.binding.textQty.setVisibility(View.GONE);
                holder.binding.editQty.setText(holder.binding.textQty.getText().toString());
                holder.binding.editQty.requestFocus();
            }

        });

        holder.binding.imgDone.setOnClickListener(view -> {

            DAOExtraOrder dao = new DAOExtraOrder(activity);
            if (!holder.binding.editQty.getText().toString().replace("\\", "").equals("")) {
                dao.getReference().document(readyStockList.get(position).getId()).update("quantity", Integer.parseInt(holder.binding.editQty.getText().toString().replace("\\", ""))).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        holder.binding.textQty.setVisibility(View.VISIBLE);
                        holder.binding.qtyEditContainer.setVisibility(View.GONE);
                        holder.binding.textQty.setText(holder.binding.editQty.getText().toString());
                        holder.binding.editQty.clearFocus();
                        holder.binding.editQty.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        holder.binding.qtyEditContainer.setVisibility(View.GONE);
                    }
                });
            } else {

                holder.binding.editQty.clearFocus();
                holder.binding.qtyEditContainer.setVisibility(View.GONE);
                holder.binding.textQty.setVisibility(View.VISIBLE);
            }
        });

        holder.binding.textQty.setText("\\" + readyStockModel.getQuantity());


        holder.binding.editQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // prepend slash if text is empty or doesn't start with slash
                if (s.length() == 0 || s.charAt(0) != '\\') {
                    holder.binding.editQty.setText("\\" + s);
                    holder.binding.editQty.setSelection(holder.binding.editQty.getText().length()); // move cursor to end
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                // do nothing
            }
        });

        holder.binding.btnEdit.setOnClickListener(view -> {

            if (isUpdatePermissionGranted(PermissionState.READY_STOCK.getValue())) {

                Bundle bundle = new Bundle();
                bundle.putString("tracker", "edit");
                bundle.putSerializable("extra_order_data", readyStockList.get(position));
                activity.startActivity(new Intent(activity, ReadyStockActivity.class).putExtras(bundle));

            } else {
                DialogUtil.showAccessDeniedDialog(activity);
            }

        });

        holder.binding.btnDelete.setOnClickListener(view -> {

            if (isDeletePermissionGranted(PermissionState.READY_STOCK.getValue())) {
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
                    daoExtraOrder.delete(readyStockList.get(position).getId()).addOnSuccessListener(runnable -> {
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

        if (isDeletePermissionGranted(PermissionState.READY_STOCK.getValue())) {
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
                for (int i = 0; i < readyStockList.size(); i++) {
                    if (itemModels.get(i).isSelected()) {
                        selected_item.add(itemModels.get(i).getPosition());
                    }
                }

                daoExtraOrder.removeSelectedItem(selected_item);

//                Collections.sort(selected_item);
//                daoExtraOrder.getReference().runTransaction(new Transaction.Handler() {
//                    @Override
//                    public Transaction.Result doTransaction(MutableData mutableData) {
//                        for (int i = selected_item.size() - 1; i >= 0; i--) {
//                            mutableData.child(readyStockList.get(selected_item.get(i)).getId()).setValue(null);
//                        }
//                        return Transaction.success(mutableData);
//                    }
//
//                    @Override
//                    public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
//                        if (databaseError == null && committed) {
//                            DialogUtil.showDeleteDialog(activity);
//                            onSelectionCountListeners.onCountChanged(true, 0, false);
//                        } else {
//                            new CustomSnackUtil().showSnack(activity, "Something Want Wrong", R.drawable.error_msg_icon);
//                        }
//                    }
//                });
            });

            dialog.show();
        } else {
            DialogUtil.showAccessDeniedDialog(activity);
        }
    }

    @Override
    public int getItemCount() {
        return readyStockList.size();
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

            daoExtraOrder.delete(child).addOnSuccessListener(runnable -> {
                DialogUtil.showDeleteDialog(activity);
            });

        });

        dialog.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ExtraOrderItemBinding binding;

        public ViewHolder(@Nonnull ExtraOrderItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}