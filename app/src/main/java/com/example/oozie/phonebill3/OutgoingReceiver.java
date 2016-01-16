package com.example.oozie.phonebill3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Broadcast receiver to detect the outgoing calls.
 */
public class OutgoingReceiver extends BroadcastReceiver {

    private static final String TAG = "OutgoingReceiver";

    public OutgoingReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        Log.v(TAG, "Outgoing Call Detected: " + number);
        Intent i = new Intent(context, CallDurationExceededService.class);
        context.startService(i);

    }

}