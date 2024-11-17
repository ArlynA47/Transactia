package com.egls.transactia;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ArchiveDetails extends AppCompatActivity {

    FirebaseUser user;

    private ImageView currentlySelectedImageView = null;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private FirebaseStorage storage;

    String fireBUserID;

    TextView errorTv;

    private ImageView selectedImageView;

    ProgressBar progressBar;

    private String imageUrl;

    // Listing Detail holders
    String title;
    String listingType;
    String listingCategory;
    String listingDescription;
    String listingValue;
    String selectedListingId;

    TextView textView23, textView24, textView25;
    TextView textView14, textView27;

    ImageView skillsImageView;
    ImageView favorsImageView;
    ImageView itemsImageView;
    EditText listingTitle;
    EditText listvalue;
    EditText listingdesc;
    EditText inexchange;

    Drawable defaultBgET;

    TextView unarchiveButton;

    String listingId = null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_archive_details);

        storage = FirebaseStorage.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        fireBUserID = user.getUid();

        // ImageViews for skills, favors, and items
        skillsImageView = findViewById(R.id.skills);
        favorsImageView = findViewById(R.id.favors);
        itemsImageView = findViewById(R.id.items);

        textView23 = findViewById(R.id.textView23);
        textView24 = findViewById(R.id.textView24);
        textView25 = findViewById(R.id.textView25);

        listingTitle = findViewById(R.id.searchBar);
        listvalue = findViewById(R.id.listvalue);
        listingdesc = findViewById(R.id.listingdesc);
        inexchange = findViewById(R.id.inexchange);

        defaultBgET = listingTitle.getBackground();

        // ImageView to display the selected image from the gallery
        selectedImageView = findViewById(R.id.selectedImageView);

        progressBar = findViewById(R.id.progressBar);

        textView14 = findViewById(R.id.textView14);

        unarchiveButton = findViewById(R.id.unarch);

        listingId = getIntent().getStringExtra("listingId");
        listingType = getIntent().getStringExtra("listingType");
        textView14.setText("Archived " +listingType);

        // OnClickListener to view the image in full screen
        selectedImageView.setOnClickListener(v -> {
            imgFullScreen();
        });

        unarchiveButton.setOnClickListener(v -> {
            UnarchiveListing();
        });

        loadListingData();

        // Set padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Handle the result of the gallery selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            selectedImageView.setImageURI(imageUri); // Display the selected image in pfp ImageView
        }
    }

    // Show Image on full screen
    private void imgFullScreen() {

        Intent fullScreenIntent = new Intent(ArchiveDetails.this, FullScreenImageActivity.class);
        if (imageUri != null) {
            // Pass imageUri for newly selected images
            fullScreenIntent.putExtra("imageUri", imageUri.toString());
        } else if (imageUrl != null) {
            // Pass imageUrl for images loaded from Firestore
            fullScreenIntent.putExtra("imageUrl", imageUrl);
        }
        startActivity(fullScreenIntent);
    }

    private void handleImageClick(ImageView imageView, int selectedImageResId, int defaultImageResId) {
        // If there is an already selected ImageView, reset it to its default image
        if (currentlySelectedImageView != null && currentlySelectedImageView != imageView) {
            currentlySelectedImageView.setImageResource(getDefaultImageResource(currentlySelectedImageView));
        }

        // Set the clicked ImageView to the selected image
        imageView.setImageResource(selectedImageResId);
        currentlySelectedImageView = imageView;

        if (imageView == skillsImageView) {
            listingCategory = "Skill";
            textView23.setTextColor(getResources().getColor(R.color.lightgreen));
            textView24.setTextColor(getResources().getColor(R.color.black));
            textView25.setTextColor(getResources().getColor(R.color.black));
        } else if (imageView == favorsImageView) {
            listingCategory = "Favor";
            textView23.setTextColor(getResources().getColor(R.color.black));
            textView24.setTextColor(getResources().getColor(R.color.lightgreen));
            textView25.setTextColor(getResources().getColor(R.color.black));
        } else if (imageView == itemsImageView) {
            listingCategory = "Item";
            textView23.setTextColor(getResources().getColor(R.color.black));
            textView24.setTextColor(getResources().getColor(R.color.black));
            textView25.setTextColor(getResources().getColor(R.color.lightgreen));
        }
    }

    private int getDefaultImageResource(ImageView imageView) {
        if (imageView.getId() == R.id.skills) {
            return R.drawable.skill_add;
        } else if (imageView.getId() == R.id.favors) {
            return R.drawable.favor_add;
        } else if (imageView.getId() == R.id.items) {
            return R.drawable.item_add;
        }
        return 0; // Default or error case
    }

    private void fetchTitleAndCategoryById(String listingId) {
        // Set listingId to empty string if it is null
        if (listingId == null) {
            listingId = ""; // Set to empty string
        }

        // Check if the listingId is still empty before proceeding
        if (listingId.isEmpty()) {
            inexchange.setText(""); // Optionally clear the EditText if no ID is available
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
                        inexchange.setText(titleAndType);
                    } else {
                        // Case 01A: If the document doesn't exist, you might want to handle this case
                        inexchange.setText(""); // Clear the EditText if the listing is not found
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the error gracefully
                    Toast.makeText(this, "Error fetching listing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    inexchange.setText(""); // Clear the EditText in case of an error
                });
    }

    private void loadListingData() {
        if (listingId == null || listingId.isEmpty()) {
            return; // No listingId to load
        }

        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch the listing document from Firestore
        db.collection("Listings").document(listingId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Populate fields with the fetched data
                        title = documentSnapshot.getString("title");
                        listingCategory = documentSnapshot.getString("listingCategory");
                        listingDescription = documentSnapshot.getString("listingDescription");
                        listingValue = documentSnapshot.getString("listingValue");
                        selectedListingId = documentSnapshot.getString("inExchange");
                        fetchTitleAndCategoryById(selectedListingId);

                        // Populate UI fields
                        listingTitle.setText(title);
                        listingdesc.setText(listingDescription);

                        // Format listingValue as currency
                        if (listingValue != null && !listingValue.isEmpty()) {
                            double parsedValue = Double.parseDouble(listingValue.replaceAll("[^\\d.]", ""));
                            String formattedValue = NumberFormat.getCurrencyInstance(Locale.getDefault()).format(parsedValue);
                            listvalue.setText(formattedValue);
                        } else {
                            String noVal = "0";
                            double parsedValue = Double.parseDouble(noVal); // No need to replace anything here
                            String formattedValue = NumberFormat.getCurrencyInstance(Locale.getDefault()).format(parsedValue);
                            listvalue.setText(formattedValue);
                        }

                        // Set the selected category
                        switch (listingCategory) {
                            case "Skill":
                                handleImageClick(skillsImageView, R.drawable.skillsel_add, R.drawable.skill_add);
                                break;
                            case "Favor":
                                handleImageClick(favorsImageView, R.drawable.favorsel_add, R.drawable.favor_add);
                                break;
                            case "Item":
                                handleImageClick(itemsImageView, R.drawable.itemsel_add, R.drawable.item_add);
                                break;
                        }

                        // Load listing image if available
                        imageUrl = documentSnapshot.getString("listingImage");
                        if (imageUrl != null) {
                            // Load image with your preferred image loading library (e.g., Glide)
                            Glide.with(this).load(imageUrl).into(selectedImageView);
                        }
                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Listing not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load listing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void UnarchiveListing() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Get a reference to the Firestore instance and the specific document
        db.collection("Listings").document(listingId)
                .update("storedIn", "Active")
                .addOnSuccessListener(aVoid -> {
                    CustomToast.show(ArchiveDetails.this, "Listing unarchived successfully.");
                    finish();
                })
                .addOnFailureListener(e ->
                        CustomToast.show(ArchiveDetails.this, "Failed to unarchive listing: " + e.getMessage())
                );
    }


}
