package com.example.texttile.presentation.screen.features.reading_feature.view_order;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


public class ViewOrderViewModelFactory implements ViewModelProvider.Factory {
    Activity activity;

    public ViewOrderViewModelFactory(Activity activity){
        this.activity = activity;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ViewOrderViewModel.class)) {
            return (T) new ViewOrderViewModel(activity);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
