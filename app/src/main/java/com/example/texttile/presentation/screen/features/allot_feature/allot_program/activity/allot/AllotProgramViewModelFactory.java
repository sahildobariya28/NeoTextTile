package com.example.texttile.presentation.screen.features.allot_feature.allot_program.activity.allot;

import android.app.Activity;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.checkerframework.checker.nullness.qual.NonNull;

public class AllotProgramViewModelFactory implements ViewModelProvider.Factory {

    String tracker;
    Activity activity;

    public AllotProgramViewModelFactory(String tracker, Activity activity){
        this.tracker = tracker;
        this.activity = activity;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AllotProgramViewModel.class)) {
            return (T) new AllotProgramViewModel(tracker, activity);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
