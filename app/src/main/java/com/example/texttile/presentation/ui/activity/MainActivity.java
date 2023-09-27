package com.example.texttile.presentation.ui.activity;

import static com.example.texttile.core.MyApplication.USER_DATA;
import static com.example.texttile.core.MyApplication.setUSERMODEL;
import static com.example.texttile.presentation.ui.util.PermissionUtil.isPermissionGranted;
import static com.example.texttile.presentation.ui.util.PermissionUtil.takePermission;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.texttile.core.MyApplication;
import com.example.texttile.R;
import com.example.texttile.presentation.ui.adapter.BottomSheetAdapter;
import com.example.texttile.presentation.ui.lib.bottom_navigation.MeowBottomNavigation;
import com.example.texttile.databinding.ActivityMainBinding;
import com.example.texttile.databinding.BottomSheetDialog1Binding;
import com.example.texttile.data.dao.DaoAuthority;
import com.example.texttile.presentation.ui.fragment.DashboardFragment;
import com.example.texttile.presentation.ui.fragment.MainFragment;
import com.example.texttile.presentation.ui.fragment.ProfileFragment;
import com.example.texttile.presentation.ui.fragment.ScannerFragment;
import com.example.texttile.presentation.ui.interfaces.LoadFragmentChangeListener;
import com.example.texttile.presentation.screen.authority.list.UserListActivity;
import com.example.texttile.data.model.OptionModel;
import com.example.texttile.data.model.UserDataModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PrefUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class MainActivity extends AppCompatActivity implements BottomSheetAdapter.AddOnAddButtonClickListener, LoadFragmentChangeListener {

    MeowBottomNavigation.Model scanner_model, menu_model, profile_model;
    BottomSheetDialog bottomSheetDialog;
    ArrayList<OptionModel> optionList = new ArrayList<>();

    ActivityMainBinding binding;
    boolean mKeyboardVisible;

    public static String HISTORY_TRACKER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        scanner_model = new MeowBottomNavigation.Model(0, R.drawable.scanner);
        menu_model = new MeowBottomNavigation.Model(1, R.drawable.home);
        profile_model = new MeowBottomNavigation.Model(2, R.drawable.user);
        binding.meowBottomNavigation.add(scanner_model);
        binding.meowBottomNavigation.add(menu_model);
        binding.meowBottomNavigation.add(profile_model);


        binding.meowBottomNavigation.setOnClickMenuListener(item -> {
            switch (item.getId()) {
                case 0:
                    replaceFragment(new ScannerFragment());
                    menu_model = new MeowBottomNavigation.Model(1, R.drawable.home);
                    binding.meowBottomNavigation.set(menu_model, 1);
                    break;
                case 1:
                    replaceFragment(new DashboardFragment());
                    menu_model = new MeowBottomNavigation.Model(1, R.drawable.menu);
                    binding.meowBottomNavigation.set(menu_model, 1);
                    break;
                case 2:
                    replaceFragment(new ProfileFragment(this));
                    menu_model = new MeowBottomNavigation.Model(1, R.drawable.home);
                    binding.meowBottomNavigation.set(menu_model, 1);
                    break;
                default:
                    replaceFragment(new DashboardFragment());
            }
        });
        binding.meowBottomNavigation.setOnShowListener(item -> {
        });

        binding.meowBottomNavigation.setOnReselectListener(item -> {
            if (item.getId() == 1) {
                show_bottom_sheet();
            }
        });
        replaceFragment(new DashboardFragment());
        menu_model = new MeowBottomNavigation.Model(1, R.drawable.menu);
        binding.meowBottomNavigation.set(menu_model, 1);
        binding.meowBottomNavigation.show(1, true);
    }

    public void load_bottom_sheet() {
        if (!bottomSheetDialog.isShowing()) {
            BottomSheetDialog1Binding bottom_binding = BottomSheetDialog1Binding.inflate(getLayoutInflater());
            bottomSheetDialog.setContentView(bottom_binding.getRoot());
            bottomSheetDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            USER_DATA.observe(this, userDataModel -> {
                if (userDataModel != null && userDataModel.getU_name() != null) {

                    new DaoAuthority(this).getReference().whereEqualTo("u_name", userDataModel.getU_name()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                    UserDataModel userModel = documentSnapshot.toObject(UserDataModel.class);
                                    if (userModel != null && userModel.getType().equals(Const.ADMIN)) {
                                        optionList.clear();
                                        optionList.add(new OptionModel("Master", R.drawable.master_data));
                                        optionList.add(new OptionModel("Menu", R.drawable.img_menu));
                                        optionList.add(new OptionModel("Authority", R.drawable.img_authority));
                                        bottom_binding.progress.setVisibility(View.GONE);
                                        BottomSheetAdapter bottomsheetAdapter = new BottomSheetAdapter(MainActivity.this, optionList, MainActivity.this);
                                        bottom_binding.recyclerView.setAdapter(bottomsheetAdapter);
                                    } else {
                                        optionList.clear();
                                        optionList.add(new OptionModel("Master", R.drawable.master_data));
                                        optionList.add(new OptionModel("Menu", R.drawable.img_menu));
                                        bottom_binding.progress.setVisibility(View.GONE);
                                        BottomSheetAdapter bottomsheetAdapter = new BottomSheetAdapter(MainActivity.this, optionList, MainActivity.this);
                                        bottom_binding.recyclerView.setAdapter(bottomsheetAdapter);
                                    }
                                }
                            } else {
                                startActivity(new Intent(MainActivity.this, SignupActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        } else {
                            Log.e("Firestore", "Error getting data: ", task.getException());
                        }
                    });

//                    new DaoAuthority(this).getReference().orderByChild("u_name").equalTo(userDataModel.getU_name()).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@Nonnull DataSnapshot snapshot) {
//                            if (snapshot.exists()) {
//                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                                    UserDataModel userModel = postSnapshot.getValue(UserDataModel.class);
//                                    if (userModel.getType().equals(Const.ADMIN)) {
//                                        optionList.clear();
//                                        optionList.add(new OptionModel("Master", R.drawable.master_data));
//                                        optionList.add(new OptionModel("Menu", R.drawable.img_menu));
//                                        optionList.add(new OptionModel("Authority", R.drawable.img_authority));
//                                        bottom_binding.progress.setVisibility(View.GONE);
//                                        BottomSheetAdapter bottomsheetAdapter = new BottomSheetAdapter(MainActivity.this, optionList, MainActivity.this);
//                                        bottom_binding.recyclerView.setAdapter(bottomsheetAdapter);
//                                    } else {
//                                        optionList.clear();
//                                        optionList.add(new OptionModel("Master", R.drawable.master_data));
//                                        optionList.add(new OptionModel("Menu", R.drawable.img_menu));
//                                        bottom_binding.progress.setVisibility(View.GONE);
//                                        BottomSheetAdapter bottomsheetAdapter = new BottomSheetAdapter(MainActivity.this, optionList, MainActivity.this);
//                                        bottom_binding.recyclerView.setAdapter(bottomsheetAdapter);
//                                    }
//
//                                }
//                            } else {
//                                startActivity(new Intent(MainActivity.this, SignupActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@Nonnull DatabaseError error) {
//                        }
//                    });
                } else {
                    new DaoAuthority(this).getReference().whereEqualTo("u_name", new PrefUtil(this).getUser().getU_name())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@Nonnull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            UserDataModel userModel = document.toObject(UserDataModel.class);
                                            setUSERMODEL(userModel);
                                            load_bottom_sheet();
                                        }
                                    } else {
                                        Log.d("Firestore", "Error getting documents: ", task.getException());
                                    }
                                }
                            });
//                    new DaoAuthority(this).getReference().orderByChild("u_name").equalTo(new PrefUtil(this).getUser().getU_name()).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@Nonnull DataSnapshot snapshot) {
//                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                                UserDataModel userModel = postSnapshot.getValue(UserDataModel.class);
//                                setUSERMODEL(userModel);
//                                load_bottom_sheet();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@Nonnull DatabaseError error) {
//                        }
//                    });
                }
            });


        }
    }

    public void show_bottom_sheet() {
        if (bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        } else {
            bottomSheetDialog.show();
        }
    }


    @Override
    public void onAddButtonClick(int position, String title) {
        Bundle bundle = new Bundle();
        Fragment fragment;
        switch (title) {
            case "Master":
                bottomSheetDialog.dismiss();
                fragment = new MainFragment();
                bundle.putString("tracker", "Master");
                fragment.setArguments(bundle);
                replaceFragment(fragment);
                break;
            case "Menu":
                bottomSheetDialog.dismiss();
                fragment = new MainFragment();
                bundle.putString("tracker", "Options");
                fragment.setArguments(bundle);
                replaceFragment(fragment);
                break;
            case "Authority":
                bottomSheetDialog.dismiss();
                startActivity(new Intent(this, UserListActivity.class));
                break;
        }

    }

    public void replaceFragment(Fragment fragment) {
        if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
            getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(binding.container.getId(), fragment);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    @Override
    public void onLoadFragment(Fragment fragment, int position) {

        replaceFragment(fragment);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                new CustomSnackUtil().showSnack(this, "Cancelled", R.drawable.error_msg_icon);
            } else {

                ScanQR scanQR = new ScanQR();
                scanQR.onCreate(getSupportFragmentManager(), this, intentResult.getContents(), intentResult.getFormatName());
                new CustomSnackUtil().showSnack(this, intentResult.getContents() + "    " + intentResult.getFormatName(), R.drawable.error_msg_icon);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {

        Fragment current_fragment = getSupportFragmentManager().findFragmentById(binding.container.getId());
        if (current_fragment instanceof MainFragment) {
            MainFragment readDataFragment = (MainFragment) current_fragment;
            readDataFragment.onBackPressed(this);
        } else {
            DialogUtil.showExitDialog(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @Nonnull String[] permissions, @Nonnull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            boolean readExt = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!readExt) {
                takePermission(MainActivity.this);
            }
        }
    }

    private View getContentView() {
        return findViewById(Window.ID_ANDROID_CONTENT);
    }

    @Override
    protected void onResume() {
        bottomSheetDialog = new BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme);
        load_bottom_sheet();
        if (!isPermissionGranted(this)) {
            takePermission(this);
        }

        ((MyApplication) getApplicationContext()).setActivity(this, this);

        getContentView().getViewTreeObserver().addOnGlobalLayoutListener(mLayoutKeyboardVisibilityListener);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        getContentView().getViewTreeObserver().removeOnGlobalLayoutListener(mLayoutKeyboardVisibilityListener);
    }


    private final ViewTreeObserver.OnGlobalLayoutListener mLayoutKeyboardVisibilityListener = () -> {
        final Rect rectangle = new Rect();
        final View contentView = getContentView();
        contentView.getWindowVisibleDisplayFrame(rectangle);
        int screenHeight = contentView.getRootView().getHeight();
        int keypadHeight = screenHeight - rectangle.bottom;
        boolean isKeyboardNowVisible = keypadHeight > screenHeight * 0.15;

        if (mKeyboardVisible != isKeyboardNowVisible) {
            if (isKeyboardNowVisible) {
                binding.meowBottomNavigation.setVisibility(View.GONE);
            } else {
                binding.meowBottomNavigation.setVisibility(View.VISIBLE);
            }
        }

        mKeyboardVisible = isKeyboardNowVisible;
    };


}