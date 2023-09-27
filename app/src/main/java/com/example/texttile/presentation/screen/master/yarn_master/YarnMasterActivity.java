package com.example.texttile.presentation.screen.master.yarn_master;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import javax.annotation.Nonnull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.FragmentNewYarnBinding;
import com.example.texttile.databinding.YarnItemBinding;
import com.example.texttile.data.dao.DaoYarnMaster;
import com.example.texttile.data.model.YarnCompanyModel;
import com.example.texttile.data.model.YarnMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class YarnMasterActivity extends AppCompatActivity {

    public final int STEP1 = 1;
    public final int STEP2 = 2;
    public int CURRENT_STEP = 1;
    YarnMasterModel yarnMasterModel;
    ArrayList<YarnCompanyModel> filterYarnList = new ArrayList<>();
    String[] category = {"Not selected", "WARP", "WEFT"};
    String tracker;
    DaoYarnMaster dao;

    FragmentNewYarnBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentNewYarnBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dao = new DaoYarnMaster(this);

        initToolbar();
        initData();
        setUpStep1();

        binding.btnBack.setOnClickListener(view -> {
            if (CURRENT_STEP == 2) {
                setUpStep1();
            }
        });

        binding.btnNext.setOnClickListener(view -> {
            if (CURRENT_STEP == 1) {


                String yarn_name = binding.editYarnName.getText().toString().trim();
                String denier = binding.editDenier.getText().toString().trim();
                String qty = binding.editQty.getText().toString().trim();


                if (TextUtils.isEmpty(yarn_name)) {
                    new CustomSnackUtil().showSnack(this, "Please enter a Yarn Name", R.drawable.error_msg_icon);
                } else if (TextUtils.isEmpty(denier)) {
                    new CustomSnackUtil().showSnack(this, "Please enter a Denier", R.drawable.error_msg_icon);
                } else if (TextUtils.isEmpty(qty)) {
                    new CustomSnackUtil().showSnack(this, "Please enter Qty", R.drawable.error_msg_icon);
                } else if (binding.spinner.getSelectedItemPosition() == 0) {
                    new CustomSnackUtil().showSnack(this, "Please Select Yarn Type", R.drawable.error_msg_icon);
                } else {
                    yarnMasterModel.setYarn_name(yarn_name);
                    yarnMasterModel.setDenier(Integer.parseInt(denier));
                    yarnMasterModel.setYarn_type(category[binding.spinner.getSelectedItemPosition()]);
                    yarnMasterModel.setQty(Integer.parseInt(qty));
                    setUpStep2();
                }
            } else if (CURRENT_STEP == 2) {
                boolean is_valid = true;
                for (int i = 0; i < filterYarnList.size(); i++) {

                    if (filterYarnList.get(i).getCompany_name() == null) {
                        new CustomSnackUtil().showSnack(this, "Please Enter Company Name in Position" + (i + 1), R.drawable.error_msg_icon);
                        is_valid = false;
                        break;
                    } else if (filterYarnList.get(i).getCompany_name().isEmpty()) {
                        new CustomSnackUtil().showSnack(this, "Please Enter Company Name in Position" + (i + 1), R.drawable.error_msg_icon);
                        is_valid = false;
                        break;
                    } else if (filterYarnList.get(i).getCompany_shade_no() == null) {
                        new CustomSnackUtil().showSnack(this, "Please Enter Company Shade No in Position" + (i + 1), R.drawable.error_msg_icon);
                        is_valid = false;
                        break;
                    } else if (filterYarnList.get(i).getCompany_shade_no().isEmpty()) {
                        new CustomSnackUtil().showSnack(this, "Please Enter Company Shade No in Position" + (i + 1), R.drawable.error_msg_icon);
                        is_valid = false;
                        break;
                    }
                }
                if (is_valid) {
                    yarnMasterModel.setYarnCompanyList(filterYarnList);
                    addArtist();
                }
            }
        });
    }


    private void addArtist() {


        String id = dao.getId();
        DialogUtil.showProgressDialog(this);
        if (tracker.equals("edit")) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("yarn_name", yarnMasterModel.getYarn_name());
            hashMap.put("denier", yarnMasterModel.getDenier());
            hashMap.put("yarn_type", yarnMasterModel.getYarn_type());
            hashMap.put("qty", yarnMasterModel.getQty());
            hashMap.put("yarnCompanyList", yarnMasterModel.getYarnCompanyList());
            dao.update(yarnMasterModel.getId(), hashMap).addOnSuccessListener(runnable -> {
                DialogUtil.showUpdateSuccessDialog(this, true);
            });
        } else {

            yarnMasterModel.setId(id);
            dao.insert(id, yarnMasterModel).addOnSuccessListener(runnable -> {
                DialogUtil.showSuccessDialog(this, true);
            });

        }
    }

    public void setUpStep1() {
        viewVisibilityChanger(STEP1);

    }

    public void setUpStep2() {

        viewVisibilityChanger(STEP2);
        setAdapter();
    }

    public void viewVisibilityChanger(int step) {
        CURRENT_STEP = step;
        if (step == STEP1) {
            seekTrack(1);
            binding.btnBack.setVisibility(View.GONE);
            binding.viewYarn1.setVisibility(View.VISIBLE);
            binding.viewYarn2.setVisibility(View.GONE);
        } else {
            seekTrack(2);
            binding.btnBack.setVisibility(View.VISIBLE);
            binding.viewYarn1.setVisibility(View.GONE);
            binding.viewYarn2.setVisibility(View.VISIBLE);
        }

    }

    public void initData() {
        ArrayAdapter aa = new ArrayAdapter(this, R.layout.white_simple_spinner_item, category);
        aa.setDropDownViewResource(R.layout.white_simple_spinner_dropdown_item);
        binding.spinner.setAdapter(aa);

        tracker = (getIntent().getStringExtra("tracker") != null) ? getIntent().getStringExtra("tracker") : "add";
        yarnMasterModel = (tracker.equals("edit") && getIntent().getSerializableExtra("yarn_data") != null) ? (YarnMasterModel) getIntent().getSerializableExtra("yarn_data") : new YarnMasterModel();

        if (tracker != null && tracker.equals("edit")) {
            if (yarnMasterModel != null) {

                binding.editYarnName.setText(yarnMasterModel.getYarn_name());
                binding.editDenier.setText(String.valueOf(yarnMasterModel.getDenier()));
                binding.editQty.setText(String.valueOf(yarnMasterModel.getQty()));

                if (yarnMasterModel.getYarn_type().equals("WARP")) {
                    binding.spinner.setSelection(1);
                } else {
                    binding.spinner.setSelection(2);
                }
                filterYarnList = getFilteredList();
            }
        } else {
            if (yarnMasterModel != null) {
                filterYarnList = getFilteredList();
                binding.editYarnName.requestFocus();
            }
        }
    }

    public ArrayList<YarnCompanyModel> getFilteredList() {
        ArrayList<YarnCompanyModel> list = new ArrayList<>();
        if (tracker.equals("edit")) {
            ArrayList<YarnCompanyModel> favYarnList = new ArrayList<>();
            ArrayList<YarnCompanyModel> unFavYarnList = new ArrayList<>();
            for (int i = 0; i < yarnMasterModel.getYarnCompanyList().size(); i++) {
                if (yarnMasterModel.getYarnCompanyList().get(i).isFav()) {
                    favYarnList.add(yarnMasterModel.getYarnCompanyList().get(i));
                } else {
                    unFavYarnList.add(yarnMasterModel.getYarnCompanyList().get(i));
                }
            }
            if (favYarnList.size() != 0) {
                list.addAll(favYarnList);
            }
            if (unFavYarnList.size() != 0) {
                list.addAll(unFavYarnList);
            }
        } else {
            if (list != null && list.size() == 0) {
                list.add(new YarnCompanyModel());
            }
        }

        return list;
    }

    public void setAdapter() {
        Yarn2Adapter yarn2Adapter = new Yarn2Adapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(yarn2Adapter);
    }


    public void initToolbar() {
        binding.include.textTitle.setText("Yarn Master");
        binding.include.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    public class Yarn2Adapter extends RecyclerView.Adapter<Yarn2Adapter.ViewHolder> {


        @Nonnull
        @Override
        public Yarn2Adapter.ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
            YarnItemBinding binding = YarnItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

            return new Yarn2Adapter.ViewHolder(binding);
        }

        public void reFilter() {
            ArrayList<YarnCompanyModel> favYarnList = new ArrayList<>();
            ArrayList<YarnCompanyModel> unFavYarnList = new ArrayList<>();
            for (int i = 0; i < filterYarnList.size(); i++) {
                if (filterYarnList.get(i).isFav()) {
                    favYarnList.add(filterYarnList.get(i));
                } else {
                    unFavYarnList.add(filterYarnList.get(i));
                }
            }
            filterYarnList.clear();
            filterYarnList.addAll(favYarnList);
            filterYarnList.addAll(unFavYarnList);
        }

        @Override
        public void onBindViewHolder(@Nonnull Yarn2Adapter.ViewHolder holder, int position) {
            if (position == filterYarnList.size()) {
                holder.binding.imgFav.setVisibility(View.GONE);
                holder.binding.viewInput.setVisibility(View.GONE);
            } else {
                holder.binding.imgFav.setVisibility(View.VISIBLE);
                holder.binding.viewInput.setVisibility(View.VISIBLE);
                holder.binding.editCompanyName.setText(filterYarnList.get(position).getCompany_name());
                holder.binding.editCompanyShadeNo.setText(filterYarnList.get(position).getCompany_shade_no());
                if (filterYarnList.get(position).isFav()) {
                    holder.binding.imgFav.setImageResource(R.drawable.fav);
                } else {
                    holder.binding.imgFav.setImageResource(R.drawable.un_fav);
                }
            }

            holder.binding.imgFav.setOnClickListener(view -> {
                if (filterYarnList.get(position).isFav()) {
                    filterYarnList.get(position).setFav(false);
                    holder.binding.imgFav.setImageResource(R.drawable.un_fav);
                } else {
                    filterYarnList.get(position).setFav(true);
                    holder.binding.imgFav.setImageResource(R.drawable.fav);
                }
                reFilter();
                notifyDataSetChanged();
            });

            holder.binding.editCompanyShadeNo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence != null) {

                        filterYarnList.get(holder.getAdapterPosition()).setCompany_shade_no(holder.binding.editCompanyShadeNo.getText().toString().trim());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            holder.binding.editCompanyName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence != null) {
                        filterYarnList.get(holder.getAdapterPosition()).setCompany_name(holder.binding.editCompanyName.getText().toString().trim());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            holder.itemView.setOnClickListener(view -> {
                if (position == filterYarnList.size()) {
                    filterYarnList.add(new YarnCompanyModel());
                    notifyItemInserted(position);
                    notifyItemChanged(filterYarnList.size());
                }
            });
        }

        @Override
        public int getItemCount() {
            return filterYarnList.size() + 1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            YarnItemBinding binding;

            public ViewHolder(@Nonnull YarnItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public void seekTrack(int i) {
        binding.dot1.setImageResource(R.drawable.circle_border);
        binding.dot2.setImageResource(R.drawable.circle_border);
        binding.dot3.setImageResource(R.drawable.circle_border);
        binding.dot1.setColorFilter(ContextCompat.getColor(this, R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.dot2.setColorFilter(ContextCompat.getColor(this, R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.dot3.setColorFilter(ContextCompat.getColor(this, R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.line1.setColorFilter(ContextCompat.getColor(this, R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.line2.setColorFilter(ContextCompat.getColor(this, R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.textStep1.setTextColor(ContextCompat.getColor(this, R.color.unSelected_btn));
        binding.textStep2.setTextColor(ContextCompat.getColor(this, R.color.unSelected_btn));
        binding.textStep3.setTextColor(ContextCompat.getColor(this, R.color.unSelected_btn));

        if (i == 1) {
            binding.dot1.setImageResource(R.drawable.circle);
            binding.dot1.setColorFilter(ContextCompat.getColor(this, R.color.theme), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.textStep1.setTextColor(ContextCompat.getColor(this, R.color.theme));
        } else if (i == 2) {
            seekTrack(1);
            binding.dot2.setImageResource(R.drawable.circle);
            binding.dot2.setColorFilter(ContextCompat.getColor(this, R.color.theme), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.line1.setColorFilter(ContextCompat.getColor(this, R.color.theme), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.textStep2.setTextColor(ContextCompat.getColor(this, R.color.theme));
        } else if (i == 3) {
            seekTrack(2);
            binding.dot3.setImageResource(R.drawable.circle);
            binding.dot3.setColorFilter(ContextCompat.getColor(this, R.color.theme), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.line2.setColorFilter(ContextCompat.getColor(this, R.color.theme), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.textStep3.setTextColor(ContextCompat.getColor(this, R.color.theme));
        } else {
            binding.dot1.setImageResource(R.drawable.circle);
            binding.dot1.setColorFilter(ContextCompat.getColor(this, R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.dot2.setColorFilter(ContextCompat.getColor(this, R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.dot3.setColorFilter(ContextCompat.getColor(this, R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.line1.setColorFilter(ContextCompat.getColor(this, R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.line2.setColorFilter(ContextCompat.getColor(this, R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.textStep1.setTextColor(ContextCompat.getColor(this, R.color.unSelected_btn));
            binding.textStep2.setTextColor(ContextCompat.getColor(this, R.color.unSelected_btn));
            binding.textStep3.setTextColor(ContextCompat.getColor(this, R.color.unSelected_btn));
        }
    }
}