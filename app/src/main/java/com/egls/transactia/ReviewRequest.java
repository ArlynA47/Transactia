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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReviewRequest extends AppCompatActivity {

    String transactionid;

    ConstraintLayout constraintLayout9, constraintLayout10;
    TextView transactionTitle, transactiontimestamp;
    TextView acceptreqbt, declinereqbt, completebt, rateUser;
    boolean isAccepted, isCompleted;
    String fireBUserID;
    String senderID, myName;

    ImageView imageView2;
    int homscr = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review_request);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        fireBUserID = user.getUid();
        fetchUserName(fireBUserID);

        imageView2 = findViewById(R.id.imageView2);

        acceptreqbt = findViewById(R.id.acceptreqbt);
        declinereqbt = findViewById(R.id.declinereqbt);

        completebt = findViewById(R.id.completebt);

        rateUser = findViewById(R.id.rateUser);

        transactionTitle = findViewById(R.id.transactionTitle);
        transactiontimestamp = findViewById(R.id.transactiontimestamp);

        constraintLayout9 = findViewById(R.id.constraintLayout9);
        constraintLayout10 = findViewById(R.id.constraintLayout10);

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

            declinereqbt.setOnClickListener(v -> {
                // Build the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialogTheme));
                builder.setTitle("Decline Transaction Request")
                        .setMessage("Are you sure you want to decline this transaction request?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            cancelTransactionRequest(transactionid);

                            // Delay the intent by 2 seconds (2000 milliseconds)
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                // Access the existing instance of MainHome
                                MainHome mainHomeActivity = (MainHome) MainHome.getInstance(); // Create a static instance accessor in MainHome
                                if (mainHomeActivity != null) {
                                    mainHomeActivity.whatHomeScreen(homscr); // Set the value you want
                                    finish();
                                }
                            }, 2000); // 2000 milliseconds = 2 seconds
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss(); // Close the dialog
                        });

                // Show the dialog
                builder.show();
            });

            acceptreqbt.setOnClickListener(v -> {
                // Build the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialogTheme));
                builder.setTitle("Accept Transaction Request")
                        .setMessage("Are you sure you want to accept this transaction request?")
                        .setPositiveButton("Confirm", (dialog, which) -> {
                            updateTransactionStatus(transactionid);
                            // Delay the intent by 2 seconds (2000 milliseconds)
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                // Access the existing instance of MainHome
                                MainHome mainHomeActivity = (MainHome) MainHome.getInstance(); // Create a static instance accessor in MainHome
                                if (mainHomeActivity != null) {
                                    mainHomeActivity.whatHomeScreen(homscr); // Set the value you want
                                    finish();
                                }
                            }, 2000); // 2000 milliseconds = 2 seconds
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss(); // Close the dialog
                        });

                // Show the dialog
                builder.show();
            });


            if(isAccepted) {
                completebt.setVisibility(View.VISIBLE);
                declinereqbt.setVisibility(View.GONE);
                acceptreqbt.setVisibility(View.GONE);
            } else {
                completebt.setVisibility(View.GONE);
                declinereqbt.setVisibility(View.VISIBLE);
                acceptreqbt.setVisibility(View.VISIBLE);
            }

            checkCompletion(transactionid, fireBUserID);
        }
    }

    private void CompletedTransaction() {

        declinereqbt.setVisibility(View.GONE);
        acceptreqbt.setVisibility(View.GONE);

        rateUser.setVisibility(View.VISIBLE);
        completebt.setVisibility(View.VISIBLE);
        completebt.setEnabled(false);
        completebt.setText("Transaction Completed");
        completebt.setTextColor(ContextCompat.getColor(ReviewRequest.this, R.color.lightgreen));


        rateUser.setOnClickListener(v -> {
            markTransactionAsRated(transactionid, fireBUserID);
        });

        imageView2.setOnClickListener(v -> {
            Intent intentprof = new Intent(this, Traderprofile.class);
            intentprof.putExtra("userId", senderID);
            startActivity(intentprof);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
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

    @Override
    public void onBackPressed() {
        // Access the existing instance of MainHome
        MainHome mainHomeActivity = (MainHome) MainHome.getInstance(); // Create a static instance accessor in MainHome
        if (mainHomeActivity != null) {
            mainHomeActivity.whatHomeScreen(homscr); // Set the value you want
            finish();
        }
        super.onBackPressed(); // Navigate back
    }


    public interface OnCheckCompleteListener {
        void onCheckComplete(boolean hasMarked); // True if the user has marked, false otherwise
        void onError(String errorMessage);      // Handle any errors
    }

    private void checkCompletion(String transactionId, String currentUserId) {
        checkIfUserMarkedCompleted(transactionId, currentUserId, new ReviewRequest.OnCheckCompleteListener() {
            @Override
            public void onCheckComplete(boolean hasMarked) {
                if (hasMarked) {
                    completebt.setText("Marked as Completed");
                    completebt.setTextColor(ContextCompat.getColor(ReviewRequest.this, R.color.lightgreen));
                    homscr = 4;
                    completebt.setEnabled(false);
                } else {
                    completebt.setText("Mark as Completed");
                    completebt.setEnabled(true);
                }
            }

            @Override
            public void onError(String errorMessage) {
                CustomToast.show(ReviewRequest.this, errorMessage);
            }
        });
    }

    private void markTransactionAsCompleted(String transactionId, String currentUserId) {
        checkIfUserMarkedCompleted(transactionId, currentUserId, new OnCheckCompleteListener() {
            @Override
            public void onCheckComplete(boolean hasMarked) {
                if (hasMarked) {
                    CustomToast.show(ReviewRequest.this, "You have already marked this transaction as completed.");
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
                                                                            sendCompNotification();
                                                                            CustomToast.show(ReviewRequest.this, "Transaction marked as Completed!");
                                                                            homscr = 4;
                                                                            CompletedTransaction();
                                                                        })
                                                                        .addOnFailureListener(e -> {
                                                                            CustomToast.show(ReviewRequest.this, "Error updating transaction status: " + e.getMessage());
                                                                        });
                                                            } else {
                                                                // One user has marked as completed, waiting for the other user
                                                                CustomToast.show(ReviewRequest.this, "Marked as completed. Waiting for the other user to confirm.");
                                                                checkCompletion(transactionId, currentUserId);
                                                            }
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            CustomToast.show(ReviewRequest.this, "Error querying updated transaction: " + e.getMessage());
                                                        });
                                            })
                                            .addOnFailureListener(e -> {
                                                CustomToast.show(ReviewRequest.this, "Error updating completion status: " + e.getMessage());
                                            });
                                } else {
                                    CustomToast.show(ReviewRequest.this, "No transaction found with the given ID.");
                                }
                            })
                            .addOnFailureListener(e -> {
                                CustomToast.show(ReviewRequest.this, "Error querying transaction: " + e.getMessage());
                            });
                }
            }

            @Override
            public void onError(String errorMessage) {
                CustomToast.show(ReviewRequest.this, errorMessage);
            }
        });
    }


    private void updateTransactionStatus(String transactionId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a map for the field updates
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "Accepted");  // Update the status to "Accepted"
        updates.put("timestamp", FieldValue.serverTimestamp());  // Set the current server time

        // Query for the document with the matching transactionid
        db.collection("Transactions")
                .whereEqualTo("transactionid", transactionId)  // Filter by transactionid
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Assuming there is only one matching document
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                        // Update the document using the document reference
                        document.getReference()
                                .update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    sendNotification();
                                    CustomToast.show(ReviewRequest.this, "Transaction request accepted.");
                                })
                                .addOnFailureListener(e -> {
                                    CustomToast.show(ReviewRequest.this, "Error updating transaction status: " + e.getMessage());
                                });
                    } else {
                        CustomToast.show(ReviewRequest.this, "No transaction found with the given ID.");
                    }
                })
                .addOnFailureListener(e -> {
                    CustomToast.show(ReviewRequest.this, "Error querying transaction: " + e.getMessage());
                });
    }

    private void sendNotification() {
        // Get a reference to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a map to represent the notification
        Map<String, Object> notification = new HashMap<>();
        notification.put("message", myName + " has accepted your transaction request for "+ transactionTitle + ".");
        notification.put("status", "unread");
        notification.put("timestamp", FieldValue.serverTimestamp());  // Automatically set timestamp to the current server time
        notification.put("title", "Transaction Request Accepted");
        notification.put("userId", senderID);  // Replace with the actual user ID

        // Add the notification to the UserNotifications collection
        db.collection("UserNotifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {

                })
                .addOnFailureListener(e -> {

                });

    }

    private void sendCompNotification() {
        // Get a reference to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a map to represent the notification
        Map<String, Object> notification = new HashMap<>();
        notification.put("message", myName + " marked the transaction "+ transactionTitle + " as completed.");
        notification.put("status", "unread");
        notification.put("timestamp", FieldValue.serverTimestamp());  // Automatically set timestamp to the current server time
        notification.put("title", "Transaction Marked as Comleted");
        notification.put("userId", senderID);  // Replace with the actual user ID

        // Add the notification to the UserNotifications collection
        db.collection("UserNotifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {

                })
                .addOnFailureListener(e -> {

                });

    }

    private void fetchUserName(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("UserDetails").document(userId)
                .get()
                .addOnSuccessListener(userSnapshot -> {
                    if (userSnapshot.exists()) {
                        myName = userSnapshot.getString("name");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UserDetails", "Error fetching user details: " + e.getMessage());
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
                                        CustomToast.show(ReviewRequest.this, "Transaction Request Declined successfully.");

                                    })
                                    .addOnFailureListener(e -> {
                                        CustomToast.show(ReviewRequest.this, "Error deleting transaction: " + e.getMessage());
                                    });
                        }
                    } else {
                        CustomToast.show(ReviewRequest.this, "No matching transaction found.");
                    }
                })
                .addOnFailureListener(e -> {
                    CustomToast.show(ReviewRequest.this, "Error finding transaction: " + e.getMessage());
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
                        senderID = transactionSnapshot.getString("senderID");
                        String receiverID = transactionSnapshot.getString("receiverID");
                        String senderListing = transactionSnapshot.getString("senderListing");
                        String receiverListing = transactionSnapshot.getString("receiverListing");
                        String paymentTransaction = transactionSnapshot.getString("paymentTransaction");
                        String paymentMode = transactionSnapshot.getString("paymentMode");
                        String senderNote = transactionSnapshot.getString("senderNote");

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


                        // Format listingValue as currency
                        if (paymentTransaction != null) {
                            String cleanString = paymentTransaction.replaceAll("[^\\d]", ""); // Remove all non-numeric characters
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
                            constraintLayout9.setVisibility(View.GONE);
                            constraintLayout10.setVisibility(View.GONE);
                        }

                        // Fetch user details for receiver
                        fetchUserDetails(db, senderID);
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

                            // Format listingValue as currency
                            if (priceValue != null) {
                                String cleanString = priceValue.toString().replaceAll("[^\\d]", ""); // Remove all non-numeric characters
                                double parsed = Double.parseDouble(cleanString);
                                String formatted = NumberFormat.getCurrencyInstance(new Locale("en", "PH")).format(parsed / 100);
                                ((TextView) findViewById(R.id.valuemine)).setText(formatted);
                            } else {
                                // If listingValue is empty, display "₱0.00"
                                double parsedValue = 0;
                                String formattedValue = NumberFormat.getCurrencyInstance(new Locale("en", "PH")).format(parsedValue); // Format as PHP
                                ((TextView) findViewById(R.id.valuemine)).setText(formattedValue);
                            }
                        } else {
                            loadImageIntoView(listingImage, R.id.imageView);
                            ((TextView) findViewById(R.id.listingtype)).setText(listingType);
                            ((TextView) findViewById(R.id.listingCateg)).setText(listingCategory);
                            ((TextView) findViewById(R.id.listingTitle)).setText(listingTitle);
                            ((TextView) findViewById(R.id.listingDescription)).setText(listingDescription);
                            // Format listingValue as currency
                            if (priceValue != null) {
                                String cleanString = priceValue.toString().replaceAll("[^\\d]", ""); // Remove all non-numeric characters
                                double parsed = Double.parseDouble(cleanString);
                                String formatted = NumberFormat.getCurrencyInstance(new Locale("en", "PH")).format(parsed / 100);
                                ((TextView) findViewById(R.id.listingvalmine)).setText(formatted);
                            } else {
                                // If listingValue is empty, display "₱0.00"
                                double parsedValue = 0;
                                String formattedValue = NumberFormat.getCurrencyInstance(new Locale("en", "PH")).format(parsedValue); // Format as PHP
                                ((TextView) findViewById(R.id.listingvalmine)).setText(formattedValue);
                            }

                            if(timestamp!=null) {
                                long timestampMillis = timestamp.getSeconds() * 1000; // Firestore Timestamp to milliseconds
                                Date date = new Date(timestampMillis);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                                String formattedDate = dateFormat.format(date);
                                ((TextView) findViewById(R.id.timestamp)).setText(formattedDate);
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
                            Intent intentRate = new Intent(ReviewRequest.this, RateTrader.class);
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