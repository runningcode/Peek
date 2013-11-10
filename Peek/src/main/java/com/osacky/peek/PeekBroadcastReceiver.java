package com.osacky.peek;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class PeekBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            JSONObject jsonObject = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            String username = jsonObject.getString("username");
            Long time = jsonObject.getLong("time");

            Intent peekIntent = new Intent(context, CreatePeekActivity.class);
            peekIntent.putExtra("username", username);
            peekIntent.putExtra("time", time);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(CreatePeekActivity.class);
            stackBuilder.addNextIntent(peekIntent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            String cancel = context.getResources().getString(android.R.string.cancel);
            String go = context.getResources().getString(android.R.string.ok);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(context.getString(R.string.peek_request))
                    .setContentText(username)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, cancel, null)
                    .addAction(android.R.drawable.ic_menu_send, go, pendingIntent)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, builder.build());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
