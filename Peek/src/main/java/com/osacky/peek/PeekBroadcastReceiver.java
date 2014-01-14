package com.osacky.peek;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

public class PeekBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            JSONObject jsonObject = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            String username = jsonObject.getString("username");
            Long time = jsonObject.getLong("time");

            Intent peekIntent = new Intent(context, NotificationCountdownService.class);
            peekIntent.putExtra("username", username);
            peekIntent.putExtra("time", time);
            peekIntent.setAction(NotificationCountdownService.START_COUNTDOWN);

            context.startService(peekIntent);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


}
