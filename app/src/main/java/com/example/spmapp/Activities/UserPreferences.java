package com.example.spmapp.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spmapp.R;

public class UserPreferences extends AppCompatActivity {

    CheckBox screenCheck;
    CheckBox sleepCheck;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        screenCheck = findViewById(R.id.screentimeCheckBox);
        sleepCheck = findViewById(R.id.sleepCheckBox);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean screenChecked = sharedPreferences.getBoolean("screenCheck", false);
        boolean sleepChecked = sharedPreferences.getBoolean("sleepCheck", false);

        screenCheck.setChecked(screenChecked);
        sleepCheck.setChecked(sleepChecked);

    }

    public void checkboxClick(View view){
        switch(view.getId()){
            case R.id.sleepCheckBox:
                sharedPreferences.edit().putBoolean("sleepCheck", ((CheckBox) view).isChecked()).commit();
                break;
            case R.id.screentimeCheckBox:
                sharedPreferences.edit().putBoolean("screenCheck", ((CheckBox) view).isChecked()).commit();
                break;
        }
    }

}
