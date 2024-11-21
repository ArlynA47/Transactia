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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ManageRequest extends AppCompatActivity {

    String transactionid;
    ConstraintLayout constraintLayoutMine;
    TextView cancelreqbt, updatereqbt, completebt, rateUser;

    private static String listingOwnerParam, listingIDParam;

    TextView transactionTitle, transactiontimestamp;

    boolean isAccepted, isCompleted;
    String fireBUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_request);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        fireBUserID = user.getUid();

        transactionTitle = findViewById(R.id.transactionTitle);
        transactiontimestamp = findViewById(R.id.transactiontimestamp);

        constraintLayoutMine = findViewById(R.id.constraintLayoutMine);
        cancelreqbt = findViewById(R.id.cancelreqbt);
        updatereqbt = findViewById(R.id.updatereqbt);

        completebt = findViewById(R.id.completebt);

        rateUser = findViewById(R.id.rateUser);

        transactionid = getIntent().getStringExtra("transactionid");
        loadTransactionDetails(transactionid);

        isCompleted = getIntent().getBooleanExtra("isCompleted", false);

        if(isCompleted) {
            CompletedTransaction();
        } else {
            isAccepted = getIntent().getBooleanExtra("isAccepted", false);

            completebt.setOnClickListener(v -> {

                // Build the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialogTheme));
                builder.setTitle("Confirm Transaction Completion")
                        .setPositiveButton("Confirm", (dialog, which) -> {
                            markTransactionAsCompleted(transactionid, fireBUserID);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss(); // Close the dialog
                        });

                // Show the dialog
                builder.show();
            });

            updatereqbt.setOnClickListener(v -> {

                String lop = listingOwnerParam;
                String lip = listingIDParam;

                Intent intent = new Intent(ManageRequest.this, Request.class);
                intent.putExtra("newRequest", false);
                intent.putExtra("transactionid", transactionid);
                intent.putExtra("ownerUserId", lop); // Add owner User ID
                intent.putExtra("listingId", lip); // Pass the listing ID (document ID)
                startActivity(intent);
                finish();
            });

            cancelreqbt.setOnClickListener(v -> {

                // Build the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialogTheme));
                builder.setTitle("Cancel Transaction Request")
                        .setMessage("Are you sure you want to cancel this transaction request?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            cancelTransactionRequest(transactionid);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss(); // Close the dialog
                        });

                // Show the dialog
                builder.show();

                // Delay the intent by 2 seconds (2000 milliseconds)
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(ManageRequest.this, MyRequests.class);
                    startActivity(intent);
                    finish();
                }, 2000); // 2000 milliseconds = 2 seconds
            });

            if(isAccepted) {
                completebt.setVisibility(View.VISIBLE);
                cancelreqbt.setVisibility(View.GONE);
                updatereqbt.setVisibility(View.GONE);
            } else {
                completebt.setVisibility(View.GONE);
                cancelreqbt.setVisibility(View.VISIBLE);
                updatereqbt.setVisibility(View.VISIBLE);
            }

            checkCompletion(transactionid, fireBUserID);
        }



    }

    private void CompletedTransaction() {

        cancelreqbt.setVisibility(View.GONE);
        updatereqbt.setVisibility(View.GONE);

        rateUser.setVisibility(View.VISIBLE);
        completebt.setVisibility(View.VISIBLE);
        completebt.setEnabled(false);
        completebt.setText("Transaction Completed");
        completebt.setTextColor(ContextCompat.getColor(ManageRequest.this, R.color.lightgreen));

        rateUser.setOnClickListener(v -> {
            markTransactionAsRated(transactionid, fireBUserID);
        });

    }

    private void checkIfUserMarkedCompleted(String transactionId, String currentUserId, OnCheckCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Transactions")
                .whereEqualTo("transactionid", transactionId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        List<String> completionMarkedBy = (List<String>) document.get("completionMarkedBy");

                        if (completionMarkedBy != null && completionMarkedBy.contains(currentUserId)) {
                            // User has marked as completed
                            listener.onCheckComplete(true);
                        } else {
                            // User has not marked as completed
                            listener.onCheckComplete(false);
                        }
                    } else {
                        listener.onError("No transaction found with the given ID.");
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onError("Error querying transaction: " + e.getMessage());
                });
    }


    public interface OnCheckCompleteListener {
        void onCheckComplete(boolean hasMarked); // True if the user has marked, false otherwise
        void onError(String errorMessage);      // Handle any errors
    }

    private void checkCompletion(String transactionId, String currentUserId) {
        checkIfUserMarkedCompleted(transactionId, currentUserId, new OnCheckCompleteListener() {
            @Override
            public void onCheckComplete(boolean hasMarked) {
                if (hasMarked) {
                    completebt.setText("Marked as Completed");
                    completebt.setTextColor(ContextCompat.getColor(ManageRequest.this, R.color.lightgreen));
                    completebt.setEnabled(false);
                } else {
                    completebt.setText("Mark as Completed");
                    completebt.setEnabled(true);
                }
            }

            @Override
            public void onError(String errorMessage) {
                CustomToast.show(ManageRequest.this, errorMessage);
            }
        });
    }

    private void markTransactionAsCompleted(String transactionId, String currentUserId) {
        checkIfUserMarkedCompleted(transactionId, currentUserId, new OnCheckCompleteListener() {
            @Override
            public void onCheckComplete(boolean hasMarked) {
                if (hasMarked) {
                    CustomToast.show(ManageRequest.this, "You have already marked this transaction as completed.");
                } else {
                    // Proceed with marking as completed if not already marked
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Transactions")
                            .whereEqualTo("transactionid", transactionId)
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                if (!querySnapshot.isEmpty()) {
                                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                    DocumentReference transactionRef = document.getReference();

                                    // Add the current user to the completionMarkedBy list
                                    transactionRef.update("completionMarkedBy", FieldValue.arrayUnion(currentUserId))
                                            .addOnSuccessListener(aVoid -> {
                                                // After updating completionMarkedBy, check the field again
                                                transactionRef.get()
                                                        .addOnSuccessListener(updatedDoc -> {
                                                            List<String> completionMarkedBy = (List<String>) updatedDoc.get("completionMarkedBy");
                                                            String senderId = document.getString("senderID");
                                                            String receiverId = document.getString("receiverID");

                                                            if (completionMarkedBy != null && completionMarkedBy.contains(senderId) && completionMarkedBy.contains(receiverId)) {
                                                                // Both users have marked the transaction as completed
                                                                transactionRef.update("status", "Completed", "timestamp", FieldValue.serverTimestamp())
                                                                        .addOnSuccessListener(aVoid1 -> {
                                                                            CustomToast.show(ManageRequest.this, "Transaction marked as Completed!");
                                                                            CompletedTransaction();
                                                                        })
                                                                        .addOnFailureListener(e -> {
                                                                            CustomToast.show(ManageRequest.this, "Error updating transaction status: " + e.getMessage());
                                                                        });
                                                            } else {
                                                                // One user has marked as completed, waiting for the other user
                                                                CustomToast.show(ManageRequest.this, "Marked as completed. Waiting for the other user to confirm.");
                                                                checkCompletion(transactionId, currentUserId);
                                                            }
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            CustomToast.show(ManageRequest.this, "Error querying updated transaction: " + e.getMessage());
                                                        });
                                            })
                                            .addOnFailureListener(e -> {
                                                CustomToast.show(ManageRequest.this, "Error updating completion status: " + e.getMessage());
                                            });
                                } else {
                                    CustomToast.show(ManageRequest.this, "No transaction found with the given ID.");
                                }
                            })
                            .addOnFailureListener(e -> {
                                CustomToast.show(ManageRequest.this, "Error querying transaction: " + e.getMessage());
                            });
                }
            }

            @Override
            public void onError(String errorMessage) {
                CustomToast.show(ManageRequest.this, errorMessage);
            }
        });
    }



    private void cancelTransactionRequest(String transactionId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query to find the document(s) with the matching transactionId
        db.collection("Transactions")
                .whereEqualTo("transactionid", transactionId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Loop through the matching documents and delete them
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            db.collection("Transactions").document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        CustomToast.show(ManageRequest.this, "Transaction Request Cancelled successfully.");

                                    })
                                    .addOnFailureListener(e -> {
                                        CustomToast.show(ManageRequest.this, "Error deleting transaction: " + e.getMessage());
                                    });
                        }
                    } else {
                        CustomToast.show(ManageRequest.this, "No matching transaction found.");
                    }
                })
                .addOnFailureListener(e -> {
                    CustomToast.show(ManageRequest.this, "Error finding transaction: " + e.getMessage());
                });
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
                        String senderListing = transactionSnapshot.getString("senderListing");
                        String receiverListing = transactionSnapshot.getString("receiverListing");
                        String paymentTransaction = transactionSnapshot.getString("paymentTransaction");
                        String paymentMode = transactionSnapshot.getString("paymentMode");
                        String senderNote = transactionSnapshot.getString("senderNote");

                        String title = transactionSnapshot.getString("transactionTitle");
                        Timestamp timestmp = transactionSnapshot.getTimestamp("timestamp");

                        transactionTitle.setText(title);

                        listingIDParam = receiverListing;
                        listingOwnerParam = receiverID;

                        if(timestmp!=null) {
                            long timestampMillis = timestmp.getSeconds() * 1000; // Firestore Timestamp to milliseconds
                            Date date = new Date(timestampMillis);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                            String formattedDate = dateFormat.format(date);
                            transactiontimestamp.setText(formattedDate);
                        }


                        // Format listingValue as currency
                        if (paymentTransaction != null && !paymentTransaction.isEmpty()) {
                            String cleanString = paymentTransaction.toString().replaceAll("[^\\d]", ""); // Remove all non-numeric characters
                            double parsed = Double.parseDouble(cleanString);
                            String formatted = NumberFormat.getCurrencyInstance(new Locale("en", "PH")).format(parsed / 100);
                            ((TextView) findViewById(R.id.valuemine)).setText(formatted);
                        } else {
                            // If listingValue is empty, display "₱0.00"
                            double parsedValue = 0;
                            String formattedValue = NumberFormat.getCurrencyInstance(new Locale("en", "PH")).format(parsedValue); // Format as PHP
                            ((TextView) findViewById(R.id.valuemine)).setText(formattedValue);
                        }

                        // Populate transaction-specific views
                        ((TextView) findViewById(R.id.paymentmode)).setText(paymentMode);
                        ((TextView) findViewById(R.id.sendernote)).setText(senderNote);

                        // Fetch sender and receiver listing details
                        fetchListingDetails(db, receiverListing, true);

                        if(senderListing!=null) {
                            fetchListingDetails(db, senderListing, false);
                        } else {
                            constraintLayoutMine.setVisibility(View.GONE);
                        }

                        // Fetch user details for receiver
                        fetchUserDetails(db, receiverID);
                    } else {
                        Log.e("TransactionDetails", "No transaction found with transactionId: " + transactionId);
                    }
                })
                .addOnFailureListener(e -> Log.e("TransactionDetails", "Error fetching transaction: " + e.getMessage()));
    }


    private void fetchListingDetails(FirebaseFirestore db, String listingId, boolean isReceiverListing) {
        db.collection("Listings").document(listingId)
                .get()
                .addOnSuccessListener(listingSnapshot -> {
                    if (listingSnapshot.exists()) {
                        String listingImage = listingSnapshot.getString("listingImage");
                        String listingType = listingSnapshot.getString("listingType");
                        String listingCategory = listingSnapshot.getString("listingCategory");
                        String listingTitle = listingSnapshot.getString("title");
                        String listingDescription = listingSnapshot.getString("listingDescription");
                        String inExchange = listingSnapshot.getString("inExchange");
                        String priceValue = listingSnapshot.getString("listingValue");
                        Timestamp timestamp = listingSnapshot.getTimestamp("createdTimestamp");

                        // Populate listing-specific views
                        if (isReceiverListing) {
                            loadImageIntoView(listingImage, R.id.imageView);
                            ((TextView) findViewById(R.id.listingtype)).setText(listingType);
                            ((TextView) findViewById(R.id.listingCateg)).setText(listingCategory);
                            ((TextView) findViewById(R.id.listingTitle)).setText(listingTitle);
                            ((TextView) findViewById(R.id.listingDescription)).setText(listingDescription);
                            fetchTitleAndCategoryById(inExchange);

                            if(timestamp!=null) {
                                long timestampMillis = timestamp.getSeconds() * 1000; // Firestore Timestamp to milliseconds
                                Date date = new Date(timestampMillis);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                                String formattedDate = dateFormat.format(date);
                                ((TextView) findViewById(R.id.timestamp)).setText(formattedDate);
                            }

                            // Format listingValue as currency
                            if (priceValue != null) {
                                String cleanString = priceValue.toString().replaceAll("[^\\d]", ""); // Remove all non-numeric characters
                                double parsed = Double.parseDouble(cleanString);
                                String formatted = NumberFormat.getCurrencyInstance(new Locale("en", "PH")).format(parsed / 100);
                                ((TextView) findViewById(R.id.Pricevalue)).setText(formatted);
                            } else {
                                // If listingValue is empty, display "₱0.00"
                                double parsedValue = 0;
                                String formattedValue = NumberFormat.getCurrencyInstance(new Locale("en", "PH")).format(parsedValue); // Format as PHP
                                ((TextView) findViewById(R.id.Pricevalue)).setText(formattedValue);
                            }
                        } else {
                            loadImageIntoView(listingImage, R.id.imageViewMine);
                            ((TextView) findViewById(R.id.listingtypemine)).setText(listingType);
                            ((TextView) findViewById(R.id.listingCategmine)).setText(listingCategory);
                            ((TextView) findViewById(R.id.listingTitlemine)).setText(listingTitle);
                            ((TextView) findViewById(R.id.listingDescriptionmine)).setText(listingDescription);

                            if(timestamp!=null) {
                                long timestampMillis = timestamp.getSeconds() * 1000; // Firestore Timestamp to milliseconds
                                Date date = new Date(timestampMillis);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                                String formattedDate = dateFormat.format(date);
                                ((TextView) findViewById(R.id.timestampmine)).setText(formattedDate);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("ListingDetails", "Error fetching listing: " + e.getMessage()));
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

    private void fetchTitleAndCategoryById(String listingId) {
        // Set listingId to empty string if it is null
        if (listingId == null) {
            listingId = ""; // Set to empty string
        }

        // Check if the listingId is still empty before proceeding
        if (listingId.isEmpty()) {
            ((TextView) findViewById(R.id.Exchangefor)).setText("");
            return; // Early exit, as we can't fetch anything with an empty ID
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch the document from Firestore using the valid listingId
        db.collection("Listings").document(listingId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String title = document.getString("title");
                        String category = document.getString("listingCategory");

                        // Format the title and category for display
                        String titleAndType = title + " - " + category;

                        // Display the title and category in the inExchange EditText
                        ((TextView) findViewById(R.id.Exchangefor)).setText(titleAndType);
                    } else {
                        // Case 01A: If the document doesn't exist, you might want to handle this case
                        ((TextView) findViewById(R.id.Exchangefor)).setText(""); // Clear the EditText if the listing is not found
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the error gracefully
                    Toast.makeText(this, "Error fetching listing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    ((TextView) findViewById(R.id.Exchangefor)).setText(""); // Clear the EditText in case of an error
                });
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

                        if (ratedList.contains(currentUserId)) {
                            // User has already rated, show a message
                            CustomToast.show(this, "You have already rated the trader for this transaction.");
                            return;
                        } else {
                            Intent intentRate = new Intent(ManageRequest.this, RateTrader.class);
                            intentRate.putExtra("transactionid", transactionid);
                            startActivity(intentRate);
                        }

                    } else {
                        CustomToast.show(this, "Transaction not found.");
                    }
                })
                .addOnFailureListener(e -> {
                    CustomToast.show(this, "Error querying transaction: " + e.getMessage());
                });
    }

}