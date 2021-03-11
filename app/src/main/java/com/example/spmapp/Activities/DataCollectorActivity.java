package com.example.spmapp.Activities;

import android.os.Handler;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.spmapp.Helpers.General;
import com.example.spmapp.ViewModels.DataCollectorViewModel;

/**
 * Parent class of ScreenCollectorActivity and SleepCollectorActivity
 */
public abstract class DataCollectorActivity extends AppCompatActivity {

    DataCollectorViewModel viewModel;
    //views
    TextView timerDisplayTextView;
    ImageButton startTimerBtn;
    ImageButton endTimerBtn;
    //timer variables
    protected final Handler timerHandler = new Handler();
    Long timerStart = 0L;

    @Override
    protected void onStart() {
        super.onStart();
        //set start/stop buttons initial state
        startTimerBtn.setEnabled(true);
        startTimerBtn.setAlpha(1.0F);
        endTimerBtn.setEnabled(false);
        endTimerBtn.setAlpha(0.5F);

        timerStart = viewModel.getTimerStart();
        //timer already started
        if (timerStart != 0L) {
            //begin updating the timer display
            runTimer();
            //only allow stop button to be pressed
            swapButtons();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stop the timer when leaving activity
        timerHandler.removeCallbacks(UpdateTimerTask);
        timerStart = 0L;
    }

    //enable and disable timer buttons
    protected void swapButtons() {
        startTimerBtn.setEnabled(!startTimerBtn.isEnabled());
        endTimerBtn.setEnabled(!endTimerBtn.isEnabled());
        if (startTimerBtn.isEnabled()) {
            startTimerBtn.setAlpha(1.0F);
            endTimerBtn.setAlpha(0.5F);
        } else {
            startTimerBtn.setAlpha(0.5F);
            endTimerBtn.setAlpha(1.0F);
        }
    }

    //begin updating the timer display
    protected void runTimer() {
        timerStart = viewModel.getTimerStart();
        timerHandler.removeCallbacks(UpdateTimerTask);
        timerHandler.postDelayed(UpdateTimerTask, 100);
    }
    //timer display thread
    protected final Runnable UpdateTimerTask = new Runnable() {
        //recursive loop to update timer display
        public void run() {
            final long start = timerStart;
            int seconds = (int)(General.getUnixTime() - start);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;
            minutes = minutes % 60;
            //00:00:00 format
            String secsDisplay = ":" + seconds;
            String minDisplay = ":" + minutes;
            String hrDisplay = "" + hours;

            if (seconds < 10) { secsDisplay = ":0" + seconds; }
            if (minutes < 10) { minDisplay = ":0" + minutes; }
            if (hours < 10) { hrDisplay = "0" + hours; }

            String timeDisplay = hrDisplay + minDisplay + secsDisplay;

            timerDisplayTextView.setText(timeDisplay);
            timerHandler.postDelayed(this, 0);
        }
    };
}
