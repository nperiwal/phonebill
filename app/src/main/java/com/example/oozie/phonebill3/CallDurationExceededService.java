package com.example.oozie.phonebill3;

import android.Manifest;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CallDurationExceededService extends IntentService {

    private static final int NOTIF_ID = 1;
    private static final String TAG = "Call duration service";

    private int callDurationLimit;
    private int outgoingCallDuration = 0;

    public CallDurationExceededService(String name) {
        super(name);
    }

    public CallDurationExceededService() {
        super("CallDurationExceededService");
    }

    /*@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleNotification();
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "Service running");
        if (checkIfNotificationRequired()){
            handleNotification();
        }
    }

    private boolean checkIfNotificationRequired() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        String tempDuration = sharedPref.getString("callDuration", null);
        String tempFromDate = sharedPref.getString("fromDate", null);
        String tempToDate = sharedPref.getString("toDate", null);

        DateFormat format = new SimpleDateFormat("d-M-yyyy");
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Calcutta");
        format.setTimeZone(timeZone);
        Date fromDate = null;
        Date toDate = null;
        try {
            fromDate = format.parse(tempFromDate);
            toDate = format.parse(tempToDate);
            Calendar c = Calendar.getInstance();
            c.setTime(toDate);
            c.setTimeZone(timeZone);
            c.add(Calendar.DATE, 1);
            toDate = c.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Duration Limit Set is " + tempDuration);
        Log.v(TAG, "FromDate " + fromDate);
        Log.v(TAG, "ToDate " + toDate);

        callDurationLimit = Integer.parseInt(tempDuration);
        calculateOutgoingCallDuration(fromDate, toDate);
        if (outgoingCallDuration > callDurationLimit) {
            Log.v(TAG, "Outgoing Call Duration Limit Exceeded");
            return true;
        }
        return false;
    }

    private void calculateOutgoingCallDuration(Date fromDate, Date toDate) {
        int result = 0;
        int count = 0;

        StringBuffer sb = new StringBuffer();
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";

        /* Query the CallLog Content Provider */

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) !=
                PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null,
                null, strOrder);

        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        while (managedCursor.moveToNext()) {
            String callTypeCode = managedCursor.getString(type);
            String strcallDate = managedCursor.getString(date);
            Date callDate = new Date(Long.valueOf(strcallDate));

            Calendar c = Calendar.getInstance();
            TimeZone timeZone = TimeZone.getTimeZone("Asia/Calcutta");
            c.setTime(callDate);
            c.setTimeZone(timeZone);
            callDate = c.getTime();

            String callDuration = managedCursor.getString(duration);
            int callcode = Integer.parseInt(callTypeCode);

            if (callcode == CallLog.Calls.OUTGOING_TYPE) {
                //Log.v(TAG, "Call Date: " + callDate);
                if (callDate.compareTo(fromDate)>=0 && callDate.compareTo(toDate)<0) {
                    count++;
                    int durationInSec = Integer.parseInt(callDuration);
                    //Log.v(TAG, "Outgoing call: " + durationInSec);
                    int durationInMin = (durationInSec%60 == 0) ?
                            durationInSec/60 : durationInSec/60+1;
                    result += durationInMin;
                }
            }
        }
        Log.v(TAG, "Total number of outgoing calls: " + count);
        Log.v(TAG, "outgoingCallDuration " + result);
        Log.v(TAG, "Limit Set is " + callDurationLimit);
        outgoingCallDuration = result;
    }

    private void handleNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.reminder)
                        .setContentTitle("Outgoing Call Duration")
                        .setContentText("You have exceeded the outgoing call duration limit");


        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String[] events = new String[3];
        events[0] = "You have exceeded the outgoing call duration limit";
        events[1] = "Call duration limit is " + callDurationLimit + "min";
        events[2] = "Total OutgoingCallDuration is " + outgoingCallDuration + "min";
        inboxStyle.setBigContentTitle("Details:");
        for (int i=0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }
        mBuilder.setStyle(inboxStyle);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, PhoneBillActivity.class);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(PhoneBillActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        // NOTIF_ID allows you to update the notification later on.
        mNotificationManager.notify(NOTIF_ID, mBuilder.build());
    }
}
