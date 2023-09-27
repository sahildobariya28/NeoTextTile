package com.example.texttile.presentation.screen.master.design_master.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.annotation.Nonnull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.SelectedItemBinding;
import com.example.texttile.data.model.JalaWithFileModel;

import java.util.ArrayList;

public class JalaSelectedListAdapter extends RecyclerView.Adapter<JalaSelectedListAdapter.ViewHolder> {

    ArrayList<JalaWithFileModel> jalaWithFileModelsList;
    Context context;
    OnClickImageSelectButton onClickImageSelectButton;

    public interface OnClickImageSelectButton{
        void onAddImage(int position);
        void onDeleteImage(int position);
    }
    public JalaSelectedListAdapter(ArrayList<JalaWithFileModel> jalaWithFileModelsList, Context context, OnClickImageSelectButton onClickImageSelectButton) {
        this.jalaWithFileModelsList = jalaWithFileModelsList;
        this.context = context;
        this.onClickImageSelectButton = onClickImageSelectButton;

    }

    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        SelectedItemBinding binding = SelectedItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@Nonnull ViewHolder holder, int position) {

        if (jalaWithFileModelsList.get(position).isReady()){
            holder.binding.btnDelete.setVisibility(View.VISIBLE);
            holder.binding.btnSelectImg.setImageResource(R.drawable.btn_show);
        }else {
            holder.binding.btnDelete.setVisibility(View.VISIBLE);
            holder.binding.btnSelectImg.setImageResource(R.drawable.btn_add);
        }

        holder.binding.textJalaName.setText(jalaWithFileModelsList.get(position).getJala_name());
        holder.binding.btnSelectImg.setOnClickListener(view -> {
                onClickImageSelectButton.onAddImage(position);
        });

        holder.binding.btnDelete.setOnClickListener(view -> {
            if (jalaWithFileModelsList.get(position).isReady()){
                onClickImageSelectButton.onDeleteImage(position);
            }else {
                jalaWithFileModelsList.remove(position);
                notifyDataSetChanged();
            }
        });
        holder.itemView.setOnClickListener(view -> {

        });

    }

    @Override
    public int getItemCount() {
        return jalaWithFileModelsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        SelectedItemBinding binding;
        public ViewHolder(@Nonnull SelectedItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }
    }
}
