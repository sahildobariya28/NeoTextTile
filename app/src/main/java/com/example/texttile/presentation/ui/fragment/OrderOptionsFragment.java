package com.example.texttile.presentation.ui.fragment;

import static com.example.texttile.core.MyApplication.getUSERMODEL;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.texttile.R;
import com.example.texttile.presentation.ui.adapter.BottomSheetAdapter;
import com.example.texttile.databinding.FragmentOrderOptionsBinding;
import com.example.texttile.data.dao.DaoAuthority;
import com.example.texttile.presentation.ui.interfaces.LoadFragmentChangeListener;
import com.example.texttile.presentation.screen.features.allot_feature.allot_program.activity.list.AllotProgramListActivity;
import com.example.texttile.presentation.screen.features.reading_feature.daily_reading.list.DailyReadingListActivity;
import com.example.texttile.presentation.screen.features.allot_feature.employee_allotment.list.EmployeeAllotListActivity;
import com.example.texttile.presentation.screen.features.order_feature.order_master.new_order.orderlist.OrderListActivity;
import com.example.texttile.presentation.screen.features.storage_feature.ready_stock.list.ReadyStockListActivity;
import com.example.texttile.presentation.screen.features.order_feature.order_history.OrderHistoryActivity;
import com.example.texttile.presentation.screen.features.reading_feature.view_order.ViewOrderActivity;
import com.example.texttile.presentation.screen.features.reading_feature.yarn_reading.yarn_reading_list.YarnReadingListActivity;
import com.example.texttile.data.model.OptionModel;
import com.example.texttile.data.model.UserDataModel;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class OrderOptionsFragment extends Fragment implements BottomSheetAdapter.AddOnAddButtonClickListener {
    FragmentOrderOptionsBinding binding;
    ArrayList<OptionModel> optionList = new ArrayList<>();
    LoadFragmentChangeListener loadFragmentChangeListener;
    String tracker;

    public OrderOptionsFragment() {
    }

    public OrderOptionsFragment(LoadFragmentChangeListener loadFragmentChangeListener) {

        this.loadFragmentChangeListener = loadFragmentChangeListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrderOptionsBinding.inflate(getLayoutInflater());

        tracker = getArguments().getString("tracker");

        initToolbar();

        if (getUSERMODEL() != null && getUSERMODEL().getU_name() != null) {
            new DaoAuthority(getActivity()).getReference().whereEqualTo("u_name", getUSERMODEL().getU_name()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@Nonnull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            UserDataModel userModel = document.toObject(UserDataModel.class);
                            if (userModel.getType().equals(Const.ADMIN)) {
                                optionList.clear();
                                for (int i = 0; i < userModel.getPermissionList().size(); i++) {
                                    addButtonInList(i);
                                }
                            } else {
                                optionList.clear();
                                for (int i = 0; i < userModel.getPermissionList().size(); i++) {
                                    if (userModel.getPermissionList().get(i).isRead() || userModel.getPermissionList().get(i).isInsert() || userModel.getPermissionList().get(i).isUpdate() || userModel.getPermissionList().get(i).isDelete()) {
                                        addButtonInList(i);
                                    }
                                }
                            }
                            if (tracker.equals("Order Option")) {
                                if (userModel.getPermissionList().get(PermissionState.PENDING.getValue()).isRead() || userModel.getPermissionList().get(PermissionState.ON_MACHINE_PENDING.getValue()).isRead() || userModel.getPermissionList().get(PermissionState.ON_MACHINE_COMPLETED.getValue()).isRead() || userModel.getPermissionList().get(PermissionState.READY_TO_DISPATCH.getValue()).isRead() || userModel.getPermissionList().get(PermissionState.WAREHOUSE.getValue()).isRead() || userModel.getPermissionList().get(PermissionState.FINAL_DISPATCH.getValue()).isRead() || userModel.getPermissionList().get(PermissionState.DELIVERED.getValue()).isRead() || userModel.getPermissionList().get(PermissionState.DAMAGE.getValue()).isRead()) {
                                    optionList.add(new OptionModel("Order\nHistory", R.drawable.order_history));
                                }
                            } else if (tracker.equals("Reading Option")) {
                                if (userModel.getPermissionList().get(PermissionState.PENDING.getValue()).isRead() || userModel.getPermissionList().get(PermissionState.ON_MACHINE_PENDING.getValue()).isRead() || userModel.getPermissionList().get(PermissionState.ON_MACHINE_COMPLETED.getValue()).isRead() || userModel.getPermissionList().get(PermissionState.READY_TO_DISPATCH.getValue()).isRead() || userModel.getPermissionList().get(PermissionState.WAREHOUSE.getValue()).isRead() || userModel.getPermissionList().get(PermissionState.FINAL_DISPATCH.getValue()).isRead() || userModel.getPermissionList().get(PermissionState.DELIVERED.getValue()).isRead() || userModel.getPermissionList().get(PermissionState.DAMAGE.getValue()).isRead()) {
                                    optionList.add(new OptionModel("View\nOrder", R.drawable.view_order));
                                }
                            }
                            BottomSheetAdapter bottomsheetAdapter = new BottomSheetAdapter(getContext(), optionList, OrderOptionsFragment.this);
                            binding.recyclerView.setAdapter(bottomsheetAdapter);
                        }
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                }
            });
        }

        return binding.getRoot();
    }

    public void addButtonInList(int i) {

        if (tracker.equals("Order Option")) {

            switch (i) {
                case 7:
                    optionList.add(new OptionModel("New\nOrder", R.drawable.new_order));
                    break;
            }
        } else if (tracker.equals("Allot Option")) {

            switch (i) {
                case 17:
                    optionList.add(new OptionModel("Allot\nProgram", R.drawable.allot_program));
                    break;
                case 18:
                    optionList.add(new OptionModel("Employee\nAllotment", R.drawable.employee_allotment));
                    break;
            }
        } else if (tracker.equals("Reading Option")) {
            switch (i) {
                case 19:
                    optionList.add(new OptionModel("Daily\nReading", R.drawable.daily_reading));
                    break;
                case 20:
                    optionList.add(new OptionModel("Yarn\nReading", R.drawable.yarn_reading));
                    break;


            }
        } else if (tracker.equals("Storage Option")) {

            switch (i) {
                case 21:
                    optionList.add(new OptionModel("Ready\nStock", R.drawable.ready_stock));
                    break;
            }
        }
    }

    @Override
    public void onAddButtonClick(int position, String title) {
        if (tracker.equals("Order Option")) {
            switch (title) {
                case "New\nOrder":
                    getActivity().startActivity(new Intent(getActivity(), OrderListActivity.class).putExtra("tracker", "order"));
                    break;
                case "Order\nHistory":
                    getActivity().startActivity(new Intent(getActivity(), OrderHistoryActivity.class));
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + position);
            }

        } else if (tracker.equals("Allot Option")) {

            switch (title) {
                case "Allot\nProgram":
                    getActivity().startActivity(new Intent(getActivity(), AllotProgramListActivity.class).putExtra("tracker", Const.ALLOT_PROGRAM));
                    break;

                case "Employee\nAllotment":
                    getActivity().startActivity(new Intent(getActivity(), EmployeeAllotListActivity.class).putExtra("tracker", Const.EMPLOYEE_ALLOTMENT));
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + position);
            }

        } else if (tracker.equals("Reading Option")) {
            switch (title) {
                case "Daily\nReading":
                    getActivity().startActivity(new Intent(getActivity(), DailyReadingListActivity.class).putExtra("tracker", Const.DAILY_READING));
                    break;
                case "Yarn\nReading":
                    getActivity().startActivity(new Intent(getActivity(), YarnReadingListActivity.class).putExtra("tracker", Const.YARN_READING));
                    break;
                case "View\nOrder":
                    getActivity().startActivity(new Intent(getActivity(), ViewOrderActivity.class).putExtra("tracker", Const.ON_PENDING));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + position);
            }

        } else if (tracker.equals("Storage Option")) {
            switch (title) {
                case "Ready\nStock":
                    getActivity().startActivity(new Intent(getActivity(), ReadyStockListActivity.class).putExtra("tracker", Const.READY_STOCK));
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + position);
            }
        }
    }


    public void initToolbar() {
        binding.textTitle.setText(tracker);
        binding.btnBack.setOnClickListener(view -> {
            requireActivity().onBackPressed();
        });
    }
}