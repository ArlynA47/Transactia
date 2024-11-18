package com.egls.transactia;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TransactionManager {

    public void createTransaction(
            String transactionTitle,
            String senderID,
            String receiverID,
            String senderListing,
            String receiverListing,
            String status,
            String paymentTransaction,
            String paymentMode,
            String senderNote,
            OnTransactionCompleteListener listener // Callback interface
    ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a map for the transaction fields
        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("transactionid", UUID.randomUUID().toString());
        transactionData.put("transactionTitle", transactionTitle);
        transactionData.put("senderID", senderID);
        transactionData.put("receiverID", receiverID);
        transactionData.put("senderListing", senderListing);
        transactionData.put("receiverListing", receiverListing);
        transactionData.put("status", status);
        transactionData.put("paymentTransaction", paymentTransaction);
        transactionData.put("paymentMode", paymentMode);
        transactionData.put("senderNote", senderNote);
        transactionData.put("receiverNote", "");
        transactionData.put("senderAcceptTimestamp", "");
        transactionData.put("transactionDoneTimestamp", "");
        transactionData.put("timestamp", FieldValue.serverTimestamp());

        // Save the transaction to Firestore
        db.collection("Transactions").add(transactionData)
                .addOnSuccessListener(documentReference -> {
                    // Notify success via the callback
                    listener.onTransactionSuccess();
                })
                .addOnFailureListener(e -> {
                    // Notify failure via the callback
                    listener.onTransactionFailure(e.getMessage());
                });
    }

    // Callback interface for transaction completion
    public interface OnTransactionCompleteListener {
        void onTransactionSuccess();
        void onTransactionFailure(String errorMessage);
    }

    public void updateTransaction(
            String transactionId,
            String senderListing,
            String paymentTransaction,
            String paymentMode,
            String senderNote,
            OnTransactionUpdateListener listener // Callback interface
    ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a map for the fields to update
        Map<String, Object> updates = new HashMap<>();
        updates.put("senderListing", senderListing);
        updates.put("paymentTransaction", paymentTransaction);
        updates.put("paymentMode", paymentMode);
        updates.put("senderNote", senderNote); // Add senderNote to the update
        updates.put("timestamp", FieldValue.serverTimestamp()); // Update the timestamp

        // Update the document in Firestore
        db.collection("Transactions").document(transactionId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Notify success via the callback
                    listener.onTransactionUpdateSuccess();
                })
                .addOnFailureListener(e -> {
                    // Notify failure via the callback
                    listener.onTransactionUpdateFailure(e.getMessage());
                });
    }

    // Callback interface for transaction updates
    public interface OnTransactionUpdateListener {
        void onTransactionUpdateSuccess();
        void onTransactionUpdateFailure(String errorMessage);
    }

}
