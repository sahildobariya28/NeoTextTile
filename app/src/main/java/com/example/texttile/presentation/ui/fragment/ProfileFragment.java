package com.example.texttile.presentation.ui.fragment;

import static android.content.Context.MODE_PRIVATE;

import static com.example.texttile.core.MyApplication.getUSERMODEL;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import javax.annotation.Nonnull;
import androidx.fragment.app.Fragment;

import com.example.texttile.R;
import com.example.texttile.databinding.FragmentProfileBinding;
import com.example.texttile.presentation.ui.activity.ExcelToJson;
import com.example.texttile.presentation.ui.activity.LoginActivity;
import com.example.texttile.presentation.ui.interfaces.LoadFragmentChangeListener;
import com.example.texttile.presentation.screen.authority.user.AuthorityActivity;
import com.example.texttile.presentation.ui.util.Const;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    LoadFragmentChangeListener loadFragmentChangeListener;

    public ProfileFragment(LoadFragmentChangeListener loadFragmentChangeListener) {
        this.loadFragmentChangeListener = loadFragmentChangeListener;
    }

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());

        initToolbar();


        if (getUSERMODEL() != null) {
            binding.textFullName.setText(getUSERMODEL().getF_name());
            binding.textTitle.setText(getUSERMODEL().getU_name());
            binding.textType.setText(getUSERMODEL().getType());
            binding.textPhone.setText(getUSERMODEL().getPhone_number());

            binding.btnEdit.setOnClickListener(view -> {

                Bundle bundle = new Bundle();
                bundle.putString("tracker", "edit");
                bundle.putString("position", "user");
                bundle.putSerializable(Const.USER_DATA, getUSERMODEL());
                startActivity(new Intent(getActivity(), AuthorityActivity.class).putExtras(bundle));
            });
        }


        binding.btnExportToJson.setOnClickListener(view -> {
            getActivity().startActivity(new Intent(getContext(), ExcelToJson.class));
        });
        binding.btnLogout.setOnClickListener(view -> {

            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.exit_screen);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView text_description = dialog.findViewById(R.id.text_description);
            TextView text_title = dialog.findViewById(R.id.text_title);
            TextView btn_no = dialog.findViewById(R.id.text_cancel);
            TextView btn_yes = dialog.findViewById(R.id.text_exit);

            text_description.setText("Are you sure!\nYou want to logout this account ?");
            text_title.setText("Logout ?");
            btn_no.setText("No");
            btn_yes.setText("YES");
            btn_no.setBackgroundColor(Color.parseColor("#F15249"));
            btn_yes.setBackgroundColor(Color.parseColor("#32BA7C"));

            btn_no.setOnClickListener(v1 -> {
                dialog.dismiss();
            });
            btn_yes.setOnClickListener(v1 -> {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("isLogin", null);
                myEdit.commit();
                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            });

            dialog.show();
        });
        return binding.getRoot();
    }

    public void initToolbar() {

        binding.textTitle.setText("Profile");
        binding.btnBack.setOnClickListener(view -> {
            getActivity().onBackPressed();
        });
    }

}