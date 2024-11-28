package com.egls.transactia;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.google.firebase.firestore.FirebaseFirestore;

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

        // Format the timestamp
        if (notification.getTimestamp() != null) {
            long timestampMillis = notification.getTimestamp().getSeconds() * 1000;
            Date date = new Date(timestampMillis);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            String formattedDate = dateFormat.format(date);
            holder.timestampTextView.setText(formattedDate);
        } else {
            holder.timestampTextView.setText("");
        }

        // Highlight unread notifications
        if (notification.getStatus().equals("unread")) {
            holder.titleTextView.setTypeface(null, Typeface.BOLD);
        } else {
            holder.titleTextView.setTypeface(null, Typeface.NORMAL);
        }

        // Set the onClickListener
        holder.cardView.setOnClickListener(v -> {
            // Retrieve the document ID (Firestore handles it)
            String documentId = notification.getDocumentId(); // Ensure `Notification` model includes this

            // Pass data to View Notification Activity
            Intent intent = new Intent(v.getContext(), ViewNotification.class);
            intent.putExtra("title", notification.getTitle());
            intent.putExtra("message", notification.getMessage());
            intent.putExtra("documentId", documentId);
            v.getContext().startActivity(intent);

            // Update the notification status to "read"
            updateNotificationStatus(documentId);
        });
    }

    private void updateNotificationStatus(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("UserNotifications").document(documentId)
                .update("status", "read")
                .addOnSuccessListener(aVoid -> {
                    // Status updated successfully
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
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

