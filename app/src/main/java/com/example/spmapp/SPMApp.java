package com.example.spmapp;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.spmapp.Helpers.General;
import com.example.spmapp.Services.DataService;

public class SPMApp extends Application {
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onCreate() {
        super.onCreate();
        DataService.create(this);
    }
}
