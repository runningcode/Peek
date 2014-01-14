package com.osacky.peek;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.util.concurrent.TimeUnit;

public class NotificationCountdownService extends Service {

    // Private actions processed by the receiver
    public static final String START_COUNTDOWN = "start_countdown";
    public static final String KILL_NOTIF = "kill_notification";
    public static final String PREF_START_TIME  = "sw_start_time";
    public static final String PREF_ACCUM_TIME = "sw_accum_time";
    public static final String NOTIF_CLOCK_BASE = "notif_clock_base";
    public static final String NOTIF_CLOCK_ELAPSED = "notif_clock_elapsed";
    public static final String NOTIF_CLOCK_RUNNING = "notif_clock_running";

    public static final String SELECT_TAB_INTENT_EXTRA = "deskclock.select.tab";

    public static final int STOPWATCH_TAB_INDEX = 3;

    // Member fields
    private NotificationManager mNotificationManager;
    private CountDownTimer mPeekCountDownTimer;

    // Constants for intent information
    // Make this a large number to avoid the alarm ID's which seem to be 1, 2, ...
    // Must also be different than TimerReceiver.IN_USE_NOTIFICATION_ID
    private static final int NOTIFICATION_ID = Integer.MAX_VALUE - 1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return Service.START_NOT_STICKY;
        }

        String actionType = intent.getAction();
        if (actionType.equals(START_COUNTDOWN)) {
            Bundle extras = intent.getExtras();
            String username = extras.getString("username");
            setNotification(username);
        } else if (actionType.equals(KILL_NOTIF)) {
            mNotificationManager.cancel(NOTIFICATION_ID);
            if (mPeekCountDownTimer != null) {
                mPeekCountDownTimer.onFinish();
                mPeekCountDownTimer.cancel();
            }
            stopSelf();
        }

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mNotificationManager.cancel(NOTIFICATION_ID);
        clearSavedNotification();
    }

    private void setNotification(String username) {
        Context context = getApplicationContext();
        // Intent to load the app for a non-button click.
        Intent intent = new Intent(context, CreatePeekActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("username", username);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_UPDATE_CURRENT);

        Intent dismissIntent = new Intent(context, NotificationCountdownService.class);
        dismissIntent.setAction(KILL_NOTIF);
        PendingIntent dismissPendingIntent = PendingIntent.getService(context, 0, dismissIntent, 0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setOngoing(true)
                .setSmallIcon(R.drawable.notification)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentText("Peek Request from " + username)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, context.getString(android.R.string.cancel), dismissPendingIntent);
        Utils.getNameAndPic(context, username, notification);
        mPeekCountDownTimer = new PeekCountDownTimer(notification).start();
        mNotificationManager.notify(NOTIFICATION_ID, notification.build());
    }

    /** Save the notification to be shown when the app is closed. **/
    private void saveNotification(long clockTime) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(NOTIF_CLOCK_BASE, clockTime);
        editor.putLong(NOTIF_CLOCK_ELAPSED, -1);
        editor.putBoolean(NOTIF_CLOCK_RUNNING, true);
        editor.commit();
    }

    /** Show the most recently saved notification. **/
    private boolean showSavedNotification() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());
        long clockBaseTime = prefs.getLong(NOTIF_CLOCK_BASE, -1);
        long clockElapsedTime = prefs.getLong(NOTIF_CLOCK_ELAPSED, -1);
        if (clockBaseTime == -1) {
            if (clockElapsedTime == -1) {
                return false;
            }
        }
        setNotification("error");
        return true;
    }

    private void clearSavedNotification() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(NOTIF_CLOCK_BASE);
        editor.remove(NOTIF_CLOCK_RUNNING);
        editor.remove(NOTIF_CLOCK_ELAPSED);
        editor.commit();
    }

    private void closeNotificationShade() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(intent);
    }

    private void readFromSharedPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());
    }

    private void writeToSharedPrefs(Long startTime, Long elapsedTime,
                                    Integer state) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        if (startTime != null) {
            editor.putLong(PREF_START_TIME, startTime);
        }
        if (elapsedTime != null) {
            editor.putLong(PREF_ACCUM_TIME, elapsedTime);
        }
        editor.commit();
    }

    private class PeekCountDownTimer extends CountDownTimer {
        private NotificationCompat.Builder notification;

        public PeekCountDownTimer(NotificationCompat.Builder notification){
            super(TimeUnit.MINUTES.toMillis(10), 1000);
            this.notification = notification;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int minutes = (int) millisUntilFinished / (60 * 1000);
            int seconds = (int) (millisUntilFinished / 1000) % 60;
            String timeLeft = String.format("%d:%02d", minutes, seconds);
            notification.setContentTitle("Time left: " + timeLeft);
            mNotificationManager.notify(NOTIFICATION_ID, notification.build());
        }

        @Override
        public void onFinish() {
            mNotificationManager.cancel(NOTIFICATION_ID);
        }
    }
}
