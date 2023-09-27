package com.example.texttile.presentation.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.texttile.databinding.FragmentMainBinding;
import com.example.texttile.presentation.ui.interfaces.LoadFragmentChangeListener;
import com.example.texttile.presentation.screen.authority.list.UserListActivity;
import com.example.texttile.presentation.ui.util.Const;


public class MainFragment extends Fragment implements LoadFragmentChangeListener {

    FragmentMainBinding binding;
    Fragment fragment;
    String tracker;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(getLayoutInflater());

        tracker = getArguments().getString("tracker");

        FragmentManager fm = getChildFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        if (tracker.equals("Master")) {
            fragment = new MasterFragment(this);
            Bundle bundle = new Bundle();
            bundle.putString("tracker", "Master");
            fragment.setArguments(bundle);


            loadFragment(fragment);
        } else if (tracker.equals(Const.AUTHORITY)) {
            startActivity(new Intent(getActivity(), UserListActivity.class));
        } else {
            fragment = new MasterFragment(this);
            Bundle bundle = new Bundle();
            bundle.putString("tracker", "Options");
            fragment.setArguments(bundle);
            loadFragment(fragment);
        }


        return binding.getRoot();
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(binding.container.getId(), fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void onBackPressed(LoadFragmentChangeListener loadFragmentChangeListener) {

        if (getChildFragmentManager().getBackStackEntryCount() > 1) {
            getChildFragmentManager().popBackStackImmediate();
        } else {
            Fragment fragment = new DashboardFragment();
            loadFragmentChangeListener.onLoadFragment(fragment, 1);
        }
    }

    @Override
    public void onLoadFragment(Fragment fragment, int position) {
        loadFragment(fragment);
    }
}