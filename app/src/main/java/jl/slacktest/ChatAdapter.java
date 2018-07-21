package jl.slacktest;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatAppMsgViewHolder> {

    private List<ModelClass> msgDtoList = null;

    public ChatAdapter(List<ModelClass> msgDtoList) {
        this.msgDtoList = msgDtoList;
    }

    @Override
    public void onBindViewHolder(ChatAppMsgViewHolder holder, int position) {
        ModelClass msgDto = this.msgDtoList.get(position);
        // If the message is a received message.
        if (msgDto.MSG_TYPE_SENT.equals(msgDto.getMsgType())) {
            // Show received message in left linearlayout.
            holder.leftMsgLayout.setVisibility(View.VISIBLE);
            holder.leftMsgTextView.setText(msgDto.getMsgContent());
            holder.left_channel.setText(msgDto.getChannel_name());
            holder.sender.setText(msgDto.getSname());
            // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
            // Otherwise each iteview's distance is too big.
            holder.rightMsgLayout.setVisibility(View.GONE);
        }
        // If the message is a sent message.
        else if (msgDto.MSG_TYPE_RECEIVED.equals(msgDto.getMsgType())) {
            // Show sent message in right linearlayout.
            holder.rightMsgLayout.setVisibility(View.VISIBLE);
            holder.rightMsgTextView.setText(msgDto.getMsgContent());
            holder.right_channel.setText(msgDto.getChannel_name());
            holder.receviedsender.setText(msgDto.getSname());
            // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
            // Otherwise each iteview's distance is too big.
            holder.leftMsgLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public ChatAppMsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_chat_app_item_view, parent, false);
        return new ChatAppMsgViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (msgDtoList == null) {
            msgDtoList = new ArrayList<ModelClass>();
        }
        return msgDtoList.size();
    }


    public class ChatAppMsgViewHolder extends RecyclerView.ViewHolder {

        CardView leftMsgLayout, rightMsgLayout;
        TextView leftMsgTextView, rightMsgTextView, sender, receviedsender, right_channel, left_channel;

        public ChatAppMsgViewHolder(View itemView) {
            super(itemView);

            if (itemView != null) {
                leftMsgLayout = (CardView) itemView.findViewById(R.id.chat_left_msg_layout);
                rightMsgLayout = (CardView) itemView.findViewById(R.id.chat_right_msg_layout);

                leftMsgTextView = (TextView) itemView.findViewById(R.id.message);
                rightMsgTextView = (TextView) itemView.findViewById(R.id.text_message_body);

                sender = (TextView) itemView.findViewById(R.id.sender);
                receviedsender = (TextView) itemView.findViewById(R.id.receviedsender);

                right_channel = (TextView) itemView.findViewById(R.id.right_channel);
                left_channel = (TextView) itemView.findViewById(R.id.left_channel);


            }
        }
    }
}