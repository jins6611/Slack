package jl.slacktest;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;

import com.fasterxml.jackson.databind.JsonNode;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.exception.SlackResponseErrorException;
import allbegray.slack.rtm.Event;
import allbegray.slack.rtm.SlackRealTimeMessagingClient;
import allbegray.slack.type.Bot;
import allbegray.slack.type.Channel;
import allbegray.slack.type.User;
import allbegray.slack.webapi.SlackWebApiClient;

public class SlackService extends Service {
    static final int NOTIFICATION_ID = 543;
    SlackRealTimeMessagingClient mRtmClient;
    SlackWebApiClient webApiClient;
    DBHelper meDbHelper;
    String  text, userName, eventts;
    Channel channel;
    public static boolean isServiceRunning = false;
    String C = "jl.slacktest";

    @Override
    public void onCreate() {
        super.onCreate();
        //startServiceWithNotification(text);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction().equals(C)) {

            meDbHelper = new DBHelper(this);
            meDbHelper.open();
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            if (mRtmClient == null) {
                webApiClient = SlackClientFactory.createWebApiClient(Constant.TOKEN);
                String webSocketUrl = webApiClient.startRealTimeMessagingApi().findPath("url").asText();
                mRtmClient = new SlackRealTimeMessagingClient(webSocketUrl, null);
                mRtmClient.connect();
            } else {
            }

            mRtmClient.addListener(Event.MESSAGE, new allbegray.slack.rtm.EventListener() {
                @Override
                public void handleMessage(JsonNode message) {
                    String channelId = message.findPath("channel").asText();
                    String userId = message.findPath("user").asText();
                    final String subtype = message.findPath("subtype").asText();
                    String bt_id = message.findPath("bot_id").asText();
                    text = message.findPath("text").asText();
                    eventts = message.findPath("event_ts").asText();
                    if (!userId.equals("")) {
                        User user = webApiClient.getUserInfo(userId);
                        userName = user.getName();
                    }
                    if (!bt_id.equals("")) {
                        Bot user = webApiClient.getBotInfo(bt_id);
                        userName = user.getName();
                    }
                    try {
                        channel = webApiClient.getChannelInfo(channelId);
                    } catch (SlackResponseErrorException e) {
                        channel = null;
                    }

                    if (!subtype.equals("me_message")) {
                        startServiceWithNotification(text);
                        int count = meDbHelper.getProfilesCount(eventts);
                        if (count > 0) {

                        } else {
                            meDbHelper.insertMsgDetail(userName, channel.getName(), text, ModelClass.MSG_TYPE_RECEIVED, eventts);
                        }

                    }
                }
            });
        } else stopMyService();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isServiceRunning = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    void startServiceWithNotification(String text) {
        if (isServiceRunning) return;
        isServiceRunning = true;

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        //notificationIntent.setAction(C);  // A string containing the action name
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setTicker(getResources().getString(R.string.app_name))
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher)
                //.setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(contentPendingIntent)
                .setOngoing(true)
//                .setDeleteIntent(contentPendingIntent)  // if needed
                .build();
        notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
        startForeground(NOTIFICATION_ID, notification);
    }

    void stopMyService() {
        stopForeground(true);
        stopSelf();
        isServiceRunning = false;
    }
}
