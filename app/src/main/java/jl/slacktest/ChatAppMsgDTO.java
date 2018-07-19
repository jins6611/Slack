package jl.slacktest;

public class ChatAppMsgDTO {

    public final static String MSG_TYPE_SENT = "MSG_TYPE_SENT";

    public final static String MSG_TYPE_RECEIVED = "MSG_TYPE_RECEIVED";

    // Message content.
    private String msgContent, channel_name, sname;

    // Message type.
    private String msgType;

    public ChatAppMsgDTO() {

    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public static String getMsgTypeSent() {
        return MSG_TYPE_SENT;
    }

    public static String getMsgTypeReceived() {
        return MSG_TYPE_RECEIVED;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }
}