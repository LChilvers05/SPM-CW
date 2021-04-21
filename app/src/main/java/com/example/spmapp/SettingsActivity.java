package com.example.spmapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.example.spmapp.Activities.HomeActivity;
import com.example.spmapp.Helpers.NotificationReceiver;

import java.time.LocalDateTime;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    EditText reminderTimeEditTxt;
    TimePickerDialog timePicker;

    LocalDateTime localDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        reminderTimeEditTxt = findViewById(R.id.reminderTimeEntry);
        localDateTime = LocalDateTime.now();

        launchTimePicker();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void launchTimePicker() {
        //edit text
        reminderTimeEditTxt.setInputType(0);

        reminderTimeEditTxt.setOnClickListener(view -> {
            timePicker = new TimePickerDialog(SettingsActivity.this, (thisTimePicker, selectedHours, selectedMinutes) -> {
                String timeString;
                if(selectedMinutes < 10) {
                    timeString = selectedHours + ":0" + selectedMinutes;
                } else {
                    timeString = selectedHours + ":" + selectedMinutes;
                }
                reminderTimeEditTxt.setText(timeString);
                setReminder(selectedHours, selectedMinutes);

            }, localDateTime.getHour(), localDateTime.getMinute(), true);
            timePicker.show();
        });
    }

    private void setReminder(int hour, int minute) {
        Intent intent = new Intent(SettingsActivity.this, NotificationReceiver.class);
        PendingIntent notificationIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, hour);
        startTime.set(Calendar.MINUTE, minute);
        startTime.set(Calendar.SECOND, 0);
        long alarmStartTime = startTime.getTimeInMillis();

//        alarm.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime, AlarmManager.INTERVAL_DAY, notificationIntent);
        alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, notificationIntent);
    }
}