package com.example.texttile.presentation.screen.master.shade_master.list;

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
import com.example.texttile.data.dao.DAOShadeMaster;
import com.example.texttile.data.model.ItemState;
import com.example.texttile.data.model.ShadeMasterModel;
import com.example.texttile.databinding.FilterShadeMasterItemBinding;
import com.example.texttile.databinding.ShadeMasterItemBinding;
import com.example.texttile.presentation.screen.master.shade_master.shade_master.ShadeMultipleActivity;
import com.example.texttile.presentation.screen.master.shade_master.sublist.ShadeMasterListActivity;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.LoadFragmentChangeListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.SwipeRevealLayout;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.ViewBinderHelper;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class FilterShadeMasterAdapter extends RecyclerView.Adapter<FilterShadeMasterAdapter.ViewHolder>{

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


    public FilterShadeMasterAdapter(ArrayList<ShadeMasterModel> shadeMasterList, Activity activity, LoadFragmentChangeListener loadFragmentChangeListener, EmptyDataListener emptyDataListener, OnSelectionCountListeners onSelectionCountListeners, DAOShadeMaster daoShadeMaster) {

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
        FilterShadeMasterItemBinding binding = FilterShadeMasterItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@Nonnull ViewHolder holder, int position) {
        ShadeMasterModel shadeMasterModel = shadeMasterList.get(position);
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.scale_up);
        holder.binding.viewMain.startAnimation(animation);

        holder.binding.viewMain.setOnClickListener(view -> {
            activity.startActivity(new Intent(activity, ShadeMasterListActivity.class).putExtra("tracker", shadeMasterModel.getDesign_no()));
        });

        holder.binding.textDesignNo.setText(shadeMasterModel.getDesign_no());

    }






    @Override
    public int getItemCount() {
        return shadeMasterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        FilterShadeMasterItemBinding binding;

        public ViewHolder(@Nonnull FilterShadeMasterItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
