package jl.slacktest;

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
import android.widget.Toast;

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

    private static final String TOKEN = "xoxp-362211397057-396183436321-396957814656-f85f833f27f819a23d95bb0d09477fe4";
    SlackRealTimeMessagingClient mRtmClient;
    SlackWebApiClient webApiClient;
    String stat, text, userName;
    TextView status;
    Channel channel;
    EditText msgbox;
    Button send;
    RecyclerView msglist;
    ArrayList<ChatAppMsgDTO> msglists = new ArrayList<>();
    ChatAppMsgAdapter chatAppMsgAdapter;
    DBHelper meDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        meDbHelper = new DBHelper(this);
        meDbHelper.open();

        if (mRtmClient == null) {
            Toast.makeText(getApplicationContext(), "connected", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "not", Toast.LENGTH_LONG).show();
        }
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

        webApiClient = SlackClientFactory.createWebApiClient(TOKEN);
        // mWebApiClient = SlackClientFactory.createWebApiClient(slackToken);
        String webSocketUrl = webApiClient.startRealTimeMessagingApi().findPath("url").asText();
        Toast.makeText(getApplicationContext(), webSocketUrl, Toast.LENGTH_LONG).show();
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
                //msglists.add(new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_SENT, msg, channel.getName(), "me"));
                meDbHelper.insertMsgDetail("Me", channel.getName(), msg, ChatAppMsgDTO.MSG_TYPE_SENT);
                getDatafrmDB();
               /* chatAppMsgAdapter = new ChatAppMsgAdapter(msglists);
                msglist.setAdapter(chatAppMsgAdapter);*/
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
                            //msglists.add(new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_RECEIVED, text, channel.getName(), userName));
                            meDbHelper.insertMsgDetail(userName, channel.getName(), text, ChatAppMsgDTO.MSG_TYPE_RECEIVED);
                            getDatafrmDB();
                            /*int newMsgPosition = msglists.size() - 1;

                            // Notify recycler view insert one new data.

                            chatAppMsgAdapter = new ChatAppMsgAdapter(msglists);
                            chatAppMsgAdapter.notifyItemInserted(newMsgPosition);

                            // Scroll RecyclerView to the last message.
                            msglist.scrollToPosition(newMsgPosition);*/
                        }
                    }
                });
             /*   if (userId != null) {
                    Channel channel;
                    try {
                        channel = webApiClient.getChannelInfo(channelId);
                    } catch (SlackResponseErrorException e) {
                        channel = null;
                    }
                    User user = webApiClient.getUserInfo(userId);
                    String userName = user.getName();

                    System.out.println("Channel id: " + channelId);
                    System.out.println("Channel name: " + (channel != null ? "#" + channel.getName() : "DM"));
                    System.out.println("User id: " + userId);
                    System.out.println("User name: " + userName);
                    System.out.println("Text: " + text);

                    // Copy cat
                    webApiClient.meMessage(channelId, userName + ": " + text);
                }*/


            }
        });


        mRtmClient.addListener("message", new EventListener() {
            @Override
            public void handleMessage(JsonNode jsonNode) {
            }
        });
        mRtmClient.connect();
        getDatafrmDB();
    }/**/

    @Override
    protected void onStop() {
        super.onStop();
        /*Intent i = new Intent(getApplicationContext(), MyService.class);
        startService(i);*/

    }

    @Override
    protected void onPause() {
        super.onPause();
        //Intent i = new Intent(getApplicationContext(), MyService.class);
        // startService(i);
    }

    public void getDatafrmDB() {
        msglists.clear();
        Cursor notesCursor = meDbHelper.fetchAllMessage();
        startManagingCursor(notesCursor);
        if (notesCursor.moveToFirst()) {

            do {
                ChatAppMsgDTO md = new ChatAppMsgDTO();
                md.setSname(notesCursor.getString(notesCursor.getColumnIndexOrThrow("sender")));
                md.setChannel_name(notesCursor.getString(notesCursor.getColumnIndexOrThrow("channelname")));
                md.setMsgContent(notesCursor.getString(notesCursor.getColumnIndexOrThrow("message")));
                md.setMsgType(notesCursor.getString(notesCursor.getColumnIndexOrThrow("messagetype")));
                msglists.add(md);
            } while (notesCursor.moveToNext());
        }
        if (msglist.getAdapter() == null) {
            chatAppMsgAdapter = new ChatAppMsgAdapter(msglists);
            msglist.setAdapter(chatAppMsgAdapter);
            chatAppMsgAdapter.notifyDataSetChanged();
        } else {
            chatAppMsgAdapter.notifyDataSetChanged();
            int newMsgPosition = msglists.size() - 1;
            msglist.smoothScrollToPosition(newMsgPosition);

        }
    }

}