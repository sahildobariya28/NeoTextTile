package com.example.texttile.presentation.screen.master.design_master.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.databinding.FItemForDesignBinding;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class FAdapter extends RecyclerView.Adapter<FAdapter.ViewHolder> {

    ArrayList<Integer> list = new ArrayList<>();

    public FAdapter(ArrayList<Integer> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FItemForDesignBinding binding = FItemForDesignBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FAdapter.ViewHolder holder, int position) {
        if (position == list.size()) {
            if (position == 8) {
                holder.binding.mainView.setVisibility(View.GONE);
                holder.binding.btnAdd.setVisibility(View.GONE);
            } else {
                holder.binding.mainView.setVisibility(View.INVISIBLE);
                holder.binding.btnAdd.setVisibility(View.VISIBLE);
            }
        } else {
            holder.binding.mainView.setVisibility(View.VISIBLE);
            holder.binding.btnAdd.setVisibility(View.INVISIBLE);
            holder.binding.fTitle.setText("F" + (int) (position + 1));
            if (list.get(position) != null) {
                holder.binding.autoF1.setText("" + list.get(position));
            } else {
                holder.binding.autoF1.setHint("Enter F" + (int) (position + 1));

            }
        }

        holder.binding.btnAdd.setOnClickListener(view -> {
            if (list.size() <= 7) {
                if (position == list.size()) {
                    list.add(0);
                    notifyDataSetChanged();
                }
            }
        });

        holder.binding.autoF1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    list.set(holder.getAdapterPosition(), Integer.parseInt(charSequence.toString().trim()));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.binding.btnDeleteFItem.setOnClickListener(view -> {
            if (list.size() != 0 && list.size() != 1) {
                list.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        FItemForDesignBinding binding;

        public ViewHolder(FItemForDesignBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}