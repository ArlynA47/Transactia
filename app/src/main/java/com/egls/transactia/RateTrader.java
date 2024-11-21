package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RateTrader extends AppCompatActivity {

    ImageView star1, star2, star3, star4, star5, imageView2;
    TextView ratingLabel, transactionTitle, transactiontimestamp, tradername, userlocation, submit;

    int starRating;

    String fireBUserID;

    String transactionid;

    String userToBeRated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rate_trader);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        fireBUserID = user.getUid();

        transactionid = getIntent().getStringExtra("transactionid");
        loadTransactionDetails(transactionid);

        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);
        ratingLabel = findViewById(R.id.ratingLabel);

        submit = findViewById(R.id.submit);

        transactionTitle = findViewById(R.id.transactionTitle);
        transactiontimestamp = findViewById(R.id.transactiontimestamp);
        imageView2 = findViewById(R.id.imageView2);
        tradername= findViewById(R.id.tradername);
        userlocation= findViewById(R.id.userlocation);

        clickStar(1);
        starRating = 1;

        star1.setOnClickListener(v -> {
            clickStar(1);
            starRating = 1;
        });
        star2.setOnClickListener(v -> {
            clickStar(2);
            starRating = 2;
        });
        star3.setOnClickListener(v -> {
            clickStar(3);
            starRating = 3;
        });
        star4.setOnClickListener(v -> {
            clickStar(4);
            starRating = 4;
        });
        star5.setOnClickListener(v -> {
            clickStar(5);
            starRating = 5;
        });

        submit.setOnClickListener(v -> {
            markTransactionAsRated(transactionid, fireBUserID);
        });

    }

    private void clickStar(int starNum) {

        star1.setImageResource(R.drawable.startuc);
        star2.setImageResource(R.drawable.startuc);
        star3.setImageResource(R.drawable.startuc);
        star4.setImageResource(R.drawable.startuc);
        star5.setImageResource(R.drawable.startuc);

        switch (starNum) {
            case 1:
                ratingLabel.setText("1 Star Rating");
                    star1.setImageResource(R.drawable.startc);
                break;
            case 2:
                ratingLabel.setText("2 Star Rating");
                star1.setImageResource(R.drawable.startc);
                star2.setImageResource(R.drawable.startc);
                break;
            case 3:
                ratingLabel.setText("3 Star Rating");
                star1.setImageResource(R.drawable.startc);
                star2.setImageResource(R.drawable.startc);
                star3.setImageResource(R.drawable.startc);
                break;
            case 4:
                ratingLabel.setText("4 Star Rating");
                star1.setImageResource(R.drawable.startc);
                star2.setImageResource(R.drawable.startc);
                star3.setImageResource(R.drawable.startc);
                star4.setImageResource(R.drawable.startc);
                break;
            case 5:
                ratingLabel.setText("5 Star Rating");
                star1.setImageResource(R.drawable.startc);
                star2.setImageResource(R.drawable.startc);
                star3.setImageResource(R.drawable.startc);
                star4.setImageResource(R.drawable.startc);
                star5.setImageResource(R.drawable.startc);
                break;
        }

    }

    private void loadTransactionDetails(String transactionId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query the Transactions collection where the transactionId matches
        db.collection("Transactions")
                .whereEqualTo("transactionid", transactionId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot transactionSnapshot = queryDocumentSnapshots.getDocuments().get(0); // Get the first document
                        String senderID = transactionSnapshot.getString("senderID");
                        String receiverID = transactionSnapshot.getString("receiverID");

                        String title = transactionSnapshot.getString("transactionTitle");
                        Timestamp timestmp = transactionSnapshot.getTimestamp("timestamp");

                        transactionTitle.setText(title);

                        if(timestmp!=null) {
                            long timestampMillis = timestmp.getSeconds() * 1000; // Firestore Timestamp to milliseconds
                            Date date = new Date(timestampMillis);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                            String formattedDate = dateFormat.format(date);
                            transactiontimestamp.setText(formattedDate);
                        }

                        if(fireBUserID.equals(senderID)) {
                            // Fetch user details for receiver
                            fetchUserDetails(db, receiverID);
                            userToBeRated = receiverID;
                        } else if(fireBUserID.equals(receiverID)) {
                            // Fetch user details for receiver
                            fetchUserDetails(db, senderID);
                            userToBeRated = senderID;
                        }

                    } else {
                        Log.e("TransactionDetails", "No transaction found with transactionId: " + transactionId);
                    }
                })
                .addOnFailureListener(e -> Log.e("TransactionDetails", "Error fetching transaction: " + e.getMessage()));
    }

    private void fetchUserDetails(FirebaseFirestore db, String userId) {
        db.collection("UserDetails").document(userId)
                .get()
                .addOnSuccessListener(userSnapshot -> {
                    if (userSnapshot.exists()) {
                        String profileImage = userSnapshot.getString("imageUrl");
                        String name = userSnapshot.getString("name");
                        Map<String, String> locationMap = (Map<String, String>) userSnapshot.get("locationMap");

                        // Format the location string
                        String location = "";
                        if (locationMap != null) {
                            location = String.format("%s, %s, %s",
                                    locationMap.get("city"),
                                    locationMap.get("state"),
                                    locationMap.get("country"));
                        }

                        // Populate user-specific views
                        loadImageIntoView(profileImage, R.id.imageView2);
                        ((TextView) findViewById(R.id.tradername)).setText(name);
                        ((TextView) findViewById(R.id.userlocation)).setText(location);
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

    private void markTransactionAsRated(String transactionId, String currentUserId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query for the document with the matching transaction ID
        db.collection("Transactions")
                .whereEqualTo("transactionid", transactionId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Assuming there is only one matching document
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                        // Get the current list of users who rated
                        List<String> ratedList = (List<String>) document.get("rated");

                        if (ratedList == null) {
                            ratedList = new ArrayList<>(); // Initialize if null
                        }

                        // Add the current user's ID to the rated list
                        ratedList.add(currentUserId);

                        // Update the rated field in the document
                        document.getReference()
                                .update("rated", ratedList)
                                .addOnSuccessListener(aVoid -> {
                                    updateUserRating(userToBeRated, starRating);
                                })
                                .addOnFailureListener(e -> {
                                    CustomToast.show(this, "Error while saving your rating: " + e.getMessage());
                                });
                    } else {
                        CustomToast.show(this, "Transaction not found.");
                    }
                })
                .addOnFailureListener(e -> {
                    CustomToast.show(this, "Error querying transaction: " + e.getMessage());
                });
    }

    private void updateUserRating(String userId, int newRating) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the user's document in the UserDetails collection
        DocumentReference userRef = db.collection("UserDetails").document(userId);

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(userRef);

            // Get current ratings and numberOfRatings; initialize if they don't exist
            double currentRatings = snapshot.contains("ratings") ? snapshot.getDouble("ratings") : 0.0;
            long numberOfRatings = snapshot.contains("numberofratings") ? snapshot.getLong("numberofratings") : 0;

            // Compute new ratings and increment numberOfRatings
            double updatedRatings = currentRatings + newRating;
            long updatedNumberOfRatings = numberOfRatings + 1;

            // Compute the average (optional, but can be useful if needed immediately)
            double averageRating = updatedRatings / updatedNumberOfRatings;

            // Update the fields in the database
            transaction.update(userRef, "ratings", updatedRatings);
            transaction.update(userRef, "numberofratings", updatedNumberOfRatings);

            return averageRating; // Return the new average rating
        }).addOnSuccessListener(averageRating -> {
            // Transaction was successful
            CustomToast.show(this, "Thank you for rating!");
            // Delay the intent by 2 seconds (2000 milliseconds)
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                finish();
            }, 2000); // 2000 milliseconds = 2 seconds
        }).addOnFailureListener(e -> {
            // Handle the error
            CustomToast.show(this, "Error updating rating: " + e.getMessage());
        });
    }
}