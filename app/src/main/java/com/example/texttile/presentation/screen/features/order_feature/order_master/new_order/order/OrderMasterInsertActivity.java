package com.example.texttile.presentation.screen.features.order_feature.order_master.new_order.order;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isInsertPermissionGranted;
import static com.example.texttile.presentation.ui.util.PermissionUtil.isPricePermissionGranted;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityOrderBinding;
import com.example.texttile.databinding.SubOrderItemBinding;
import com.example.texttile.presentation.screen.master.design_master.dialog.DesignMasterDialogFragment;
import com.example.texttile.presentation.ui.dialog.PartyDialog;
import com.example.texttile.presentation.ui.dialog.ShadeMasterDialog;
import com.example.texttile.data.dao.DAOOrder;
import com.example.texttile.presentation.ui.interfaces.AddOnSearchedItemSelectListener;
import com.example.texttile.data.model.OrderMasterModel;
import com.example.texttile.data.model.OrderStatusModel;
import com.example.texttile.data.model.SubOrderModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.ListShowDialog;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class OrderMasterInsertActivity extends AppCompatActivity implements AddOnSearchedItemSelectListener {
    ActivityOrderBinding binding;

    OrderMasterInsertActivity.Order2Adapter order2Adapter;

    OrderViewModel orderViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        orderViewModel = new ViewModelProvider(this, new OrderViewModelFactory(this)).get(OrderViewModel.class);
        setContentView(binding.getRoot());

        orderViewModel.currentState.observe(this, orderStepState -> {
            seekTrack(orderStepState);
            if (orderStepState == OrderStepState.STEP1) {
                binding.btnBack.setVisibility(View.GONE);
                binding.viewOrder1.setVisibility(View.VISIBLE);
                binding.viewOrder2.setVisibility(View.GONE);
            } else {
                binding.btnBack.setVisibility(View.VISIBLE);
                binding.viewOrder1.setVisibility(View.GONE);
                binding.viewOrder2.setVisibility(View.VISIBLE);
            }
        });

        initToolbar();

        binding.orderType.setAdapter(orderViewModel.getCategoryAdapter());
        binding.orderType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 2) {
                    binding.partyContainer.setVisibility(View.GONE);
                    binding.approxDateContainer.setVisibility(View.GONE);
                    binding.priceContainer.setVisibility(View.GONE);
                } else {
                    binding.partyContainer.setVisibility(View.VISIBLE);
                    binding.designNoContainer.setVisibility(View.VISIBLE);
                    binding.approxDateContainer.setVisibility(View.VISIBLE);
                    binding.priceContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        orderViewModel.currentDate.observe(this, s -> {
            binding.editDate.setText(s);
        });

        binding.priceContainer.setVisibility(isPricePermissionGranted(PermissionState.NEW_ORDER.getValue()) ? View.VISIBLE : View.GONE);

        setAutoAdapter();
        autoSetData();

        binding.editDate.setOnClickListener(view -> {
            orderViewModel.openDatePicker();
        });
        binding.btnBack.setOnClickListener(view -> {
            orderViewModel.onBackPress();
        });
        binding.btnNext.setOnClickListener(view -> {

            if (orderViewModel.currentState.getValue() == OrderStepState.STEP1) {
                String order_no = binding.editOrderNo.getText().toString().trim();
                String party_name = "self";
                String date = binding.editDate.getText().toString().trim();
                String design_name = binding.textDesignNo.getText().toString().trim();
                String approx_date = binding.editApproxDays.getText().toString().trim();
                String price = binding.editPrice.getText().toString().trim();
                if (price.isEmpty()){
                    price = "0";
                }

                if (binding.orderType.getSelectedItemPosition() == 2) {
                    if (TextUtils.isEmpty(order_no)) {
                        new CustomSnackUtil().showSnack(this, "Please enter Order No", R.drawable.error_msg_icon);
                    } else if (TextUtils.isEmpty(date)) {
                        new CustomSnackUtil().showSnack(this, "Please enter a Date", R.drawable.error_msg_icon);
                    } else if (binding.priceContainer.getVisibility() == View.VISIBLE && TextUtils.isEmpty(price)) {
                        new CustomSnackUtil().showSnack(this, "Please enter Price", R.drawable.error_msg_icon);
                    } else {
                        orderViewModel.orderMasterModel.setOrder_no(order_no);
                        orderViewModel.orderMasterModel.setOrder_type(orderViewModel.category[binding.orderType.getSelectedItemPosition()]);
                        orderViewModel.orderMasterModel.setDate(date);
                        orderViewModel.orderMasterModel.setParty_name("self");
                        orderViewModel.orderMasterModel.setDesign_no(design_name);
                        orderViewModel.orderMasterModel.setApprox_Delivery_date(date);
                        orderViewModel.orderMasterModel.setPrice(price);
                        orderViewModel.currentState.setValue(OrderStepState.STEP2);
                    }
                } else {
                    if (TextUtils.isEmpty(party_name)) {
                        new CustomSnackUtil().showSnack(this, "Please enter Party Name", R.drawable.error_msg_icon);
                    } else if (TextUtils.isEmpty(order_no)) {
                        new CustomSnackUtil().showSnack(this, "Please enter Order No", R.drawable.error_msg_icon);
                    } else if (TextUtils.isEmpty(date)) {
                        new CustomSnackUtil().showSnack(this, "Please enter a Date", R.drawable.error_msg_icon);
                    } else if (TextUtils.isEmpty(design_name)) {
                        new CustomSnackUtil().showSnack(this, "Please enter Design no", R.drawable.error_msg_icon);
                    } else if (TextUtils.isEmpty(approx_date)) {
                        new CustomSnackUtil().showSnack(this, "Please enter a Approx Date", R.drawable.error_msg_icon);
                    } else if (binding.priceContainer.getVisibility() == View.VISIBLE && TextUtils.isEmpty(price)) {
                        new CustomSnackUtil().showSnack(this, "Please enter Price", R.drawable.error_msg_icon);
                    } else {
                        orderViewModel.orderMasterModel.setOrder_no(order_no);
                        orderViewModel.orderMasterModel.setOrder_type(orderViewModel.category[binding.orderType.getSelectedItemPosition()]);
                        orderViewModel.orderMasterModel.setDate(date);
                        orderViewModel.orderMasterModel.setParty_name(party_name);
                        orderViewModel.orderMasterModel.setDesign_no(design_name);
                        orderViewModel.orderMasterModel.setApprox_Delivery_date(approx_date);
                        orderViewModel.orderMasterModel.setPrice(price);
                        orderViewModel.currentState.setValue(OrderStepState.STEP2);
                    }
                }

            } else {
                boolean is_valid = true;
                if (orderViewModel.subOrderList.size() == 0) {
                    new CustomSnackUtil().showSnack(this, "Please add at least 1 item", R.drawable.error_msg_icon);
                } else {
                    for (int i = 0; i < orderViewModel.subOrderList.size(); i++) {

                        if (orderViewModel.subOrderList.get(i).getShadeNo() == null) {
                            new CustomSnackUtil().showSnack(this, "Please Enter Position " + (i + 1), R.drawable.error_msg_icon);
                            is_valid = false;
                            break;
                        } else if (orderViewModel.subOrderList.get(i).getShadeNo() != null && orderViewModel.subOrderList.get(i).getShadeNo().isEmpty()) {
                            new CustomSnackUtil().showSnack(this, "Please Enter Position " + (i + 1), R.drawable.error_msg_icon);
                            is_valid = false;
                            break;
                        } else if (orderViewModel.subOrderList.get(i).getQty() <= 0) {
                            new CustomSnackUtil().showSnack(this, "Please Enter Position " + (i + 1), R.drawable.error_msg_icon);
                            is_valid = false;
                            break;
                        }
                    }
                    if (is_valid) {
                        DialogUtil.showProgressDialog(this);
                        for (int i = 0; i < orderViewModel.subOrderList.size(); i++) {
                            String id = orderViewModel.dao.getId();


                            orderViewModel.orderMasterModel.setId(id);
                            orderViewModel.orderMasterModel.setSub_order_no(orderViewModel.orderMasterModel.getOrder_no() + ((char) (65 + i)));
                            orderViewModel.orderMasterModel.setShade_no(orderViewModel.subOrderList.get(i).getShadeNo());
                            orderViewModel.orderMasterModel.setQuantity(orderViewModel.subOrderList.get(i).getQty());
                            orderViewModel.orderMasterModel.setOrderStatusModel(new OrderStatusModel(orderViewModel.subOrderList.get(i).getQty(), 0, 0, 0, 0, 0, 0, 0, orderViewModel.subOrderList.get(i).getQty()));

                            if (i == orderViewModel.subOrderList.size() - 1) {
                                orderViewModel.dao.insert(id, orderViewModel.orderMasterModel).addOnSuccessListener(runnable -> {
                                    DialogUtil.hideProgressDialog();
                                    DialogUtil.showSuccessDialog(this, true);
                                });
                            } else {
                                orderViewModel.dao.insert(id, orderViewModel.orderMasterModel);
                            }
                        }
                    }
                }
            }
        });

        binding.textPartyName.setOnClickListener(view -> {
            orderViewModel.onPartyNameClicked(OrderMasterInsertActivity.this);
        });
        binding.textDesignNo.setOnClickListener(view -> {
            orderViewModel.onDesignNoClicked(OrderMasterInsertActivity.this);
        });

        order2Adapter = new OrderMasterInsertActivity.Order2Adapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(order2Adapter);

        orderViewModel.getPartyMasterList(this, OrderMasterInsertActivity.this);

        orderViewModel.getDesignMasterList(this, OrderMasterInsertActivity.this);
    }

    private void autoSetData() {

        new DAOOrder(this).getReference().addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                return;
            }
            int max_no = 0;
            for (QueryDocumentSnapshot doc : snapshot) {
                OrderMasterModel orderMasterModel = doc.toObject(OrderMasterModel.class);
                int orderNo = Integer.valueOf(orderMasterModel.getOrder_no());
                if (orderNo > max_no) {
                    max_no = orderNo;
                }
            }
            binding.editOrderNo.setText("" + (max_no + 1));

            // Handle snapshot data
        });
    }

    private void setAutoAdapter() {
        binding.progress.setVisibility(View.VISIBLE);
        binding.progress1.setVisibility(View.VISIBLE);
        orderViewModel.daoPartyMaster.getPartyMasterList("");
        orderViewModel.daoDesignMaster.getDesignMasterList("");
    }

    public void seekTrack(OrderStepState i) {
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

        if (i == OrderStepState.STEP1) {
            binding.dot1.setImageResource(R.drawable.circle);
            binding.dot1.setColorFilter(ContextCompat.getColor(this, R.color.theme), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.textStep1.setTextColor(ContextCompat.getColor(this, R.color.theme));
        } else if (i == OrderStepState.STEP2) {
            seekTrack(OrderStepState.STEP1);
            binding.dot2.setImageResource(R.drawable.circle);
            binding.dot2.setColorFilter(ContextCompat.getColor(this, R.color.theme), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.line1.setColorFilter(ContextCompat.getColor(this, R.color.theme), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.textStep2.setTextColor(ContextCompat.getColor(this, R.color.theme));
        } else if (i == OrderStepState.STEP3) {
            seekTrack(OrderStepState.STEP1);
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

    public void initToolbar() {
        binding.include.textTitle.setText("New Order");
        binding.include.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    @Override
    public void onSelected(int position, String name, int request_code) {
        if (request_code == orderViewModel.PARTY_REQUEST) {
            binding.textPartyName.setText(name);
        } else if (request_code == orderViewModel.DESIGN_REQUEST) {
            binding.textDesignNo.setText(name);
            orderViewModel.getShadeMasterList(name, this);
        }
    }

    @Override
    public void onAddButtonClicked(int position, String name, int request_code) {
        if (request_code == orderViewModel.PARTY_REQUEST) {

            if (isInsertPermissionGranted(PermissionState.PARTY_MASTER.getValue())) {
                PartyDialog partyDialog = new PartyDialog();
                Bundle bundle = new Bundle();
                bundle.putString("party_name", name);
                partyDialog.setArguments(bundle);
                partyDialog.show(getSupportFragmentManager(), "Party Dialog");
            } else {
                DialogUtil.showAccessDeniedDialog(OrderMasterInsertActivity.this);
            }

        } else if (request_code == orderViewModel.DESIGN_REQUEST) {

            if (isInsertPermissionGranted(PermissionState.DESIGN_MASTER.getValue())) {
                DesignMasterDialogFragment dialogFragment = new DesignMasterDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("design_no", name);
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getSupportFragmentManager(), "My Fragment");
            } else {
                DialogUtil.showAccessDeniedDialog(OrderMasterInsertActivity.this);
            }

        }
    }

    @Override
    public void onMultiSelected(ArrayList<String> shadeList, int request_code) {

    }

    public class Order2Adapter extends RecyclerView.Adapter<Order2Adapter.ViewHolder> implements AddOnSearchedItemSelectListener {

//        ListShowDialog shadeListDialog;
//        final int SHADE_REQUEST = 2;

//        private Order2Adapter() {
//            setAutoAdapter();
//        }

        @Nonnull
        @Override
        public Order2Adapter.ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
            SubOrderItemBinding binding = SubOrderItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new Order2Adapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@Nonnull Order2Adapter.ViewHolder holder, int position) {
            if (position == orderViewModel.subOrderList.size()) {
                holder.binding.imgClose.setVisibility(View.GONE);
                holder.binding.viewInput.setVisibility(View.GONE);
            } else {
                holder.binding.imgClose.setVisibility(View.VISIBLE);
                holder.binding.viewInput.setVisibility(View.VISIBLE);
                holder.binding.textShadeNo.setText(orderViewModel.subOrderList.get(position).getShadeNo());
                holder.binding.editQty.setText(String.valueOf(orderViewModel.subOrderList.get(position).getQty()));
            }
            holder.binding.txtSubChar.setText(String.valueOf(((char) (65 + position))));


            holder.binding.textShadeNo.setOnClickListener(view -> {

//                getShadeList();
                orderViewModel.shadeListDialog = new ListShowDialog(OrderMasterInsertActivity.this, orderViewModel.shade_no_list, Order2Adapter.this, position);
                orderViewModel.shadeListDialog.show_dialog();
            });

            holder.binding.imgClose.setOnClickListener(view -> {
                if (orderViewModel.subOrderList.get(position).getId() == null) {
                    orderViewModel.subOrderList.remove(position);
                    notifyDataSetChanged();
                } else {
                    deleteItem(orderViewModel.subOrderList.get(position).getId());
                }
            });

            holder.binding.editQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence != null) {
                        if (holder.binding.editQty.getText().toString().trim().isEmpty()) {
                            orderViewModel.subOrderList.get(holder.getAdapterPosition()).setQty(0);
                        } else {
                            orderViewModel.subOrderList.get(holder.getAdapterPosition()).setQty(Integer.parseInt(holder.binding.editQty.getText().toString().trim()));
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            holder.itemView.setOnClickListener(view -> {
                if (position == orderViewModel.subOrderList.size()) {
                    orderViewModel.subOrderList.add(new SubOrderModel());
                    notifyItemInserted(position);
                    notifyItemChanged(orderViewModel.subOrderList.size());
                }
            });
        }

        public void deleteItem(String child) {
            new DAOOrder(OrderMasterInsertActivity.this).delete(child).addOnSuccessListener(runnable -> {
                DialogUtil.showDeleteDialog(OrderMasterInsertActivity.this);
            });
        }

//        private void setAutoAdapter() {
//            getShadeList();
//
//        }

//        private void getShadeList() {
//
//            orderViewModel.shade_no_list.clear();
//            new DAOShadeMaster(OrderActivity.this).getReference().addSnapshotListener((snapshot, error) -> {
//                if (snapshot != null && !snapshot.isEmpty()) {
//                    List<ShadeMasterModel> shadeMasterList = new ArrayList<>();
//                    for (QueryDocumentSnapshot documentSnapshot : snapshot) {
//                        ShadeMasterModel shadeMasterModel = documentSnapshot.toObject(ShadeMasterModel.class);
//                        if (shadeMasterModel != null) {
//
//                                if (!binding.textDesignNo.getText().toString().isEmpty()) {
//                                    if (binding.textDesignNo.getText().toString().trim().equals(shadeMasterModel.getDesign_no())) {
////                                        Log.d("jfkdslf", "getDesign: " + shadeMasterModel.getDesign_no() +"   getShade: " + shadeMasterModel.getShade_no() +"   getDesignText : " + binding.textDesignNo.getText().toString());
//                                        new DAOOrder(OrderActivity.this).getReference().whereEqualTo("shade_no", shadeMasterModel.getShade_no()).addSnapshotListener((task, taskError) -> {
//
//                                                if (task != null && !task.isEmpty()) {
//                                                    int orderQty = 0, orderPendingQty = 0;
//                                                    for (QueryDocumentSnapshot documentOrderSnapshot : task) {
//                                                        OrderMasterModel orderMasterModel1 = documentOrderSnapshot.toObject(OrderMasterModel.class);
//                                                        if (orderMasterModel1.getShade_no().equals(shadeMasterModel.getShade_no())){
//                                                            orderQty += orderMasterModel1.getQuantity();
//                                                            orderPendingQty += orderMasterModel1.getOrderStatusModel().getPending();
//                                                        }
//                                                    }
//                                                    Log.d("jfkdslf", "getListSize: " + orderViewModel.shade_no_list.size());
//                                                    orderViewModel.shade_no_list.add(shadeMasterModel.getShade_no() + " (" + orderQty + ", " + orderPendingQty + ")");
//                                                    Log.d("jfkdslf", "getDesign: " + shadeMasterModel.getDesign_no() +"   getShade: " + shadeMasterModel.getShade_no() +"   getDesignText : " + orderViewModel.shade_no_list.size());
//                                                }else{
//                                                    orderViewModel.shade_no_list.add(shadeMasterModel.getShade_no() + " (" + 0 + ", " + 0 + ")");
//                                                }
//                                        });
////                                        orderViewModel.shade_no_list.add(shadeMasterModel.getShade_no());
////                                        orderViewModel.shade_no_list.add(shadeMasterModel.getShade_no() + " (" + shadeMasterModel.getDesign_no() + ")");
//                                    }
//                                }
//
//                        }
//                    }
//                    if (shadeListDialog != null) {
//                        shadeListDialog.update_list(orderViewModel.shade_no_list);
//                    } else {
//                        shadeListDialog = new ListShowDialog(OrderActivity.this, orderViewModel.shade_no_list, Order2Adapter.this, 0);
//                    }
//                    // Handle shadeMasterList
//                }
//            });
//
//        }

        @Override
        public int getItemCount() {
            return orderViewModel.subOrderList.size() + 1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            SubOrderItemBinding binding;

            public ViewHolder(@Nonnull SubOrderItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;

            }
        }

        @Override
        public void onSelected(int position, String name, int request_code) {
            orderViewModel.subOrderList.get(request_code).setShadeNo(name.split(" \\(")[0]);
            notifyItemChanged(request_code);
        }

        @Override
        public void onAddButtonClicked(int position, String name, int request_code) {
            if (request_code == orderViewModel.SHADE_REQUEST) {

                if (isInsertPermissionGranted(PermissionState.SHADE_MASTER.getValue())) {
                    ShadeMasterDialog shadeMasterDialog = new ShadeMasterDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("shade_no", name);
                    shadeMasterDialog.setArguments(bundle);
                    shadeMasterDialog.show(getSupportFragmentManager(), "My Fragment");
                } else {
                    DialogUtil.showAccessDeniedDialog(OrderMasterInsertActivity.this);
                }
            }
        }

        @Override
        public void onMultiSelected(ArrayList<String> shadeList, int request_code) {

        }

    }
}