package com.egls.transactia;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private Context context;
    private List<Conversation> conversations;

    public InboxAdapter(Context context, List<Conversation> conversations) {
        this.context = context;
        this.conversations = conversations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);

        holder.lastMessage.setText(limitWords(conversation.getLastMessage(), 5));

        Timestamp timestmp = conversation.getTimestamp();


        // Set the timestamp, format it if necessary
        if (timestmp != null) {
            long timestampMillis = timestmp.getSeconds() * 1000; // Firestore Timestamp to milliseconds
            Date date = new Date(timestampMillis);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            String formattedDate = dateFormat.format(date);
            holder.timestamp.setText(formattedDate);
        } else {
            holder.timestamp.setText("");
        }

        String otherUser = getOtherUserName(conversation.getParticipants());

        fetchUserStat(otherUser, result -> {
            String status = result;

            if(status.equals("Verified")) {
                holder.verified.setVisibility(View.VISIBLE);
            } else {
                holder.verified.setVisibility(View.GONE);
            }
        });

        fetchUserName(otherUser, result -> {
            holder.participantName.setText(result);
        });

        fetchUserImg(otherUser, result -> {
            String imgUri = result;
            // Load profile image if available
            if (imgUri != null) {
                Glide.with(holder.itemView.getContext())
                        .load(imgUri)
                        .into(holder.profileUser);
            } else {
                holder.profileUser.setImageResource(R.drawable.pfpdef); // Default image
            }
        });



        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Inbox.class);
            intent.putExtra("otheruser", otherUser);
            context.startActivity(intent);
        });
    }

    // Method to limit the number of words
    private String limitWords(String description, int maxWords) {
        String[] words = description.split("\\s+"); // Split the description into words
        if (words.length <= maxWords) {
            return description; // Return the full description if within limit
        }

        // Join the first maxWords words and return
        StringBuilder limitedDescription = new StringBuilder();
        for (int i = 0; i < maxWords; i++) {
            limitedDescription.append(words[i]).append(" ");
        }

        return limitedDescription.toString().trim()+"..."; // Remove trailing space and return
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    private String getOtherUserId(List<String> participants) {
        for (String id : participants) {
            if (!id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                return id;
            }
        }
        return null;
    }

    private String getOtherUserName(List<String> participants) {
        // Replace with a fetch for user's name from Firestore or cache
        return getOtherUserId(participants);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView lastMessage, timestamp, participantName;
        ImageView profileUser, verified;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            timestamp = itemView.findViewById(R.id.timestamp);
            participantName = itemView.findViewById(R.id.participantName);
            profileUser = itemView.findViewById(R.id.profileUser);
            verified = itemView.findViewById(R.id.verified);
        }
    }

    private void fetchUserStat(String userId, FirestoreCallback<String> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("UserDetails").document(userId)
                .get()
                .addOnSuccessListener(userSnapshot -> {
                    if (userSnapshot.exists()) {
                        String status = userSnapshot.getString("status");
                        callback.onCallback(status != null ? status : "nostat");
                    } else {
                        callback.onCallback("nostat");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UserDetails", "Error fetching user details: " + e.getMessage());
                    callback.onCallback("nostat");
                });
    }

    private void fetchUserName(String userId, FirestoreCallback<String> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("UserDetails").document(userId)
                .get()
                .addOnSuccessListener(userSnapshot -> {
                    if (userSnapshot.exists()) {
                        String name = userSnapshot.getString("name");
                        callback.onCallback(name != null ? name : "noname");
                    } else {
                        callback.onCallback("noname");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UserDetails", "Error fetching user details: " + e.getMessage());
                    callback.onCallback("noname");
                });
    }

    private void fetchUserImg(String userId, FirestoreCallback<String> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("UserDetails").document(userId)
                .get()
                .addOnSuccessListener(userSnapshot -> {
                    if (userSnapshot.exists()) {
                        String profileImage = userSnapshot.getString("imageUrl");
                        callback.onCallback(profileImage != null ? profileImage : "noimg");
                    } else {
                        callback.onCallback("noimg");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UserDetails", "Error fetching user details: " + e.getMessage());
                    callback.onCallback("noimg");
                });
    }

    public interface FirestoreCallback<T> {
        void onCallback(T result);
    }


}

