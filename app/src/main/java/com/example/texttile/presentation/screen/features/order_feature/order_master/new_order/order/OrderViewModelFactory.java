package com.example.texttile.presentation.screen.features.order_feature.order_master.new_order.order;

import android.app.Activity;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.checkerframework.checker.nullness.qual.NonNull;

public class OrderViewModelFactory implements ViewModelProvider.Factory {

    Activity activity;
    public OrderViewModelFactory(Activity activity){
        this.activity = activity;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(OrderViewModel.class)) {
            return (T) new OrderViewModel(activity);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
