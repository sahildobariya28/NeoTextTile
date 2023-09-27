package com.example.texttile.presentation.screen.master.shade_master.shade_master;

import android.app.Activity;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.checkerframework.checker.nullness.qual.NonNull;

public class ShadeMultipleViewModelFactory implements ViewModelProvider.Factory{

    Activity activity;
    FragmentManager fragmentManager;

    public ShadeMultipleViewModelFactory(Activity activity, FragmentManager fragmentManager){
        this.activity = activity;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ShadeMultipleViewModel.class)) {
            return (T) new ShadeMultipleViewModel(activity, fragmentManager);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
