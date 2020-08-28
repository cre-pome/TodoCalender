package com.example.TodoCalendar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import java.util.Calendar;

public class AlarmNotification extends BroadcastReceiver {

    @Override   // データを受信した
    public void onReceive(Context context, Intent intent) {

        Log.d("AlarmBroadcastReceiver","onReceive() pid=" + android.os.Process.myPid());

        int requestCode = intent.getIntExtra("RequestCode",0);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = "default";

        // 通知時間
        int notifyTime = intent.getIntExtra("notifyTime", 0);

        // 通知タイトル
        String title = intent.getStringExtra("taskName");

        // 通知種別
        String notifyKind = intent.getStringExtra("notifyKind");

        // 通知メッセージ
        String message = intent.getStringExtra("message");

        System.out.println("message: "+ message);

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Notification　Channel 設定
        NotificationChannel channel = new NotificationChannel(
                channelId, title , NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(message);
        channel.canShowBadge();
        channel.enableLights(true);
        channel.setLightColor(Color.BLUE);
        // the channel appears on the lockscreen
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        channel.setSound(defaultSoundUri, null);
        channel.setShowBadge(true);
        channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{200, 200, 200, 200});

        Log.d("AlarmBroadcastReceiver","バイブレーション許可" + channel.shouldVibrate());

        if(notificationManager != null){
            notificationManager.createNotificationChannel(channel);
            Log.d("AlarmBroadcastReceiver","onReceive() pid=" + android.os.Process.myPid());
            Notification notification = new Notification.Builder(context, channelId)
                    .setContentTitle(title)  //通知タイトル
                    .setContentText(message)        //通知内容
                    .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)     //通知用アイコン
                    .build();                                                //通知のビルド

            // 通知が重複しないためのユニークコード
            Calendar calendar = Calendar.getInstance();
            long utc = calendar.getTimeInMillis(); // 現在時刻のUTCは重複しないはず

            // 通知
            notificationManager.notify((int)utc, notification);

        }
    }
}