package com.example.pam3;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
    int id;
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent newIntent = new Intent(context, TaskDetailsActivity.class);
        id = intent.getIntExtra("id", 0);

        newIntent.putExtra("id", id);
        newIntent.putExtra("title", intent.getStringExtra("title"));
        newIntent.putExtra("desc", intent.getStringExtra("desc"));
        newIntent.putExtra("category", intent.getStringExtra("category"));
        newIntent.putExtra("cDate", intent.getStringExtra("cDate"));
        newIntent.putExtra("eDate", intent.getStringExtra("eDate"));
        newIntent.putExtra("eTime", intent.getStringExtra("eTime"));
        newIntent.putExtra("notification", 1);
        newIntent.putExtra("imgPath", intent.getStringExtra("imgPath"));

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channelID")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra("desc"))
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1, builder.build());
    }
}
