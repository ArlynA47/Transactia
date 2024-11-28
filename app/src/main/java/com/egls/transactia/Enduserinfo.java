package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;

import com.google.firebase.firestore.DocumentSnapshot;



import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Enduserinfo extends AppCompatActivity {

    ImageView traderprof, reportbt;
    View reportPromptLayout, reportSuccessLayout, dark_overlay;
    ImageView reportSuccess;

    TextView reportcfbt, reportbackbt, violation, description, viewReports, ok;

    private String conversationId;
    private FirebaseFirestore db;

    ConstraintLayout deleteConversationButton, backbt;
    String userId;
    String profileImage;


    String fireBUserID;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enduserinfo);

        // Set padding for edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        fireBUserID = user.getUid();

        // Initialize ImageView and set click listener
        traderprof = findViewById(R.id.traderprof);
        deleteConversationButton = findViewById(R.id.deleteConversationButton);

        backbt = findViewById(R.id.backbt);

        // Initialize views
        reportPromptLayout = findViewById(R.id.layout_reportprompttt); // layout where the report prompt is defined
        reportbt = findViewById(R.id.reportbt); // layout that triggers the report prompt visibility
        reportcfbt = findViewById(R.id.reportcfbt);
        reportbackbt = findViewById(R.id.reportbackbt);
        violation = findViewById(R.id.violation);
        description = findViewById(R.id.description);

        dark_overlay = findViewById(R.id.dark_overlay);

        reportSuccessLayout = findViewById(R.id.layout_reportSuccess);
        ok = findViewById(R.id.ok);

        // Get the conversation ID passed from Inbox activity
        conversationId = getIntent().getStringExtra("conversationId");
        userId = getIntent().getStringExtra("userId");

        fetchUserImg(userId);



        db = FirebaseFirestore.getInstance();

        traderprof.setOnClickListener(v -> goToTraderProfile());

        backbt.setOnClickListener(v -> finish());

        deleteConversationButton.setOnClickListener(v -> {

            // Build the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialogTheme));
            builder.setTitle("Delete Conversation?")
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        deleteConversation();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss(); // Close the dialog
                    });

            // Show the dialog
            builder.show();
        });

        // Set click listener for reportPrompt
        reportbt.setOnClickListener(v -> {
            // Make report prompt visible and bring it to the front
            pickCategory();
        });

        ok.setOnClickListener(v -> {
            dark_overlay.setVisibility(View.GONE);
            reportSuccessLayout.setVisibility(View.GONE);
            finish();
        });
    }

    private void deleteConversation() {
        if (conversationId == null || conversationId.isEmpty()) {
            Toast.makeText(this, "Invalid conversation ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Deleting messages in the conversation
        db.collection("Messages").document(conversationId).collection("Messages")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // Use batch to delete all messages
                    db.runTransaction(transaction -> {
                        // Iterate through querySnapshot to delete documents
                        for (DocumentSnapshot doc : querySnapshot) {
                            transaction.delete(doc.getReference()); // Use the correct DocumentSnapshot here
                        }
                        return null;
                    }).addOnSuccessListener(aVoid -> {
                        // Delete the conversation itself after deleting messages
                        db.collection("Conversations").document(conversationId)
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Conversation deleted", Toast.LENGTH_SHORT).show();
                                    finish(); // Close the activity
                                })
                                .addOnFailureListener(e -> Log.e("DeleteConversation", "Failed to delete conversation: " + e.getMessage()));
                    }).addOnFailureListener(e -> Log.e("DeleteMessages", "Failed to delete messages: " + e.getMessage()));
                })
                .addOnFailureListener(e -> Log.e("DeleteConversation", "Failed to fetch messages: " + e.getMessage()));

    }

    private void fetchUserImg(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("UserDetails").document(userId)
                .get()
                .addOnSuccessListener(userSnapshot -> {
                    if (userSnapshot.exists()) {
                        profileImage = userSnapshot.getString("imageUrl");
                        String userName = userSnapshot.getString("name");

                        ((TextView) findViewById(R.id.textView13)).setText(userName);

                        loadImageIntoView(profileImage, R.id.imageView6);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UserDetails", "Error fetching user details: " + e.getMessage());

                });
    }

    private void loadImageIntoView(String imageUrl, int imageViewId) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            ImageView imageView = findViewById(imageViewId);
            Glide.with(this).load(imageUrl).into(imageView);
        }
    }

    // Function to navigate to Traderprofile activity
    private void goToTraderProfile() {
        Intent intent = new Intent(this, Traderprofile.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Apply the transition when back button is pressed
        overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
    }


    private void pickCategory() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Step 2: Fetch Categories (sub-collections under the type)
        db.collection("Violations").document("User").collection("Categories").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<String> categories = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    categories.add(document.getId());
                }

                // Show dialog for selecting a category
                showSelectionDialog("Select Category", categories, selectedCategory -> pickSubCategory("User", selectedCategory));
            }
        });
    }

    private void pickSubCategory(String type, String category) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Step 3: Fetch SubCategories (documents in SubCategories collection)
        db.collection("Violations").document(type)
                .collection("Categories").document(category)
                .collection("SubCategories").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<String> subCategories = new ArrayList<>();
                        List<String> descriptions = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            subCategories.add(document.getId());
                            descriptions.add(document.getString("desc"));
                        }

                        // Show dialog for selecting a sub-category
                        showSelectionDialog("Select Sub-Category", subCategories, selectedSubCategory -> {
                            int index = subCategories.indexOf(selectedSubCategory);
                            String description = descriptions.get(index);
                            showViolationDetails(selectedSubCategory, description);
                        });
                    }
                });
    }

    private void showSelectionDialog(String title, List<String> options, Traderprofile.OnOptionSelectedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialogTheme));
        builder.setTitle(title)
                .setItems(options.toArray(new String[0]), (dialog, which) -> {
                    String selectedOption = options.get(which);
                    listener.onOptionSelected(selectedOption);
                })
                .show();
    }

    private void showViolationDetails(String subCategory, String description) {
        // Example data for reporter and reported user IDs
        String reporterId = fireBUserID; // ID of the user reporting
        String reportedUserId = userId; // Replace this with the actual reported user ID
        // Timestamp for the report

        dark_overlay.setVisibility(View.VISIBLE);
        reportPromptLayout.setVisibility(View.VISIBLE);
        reportPromptLayout.bringToFront();

        violation.setText(subCategory);
        this.description.setText(description);

        reportcfbt.setOnClickListener(v -> {
            reportPromptLayout.setVisibility(View.GONE);
            saveViolationReport(subCategory, description, reporterId, reportedUserId);
        });
        reportbackbt.setOnClickListener(v -> {
            reportPromptLayout.setVisibility(View.GONE);
            dark_overlay.setVisibility(View.GONE);
        });

    }

    private void saveViolationReport(String subCategory, String description, String reporterId, String reportedUserId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Data to be saved
        Map<String, Object> reportData = new HashMap<>();
        reportData.put("violationType", subCategory);
        reportData.put("description", description);
        reportData.put("reporterId", reporterId);
        reportData.put("reportedUserId", reportedUserId);
        reportData.put("timestamp", FieldValue.serverTimestamp());
        reportData.put("status", "Pending");

        // Save to Firestore under Reports/User
        db.collection("Reports").document("User").collection("UserReports").add(reportData)
                .addOnSuccessListener(documentReference -> {
                    reportSuccessLayout.setVisibility(View.VISIBLE);
                    reportSuccessLayout.bringToFront();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to submit report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Interface for handling dialog selections
    interface OnOptionSelectedListener {
        void onOptionSelected(String selectedOption);
    }


}



