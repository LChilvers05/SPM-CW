package com.example.spmapp.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spmapp.R;

public class PrivacyAgreement extends AppCompatActivity {

    CheckBox privacyCheckbox;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_agreement);

        privacyCheckbox = findViewById(R.id.privacyAgreementCheckbox);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean policyAgreed = sharedPreferences.getBoolean("policyAgreed", false);
        privacyCheckbox.setChecked(policyAgreed);
    }

    public void checkboxClick(View view){
        sharedPreferences.edit().putBoolean("policyAgreed", ((CheckBox) view).isChecked()).commit();
    }
}
