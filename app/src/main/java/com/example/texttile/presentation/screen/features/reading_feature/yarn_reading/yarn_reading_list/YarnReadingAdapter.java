package com.example.texttile.presentation.screen.features.reading_feature.yarn_reading.yarn_reading_list;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isUpdatePermissionGranted;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.YarnReadingItemBinding;
import com.example.texttile.data.dao.DaoYarnMaster;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.data.model.ItemState;
import com.example.texttile.data.model.YarnMasterModel;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.SwipeRevealLayout;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.ViewBinderHelper;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.ExCoAnim;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

public class YarnReadingAdapter extends RecyclerView.Adapter<YarnReadingAdapter.ViewHolder> {

    Activity activity;
    LinearLayout old_item, new_item;
    EmptyDataListener emptyDataListener;
    ArrayList<YarnMasterModel> yarnMasterList;

    ArrayList<ItemState> itemModels = new ArrayList<>();
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public boolean isSelectionMode = false;


    public YarnReadingAdapter(ArrayList<YarnMasterModel> yarnMasterList, Activity activity, EmptyDataListener emptyDataListener) {
        this.yarnMasterList = yarnMasterList;
        this.activity = activity;
        this.emptyDataListener = emptyDataListener;

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
        YarnReadingItemBinding binding = YarnReadingItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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

        binderHelper.bind(holder.binding.swipeRevealLayout, String.valueOf(position));

        YarnMasterModel yarnMasterModel = yarnMasterList.get(position);
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

        holder.binding.txtTitle.setText(String.valueOf(yarnMasterModel.getYarn_name()));
        holder.binding.yarnType.setText(yarnMasterModel.getYarn_type());
        holder.binding.yarnQty.setText(String.valueOf(yarnMasterModel.getQty()));

        holder.binding.btnReading.setOnClickListener(view -> {


            if (isUpdatePermissionGranted(PermissionState.YARN_READING.getValue())) {
                yarn_reading_Dialog(yarnMasterList.get(position), yarnMasterList.get(position).getId());
            } else{
                DialogUtil.showAccessDeniedDialog(activity);
            }

        });
    }


    @Override
    public int getItemCount() {
        return yarnMasterList.size();
    }

    private void yarn_reading_Dialog(@Nonnull YarnMasterModel yarnMasterModel, String id) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_yarn_reading);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        EditText edit_qty = dialog.findViewById(R.id.edit_qty);
        TextView btn_add = dialog.findViewById(R.id.btn_add);
        TextView btn_cancel = dialog.findViewById(R.id.btn_cancel);
        TextView text_available_qty = dialog.findViewById(R.id.text_available_qty);

        text_available_qty.setText("\\" + yarnMasterModel.getQty());

        btn_add.setOnClickListener(view -> {

            if (!edit_qty.getText().toString().isEmpty()) {

                addArtist(edit_qty.getText().toString(), dialog, id);

            }
        });
        btn_cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });


        dialog.show();
    }


    private void addArtist(String readingQty, Dialog dialog, String id) {

//        DatabaseReference databaseArtists = new DaoYarnMaster(activity).getReference();

        if (TextUtils.isEmpty(readingQty)) {
            new CustomSnackUtil().showSnack(activity, "Please enter a Reading Qty", R.drawable.error_msg_icon);
        } else {


            Map<String, Object> data = new HashMap<>();
            data.put("qty", Integer.valueOf(readingQty));

            new DaoYarnMaster(activity).getReference().document(id).update(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // handle failure
                        }
                    });

//            databaseArtists.child(id).child("qty").setValue(Integer.valueOf(readingQty)).addOnSuccessListener(runnable -> {
//                dialog.dismiss();
//
//            }).addOnFailureListener(runnable -> {
//
//            });

            dialog.dismiss();
        }
    }

    public void expand_item(LinearLayout linearLayout, ImageView btn_down, TextView btn_update) {
        ExCoAnim exCoAnim = new ExCoAnim();


        if (new_item != null) {
            if (new_item != linearLayout) {
                old_item = new_item;

                btn_down.setVisibility(View.VISIBLE);
                btn_update.setVisibility(View.GONE);
                exCoAnim.collapse(old_item);
                btn_down.setImageResource(R.drawable.down);
                new_item = linearLayout;
                exCoAnim.expand(new_item);
                btn_down.setVisibility(View.GONE);
                btn_update.setVisibility(View.VISIBLE);


            } else {
                old_item = new_item;
                if (new_item.getVisibility() == View.VISIBLE) {
                    exCoAnim.collapse(new_item);
                    btn_down.setVisibility(View.VISIBLE);
                    btn_update.setVisibility(View.GONE);
                    btn_down.setImageResource(R.drawable.down);
                } else {
                    exCoAnim.expand(new_item);
                    btn_down.setVisibility(View.GONE);
                    btn_update.setVisibility(View.VISIBLE);
                }
                new_item = linearLayout;
            }

        } else {
            new_item = linearLayout;
            exCoAnim.expand(new_item);
            btn_down.setVisibility(View.GONE);
            btn_update.setVisibility(View.VISIBLE);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        YarnReadingItemBinding binding;

        public ViewHolder(@Nonnull YarnReadingItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }
    }
}
