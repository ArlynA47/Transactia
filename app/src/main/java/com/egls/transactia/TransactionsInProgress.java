package com.egls.transactia;

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
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class TransactionsInProgress extends AppCompatActivity {


    FirebaseUser user;
    String fireBUserID;

    RecyclerView requestrv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transactions_in_progress);
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query for transactions where the current user is the sender
        Query senderQuery = db.collection("Transactions")
                .whereEqualTo("senderID", currentUserId)
                .whereEqualTo("status", "Accepted");

        // Query for transactions where the current user is the receiver
        Query receiverQuery = db.collection("Transactions")
                .whereEqualTo("receiverID", currentUserId)
                .whereEqualTo("status", "Accepted");

        // Execute both queries and merge the results
        senderQuery.get().addOnSuccessListener(senderSnapshot -> {
            receiverQuery.get().addOnSuccessListener(receiverSnapshot -> {
                List<Transaction> transactionList = new ArrayList<>();

                // Add transactions where the user is the sender
                for (DocumentSnapshot document : senderSnapshot) {
                    Transaction transaction = document.toObject(Transaction.class);
                    transactionList.add(transaction);
                }

                // Add transactions where the user is the receiver
                for (DocumentSnapshot document : receiverSnapshot) {
                    Transaction transaction = document.toObject(Transaction.class);
                    transactionList.add(transaction);
                }

                // Set up RecyclerView with adapter
                TransactionAdapter tAdapter = new TransactionAdapter(this, transactionList, currentUserId);
                requestrv.setAdapter(tAdapter);
            }).addOnFailureListener(e -> {
                CustomToast.show(this, "Error fetching receiver transactions: " + e.getMessage());
            });
        }).addOnFailureListener(e -> {
            CustomToast.show(this, "Error fetching sender transactions: " + e.getMessage());
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