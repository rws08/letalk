package kr.co.allright.letalk.message;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import kr.co.allright.letalk.MainActivity;
import kr.co.allright.letalk.R;

import static android.content.ContentValues.TAG;
import static kr.co.allright.letalk.manager.PushManager.PUSH_ACTION_NEW_CHAT;
import static kr.co.allright.letalk.manager.PushManager.PUSH_ACTION_NEW_MESSAGE;
import static kr.co.allright.letalk.manager.PushManager.PUSH_ACTION_REMOVE_CHAT;

/**
 * Created by MacPro on 2017. 1. 3..
 */

public class FbMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            String action = remoteMessage.getData().get("action");

            if (MainActivity.getInstance() != null && MainActivity.getInstance().isAppWentToBg) {
                sendPushNotification(title, body, action);
            }

            onPushAction(action);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void sendPushNotification(String _title, String _body, String _action) {
        System.out.println("received message : " + _title);
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("action", _action);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_chat_bubble_outline).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher) )
                .setContentTitle(_title)
                .setContentText(_body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri).setLights(255000255,500,2000)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakelock.acquire(5000);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public static void onPushAction(String _action){
        Log.d(TAG, "onPushAction: " + _action);

        if (MainActivity.getInstance() != null) {
            if (_action.equals(PUSH_ACTION_NEW_MESSAGE)) {
                MainActivity.getInstance().actionNewMessage();
            } else if (_action.equals(PUSH_ACTION_NEW_CHAT)) {
                MainActivity.getInstance().actionNewMessage();
            } else if (_action.equals(PUSH_ACTION_REMOVE_CHAT)) {
                MainActivity.getInstance().actionNewMessage();
            }
        }
    }
}
