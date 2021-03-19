package com.example.spmapp;

import android.app.AppOpsManager;
import android.app.Application;
import android.content.Context;

import com.example.spmapp.Services.DataService;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;

public class SPMApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DataService.create(this);
    }
}
