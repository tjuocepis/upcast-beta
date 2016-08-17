package com.example.titusjuocepis.upcastbeta;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by titusjuocepis on 7/21/16.
 */
public class FirebaseBackgroundService extends Service {

    private Firebase f;
    private ValueEventListener handler;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(getApplicationContext());
        f = new Firebase("https://upcast-beta.firebaseio.com/channels");

        handler = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot arg0) {
                postNotif(arg0.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        f.addValueEventListener(handler);
    }

    private void postNotif(String notifString) {

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;

        Context context = getApplicationContext();
        CharSequence contentTitle = "UB - ";
        int icon = R.drawable.ic_user_32;

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            notification = new Notification();
            notification.icon = icon;
            try {
                Method deprecatedMethod = notification.getClass().getMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
                deprecatedMethod.invoke(notification, context, contentTitle, notifString, contentIntent);
            } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                Log.w("[POST NOTIF] - ", "Method not found", e);
            }
        } else {
            // Use new API
            Notification.Builder builder = new Notification.Builder(context)
                    .setContentIntent(contentIntent)
                    .setSmallIcon(icon)
                    .setContentTitle("New Channel Created!");
            notification = builder.build();
        }

        mNotificationManager.notify(1, notification);
    }
}