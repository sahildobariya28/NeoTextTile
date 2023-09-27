package com.example.texttile.presentation.screen.authority.user;

import static com.example.texttile.core.MyApplication.getUSERMODEL;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityAuthorityBinding;
import com.example.texttile.data.dao.DaoAuthority;
import com.example.texttile.data.model.UserDataModel;
import com.example.texttile.presentation.screen.authority.permission.PermissionActivity;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthorityActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public String[] category = {"Not selected", "Design Master", "Design Shade Master", "Yarn Master", "Machine Master", "jala Master", "Employee Master Data"};
    int spinner_position = 0;
    UserDataModel userDataModel;
    String tracker;

    ActivityAuthorityBinding binding;

    DaoAuthority dao;

    public AuthorityActivity() {
        dao = new DaoAuthority(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthorityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        initToolbar();

        binding.spinner.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this, R.layout.white_simple_spinner_item, category);
        aa.setDropDownViewResource(R.layout.white_simple_spinner_dropdown_item);
        binding.spinner.setAdapter(aa);

        tracker = (getIntent().getStringExtra("tracker") != null) ? getIntent().getStringExtra("tracker") : "add";
        userDataModel = (tracker.equals("edit") && getIntent().getSerializableExtra(Const.USER_DATA) != null) ? (UserDataModel) getIntent().getSerializableExtra(Const.USER_DATA) : new UserDataModel();

        if (tracker != null && tracker.equals("edit")) {
            if (userDataModel != null) {
                binding.btnSignUp.setText("Update");
                binding.editUsername.setText(userDataModel.getU_name());
                binding.editFullName.setText(userDataModel.getF_name());
                binding.editPhone.setText(userDataModel.getPhone_number());
                binding.editPassword.setText(String.valueOf(userDataModel.getPassword()));
                binding.editConformPassword.setText(String.valueOf(userDataModel.getPassword()));


                switch (userDataModel.getType()) {
                    case "Admin":
                        setSpinner(new String[]{"Not selected", "Admin"}, 1);
                        break;
                    case "Design Master":
                        setSpinner(new String[]{"Not selected", "Design Master", "Design Shade Master", "Yarn Master", "Machine Master", "jala Master", "Employee Master Data"}, 1);
                        break;
                    case "Design Shade Master":
                        setSpinner(new String[]{"Not selected", "Design Master", "Design Shade Master", "Yarn Master", "Machine Master", "jala Master", "Employee Master Data"}, 2);
                        break;
                    case "Yarn Master":
                        setSpinner(new String[]{"Not selected", "Design Master", "Design Shade Master", "Yarn Master", "Machine Master", "jala Master", "Employee Master Data"}, 3);
                        break;
                    case "Machine Master":
                        setSpinner(new String[]{"Not selected", "Design Master", "Design Shade Master", "Yarn Master", "Machine Master", "jala Master", "Employee Master Data"}, 4);
                        break;
                    case "jala Master":
                        setSpinner(new String[]{"Not selected", "Design Master", "Design Shade Master", "Yarn Master", "Machine Master", "jala Master", "Employee Master Data"}, 5);
                        break;
                    case "Employee Master Data":
                        setSpinner(new String[]{"Not selected", "Design Master", "Design Shade Master", "Yarn Master", "Machine Master", "jala Master", "Employee Master Data"}, 6);
                        break;
                }
                Log.d("defdsfsdfsdfdfs", "onCreateView: " + "yes i am work");

            }
        } else {
            if (userDataModel != null) {
                binding.editUsername.requestFocus();
            }
        }


        binding.btnSignUp.setOnClickListener(view -> {

            addArtist();
        });
    }

    public void setSpinner(String[] newCategory, int pos) {
        category = newCategory;
        binding.spinner.setOnItemSelectedListener(this);
        ArrayAdapter type_adapter = new ArrayAdapter(this, R.layout.white_simple_spinner_item, newCategory);
        type_adapter.setDropDownViewResource(R.layout.white_simple_spinner_dropdown_item);
        binding.spinner.setAdapter(type_adapter);
        binding.spinner.setSelection(pos);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        spinner_position = i;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void addArtist() {
        String username = binding.editUsername.getText().toString().trim();
        String phone = binding.editPhone.getText().toString().trim();
        String password = binding.editPassword.getText().toString().trim();
        String full_name = binding.editFullName.getText().toString().trim();
        String conform_password = binding.editConformPassword.getText().toString().trim();

        String expression = "^(?=[a-zA-Z0-9._]{8,20}$)(?!.*[_.]{2})[^_.].*[^_.]$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(username);


        if (TextUtils.isEmpty(username)) {
            new CustomSnackUtil().showSnack(this, "Please enter a username", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(phone)) {
            new CustomSnackUtil().showSnack(this, "Please enter a phone number", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(password)) {
            new CustomSnackUtil().showSnack(this, "Please enter a password", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(full_name)) {
            new CustomSnackUtil().showSnack(this, "Please enter a full name", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(conform_password)) {
            new CustomSnackUtil().showSnack(this, "Please enter a conform password", R.drawable.error_msg_icon);
        } else if (binding.spinner.getSelectedItemPosition() == 0) {
            new CustomSnackUtil().showSnack(this, "Please Select position", R.drawable.error_msg_icon);
        } else if (!password.equals(conform_password)) {
            new CustomSnackUtil().showSnack(this, "Password not match", R.drawable.error_msg_icon);
        } else if (!matcher.matches()) {
            new CustomSnackUtil().showSnack(this, "Invalid Username", R.drawable.error_msg_icon);
        } else {

            dao.getReference().whereEqualTo("u_name", binding.editUsername.getText().toString().trim()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                if (tracker.equals("edit")) {
                                    UserDataModel userModel = querySnapshot.getDocuments().get(0).toObject(UserDataModel.class);
                                    if (getUSERMODEL().getType().equals(Const.ADMIN)) {
                                        UserDataModel userData = new UserDataModel(userDataModel.getId(), username, full_name, category[spinner_position], phone, password);
                                        userData.setPermissionList(userDataModel.getPermissionList());

                                        Bundle bundle = new Bundle();
                                        bundle.putString("tracker", tracker);
                                        bundle.putString("position", userModel.getType());
                                        bundle.putSerializable(Const.USER_DATA, userData);
                                        startActivity(new Intent(AuthorityActivity.this, PermissionActivity.class).putExtras(bundle));
                                        finish();
                                    } else {
                                        DocumentReference documentReference = dao.getReference().document(userModel.getId());
                                        documentReference.update("f_name", full_name, "password", password, "phone_number", phone, "u_name", username, "type", category[spinner_position])
                                                .addOnSuccessListener(runnable -> {
                                                    DialogUtil.showUpdateSuccessDialog(AuthorityActivity.this, true);
                                                })
                                                .addOnFailureListener(e -> {
                                                    DialogUtil.showErrorDialog(AuthorityActivity.this);
                                                });
                                    }
                                } else {
                                    new CustomSnackUtil().showSnack(AuthorityActivity.this, "Username Already Exist", R.drawable.icon_warning);
                                }
                            } else {
                                if (tracker.equals("edit")) {
                                    new CustomSnackUtil().showSnack(AuthorityActivity.this, "Username Not Exist", R.drawable.icon_warning);
                                    String id = userDataModel.getId();
                                    UserDataModel userData = new UserDataModel(id, username, full_name, category[spinner_position], phone, password);
                                    userData.setPermissionList(userDataModel.getPermissionList());

                                    Bundle bundle = new Bundle();
                                    bundle.putString("tracker", "add");
                                    bundle.putSerializable(Const.USER_DATA, userData);
                                    startActivity(new Intent(AuthorityActivity.this, PermissionActivity.class).putExtras(bundle));
                                    finish();
                                } else {
                                    String id = dao.getId();
                                    UserDataModel userData = new UserDataModel(id, username, full_name, category[spinner_position], phone, password);

                                    Bundle bundle = new Bundle();
                                    bundle.putString("tracker", "add");
                                    bundle.putSerializable(Const.USER_DATA, userData);
                                    startActivity(new Intent(AuthorityActivity.this, PermissionActivity.class).putExtras(bundle));
                                    finish();
                                }
                            }
                        } else {
                            Log.e("Error", "Error getting documents: ", task.getException());
                        }
                    });

//            dao.getReference().orderByChild("u_name").equalTo(binding.editUsername.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@Nonnull DataSnapshot snapshot) {
//                    Log.d("dklfkdfkfjksj", "onDataChange: " + snapshot.exists());
//                    if (snapshot.exists()) {
//                        if (tracker.equals("edit")) {
//                            UserDataModel userModel = snapshot.getValue(UserDataModel.class);
//                            if (getUSERMODEL().getType().equals(Const.ADMIN)) {
//                                UserDataModel userData = new UserDataModel(userDataModel.getId(), username, full_name, category[spinner_position], phone, password);
//                                userData.setPermissionList(userDataModel.getPermissionList());
//
//                                Bundle bundle = new Bundle();
//                                bundle.putString("tracker", tracker);
//                                bundle.putString("position", userModel.getType());
//                                bundle.putSerializable(Const.USER_DATA, userData);
//                                startActivity(new Intent(AuthorityActivity.this, PermissionActivity.class).putExtras(bundle));
//                                finish();
//                            } else {
//                                HashMap<String, Object> hashMap = new HashMap<>();
//                                hashMap.put("f_name", full_name);
//                                hashMap.put("password", password);
//                                hashMap.put("phone_number", phone);
//                                hashMap.put("u_name", username);
//                                hashMap.put("type", category[spinner_position]);
//
//                                dao.update(userDataModel.getId(), hashMap).addOnSuccessListener(runnable -> {
//                                    DialogUtil.showUpdateSuccessDialog(AuthorityActivity.this, true);
//                                });
//                            }
//                        } else {
//                            new CustomSnackUtil().showSnack(AuthorityActivity.this, "Username Already Exist", R.drawable.icon_warning);
//                        }
//                    } else {
//                        if (tracker.equals("edit")) {
//                            new CustomSnackUtil().showSnack(AuthorityActivity.this, "Username Not Exist", R.drawable.icon_warning);
//                        } else {
//                            String id = dao.getId();
//                            UserDataModel userData = new UserDataModel(id, username, full_name, category[spinner_position], phone, password);
//
//                            Bundle bundle = new Bundle();
//                            bundle.putString("tracker", "add");
//                            bundle.putSerializable(Const.USER_DATA, userData);
//                            startActivity(new Intent(AuthorityActivity.this, PermissionActivity.class).putExtras(bundle));
//                            finish();
//                        }
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(@Nonnull DatabaseError error) {
//
//                }
//            });
        }
    }

    public void initToolbar() {
        binding.textTitle.setText("Authority");
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}