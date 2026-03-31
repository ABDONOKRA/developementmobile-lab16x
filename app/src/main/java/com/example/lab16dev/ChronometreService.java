package com.example.lab16dev;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service gérant le chronomètre en arrière-plan avec une notification persistante.
 */
public class ChronometreService extends Service {

    private static final int NOTIF_ID = 2024;
    private static final String CHANNEL_ID = "timer_service_channel";

    private final IBinder serviceBinder = new TimeBinder();
    private int elapsedSeconds = 0;
    private boolean isTimerRunning = false;
    private ScheduledExecutorService timerExecutor;
    private NotificationManager timerNotifManager;

    /**
     * Permet à l'activité de se lier au service.
     */
    public class TimeBinder extends Binder {
        public ChronometreService getServiceInstance() {
            return ChronometreService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timerNotifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        setupNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String command = (intent != null) ? intent.getAction() : null;

        if ("ACTION_STOP_TIMER".equals(command)) {
            stopSelf();
            return START_NOT_STICKY;
        }

        if (!isTimerRunning) {
            isTimerRunning = true;
            startForeground(NOTIF_ID, buildTimerNotification());
            beginCounting();
        }
        
        return START_STICKY;
    }

    /**
     * Retourne le temps actuel pour l'interface utilisateur.
     */
    public int getTempsEcoule() {
        return elapsedSeconds;
    }

    private void beginCounting() {
        timerExecutor = Executors.newSingleThreadScheduledExecutor();
        timerExecutor.scheduleWithFixedDelay(() -> {
            elapsedSeconds++;
            refreshNotification();
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Suivi du Chronomètre",
                    NotificationManager.IMPORTANCE_LOW
            );
            timerNotifManager.createNotificationChannel(channel);
        }
    }

    private Notification buildTimerNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Chrono Actif")
                .setContentText("Durée écoulée : " + getFormattedTime(elapsedSeconds))
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void refreshNotification() {
        timerNotifManager.notify(NOTIF_ID, buildTimerNotification());
    }

    private String getFormattedTime(int totalSeconds) {
        int mins = totalSeconds / 60;
        int secs = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    @Override
    public void onDestroy() {
        isTimerRunning = false;
        elapsedSeconds = 0;
        if (timerExecutor != null) {
            timerExecutor.shutdown();
        }
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE);
        super.onDestroy();
    }
}
