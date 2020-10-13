package com.example.codeset;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Remind extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat notificationManager;
        int requestCode = intent.getIntExtra("requestCode",0);
        String platform = intent.getStringExtra("platform");
        String title = intent.getStringExtra("title");
        String contestUrl = intent.getStringExtra("url");
        long currTime = System.currentTimeMillis()/1000;
        long startTime = intent.getLongExtra("startTime",0);
        notificationManager = NotificationManagerCompat.from(context);
        long minutes = (startTime-currTime)/60;
        if(minutes<0)
            minutes=0;
        int icon = 0;
        if(platform==null||title==null||contestUrl==null)
            return;
        if(platform.equalsIgnoreCase("codeforces"))
            icon = R.drawable.code_forces_logo;
        else if(platform.equalsIgnoreCase("codechef"))
            icon = R.drawable.code_chef_logo;
        else if(platform.equalsIgnoreCase("atcoder"))
            icon = R.drawable.at_coder_logo;
        else if(platform.equalsIgnoreCase("google"))
            icon = R.drawable.google_logo;
        else if(platform.equalsIgnoreCase("topcoder"))
            icon = R.drawable.top_coder_logo;

        Intent contestsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(contestUrl));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 4, contestsIntent, 0);

        Notification notification = new NotificationCompat.Builder(context, "NotificationChannel")
                .setSmallIcon(icon)
                .setContentTitle("Contest in "+minutes+" minutes")
                .setContentText("There is  a " + platform + " contest coming up")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(platform+"'s contest : " + title + " is starting in "+minutes+" minutes."))
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(requestCode, notification);
    }
}
