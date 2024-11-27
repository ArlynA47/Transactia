package com.egls.transactia;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

public class FirestoreHelper {
    public static void saveTokenToFirestore(String token) {
        if (token == null || token.isEmpty()) {
            Log.e("FCM Token", "Token is null or empty. Skipping save.");
            return;
        }

        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("UserDetails").document(userId);

            userRef.set(
                    new HashMap<String, Object>() {{
                        put("fcmToken", token);
                    }},
                    SetOptions.merge()
            ).addOnSuccessListener(aVoid -> {
                Log.d("FCM Token", "Token saved successfully!");
            }).addOnFailureListener(e -> {
                Log.e("FCM Token", "Failed to save token", e);
            });
        } else {
            Log.e("FCM Token", "User ID is null. Cannot save token.");
        }
    }
}

