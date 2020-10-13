package com.example.codeset;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SchedulerRatingLoader extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Helper.loadRatings(context);
    }
}
