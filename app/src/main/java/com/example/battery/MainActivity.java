package com.example.battery;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
//    BroadcastReceiver mBatInfoReceiver;
    TextView chargedLevel,state,source,avaible,level,batteryTech,batteryTemp,batteryVoltage;
    ProgressBar progressBar;
    BroadcastReceiver batteryInfoReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chargedLevel = findViewById(R.id.chargedLevel);
        state = findViewById(R.id.state);
        source = findViewById(R.id.source);
        avaible = findViewById(R.id.avaible);
        level = findViewById(R.id.level);
        batteryTech = findViewById(R.id.technology);
        batteryTemp = findViewById(R.id.temperature);
        batteryVoltage = findViewById(R.id.tension);
        progressBar = findViewById(R.id.progressBar);

    }
    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        if (batteryInfoReceiver == null) {
            batteryInfoReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    updateBatteryData(intent);
                }
            };
        }

        registerReceiver(batteryInfoReceiver, intentFilter);
        Log.i(MainActivity.class.getName(),"onResume() | BroadcastReceiver registered.");
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(batteryInfoReceiver);
        Log.i(MainActivity.class.getName(),"onPause() | BroadcastReceiver unregistered.");
    }

    @SuppressLint("SetTextI18n")
    private void updateBatteryData(Intent intent) {
        boolean batteryPresent = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
        if (batteryPresent) {
            // BATTERY LEVEL
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryLevel = level * 100 / scale;
            chargedLevel.setText( batteryLevel + " %");
            progressBar.setProgress(batteryLevel);

            // BATTERY STATUS
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            String statusLbl;

            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    statusLbl = "Charging";
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    statusLbl = "Discharging";
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    statusLbl = "Full";
                    break;
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    statusLbl = "Unknown";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                default:
                    statusLbl = "Not charging";
                    break;
            }
            if (status != -1){
                state.setText( statusLbl);
            }

            // BATTERY POWER SOURCE
            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            String pluggedLbl;

            switch (plugged) {
                case BatteryManager.BATTERY_PLUGGED_AC:
                    pluggedLbl = "AC";
                    break;
                case BatteryManager.BATTERY_PLUGGED_USB:
                    pluggedLbl = "USB";
                    break;
                case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                    pluggedLbl = "Wireless";
                    break;
                default:
                    pluggedLbl = "None";
                    break;
            }
            source.setText(pluggedLbl);

            // BATTERY CAPACITY
            long capacity = getBatteryCapacity(this);

            if (capacity > 0) {
                avaible.setText( capacity + " mAh");
            }

            // BATTERY TECHNOLOGY
            if (intent.getExtras() != null) {
                String technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);

                if (!"".equals(technology)) {
                    batteryTech.setText( technology);
                }
            }

            // BATTERY TEMPERATURE
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);

            if (temperature > 0) {
                float temp = ((float) temperature) / 10f;
                batteryTemp.setText( temp + "°C");
            }

            // BATTERY VOLTAGE
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

            if (voltage > 0) {
                batteryVoltage.setText( voltage + " mV");
            }




        } else {
            Toast.makeText(this, "No battery present", Toast.LENGTH_SHORT).show();
        }
    }
    public long getBatteryCapacity(Context context) {
        BatteryManager mBatteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        long chargeCounter = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
        long capacity = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        return (long) (((float) chargeCounter / (float) capacity) * 100f);
    }
}