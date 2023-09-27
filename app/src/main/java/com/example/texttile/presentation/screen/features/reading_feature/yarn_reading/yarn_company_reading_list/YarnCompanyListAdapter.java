package com.example.texttile.presentation.screen.features.reading_feature.yarn_reading.yarn_company_reading_list;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import javax.annotation.Nonnull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.CompanyListItemBinding;
import com.example.texttile.data.dao.DaoYarnMaster;
import com.example.texttile.data.model.YarnCompanyModel;

import java.util.ArrayList;

public class YarnCompanyListAdapter extends RecyclerView.Adapter<YarnCompanyListAdapter.ViewHolder> {

    ArrayList<YarnCompanyModel> filterYarnList;
    String id;
    Activity activity;
    DaoYarnMaster daoYarnMaster;

    public YarnCompanyListAdapter(ArrayList<YarnCompanyModel> filterYarnList, String id, Activity activity) {
        this.filterYarnList = filterYarnList;
        this.id = id;
        this.activity = activity;
        daoYarnMaster = new DaoYarnMaster(activity);
    }

    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        CompanyListItemBinding binding = CompanyListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@Nonnull ViewHolder holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.scale_up);
        holder.binding.layoutMain.startAnimation(animation);

        holder.binding.imgFav.setVisibility(View.VISIBLE);
        holder.binding.viewInput.setVisibility(View.VISIBLE);

        holder.binding.txtCompanyName.setText(filterYarnList.get(position).getCompany_name());
        holder.binding.yarnCompanyShadeNo.setText(filterYarnList.get(position).getCompany_shade_no());

        if (filterYarnList.get(position).isFav()) {
            holder.binding.imgFav.setImageResource(R.drawable.fav);
        } else {
            holder.binding.imgFav.setImageResource(R.drawable.un_fav);
        }
        holder.itemView.setOnClickListener(view -> {
        });
    }

    @Override
    public int getItemCount() {
        return filterYarnList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CompanyListItemBinding binding;

        public ViewHolder(@Nonnull CompanyListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
