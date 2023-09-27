package com.example.texttile.presentation.screen.master.yarn_master;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isDeletePermissionGranted;
import static com.example.texttile.presentation.ui.util.PermissionUtil.isReadPermissionGranted;
import static com.example.texttile.presentation.ui.util.PermissionUtil.isUpdatePermissionGranted;
import static com.example.texttile.presentation.ui.util.PrinterUtils.printBluetooth;

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
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.YarnMasterItemBinding;
import com.example.texttile.data.dao.DaoYarnMaster;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.data.model.ItemState;
import com.example.texttile.data.model.YarnMasterModel;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.SwipeRevealLayout;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.ViewBinderHelper;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class YarnMasterAdapter extends RecyclerView.Adapter<YarnMasterAdapter.ViewHolder> {

    Activity activity;
    int old_item = -1, new_item = -1;
    EmptyDataListener emptyDataListener;
    ArrayList<YarnMasterModel> yarnMasterList;

    ArrayList<ItemState> itemModels = new ArrayList<>();
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public boolean isSelectionMode = false;
    OnSelectionCountListeners onSelectionCountListeners;
    DaoYarnMaster daoYarnMaster;

    public YarnMasterAdapter(ArrayList<YarnMasterModel> yarnMasterList, Activity activity, EmptyDataListener emptyDataListener, OnSelectionCountListeners onSelectionCountListeners, DaoYarnMaster daoYarnMaster) {
        this.yarnMasterList = yarnMasterList;
        this.activity = activity;
        this.emptyDataListener = emptyDataListener;
        this.onSelectionCountListeners = onSelectionCountListeners;
        this.daoYarnMaster = daoYarnMaster;

        emptyDataListener.onDataChanged(true, getItemCount());
        itemModels.clear();
        for (int i = 0; i < yarnMasterList.size(); i++) {
            itemModels.add(new ItemState(false, false, false, i));
        }
        binderHelper.setOpenOnlyOne(true);
    }

    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        YarnMasterItemBinding binding = YarnMasterItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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

        YarnMasterModel yarnMasterModel = yarnMasterList.get(position);
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
        holder.binding.textYarnName.setText(String.valueOf(yarnMasterModel.getYarn_name()));
        holder.binding.textDenier.setText(String.valueOf(yarnMasterModel.getDenier()));
        holder.binding.yarnType.setText(yarnMasterModel.getYarn_type());


        holder.itemView.setOnClickListener(view -> {
            expand_item(position, holder.binding.btnDown);
        });

        holder.binding.btnEdit.setOnClickListener(view -> {
            if (isUpdatePermissionGranted(PermissionState.DESIGN_MASTER.getValue())) {
                Bundle bundle = new Bundle();
                bundle.putString("tracker", "edit");
                bundle.putSerializable("yarn_data", yarnMasterList.get(position));
                activity.startActivity(new Intent(activity, YarnMasterActivity.class).putExtras(bundle));

            } else {
                DialogUtil.showAccessDeniedDialog(activity);
            }
        });

        holder.binding.btnDelete.setOnClickListener(view -> {
            if (isDeletePermissionGranted(PermissionState.DESIGN_MASTER.getValue())) {
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
                    daoYarnMaster.delete(yarnMasterList.get(position).getId()).addOnSuccessListener(runnable -> {
                        DialogUtil.showDeleteDialog(activity);
                    });
                });

                dialog.show();
            } else {
                DialogUtil.showAccessDeniedDialog(activity);
            }
        });

        holder.binding.btnPrint.setOnClickListener(view -> {
            if (isReadPermissionGranted(PermissionState.DESIGN_MASTER.getValue())) {
                final Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.dialog_not_qr_match);
                dialog.setCancelable(true);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                CardView btn_print_list = dialog.findViewById(R.id.btn_cancel);
                ImageView img_main = dialog.findViewById(R.id.img_main);
                TextView text_title = dialog.findViewById(R.id.text_title);
                TextView text_cancel = dialog.findViewById(R.id.text_cancel);
                text_title.setText("Print Yarn Company\nList ??");
                text_cancel.setText("Print");
                img_main.setImageResource(R.drawable.print_illustration);

                btn_print_list.setOnClickListener(view1 -> {
                                        setupPrinter("Yarn Master", yarnMasterList.get(position));
                                        dialog.dismiss();
                });
                dialog.show();
            } else {
                DialogUtil.showAccessDeniedDialog(activity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return yarnMasterList.size();
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
                for (int i = 0; i < yarnMasterList.size(); i++) {
                    if (itemModels.get(i).isSelected()) {
                        selected_item.add(itemModels.get(i).getPosition());
                    }
                }

                daoYarnMaster.removeSelectedItem(selected_item);
            });

            dialog.show();
        } else {
            DialogUtil.showAccessDeniedDialog(activity);
        }

    }

    public void setupPrinter(String heading, YarnMasterModel yarnMasterModel) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("[C]<b><font size='big'>" + heading + "</font></b>\n\n\n");


        StringBuilder employee_allot_item_list = new StringBuilder();
        for (int i = 0; i < yarnMasterModel.getYarnCompanyList().size(); i++) {
            employee_allot_item_list.append("[L]<font size='normal'>" + yarnMasterModel.getYarnCompanyList().get(i).getCompany_name() + "</font>[L]<font size='normal'>" + yarnMasterModel.getYarnCompanyList().get(i).getCompany_shade_no() + "</font> " + "\n");
        }
        if (!employee_allot_item_list.toString().isEmpty()) {
            stringBuilder.append("[L]<font size='normal'><u><b>Company Name</b></u></font> [L]<font size='normal'><u><b>Company Shade</b></u></font>\n");
            stringBuilder.append("[C]------------------------------------------------\n");

            stringBuilder.append(employee_allot_item_list.toString());
            stringBuilder.append("[C]\n");
            stringBuilder.append("[C]\n");
            stringBuilder.append("[C]\n");
            stringBuilder.append("[C]\n");
            stringBuilder.append("[C]\n");
            stringBuilder.append("[C]\n");

            printBluetooth(activity, stringBuilder.toString());
        } else {
            new CustomSnackUtil().showSnack(activity, "No Data Found", R.drawable.error_msg_icon);
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

        YarnMasterItemBinding binding;

        public ViewHolder(@Nonnull YarnMasterItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }
    }
}
