package com.example.codeset;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SchedulerSubmissionLoader extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Helper.loadSubmissions(context);
    }
}
