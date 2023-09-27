package com.example.texttile.presentation.screen.features.storage_feature.storage_file;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import javax.annotation.Nonnull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.StorageItemBinding;
import com.example.texttile.data.model.DesignMasterModel;

import java.util.ArrayList;

public class StorageAdapter extends RecyclerView.Adapter<StorageAdapter.ViewHolder> {

    Activity activity;
    ArrayList<DesignMasterModel> designMasterModels = new ArrayList<>();
    int old_item = -1, new_item = -1;
    ArrayList<Boolean> expandedItemModels = new ArrayList<>();

    public StorageAdapter(Activity activity, ArrayList<DesignMasterModel> designMasterModels){
        this.activity = activity;
        this.designMasterModels = designMasterModels;
        for (int i = 0; i < designMasterModels.size(); i++) {
            expandedItemModels.add(false);
        }
    }

    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        StorageItemBinding binding = StorageItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@Nonnull ViewHolder holder, int position) {
        if (expandedItemModels.get(position)) {
            holder.binding.detailContainer.setVisibility(View.VISIBLE);
            holder.binding.btnDown.setImageResource(R.drawable.up);
        } else {
            holder.binding.detailContainer.setVisibility(View.GONE);
            holder.binding.btnDown.setImageResource(R.drawable.down);
        }

        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.scale_up);
        holder.binding.layoutMain.startAnimation(animation);


        holder.binding.textDesignNo.setText(designMasterModels.get(position).getDesign_no());

        StorageImageAdapter storageImageAdapter = new StorageImageAdapter(activity, designMasterModels.get(position).getJalaWithFileModelList());
        holder.binding.recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
        holder.binding.recyclerView.setAdapter(storageImageAdapter);


        holder.itemView.setOnClickListener(view -> {

            expand_item(position, holder.binding.btnDown);

        });
    }

    @Override
    public int getItemCount() {
        return designMasterModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        StorageItemBinding binding;

        public ViewHolder(@Nonnull StorageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public void expand_item(int current_item, ImageView btn_down) {
        if (new_item != -1) {
            if (new_item != current_item) {
                old_item = new_item;

                expandedItemModels.set(old_item, false);
                btn_down.setImageResource(R.drawable.down);
                new_item = current_item;
                expandedItemModels.set(new_item, true);
                btn_down.setImageResource(R.drawable.up);
                notifyItemChanged(old_item);
                notifyItemChanged(new_item);

            } else {
                old_item = new_item;
                if (expandedItemModels.get(new_item)) {
                    expandedItemModels.set(new_item, false);
                    btn_down.setImageResource(R.drawable.down);
                } else {
                    expandedItemModels.set(new_item, true);
                    btn_down.setImageResource(R.drawable.up);

                }
                new_item = current_item;
                notifyItemChanged(old_item);
                notifyItemChanged(new_item);
            }

        } else {
            new_item = current_item;
            expandedItemModels.set(new_item, true);
            btn_down.setImageResource(R.drawable.up);
            notifyItemChanged(old_item);
            notifyItemChanged(new_item);
        }
    }
}
