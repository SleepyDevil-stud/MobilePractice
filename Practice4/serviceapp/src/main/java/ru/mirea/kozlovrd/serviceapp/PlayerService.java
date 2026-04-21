package ru.mirea.kozlovrd.serviceapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class PlayerService extends Service {
    private MediaPlayer mediaPlayer;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final String TRACK_TITLE = "Metallica - (Anesthesia) - Pulling Teeth";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            Toast.makeText(this, "Сейчас играет: " + TRACK_TITLE, Toast.LENGTH_LONG).show();
            mediaPlayer.setOnCompletionListener(mp -> {
                stopForeground(true);
                stopSelf();
            });
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Создаем уведомление и запускаем foreground сервис
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification(),
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);

        // Инициализируем MediaPlayer
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.metallica);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(false);
            } else {
                stopSelf();
            }
        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Player Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Music player foreground service channel");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Music Player")
                .setContentText(TRACK_TITLE)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Сейчас играет: " + TRACK_TITLE))
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        // Для Android 14+ добавляем тип медиаплеера в уведомление
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            builder.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE);
        }

        return builder.build();
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopForeground(true);
        super.onDestroy();
    }
}