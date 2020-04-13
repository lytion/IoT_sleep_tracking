package com.devdivision.iotsleeptracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;


public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent.getExtras().getString("extra") == "yes")
//        {
//            NotificationHelper notificationHelper = new NotificationHelper(context);
//            NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
//            notificationHelper.getManager().notify(1, nb.build());
//        }

        Intent serviceIntent = new Intent(context,RingtonePlayingService.class);
        String state = intent.getExtras().getString("extra");
        serviceIntent.putExtra("extra", state);
        context.startForegroundService(serviceIntent);
    }

}