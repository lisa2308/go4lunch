package com.example.go4lunch.ui.activites;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.go4lunch.R;
import com.example.go4lunch.utils.SharedPreferencesManager;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;


public class SettingsActivity extends AppCompatActivity {

    private final static String NOTIF_KEY = "notif key";
    @BindView(R.id.activity_settings_switch)
    Switch notificationSwitch;

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        initSwitchButton();

    }

    private void initSwitchButton() {
        boolean isNotificationOn = SharedPreferencesManager.getBoolean(this, NOTIF_KEY);
        if(isNotificationOn){
            notificationSwitch.setChecked(true);
        }else {
            notificationSwitch.setChecked(false);

        }

    }

    @OnCheckedChanged(R.id.activity_settings_switch)
    public void clickNotificationSwitch(CompoundButton button, boolean checked){
        if (checked) {
            SharedPreferencesManager.putBoolean(this, NOTIF_KEY,true);
            enableAlarmManager();
        }else {
            SharedPreferencesManager.putBoolean(this, NOTIF_KEY,false);
            disableAlarmManager();
        }
    }

    private void enableAlarmManager() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 20);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    private void disableAlarmManager() {
        alarmManager.cancel(alarmIntent);
    }
}
