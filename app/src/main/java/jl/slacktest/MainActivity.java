package jl.slacktest;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.exception.SlackResponseErrorException;
import allbegray.slack.rtm.Event;
import allbegray.slack.rtm.EventListener;
import allbegray.slack.rtm.SlackRealTimeMessagingClient;
import allbegray.slack.type.Bot;
import allbegray.slack.type.Channel;
import allbegray.slack.type.User;
import allbegray.slack.webapi.SlackWebApiClient;

public class MainActivity extends AppCompatActivity {

    SlackRealTimeMessagingClient mRtmClient;
    SlackWebApiClient webApiClient;
    String stat, text, userName, eventts;
    TextView status;
    Channel channel;
    EditText msgbox;
    Button send;
    RecyclerView msglist;
    ArrayList<ModelClass> msglists = new ArrayList<>();
    ChatAdapter chatAppMsgAdapter;
    DBHelper meDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        meDbHelper = new DBHelper(this);
        meDbHelper.open();

        SharedPreference.getInstance(getApplicationContext()).setServiceStarted("no");
        status = (TextView) findViewById(R.id.status);
        msgbox = (EditText) findViewById(R.id.msgbox);
        send = (Button) findViewById(R.id.send);
        msglist = (RecyclerView) findViewById(R.id.recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        msglist.setLayoutManager(linearLayoutManager);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        webApiClient = SlackClientFactory.createWebApiClient(Constant.TOKEN);
        String webSocketUrl = webApiClient.startRealTimeMessagingApi().findPath("url").asText();
        mRtmClient = new SlackRealTimeMessagingClient(webSocketUrl, null);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = msgbox.getText().toString();
                webApiClient.meMessage("CAN1QRPUG", msg);
                Channel channel;
                try {
                    channel = webApiClient.getChannelInfo("CAN1QRPUG");
                } catch (SlackResponseErrorException e) {
                    channel = null;
                }
                meDbHelper.insertMsgDetail("Me", channel.getName(), msg, ModelClass.MSG_TYPE_SENT, "2");
                getDatafrmDB();

            }
        });

        mRtmClient.addListener(Event.HELLO, new EventListener() {
            @Override
            public void handleMessage(JsonNode jsonNode) {
                stat = jsonNode.findPath("type").asText();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if (stat.equals("hello")) {
                            status.setText("Connected to Server");
                        } else {
                            status.setText("Connection to Server failed");
                        }
                    }
                });

            }
        });


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
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (!subtype.equals("me_message")) {
                            if (SharedPreference.getInstance(getApplicationContext()).getServiceStarted("").equals("no")) {
                                int count = meDbHelper.getProfilesCount(eventts);
                                if (count > 0) {

                                } else {
                                    meDbHelper.insertMsgDetail(userName, channel.getName(), text, ModelClass.MSG_TYPE_RECEIVED, eventts);
                                    getDatafrmDB();
                                }
                            }
                        }
                    }
                });

            }
        });

        mRtmClient.connect();
        getDatafrmDB();
    }

    @Override
    protected void onStop() {
        super.onStop();

        String C = "jl.slacktest";
        Intent startIntent = new Intent(getApplicationContext(), SlackService.class);
        startIntent.setAction(C);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(startIntent);
        finishAffinity();
        SharedPreference.getInstance(getApplicationContext()).setServiceStarted("yes");
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    public void getDatafrmDB() {
        msglists.clear();
        Cursor notesCursor = meDbHelper.fetchAllMessage();
        startManagingCursor(notesCursor);
        if (notesCursor.moveToFirst()) {

            do {
                ModelClass md = new ModelClass();
                md.setSname(notesCursor.getString(notesCursor.getColumnIndexOrThrow("sender")));
                md.setChannel_name(notesCursor.getString(notesCursor.getColumnIndexOrThrow("channelname")));
                md.setMsgContent(notesCursor.getString(notesCursor.getColumnIndexOrThrow("message")));
                md.setMsgType(notesCursor.getString(notesCursor.getColumnIndexOrThrow("messagetype")));
                msglists.add(md);
            } while (notesCursor.moveToNext());
        }
        if (msglist.getAdapter() == null) {
            chatAppMsgAdapter = new ChatAdapter(msglists);
            msglist.setAdapter(chatAppMsgAdapter);
            chatAppMsgAdapter.notifyDataSetChanged();
        } else {
            chatAppMsgAdapter.notifyDataSetChanged();
            int newMsgPosition = msglists.size() - 1;
            msglist.smoothScrollToPosition(newMsgPosition);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}