package com.egls.transactia;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;

    public NotificationAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_row, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);

        holder.titleTextView.setText(notification.getTitle());
        //holder.messageTextView.setText(notification.getMessage());

        // Set the timestamp, format it if necessary
        if (notification.getTimestamp() != null) {
            long timestampMillis = notification.getTimestamp().getSeconds() * 1000; // Firestore Timestamp to milliseconds
            Date date = new Date(timestampMillis);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            String formattedDate = dateFormat.format(date);
            holder.timestampTextView.setText(formattedDate);
        } else {
            holder.timestampTextView.setText("");
        }

        if (notification.getStatus().equals("unread")) {
            holder.titleTextView.setTypeface(null, Typeface.BOLD); // Light red for unread
        } else {
            holder.titleTextView.setTypeface(null, Typeface.NORMAL);; // Light red for unread
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, messageTextView, timestampTextView;
        CardView cardView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titletx);
            //messageTextView = itemView.findViewById(R.id.messagebox); // Add this TextView in `notification_row.xml`
            timestampTextView = itemView.findViewById(R.id.timestamptx);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}

