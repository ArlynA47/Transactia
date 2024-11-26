package com.egls.transactia;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final Context context;
    private final List<Transaction> transactionList;
    private final String currentUserId;
    Transaction transaction;

    String senderIdStr;

    public TransactionAdapter(Context context, List<Transaction> transactionList, String currentUserId) {
        this.context = context;
        this.transactionList = transactionList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.transaction_row, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position); // Local variable for the current transaction

        // Set timestamp
        if (transaction.getTimestamp() != null) {
            long timestampMillis = transaction.getTimestamp().getSeconds() * 1000; // Firestore Timestamp to milliseconds
            Date date = new Date(timestampMillis);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            String formattedDate = dateFormat.format(date);
            holder.timestamptx.setText(formattedDate);
        } else {
            holder.timestamptx.setText("No Timestamp available");
        }

        // Set transaction title
        holder.transactionName.setText(transaction.getTransactionTitle());

        String otherUserId = transaction.getSenderID().equals(currentUserId) ?
                transaction.getReceiverID() : transaction.getSenderID();

        // Fetch details for the other user
        FirebaseFirestore.getInstance().collection("UserDetails").document(otherUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);

                        // Set profile picture for the other user
                        Glide.with(context)
                                .load(userDetails.getImageUrl())
                                .placeholder(R.drawable.pfpdef) // Replace with a default image
                                .into(holder.profile2);

                        // Set other user details
                        holder.userName.setText(userDetails.getName());
                        holder.userLoc.setText(userDetails.getLocation());
                    }
                });

        // Fetch current user profile picture
        FirebaseFirestore.getInstance().collection("UserDetails").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);

                        // Set current user profile picture
                        Glide.with(context)
                                .load(userDetails.getImageUrl())
                                .placeholder(R.drawable.pfpdef) // Replace with a default image
                                .into(holder.profile1);
                    }
                });

        // Handle CardView click to navigate to MyNeeds activity
        holder.cardView.setOnClickListener(v -> {
            String senderIdStr = transaction.getSenderID();
            String transStatus = transaction.getStatus();

            if (senderIdStr.equals(currentUserId)) {

                if(transStatus.equals("Completed")) {
                    Intent intent = new Intent(context, ManageRequest.class);
                    intent.putExtra("transactionid", transaction.getTransactionid());
                    intent.putExtra("isCompleted", true);
                    context.startActivity(intent);
                } else if (transStatus.equals("Accepted")) {
                    Intent intent = new Intent(context, ManageRequest.class);
                    intent.putExtra("transactionid", transaction.getTransactionid());
                    intent.putExtra("isAccepted", true);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, ManageRequest.class);
                    intent.putExtra("transactionid", transaction.getTransactionid());
                    intent.putExtra("isAccepted", false);
                    context.startActivity(intent);
                }

            } else {

                if(transStatus.equals("Completed")) {
                    Intent intent = new Intent(context, ReviewRequest.class);
                    intent.putExtra("transactionid", transaction.getTransactionid());
                    intent.putExtra("isCompleted", true);
                    context.startActivity(intent);
                } else if (transStatus.equals("Accepted")) {
                    Intent intent = new Intent(context, ReviewRequest.class);
                    intent.putExtra("transactionid", transaction.getTransactionid());
                    intent.putExtra("isAccepted", true);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, ReviewRequest.class);
                    intent.putExtra("transactionid", transaction.getTransactionid());
                    intent.putExtra("isAccepted", false);
                    context.startActivity(intent);
                }
            }
        });

        // Apply animation
        Animation translateAnim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
        int delay = position * 150; // Adjust this value for the speed of the cascade
        translateAnim.setStartOffset(delay); // Add delay based on the position
        holder.cardView.startAnimation(translateAnim);
    }



    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView timestamptx, transactionName, userName, userLoc;
        ImageView profile1, profile2;
        CardView cardView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            timestamptx = itemView.findViewById(R.id.titletx);
            transactionName = itemView.findViewById(R.id.transactionname);
            userName = itemView.findViewById(R.id.UserName);
            userLoc = itemView.findViewById(R.id.UserLoc);
            profile1 = itemView.findViewById(R.id.itempic);
            profile2 = itemView.findViewById(R.id.profile2);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}

