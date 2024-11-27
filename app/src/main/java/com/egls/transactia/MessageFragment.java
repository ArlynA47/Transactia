package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {
    public MessageFragment() {
        // Required empty public constructor
    }

    private RecyclerView conversationsRecyclerView;
    private InboxAdapter inboxAdapter;
    private List<Conversation> conversationList;

    private String currentUserId;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_message, container, false);

        conversationsRecyclerView = view.findViewById(R.id.conversationsRecyclerView);
        conversationsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        conversationList = new ArrayList<>();
        inboxAdapter = new InboxAdapter(getActivity(), conversationList);
        conversationsRecyclerView.setAdapter(inboxAdapter);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        fetchConversations();


        return view;
    }


    private void fetchConversations() {
        db.collection("Conversations")
                .whereArrayContains("participants", currentUserId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("Inbox", "Error fetching conversations", e);
                        return;
                    }

                    conversationList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Conversation conversation = doc.toObject(Conversation.class);
                        conversationList.add(conversation);
                    }
                    inboxAdapter.notifyDataSetChanged();
                });
    }


}