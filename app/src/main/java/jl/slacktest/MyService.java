package jl.slacktest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.rtm.SlackRealTimeMessagingClient;
import allbegray.slack.webapi.SlackWebApiClient;

public class MyService extends Service {
    private static final String TOKEN = "xoxp-362211397057-396183436321-396957814656-f85f833f27f819a23d95bb0d09477fe4";
    SlackRealTimeMessagingClient mRtmClient;
    SlackWebApiClient webApiClient;
    private Context mContext;
    //  DBHelper meDbHelper;
    String Weathercondition;
    //public static Updatelist update;
    private double latitude, longitude;


    /*public static void setlistener(Updatelist updatelist) {
        MyService.update = updatelist;
    }*/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mRtmClient == null) {
            webApiClient = SlackClientFactory.createWebApiClient(TOKEN);
            String webSocketUrl = webApiClient.startRealTimeMessagingApi().findPath("url").asText();
            Toast.makeText(getApplicationContext(), webSocketUrl, Toast.LENGTH_LONG).show();
            mRtmClient = new SlackRealTimeMessagingClient(webSocketUrl, null);
            mRtmClient.connect();
            Toast.makeText(getApplicationContext(), "connected", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "not", Toast.LENGTH_LONG).show();
        }

        stopSelf();
        Toast.makeText(getApplicationContext(), "Monitoring Started", Toast.LENGTH_SHORT).show();
        mContext = getApplicationContext();


        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // I want to restart this service again in one hour

        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarm.set(alarm.RTC_WAKEUP, System.currentTimeMillis() + (10000 * 1 * 1),
                PendingIntent.getService(this, 87, new Intent(this, MyService.class), 0));


    }



    /*public void addData() {
        try {


            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateandTime = sdf.format(new Date());
            meDbHelper = new DBHelper(this);
            meDbHelper.open();
            if (DeviceSharedPreference.getInstance(getApplicationContext()).getBattery("").equals("yes")) {
                Dbtlevel = btlevel;
            } else {
                Dbtlevel = "Not\nTracked";
            }
            if (DeviceSharedPreference.getInstance(getApplicationContext()).getDevivename("").equals("yes")) {
                Ddevicename = devicenames + " " + devicesos;
            } else {
                Ddevicename = "Not\nTracked";
            }
            if (DeviceSharedPreference.getInstance(getApplicationContext()).getNetwork("").equals("yes")) {
                Dntype = nttype;
            } else {
                Dntype = "Not\nTracked";
            }
            if (DeviceSharedPreference.getInstance(getApplicationContext()).getWheater("").equals("yes")) {
                Dwheather = DeviceSharedPreference.getInstance(getApplicationContext()).getWeather("");
            } else {
                Dwheather = "Not\nTracked";
            }
            if (DeviceSharedPreference.getInstance(getApplicationContext()).getStorage("").equals("yes")) {
                Dstorage = getAvailableInternalMemorySize() + "/" + getTotalInternalMemorySize() ;
            } else {
                Dstorage = "Not\nTracked";
            }
            meDbHelper.insertDetail(Dbtlevel, Ddevicename, Dntype, Dstorage, Dwheather, currentDateandTime);
            update.update();
        } catch (Exception e) {
        e.printStackTrace();
        }
    }*/


}