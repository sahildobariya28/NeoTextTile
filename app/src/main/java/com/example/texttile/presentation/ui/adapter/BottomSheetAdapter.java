package com.example.texttile.presentation.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import javax.annotation.Nonnull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.ButtonItemBinding;
import com.example.texttile.data.model.OptionModel;

import java.util.ArrayList;

public class BottomSheetAdapter extends RecyclerView.Adapter<BottomSheetAdapter.ViewHolder> {

    Context context;
    ArrayList<OptionModel> optionList;
    AddOnAddButtonClickListener addOnAddButtonClickListener;

    public interface AddOnAddButtonClickListener {
        void onAddButtonClick(int position, String title);
    }

    public BottomSheetAdapter(Context context, ArrayList<OptionModel> optionList, AddOnAddButtonClickListener addOnAddButtonClickListener) {
        this.context = context;
        this.optionList = optionList;
        this.addOnAddButtonClickListener = addOnAddButtonClickListener;
    }

    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        ButtonItemBinding binding = ButtonItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@Nonnull ViewHolder holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale);
        holder.binding.btnDesignMaster.startAnimation(animation);
        holder.binding.btnImg.setImageResource(optionList.get(position).getImage());
        holder.binding.textTitle.setText(optionList.get(position).getName());

        holder.itemView.setOnClickListener(view -> {
            addOnAddButtonClickListener.onAddButtonClick(optionList.get(position).getId(), holder.binding.textTitle.getText().toString());
        });
    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ButtonItemBinding binding;


        public ViewHolder(@Nonnull ButtonItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
