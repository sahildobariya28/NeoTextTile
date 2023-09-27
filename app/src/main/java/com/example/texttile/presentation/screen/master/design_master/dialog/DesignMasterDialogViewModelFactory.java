package com.example.texttile.presentation.screen.master.design_master.dialog;

import android.app.Activity;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.checkerframework.checker.nullness.qual.NonNull;

public class DesignMasterDialogViewModelFactory implements ViewModelProvider.Factory{

    String tracker;
    Activity activity;

    public DesignMasterDialogViewModelFactory(String tracker, Activity activity){
        this.tracker = tracker;
        this.activity = activity;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DesignMasterDialogViewModel.class)) {
            return (T) new DesignMasterDialogViewModel(tracker, activity);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
