package com.example.spmapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spmapp.Helpers.Constants;
import com.example.spmapp.Helpers.NotificationReceiver;
import com.example.spmapp.Models.StreakCalculator;

import java.time.LocalDateTime;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences prefs;
    EditText reminderTimeEditTxt;
    TimePickerDialog bedtimePicker;

    EditText sleepLengthEditTxt;
    TimePickerDialog lengthPicker;

    TextView streakTextView;

    SharedPreferences sharedPreferences;

    LocalDateTime localDateTime;

    Intent intent;
    PendingIntent notificationIntent;
    AlarmManager alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        intent = new Intent(SettingsActivity.this, NotificationReceiver.class);
        notificationIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

        reminderTimeEditTxt = findViewById(R.id.reminderTimeEntry);
        sleepLengthEditTxt = findViewById(R.id.sleepGoal);

        streakTextView = findViewById(R.id.streakCounter);

        localDateTime = LocalDateTime.now();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        calculateStreak();
        launchBedtimePicker();
        launchSleepLengthPicker();
    }

    @Override
    protected void onStart() {
        super.onStart();
        prefs = this.getSharedPreferences(Constants.REMINDER_PREFS_KEY, Context.MODE_PRIVATE);
        String notificationTime = prefs.getString(Constants.NOTIFY_START_KEY, "NULL");
        if (!notificationTime.equals("NULL")) {
            reminderTimeEditTxt.setText(notificationTime);
        }
    }

    private void launchBedtimePicker() {
        //edit text
        reminderTimeEditTxt.setInputType(0);

        reminderTimeEditTxt.setOnClickListener(view -> {
            bedtimePicker = new TimePickerDialog(SettingsActivity.this, (thisTimePicker, selectedHours, selectedMinutes) -> {
                String timeString;
                if(selectedMinutes < 10) {
                    timeString = selectedHours + ":0" + selectedMinutes;
                } else {
                    timeString = selectedHours + ":" + selectedMinutes;
                }
                reminderTimeEditTxt.setText(timeString);
                SharedPreferences.Editor prefsEditor = prefs.edit();
                prefsEditor.putString(Constants.NOTIFY_START_KEY, timeString);
                prefsEditor.apply();

                //delete old alarm
                if (notificationIntent != null && alarm != null) {
                    alarm.cancel(notificationIntent);
                }

                setReminder(selectedHours, selectedMinutes);

                Toast.makeText(this, "Notification set", Toast.LENGTH_SHORT).show();

            }, localDateTime.getHour(), localDateTime.getMinute(), true);
            bedtimePicker.show();
        });
    }

    private void setReminder(int hour, int minute) {
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, hour);
        startTime.set(Calendar.MINUTE, minute);
        startTime.set(Calendar.SECOND, 0);
        long alarmStartTime = startTime.getTimeInMillis();

        //TODO: WANT THIS ONE BUT FOR SOME REASON DOES NOT WORK FOR PRESENTATION JUST USE SET
//        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime, AlarmManager.INTERVAL_DAY, notificationIntent);
        alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, notificationIntent);
    }

    public void cancelAlarm(View view) {
        reminderTimeEditTxt.getText().clear();
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(Constants.NOTIFY_START_KEY, null);
        prefsEditor.apply();
        alarm.cancel(notificationIntent);
    }

    private void launchSleepLengthPicker(){

        sleepLengthEditTxt.setInputType(0);

        sleepLengthEditTxt.setOnClickListener(view -> {
            lengthPicker = new TimePickerDialog(this, (startTimePicker, selectedHours, selectedMinutes) -> {
                String sessionLengthString;
                if(selectedMinutes<10){
                    sessionLengthString = selectedHours + ":0" + selectedMinutes;
                }else{
                    sessionLengthString = selectedHours + ":" + selectedMinutes;
                }
                sleepLengthEditTxt.setText(sessionLengthString);
                setLengthGoal(selectedHours, selectedMinutes);
            }, 0, 0, true);
            lengthPicker.setTitle("Select session length");
            lengthPicker.show();
        });
    }

    private void setLengthGoal(int hours, int minutes){
        long length = (hours * 3600) + ((minutes/60)*3600);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("lengthGoal", length).apply();
        Toast.makeText(this, "Length goal set", Toast.LENGTH_SHORT).show();
        calculateStreak();
    }

    private void calculateStreak(){
        long goal = sharedPreferences.getLong("lengthGoal", 28800);
        //defaults to equivalent of 8 hours, if no goal set by user
        StreakCalculator streakCalculator = new StreakCalculator(getApplication(), goal);
        int streakCount = streakCalculator.getStreak(goal);
        streakTextView.setText("Streak:"+streakCount);
    }
}