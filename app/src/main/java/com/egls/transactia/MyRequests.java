package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyRequests extends AppCompatActivity {

    FirebaseUser user;
    String fireBUserID;

    RecyclerView requestrv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_requests);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        fireBUserID = user.getUid();

        requestrv = findViewById(R.id.requestrv);

        fetchTransactions();

    }

    public void fetchTransactions() {
        String currentUserId = fireBUserID;

        FirebaseFirestore.getInstance()
                .collection("Transactions")
                .whereEqualTo("senderID", currentUserId)
                .whereEqualTo("status", "Request")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Transaction> transactionList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Transaction transaction = document.toObject(Transaction.class);
                        transactionList.add(transaction);
                    }

                    // Set up RecyclerView with adapter
                    TransactionAdapter tAdapter = new TransactionAdapter(this, transactionList, currentUserId);
                    requestrv.setAdapter(tAdapter);
                })
                .addOnFailureListener(e -> {
                    CustomToast.show(this, "Error fetching transactions: " + e.getMessage());
                });
    }

    @Override
    public void onBackPressed() {
        // Access the existing instance of MainHome
        MainHome mainHomeActivity = (MainHome) MainHome.getInstance(); // Create a static instance accessor in MainHome
        if (mainHomeActivity != null) {
            mainHomeActivity.whatHomeScreen(3); // Set the value you want
        }
        super.onBackPressed(); // Navigate back
    }



}