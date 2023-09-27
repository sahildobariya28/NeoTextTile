package com.example.texttile.presentation.screen.master.employee_master;

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

import com.bumptech.glide.Glide;
import com.example.texttile.R;
import com.example.texttile.presentation.ui.activity.ImagePreviewActivity;
import com.example.texttile.databinding.EmployeeMasterItemBinding;
import com.example.texttile.data.dao.DAOEmployeeMaster;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.LoadFragmentChangeListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.data.model.EmployeeMasterModel;
import com.example.texttile.data.model.ItemState;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.SwipeRevealLayout;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.ViewBinderHelper;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class EmployeeMasterAdapter extends RecyclerView.Adapter<EmployeeMasterAdapter.ViewHolder> {

    Activity activity;
    int old_item = -1, new_item = -1;
    LoadFragmentChangeListener loadFragmentChangeListener;
    EmptyDataListener emptyDataListener;
    DAOEmployeeMaster daoEmployeeMaster;
    ArrayList<EmployeeMasterModel> employeeMasterList;
    ArrayList<ItemState> itemModels = new ArrayList<>();
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public boolean isSelectionMode = false;
    OnSelectionCountListeners onSelectionCountListeners;

    public EmployeeMasterAdapter(ArrayList<EmployeeMasterModel> employeeMasterList, Activity activity, LoadFragmentChangeListener loadFragmentChangeListener, EmptyDataListener emptyDataListener, OnSelectionCountListeners onSelectionCountListeners, DAOEmployeeMaster daoEmployeeMaster) {


        this.employeeMasterList = employeeMasterList;
        this.activity = activity;
        this.loadFragmentChangeListener = loadFragmentChangeListener;
        this.emptyDataListener = emptyDataListener;
        this.daoEmployeeMaster = daoEmployeeMaster;
        this.onSelectionCountListeners = onSelectionCountListeners;

        emptyDataListener.onDataChanged(true, getItemCount());
        itemModels.clear();
        for (int i = 0; i < employeeMasterList.size(); i++) {
            itemModels.add(new ItemState(false, false, false, i));
        }

        binderHelper.setOpenOnlyOne(true);
    }


    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        EmployeeMasterItemBinding binding = EmployeeMasterItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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

        EmployeeMasterModel employeeMasterModel = employeeMasterList.get(position);
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

        holder.binding.textPhoneNo.setText(String.valueOf(employeeMasterModel.getPhone_no()));
        holder.binding.textEmployeeName.setText(String.valueOf(employeeMasterModel.getEmployee_name()));
        holder.binding.textAccount.setText(String.valueOf(employeeMasterModel.getAccount_no()));
        holder.binding.textIfsc.setText(String.valueOf(employeeMasterModel.getIfsc_code()));
        holder.binding.textBankName.setText(String.valueOf(employeeMasterModel.getBank_name()));
        holder.binding.textBankHolderName.setText(String.valueOf(employeeMasterModel.getBank_holder_name()));

        Glide.with(activity).load(employeeMasterModel.getEmployee_photo()).placeholder(R.drawable.no_image).into(holder.binding.imgPhoto);
        Glide.with(activity).load(employeeMasterModel.getId_proof()).placeholder(R.drawable.no_image).into(holder.binding.imgId);

        holder.binding.imgPhoto.setOnClickListener(view -> {
            activity.startActivity(new Intent(activity, ImagePreviewActivity.class).putExtra("tracker", "edit").putExtra("img_uri", employeeMasterModel.getEmployee_photo()));
        });
        holder.binding.imgId.setOnClickListener(view -> {
            activity.startActivity(new Intent(activity, ImagePreviewActivity.class).putExtra("tracker", "edit").putExtra("img_uri", employeeMasterModel.getId_proof()));
        });

        holder.itemView.setOnClickListener(view -> {
            expand_item(position, holder.binding.btnDown);
        });

        holder.binding.btnEdit.setOnClickListener(view -> {

            if (isUpdatePermissionGranted(PermissionState.EMPLOYEE_MASTER.getValue())) {
                Bundle bundle = new Bundle();
                bundle.putString("tracker", "edit");
                bundle.putSerializable("employee_data", employeeMasterList.get(position));
                activity.startActivity(new Intent(activity, EmployeeMasterActivity.class).putExtras(bundle));
            } else {
                DialogUtil.showAccessDeniedDialog(activity);
            }
        });

        holder.binding.btnDelete.setOnClickListener(view -> {
            if (isDeletePermissionGranted(PermissionState.EMPLOYEE_MASTER.getValue())) {
                deleteItem(holder.getAdapterPosition());
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
        if (isDeletePermissionGranted(PermissionState.EMPLOYEE_MASTER.getValue())) {
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
                for (int i = 0; i < employeeMasterList.size(); i++) {
                    if (itemModels.get(i).isSelected()) {
                        selected_item.add(itemModels.get(i).getPosition());
                    }
                }
                daoEmployeeMaster.removeSelectedItem(selected_item);
//                Collections.sort(selected_item);
//                new DAOEmployeeMaster(activity).getReference().runTransaction(new Transaction.Handler() {
//                    @Override
//                    public Transaction.Result doTransaction(MutableData mutableData) {
//                        for (int i = selected_item.size() - 1; i >= 0; i--) {
//                            try {
//                                FirebaseStorage.getInstance().getReferenceFromUrl(employeeMasterList.get(selected_item.get(i)).getEmployee_photo()).delete();
//                                FirebaseStorage.getInstance().getReferenceFromUrl(employeeMasterList.get(selected_item.get(i)).getId_proof()).delete();
//                            } catch (Exception e) {
//
//                            }
//                            mutableData.child(employeeMasterList.get(selected_item.get(i)).getId()).setValue(null);
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


    public void deleteItem(int pos) {

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

            if (!employeeMasterList.get(pos).getEmployee_photo().equals("null")){
                daoEmployeeMaster.deleteFile(employeeMasterList.get(pos).getEmployee_photo());
            }
            if (!employeeMasterList.get(pos).getId_proof().equals("null")){
                daoEmployeeMaster.deleteFile(employeeMasterList.get(pos).getId_proof());
            }

            daoEmployeeMaster.delete(employeeMasterList.get(pos).getId()).addOnSuccessListener(runnable -> {
                DialogUtil.showDeleteDialog(activity);
            });
        });

        dialog.show();

    }

    @Override
    public int getItemCount() {
        return employeeMasterList.size();
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

        EmployeeMasterItemBinding binding;

        public ViewHolder(@Nonnull EmployeeMasterItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }
    }
}
