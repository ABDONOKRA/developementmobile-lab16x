package com.example.lab16dev;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_NOTIF = 101;

    private TextView displayTime;
    private Button startBtn, stopBtn;
    
    private ChronometreService timerService;
    private boolean isServiceBound = false;

    // Pour mettre à jour l'interface utilisateur périodiquement
    private final Handler updateHandler = new Handler(Looper.getMainLooper());
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (isServiceBound && timerService != null) {
                int secondes = timerService.getTempsEcoule();
                displayTime.setText(formatTime(secondes));
            }
            // On recommence dans 1 seconde
            updateHandler.postDelayed(this, 1000);
        }
    };

    private final ServiceConnection timerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ChronometreService.TimeBinder binder = (ChronometreService.TimeBinder) service;
            timerService = binder.getServiceInstance();
            isServiceBound = true;
            // Dès qu'on est connecté, on commence à mettre à jour l'UI
            updateHandler.post(updateRunnable);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayTime = findViewById(R.id.tvChronometre);
        startBtn = findViewById(R.id.btnLancer);
        stopBtn = findViewById(R.id.btnArreter);

        startBtn.setOnClickListener(v -> checkPermissionAndStart());
        stopBtn.setOnClickListener(v -> terminateTimerService());
    }

    private void checkPermissionAndStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, 
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIF);
            } else {
                initiateTimerService();
            }
        } else {
            initiateTimerService();
        }
    }

    private void initiateTimerService() {
        Intent intent = new Intent(this, ChronometreService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        bindService(intent, timerConnection, Context.BIND_AUTO_CREATE);
    }

    private void terminateTimerService() {
        updateHandler.removeCallbacks(updateRunnable);
        Intent intent = new Intent(this, ChronometreService.class);
        intent.setAction("ACTION_STOP_TIMER");
        stopService(intent);

        if (isServiceBound) {
            unbindService(timerConnection);
            isServiceBound = false;
        }
        displayTime.setText(R.string.timer_default);
    }

    private String formatTime(int totalSeconds) {
        int mins = totalSeconds / 60;
        int secs = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
    }

    @Override
    protected void onDestroy() {
        updateHandler.removeCallbacks(updateRunnable);
        if (isServiceBound) {
            unbindService(timerConnection);
            isServiceBound = false;
        }
        super.onDestroy();
    }
}
