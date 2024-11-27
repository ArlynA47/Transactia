package com.egls.transactia;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Inbox extends AppCompatActivity {

    private EditText messagebox;
    private ImageView sendmsgbt;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;

    ImageView infobt;

    ConstraintLayout forkeyboard;

    private String currentUserId = "currentUserId";
    private String otherUserId = "otherUserId";
    private String conversationId;

    private FirebaseFirestore db;

    String fireBUserID;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inbox);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        forkeyboard = findViewById(R.id.forkeyboard);

        user = FirebaseAuth.getInstance().getCurrentUser();
        fireBUserID = user.getUid();

        otherUserId = getIntent().getStringExtra("otheruser");
        currentUserId = fireBUserID;

        fetchUserDetails(otherUserId);

        infobt = findViewById(R.id.infobt);

        infobt.setOnClickListener(v -> {
            Intent intent = new Intent(this, Enduserinfo.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        });
        messagebox = findViewById(R.id.messagebox);
        sendmsgbt = findViewById(R.id.sendmsgbt);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);

        db = FirebaseFirestore.getInstance();

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messageList, currentUserId);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Generate conversation ID
        conversationId = generateConversationId(currentUserId, otherUserId);

        sendmsgbt.setOnClickListener(v -> sendMessage());

        listenForMessages();

        chatRecyclerView.setOnTouchListener((v, event) -> {
            forkeyboard.setVisibility(View.GONE); // Hide your view
            return false; // Return false to allow normal scrolling
        });

        messagebox.setOnClickListener(v -> forkeyboard.setVisibility(View.VISIBLE));
        messagebox.setOnFocusChangeListener((v, hasFocus) -> {
            forkeyboard.setVisibility(View.VISIBLE);
        });

        messagebox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                forkeyboard.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // This is called every time the text is changed.
                if (charSequence.length() > 0) {
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private String generateConversationId(String user1, String user2) {
        return user1.compareTo(user2) < 0 ? user1 + "_" + user2 : user2 + "_" + user1;
    }

    private void sendMessage() {

        forkeyboard.setVisibility(View.GONE);
        String text = messagebox.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            return;
        }

        Map<String, Object> message = new HashMap<>();
        message.put("senderId", currentUserId);
        message.put("receiverId", otherUserId);
        message.put("messageText", text);
        message.put("timestamp", com.google.firebase.Timestamp.now());

        db.collection("Messages").document(conversationId)
                .collection("Messages")
                .add(message)
                .addOnSuccessListener(docRef -> {
                    messagebox.setText("");

                    // Update or create a conversation
                    Map<String, Object> conversation = new HashMap<>();
                    conversation.put("conversationId", conversationId);
                    conversation.put("lastMessage", text);
                    conversation.put("timestamp", com.google.firebase.Timestamp.now());
                    conversation.put("participants", List.of(currentUserId, otherUserId));

                    db.collection("Conversations").document(conversationId)
                            .set(conversation); // Use set() to create or update the conversation
                })
                .addOnFailureListener(e -> Log.e("Chat", "Error sending message", e));
    }


    private void listenForMessages() {
        db.collection("Messages").document(conversationId)
                .collection("Messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("Chat", "Listen failed", e);
                        return;
                    }

                    messageList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        messageList.add(doc.toObject(Message.class));
                    }
                    chatAdapter.notifyDataSetChanged();
                    chatRecyclerView.scrollToPosition(messageList.size() - 1);
                });
    }

    private void fetchUserDetails(String userId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("UserDetails").document(userId)
                .get()
                .addOnSuccessListener(userSnapshot -> {
                    if (userSnapshot.exists()) {
                        String profileImage = userSnapshot.getString("imageUrl");
                        String name = userSnapshot.getString("name");

                        // Populate user-specific views
                        loadImageIntoView(profileImage, R.id.Enduserpfp);
                        ((TextView) findViewById(R.id.Endusername)).setText(name);



                    }
                })
                .addOnFailureListener(e -> Log.e("UserDetails", "Error fetching user details: " + e.getMessage()));
    }

    private void loadImageIntoView(String imageUrl, int imageViewId) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            ImageView imageView = findViewById(imageViewId);
            Glide.with(this).load(imageUrl).into(imageView);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        forkeyboard.setVisibility(View.GONE);
        // Apply the transition when back button is pressed
        overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
    }
}
