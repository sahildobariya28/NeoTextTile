package com.example.texttile.presentation.screen.features.reading_feature.daily_reading.reading;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
public class DailyReadingViewModelFactory implements ViewModelProvider.Factory {

    String tracker;
    Activity activity;

    public DailyReadingViewModelFactory(String tracker, Activity activity){
        this.tracker = tracker;
        this.activity = activity;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DailyReadingViewModel.class)) {
            return (T) new DailyReadingViewModel(tracker, activity);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
