package com.example.texttile.presentation.ui.fragment;

import static com.example.texttile.core.MyApplication.USER_DATA;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.texttile.R;
import com.example.texttile.presentation.screen.master.shade_master.list.ShadeMasterSubListActivity;
import com.example.texttile.presentation.ui.adapter.BottomSheetAdapter;
import com.example.texttile.databinding.FragmentMasterBinding;
import com.example.texttile.data.dao.DaoAuthority;
import com.example.texttile.presentation.ui.interfaces.LoadFragmentChangeListener;
import com.example.texttile.presentation.screen.authority.user.AuthorityActivity;
import com.example.texttile.presentation.screen.master.design_master.activity.design_list.DesignMasterList;
import com.example.texttile.presentation.screen.master.employee_master.EmployeeMasterList;
import com.example.texttile.presentation.screen.master.jala_master.JalaMasterList;
import com.example.texttile.presentation.screen.master.machine_master.MachineMasterList;
import com.example.texttile.presentation.screen.master.party_master.PartyMasterList;
import com.example.texttile.presentation.screen.master.shade_master.sublist.ShadeMasterListActivity;
import com.example.texttile.presentation.screen.features.storage_feature.storage_file.StorageActivity;
import com.example.texttile.presentation.screen.master.yarn_master.YarnMasterList;
import com.example.texttile.data.model.OptionModel;
import com.example.texttile.data.model.UserDataModel;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class MasterFragment extends Fragment implements BottomSheetAdapter.AddOnAddButtonClickListener {

    FragmentMasterBinding binding;
    ArrayList<OptionModel> optionList = new ArrayList<>();
    LoadFragmentChangeListener loadFragmentChangeListener;
    String tracker;

    public MasterFragment() {
    }

    public MasterFragment(LoadFragmentChangeListener loadFragmentChangeListener) {
        this.loadFragmentChangeListener = loadFragmentChangeListener;
    }

    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMasterBinding.inflate(getLayoutInflater());

        tracker = getArguments().getString("tracker");

        initToolbar();

        optionList.clear();
        if (tracker.equals(Const.AUTHORITY)) {

            Bundle bundle = new Bundle();
            bundle.putString("tracker", "add");
            startActivity(new Intent(getActivity(), AuthorityActivity.class).putExtras(bundle));


        } else {

            if (tracker.equals("Master")) {
                USER_DATA.observe(getActivity(), userDataModel -> {
                    if (userDataModel != null && userDataModel.getU_name() != null) {


                        new DaoAuthority(getActivity()).getReference().whereEqualTo("u_name", userDataModel.getU_name()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    Log.w("firestore", "Listen failed.", error);
                                    return;
                                }

                                for (QueryDocumentSnapshot document : snapshot) {
                                    UserDataModel userModel = document.toObject(UserDataModel.class);
                                    if (userModel.getType().equals(Const.ADMIN)) {
                                        optionList.clear();
                                        for (int i = 0; i < userModel.getPermissionList().size(); i++) {
                                            addButtonInList(i);
                                        }
                                        BottomSheetAdapter optionAdapter = new BottomSheetAdapter(getContext(), optionList, MasterFragment.this);
                                        binding.recyclerView.setAdapter(optionAdapter);
                                    } else {
                                        optionList.clear();
                                        for (int i = 0; i < userModel.getPermissionList().size(); i++) {
                                            if (userModel.getPermissionList().get(i).isRead() || userModel.getPermissionList().get(i).isInsert() || userModel.getPermissionList().get(i).isUpdate() || userModel.getPermissionList().get(i).isDelete()) {
                                                addButtonInList(i);
                                            }
                                        }
                                        BottomSheetAdapter optionAdapter = new BottomSheetAdapter(getContext(), optionList, MasterFragment.this);
                                        binding.recyclerView.setAdapter(optionAdapter);
                                    }
                                }
                            }
                        });
                    }
                });


            } else {
                USER_DATA.observe(getActivity(), userDataModel -> {
                    if (userDataModel != null && userDataModel.getU_name() != null) {

                        new DaoAuthority(getActivity()).getReference().whereEqualTo("u_name", userDataModel.getU_name()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot snapshot) {
                                for (QueryDocumentSnapshot document : snapshot) {
                                    UserDataModel userModel = document.toObject(UserDataModel.class);
                                    if (userModel.getType().equals(Const.ADMIN)) {
                                        optionList.clear();
                                        optionList.add(new OptionModel("Order\nOption", R.drawable.order_option));
                                        optionList.add(new OptionModel("Allot\nOption", R.drawable.allot_program));
                                        optionList.add(new OptionModel("Reading\nOption", R.drawable.reading_option));
                                        optionList.add(new OptionModel("Storage\nOption", R.drawable.storage_option));
                                        optionList.add(new OptionModel("Storage\nFile", R.drawable.storage_file));

                                        BottomSheetAdapter optionAdapter = new BottomSheetAdapter(getContext(), optionList, MasterFragment.this);
                                        binding.recyclerView.setAdapter(optionAdapter);
                                    } else {
                                        optionList.clear();
                                        if (userModel.getPermissionList().get(PermissionState.NEW_ORDER.getValue()).isRead() ||
                                                userModel.getPermissionList().get(PermissionState.PENDING.getValue()).isRead() ||
                                                userModel.getPermissionList().get(PermissionState.ON_MACHINE_PENDING.getValue()).isRead() ||
                                                userModel.getPermissionList().get(PermissionState.ON_MACHINE_COMPLETED.getValue()).isRead() ||
                                                userModel.getPermissionList().get(PermissionState.READY_TO_DISPATCH.getValue()).isRead() ||
                                                userModel.getPermissionList().get(PermissionState.WAREHOUSE.getValue()).isRead() ||
                                                userModel.getPermissionList().get(PermissionState.FINAL_DISPATCH.getValue()).isRead() ||
                                                userModel.getPermissionList().get(PermissionState.DELIVERED.getValue()).isRead() ||
                                                userModel.getPermissionList().get(PermissionState.DAMAGE.getValue()).isRead()
                                        ) {
                                            optionList.add(new OptionModel("Order\nOption", R.drawable.order_option));
                                        }
                                        if (userModel.getPermissionList().get(PermissionState.ALLOT_PROGRAM.getValue()).isRead() || userModel.getPermissionList().get(PermissionState.EMPLOYEE_ALLOTMENT.getValue()).isRead()) {
                                            optionList.add(new OptionModel("Allot\nOption", R.drawable.allot_program));
                                        }
                                        if (userModel.getPermissionList().get(PermissionState.DAILY_READING.getValue()).isRead() || userModel.getPermissionList().get(PermissionState.YARN_READING.getValue()).isRead()) {
                                            optionList.add(new OptionModel("Reading\nOption", R.drawable.reading_option));
                                        }
                                        if (userModel.getPermissionList().get(PermissionState.DAMAGE.getValue()).isRead() || userModel.getPermissionList().get(PermissionState.READY_STOCK.getValue()).isRead()) {
                                            optionList.add(new OptionModel("Storage\nOption", R.drawable.storage_option));
                                        }
                                        if (userModel.getPermissionList().get(PermissionState.STORAGE_FILE.getValue()).isRead()) {
                                            optionList.add(new OptionModel("Storage\nFile", R.drawable.storage_file));
                                        }

                                        BottomSheetAdapter optionAdapter = new BottomSheetAdapter(getContext(), optionList, MasterFragment.this);
                                        binding.recyclerView.setAdapter(optionAdapter);
                                    }
                                }
                            }
                        });

                    }
                });
            }
        }
        return binding.getRoot();
    }

    public void addButtonInList(int i) {
        if (tracker.equals("Master")) {
            switch (i) {
                case 0:
                    optionList.add(new OptionModel( "Design\nMaster", R.drawable.img_design_master));
                    break;
                case 1:
                    optionList.add(new OptionModel("Shade\nMaster", R.drawable.img_design_shade_master));
                    break;
                case 2:
                    optionList.add(new OptionModel("Yarn\nMaster", R.drawable.img_yarn_master));
                    break;
                case 3:
                    optionList.add(new OptionModel("Machine\nMaster", R.drawable.img_machine_master));
                    break;
                case 4:
                    optionList.add(new OptionModel("Jala\nMaster", R.drawable.img_jala_master));
                    break;
                case 5:
                    optionList.add(new OptionModel("Employee\nMaster", R.drawable.img_employee_master));
                    break;
                case 6:
                    optionList.add(new OptionModel("Party\nMaster", R.drawable.party_master));
                    break;

            }
        } else if (tracker.equals("Options")) {

        }
    }

    @Override
    public void onAddButtonClick(int position, String title) {
        if (tracker.equals("Master")) {
            switch (title) {
                case "Design\nMaster":
                    getActivity().startActivity(new Intent(getContext(), DesignMasterList.class).putExtra("tracker", Const.DESIGN_MASTER));
                    break;
                case "Shade\nMaster":
                    getActivity().startActivity(new Intent(getContext(), ShadeMasterSubListActivity.class).putExtra("tracker", Const.SHADE_MASTER));
                    break;
                case "Yarn\nMaster":
                    getActivity().startActivity(new Intent(getContext(), YarnMasterList.class).putExtra("tracker", Const.YARN_MASTER));
                    break;
                case "Machine\nMaster":
                    getActivity().startActivity(new Intent(getContext(), MachineMasterList.class).putExtra("tracker", Const.MACHINE_MASTER));
                    break;
                case "Jala\nMaster":
                    getActivity().startActivity(new Intent(getContext(), JalaMasterList.class).putExtra("tracker", Const.JALA_MASTER));
                    break;
                case "Employee\nMaster":
                    getActivity().startActivity(new Intent(getContext(), EmployeeMasterList.class).putExtra("tracker", Const.EMPLOYEE_MASTER));
                    break;
                case "Party\nMaster":
                    getActivity().startActivity(new Intent(getContext(), PartyMasterList.class).putExtra("tracker", Const.PARTY_MASTER));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + position);
            }
        } else if (tracker.equals("Options")) {
            Fragment fragment;
            Bundle bundle = new Bundle();
            switch (title) {
                case "Order\nOption":
                    fragment = new OrderOptionsFragment(loadFragmentChangeListener);
                    bundle.putString("tracker", "Order Option");
                    fragment.setArguments(bundle);
                    loadFragmentChangeListener.onLoadFragment(fragment, 1);
                    break;
                case "Allot\nOption":
                    fragment = new OrderOptionsFragment(loadFragmentChangeListener);
                    bundle.putString("tracker", "Allot Option");
                    fragment.setArguments(bundle);
                    loadFragmentChangeListener.onLoadFragment(fragment, 1);
                    break;
                case "Reading\nOption":
                    fragment = new OrderOptionsFragment(loadFragmentChangeListener);
                    bundle.putString("tracker", "Reading Option");
                    fragment.setArguments(bundle);
                    loadFragmentChangeListener.onLoadFragment(fragment, 1);
                    break;
                case "Storage\nOption":
                    fragment = new OrderOptionsFragment(loadFragmentChangeListener);
                    bundle.putString("tracker", "Storage Option");
                    fragment.setArguments(bundle);
                    loadFragmentChangeListener.onLoadFragment(fragment, 1);
                    break;
                case "Storage\nFile":
                    startActivity(new Intent(getActivity(), StorageActivity.class));
//                    fragment = new StorageFragment();
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