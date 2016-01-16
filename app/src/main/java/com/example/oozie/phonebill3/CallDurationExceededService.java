package com.example.oozie.phonebill3;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class CallDurationExceededService extends IntentService {

    private static final int NOTIF_ID = 1;

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
        Log.v("Call duration service", "Service running");
        handleNotification();
    }

    private void handleNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.call)
                        .setContentTitle("Outgoing Call Duration")
                        .setContentText(" You have exceeded the outgoing call duration");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, PhoneBillActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(PhoneBillActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIF_ID, mBuilder.build());
    }
}
