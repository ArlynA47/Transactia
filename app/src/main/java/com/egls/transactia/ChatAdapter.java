package com.egls.transactia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.egls.transactia.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private Context context;
    private List<Message> messageList;
    private String currentUserId;

    public ChatAdapter(Context context, List<Message> messageList, String currentUserId) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (message.getSenderId().equals(currentUserId)) {
            holder.sentMessage.setVisibility(View.VISIBLE);
            holder.receivedMessage.setVisibility(View.GONE);
            holder.sentMessage.setText(message.getMessageText());
        } else {
            holder.receivedMessage.setVisibility(View.VISIBLE);
            holder.sentMessage.setVisibility(View.GONE);
            holder.receivedMessage.setText(message.getMessageText());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView sentMessage, receivedMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            sentMessage = itemView.findViewById(R.id.sentMessage);
            receivedMessage = itemView.findViewById(R.id.receivedMessage);
        }
    }
}

