package com.example.texttile.presentation.screen.authority.permission;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import javax.annotation.Nonnull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.ItemPermissionBinding;
import com.example.texttile.data.model.UserDataModel;

public class PermissionCategoryAdapter extends RecyclerView.Adapter<PermissionCategoryAdapter.ViewHolder> {

    Context context;
    UserDataModel userDataModel;

    public PermissionCategoryAdapter(Context context, UserDataModel userDataModel) {
        this.context = context;
        this.userDataModel = userDataModel;
    }

    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        ItemPermissionBinding binding = ItemPermissionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@Nonnull ViewHolder holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale_up);
        holder.binding.layoutMain.startAnimation(animation);


            holder.binding.radioRead.setChecked(userDataModel.getPermissionList().get(position).isRead());
            holder.binding.radioInsert.setChecked(userDataModel.getPermissionList().get(position).isInsert());
            holder.binding.radioUpdate.setChecked(userDataModel.getPermissionList().get(position).isUpdate());
            holder.binding.radioDelete.setChecked(userDataModel.getPermissionList().get(position).isDelete());
            holder.binding.radioPrice.setChecked(userDataModel.getPermissionList().get(position).isPrice());

        if (position == 7) {
            holder.binding.radioPrice.setVisibility(View.VISIBLE);
            holder.binding.radioPrice.setEnabled(true);
        } else {
            holder.binding.radioPrice.setEnabled(false);
        }

        holder.binding.textTitle.setText(userDataModel.getPermissionList().get(position).getName());

        holder.binding.radioRead.setOnClickListener(view -> {
            if (holder.binding.radioRead.isChecked()) {
                holder.binding.radioRead.setChecked(true);
                userDataModel.getPermissionList().get(position).setRead(true);
            } else {
                holder.binding.radioRead.setChecked(false);
                holder.binding.radioInsert.setChecked(false);
                holder.binding.radioUpdate.setChecked(false);
                holder.binding.radioDelete.setChecked(false);
                holder.binding.radioPrice.setChecked(false);
                userDataModel.getPermissionList().get(position).setRead(false);
                userDataModel.getPermissionList().get(position).setInsert(false);
                userDataModel.getPermissionList().get(position).setUpdate(false);
                userDataModel.getPermissionList().get(position).setDelete(false);
                userDataModel.getPermissionList().get(position).setPrice(false);

            }
        });

        holder.binding.radioInsert.setOnClickListener(view -> {
            if (holder.binding.radioInsert.isChecked()) {
                if (!holder.binding.radioRead.isChecked()) {
                    holder.binding.radioRead.setChecked(true);
                    holder.binding.radioInsert.setChecked(true);
                    userDataModel.getPermissionList().get(position).setRead(true);
                    userDataModel.getPermissionList().get(position).setInsert(true);
                } else {
                    holder.binding.radioInsert.setChecked(true);
                    userDataModel.getPermissionList().get(position).setInsert(true);
                }
            } else {
                holder.binding.radioInsert.setChecked(false);
                userDataModel.getPermissionList().get(position).setInsert(false);

            }
        });
        holder.binding.radioUpdate.setOnClickListener(view -> {
            if (holder.binding.radioUpdate.isChecked()) {
                if (!holder.binding.radioRead.isChecked()) {
                    holder.binding.radioRead.setChecked(true);
                    holder.binding.radioUpdate.setChecked(true);
                    userDataModel.getPermissionList().get(position).setRead(true);
                    userDataModel.getPermissionList().get(position).setUpdate(true);
                } else {
                    holder.binding.radioUpdate.setChecked(true);
                    userDataModel.getPermissionList().get(position).setUpdate(true);
                }
            } else {
                holder.binding.radioUpdate.setChecked(false);
                userDataModel.getPermissionList().get(position).setUpdate(false);

            }
        });
        holder.binding.radioDelete.setOnClickListener(view -> {
            if (holder.binding.radioDelete.isChecked()) {
                if (!holder.binding.radioRead.isChecked()) {
                    holder.binding.radioRead.setChecked(true);
                    holder.binding.radioDelete.setChecked(true);
                    userDataModel.getPermissionList().get(position).setRead(true);
                    userDataModel.getPermissionList().get(position).setDelete(true);
                } else {
                    holder.binding.radioDelete.setChecked(true);
                    userDataModel.getPermissionList().get(position).setDelete(true);
                }
            } else {
                holder.binding.radioDelete.setChecked(false);
                userDataModel.getPermissionList().get(position).setDelete(false);

            }
        });

        holder.binding.radioPrice.setOnClickListener(view -> {
            if (holder.binding.radioPrice.isChecked()) {
                if (!holder.binding.radioRead.isChecked()) {
                    holder.binding.radioRead.setChecked(true);
                    holder.binding.radioPrice.setChecked(true);
                    userDataModel.getPermissionList().get(position).setRead(true);
                    userDataModel.getPermissionList().get(position).setPrice(true);
                } else {
                    holder.binding.radioPrice.setChecked(true);
                    userDataModel.getPermissionList().get(position).setPrice(true);
                }
            } else {
                holder.binding.radioPrice.setChecked(false);
                userDataModel.getPermissionList().get(position).setPrice(false);
            }
        });
    }

    public UserDataModel getList() {
        return userDataModel;
    }

    @Override
    public int getItemCount() {
        return userDataModel.getPermissionList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemPermissionBinding binding;
        public ViewHolder(@Nonnull ItemPermissionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
