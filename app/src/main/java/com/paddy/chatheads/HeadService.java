package com.paddy.chatheads;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class HeadService extends Service {
    private final static int FOREGROUND_ID = 999;
    private static final String TAG = "Head service";

    private HeadLayer mHeadLayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logServiceStarted();

        initHeadLayer();

        PendingIntent pendingIntent = createPendingIntent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            addNotification(getText(R.string.notificationTitle).toString(),getText(R.string.notificationText).toString(),false,112);
            Notification notification = createNotification(pendingIntent);
            startForeground(FOREGROUND_ID, notification);
        }


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        destroyHeadLayer();
        stopForeground(true);

        logServiceEnded();
    }

    private void initHeadLayer() {
        mHeadLayer = new HeadLayer(this);
    }

    private void destroyHeadLayer() {
        if (mHeadLayer!=null){

            mHeadLayer.destroy();
            mHeadLayer = null;
        }
    }

    private PendingIntent createPendingIntent() {
        Intent intent = new Intent(this, ChatHeadActivity.class);
        return PendingIntent.getActivity(this, 0, intent, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification createNotification(PendingIntent intent) {
        String CHANNEL_ID = "my_channel_";
        CharSequence name = "my_channel";
        String Description = "This is my channel";
            return new Notification.Builder(this,CHANNEL_ID)
                    .setContentTitle(getText(R.string.notificationTitle))
                    .setContentText(getText(R.string.notificationText))
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(intent)
                    .build();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void addNotification(String small, String big, Boolean is, int id) {
        String CHANNEL_ID = "my_channel_";
        CharSequence name = "my_channel";
        String Description = "This is my channel";
        final boolean[] stop = {false};
        int NOTIFICATION_ID = id;
////        Log.e(TAG, "addNotification: start");
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            if (!is){
                small="Running";
                big="Checking messages";

                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                mChannel.setDescription(Description);
                mChannel.enableLights(false);
                mChannel.enableVibration(false);
                mChannel.setShowBadge(false);

////                Log.e("second","second called");
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(mChannel);
//                startForeground(NOTIFICATION_ID,mChannel);
                }
////                Log.e(TAG, "addNotification: -1");
            }else{

                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                mChannel.setDescription(Description);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(false);
                mChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),null);
                mChannel.setShowBadge(true);

                //Log.e("second","second called");
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(mChannel);
//                startForeground(NOTIFICATION_ID,mChannel);
                }
////                Log.e(TAG, "addNotification: -1");
            }

        }
        if (!is){
            small="Running";
            big="Checking messages";
        }


    }
    private void logServiceStarted() {
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
    }

    private void logServiceEnded() {
        Toast.makeText(this, "Service ended", Toast.LENGTH_SHORT).show();
    }
}
