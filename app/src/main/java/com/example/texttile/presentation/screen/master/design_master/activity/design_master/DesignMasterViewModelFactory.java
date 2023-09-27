package com.example.texttile.presentation.screen.master.design_master.activity.design_master;

import android.app.Activity;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.checkerframework.checker.nullness.qual.NonNull;

public class DesignMasterViewModelFactory implements ViewModelProvider.Factory{

    String tracker;
    Activity activity;

    public DesignMasterViewModelFactory(String tracker, Activity activity){
        this.tracker = tracker;
        this.activity = activity;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DesignMasterViewModel.class)) {
            return (T) new DesignMasterViewModel(tracker, activity);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
