package com.devdivision.iotsleeptracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.core.app.NotificationCompat;


public class AlertReceiver extends BroadcastReceiver {
    private MediaPlayer mediaPlayer;
    @Override
    public void onReceive(Context context, Intent intent) {
        mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound);
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1, nb.build());
        mediaPlayer.start();
//        Uri.parse("android.resource://com.devdivision.iotsleeptracking/raw/alarm_sound.mp3")
    }

    public void stopAlarm() {
        mediaPlayer.stop();
    }
}