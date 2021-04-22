package com.example.spmapp.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.spmapp.R;
import com.example.spmapp.ViewModels.DataCollectorViewModel;

import java.time.LocalDateTime;
@RequiresApi(api = Build.VERSION_CODES.Q)
public class DeleteDataActivity extends AppCompatActivity {

    //views
    EditText startTimeEditTxt;
    EditText endTimeEditTxt;
    Button deleteDataButton;
    CheckBox screenCheck;
    CheckBox sleepCheck;

    //Pickers
    LocalDateTime localDateTime;

    DataCollectorViewModel viewModel;

    int startValue = -1;
    int endValue = -2;

    Boolean forSleep = false;
    Boolean forScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_data);
        //grab views
        startTimeEditTxt = findViewById(R.id.deleteStartDateEntry);
        endTimeEditTxt = findViewById(R.id.deleteEndDateEntry);
        deleteDataButton = findViewById(R.id.deleteDataButton);
        screenCheck = findViewById(R.id.deleteScreenCheck);
        sleepCheck = findViewById(R.id.deleteSleepCheck);

        localDateTime = LocalDateTime.now();

        viewModel = new DataCollectorViewModel(getApplication(), this, true);

        setDeleteButton(checkFields());
        launchStartPicker();
        launchEndPicker();
    }

    private void launchStartPicker() {
        startTimeEditTxt.setInputType(0);
        startTimeEditTxt.setOnClickListener(view -> new DatePickerDialog(this, (datePicker, yearSelected, monthSelected, daySelected) -> {
            String dateSelected = daySelected + "/" + (monthSelected + 1) + "/" + yearSelected;
            startTimeEditTxt.setText(dateSelected);
            startValue = (Integer.parseInt(String.valueOf(yearSelected))) + (Integer.parseInt(String.valueOf(monthSelected))/100) + (Integer.parseInt(String.valueOf(daySelected))/10000);
            setDeleteButton(checkFields());
        }, localDateTime.getYear(), localDateTime.getMonthValue() - 1, localDateTime.getDayOfMonth()).show());
    }

    private void launchEndPicker() {
        endTimeEditTxt.setInputType(0);
        endTimeEditTxt.setOnClickListener(view -> new DatePickerDialog(this, (datePicker, yearSelected, monthSelected, daySelected) -> {
            String dateSelected = daySelected + "/" + (monthSelected + 1) + "/" + yearSelected;
            System.out.println(dateSelected);
            endTimeEditTxt.setText(dateSelected);
            endValue = (Integer.parseInt(String.valueOf(yearSelected))) + (Integer.parseInt(String.valueOf(monthSelected))/100) + (Integer.parseInt(String.valueOf(daySelected))/10000);
            setDeleteButton(checkFields());
        }, localDateTime.getYear(), localDateTime.getMonthValue() - 1, localDateTime.getDayOfMonth()).show());
    }

    private Boolean checkFields() {
        if (TextUtils.isEmpty(endTimeEditTxt.getText().toString())) { return false; }
        if (TextUtils.isEmpty(startTimeEditTxt.getText().toString())) { return false; }
        if (endValue < startValue) {
            Toast.makeText(this, "Start Date Must Be Before Start Date", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    public void checkboxClick(View view){
        switch(view.getId()){
            case R.id.deleteSleepCheck:
                forSleep = ((CheckBox) view).isChecked();
                System.out.println(forSleep);
                break;
            case R.id.deleteScreenCheck:
                forScreen = ((CheckBox) view).isChecked();
                break;
        }
    }

    private void setDeleteButton(Boolean enabled) {
        deleteDataButton.setEnabled(enabled);
        if (enabled) {
            deleteDataButton.setAlpha(1.0F);
        } else {
            deleteDataButton.setAlpha(0.5F);
        }
    }

    public void deleteData(View view) {
        if (checkFields()) {
            if (forSleep) {
                viewModel.deleteSession(startTimeEditTxt.getText().toString(),
                        endTimeEditTxt.getText().toString(), true);
            }
            if (forScreen) {
                viewModel.deleteSession(startTimeEditTxt.getText().toString(),
                        endTimeEditTxt.getText().toString(), false);
            }
            if (!forSleep && !forScreen) {
                Toast.makeText(this, "Choose What to Delete", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show();
        }
        startTimeEditTxt.getText().clear();
        endTimeEditTxt.getText().clear();
        setDeleteButton(checkFields());
    }
}