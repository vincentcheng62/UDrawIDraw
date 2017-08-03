package com.iems5722.group5;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import java.util.List;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by AlexLiu on 23/3/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private LocalBroadcastManager bc;

    @Override
    public void onCreate() {
        bc = LocalBroadcastManager.getInstance(this);
        super.onCreate();
    }

    @Override public void onMessageReceived(RemoteMessage remoteMessage) {
        //Log.d("!!!!!!!!!!!!!!", "from:" + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.d("!!!!!!!!!!!!!!", "data:" + remoteMessage.getData());
            String msgType = remoteMessage.getData().get("msg_type");

            if ("MESSAGE".equals(msgType)) {
                handleBroadcastMessage(remoteMessage);
            }
            else if ("EDIT_INVITE".equals(msgType)) {
                handleEditInvite(remoteMessage);
            }
        }

    }

    private void handleBroadcastMessage(RemoteMessage remoteMessage) {
        String chatroomName = remoteMessage.getData().get("chatroom_name");
        String msg = remoteMessage.getData().get("message");
        String userName = remoteMessage.getData().get("name");
        String userId = remoteMessage.getData().get("user_id");
        String timestamp = remoteMessage.getData().get("timestamp");
        String image = remoteMessage.getData().get("image");

        // notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.cuhk)
                .setContentTitle(chatroomName)
                .setLights(Color.GREEN, 1000, 300);
        if ("".equals(msg) && !"".equals(image)) {
            builder = builder.setContentText(userName + ": (image)");
        }
        else {
            builder = builder.setContentText(userName + ": " + msg);
        }
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmssSSS");
        String timeForId = sdf.format(new Date());
        Integer notificationId = Integer.parseInt(timeForId);
        mgr.notify(notificationId, notification);

        // send to ChatActivity to add instant message
        Intent intent = new Intent("NEW_MESSAGE");
        intent.putExtra("msg_type", remoteMessage.getData().get("msg_type"));
        intent.putExtra("name", userName);
        intent.putExtra("user_id", userId);
        intent.putExtra("message", msg);
        intent.putExtra("timestamp", timestamp);
        intent.putExtra("chatroom", chatroomName);
        intent.putExtra("image", image);
        bc.sendBroadcast(intent);
    }

    private void handleEditInvite(RemoteMessage remoteMessage) {
        Intent intent = new Intent("NEW_MESSAGE");
        intent.putExtra("msg_type", remoteMessage.getData().get("msg_type"));
        intent.putExtra("chatroom", remoteMessage.getData().get("chatroom_name"));
        intent.putExtra("chatroom_id", remoteMessage.getData().get("chatroom_id"));
        intent.putExtra("name", remoteMessage.getData().get("name"));
        intent.putExtra("user_id", remoteMessage.getData().get("user_id"));
        intent.putExtra("image_name", remoteMessage.getData().get("image_name"));
        bc.sendBroadcast(intent);

    }

    public boolean isForeground() {
        ActivityManager mgr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> tasks = mgr.getAppTasks();
        for(ActivityManager.AppTask task : tasks) {
            Log.d("===========", task.getTaskInfo().toString() + ":" +  task.getTaskInfo().id);
        }
        return true;
    }
}
