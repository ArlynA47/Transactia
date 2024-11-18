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

public class MyNeeds extends AppCompatActivity {

    FirebaseUser user;

    private ImageView currentlySelectedImageView = null;
    private View hiddenLayout; // Reference to the hidden layout

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private FirebaseStorage storage;

    String fireBUserID;

    TextView errorTv;

    private ImageView ShowGal;
    private ImageView selectedImageView;

    ProgressBar progressBar;

    private String imageUrl;

    // Listing Detail holders
    String title;
    String listingType = "Need";
    String counterListingType = "Offer";
    String listingCategory;
    String listingDescription;
    String listingValue;
    String selectedTitleAndType;
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

    TextView changeTextView;
    TextView saveTextView;

    boolean newListing;
    boolean isNeed;
    String listingId = null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_needs);

        user = FirebaseAuth.getInstance().getCurrentUser();

        storage = FirebaseStorage.getInstance();

        fireBUserID = user.getUid();
        boolean fromAdapter = getIntent().getBooleanExtra("fromAdapter", false);
        // ImageViews for skills, favors, and items
        skillsImageView = findViewById(R.id.skills);
        favorsImageView = findViewById(R.id.favors);
        itemsImageView = findViewById(R.id.items);

        textView23 = findViewById(R.id.textView23);
        textView24 = findViewById(R.id.textView24);
        textView25 = findViewById(R.id.textView25);

        errorTv = findViewById(R.id.errorTv);

        listingTitle = findViewById(R.id.searchBar);
        listvalue = findViewById(R.id.listvalue);
        listingdesc = findViewById(R.id.requestNote);
        inexchange = findViewById(R.id.inexchange);

        defaultBgET = listingTitle.getBackground();

        ShowGal = findViewById(R.id.ShowGal);

        // ImageView to display the selected image from the gallery
        selectedImageView = findViewById(R.id.selectedImageView);

        progressBar = findViewById(R.id.progressBar);
        TextView changeTextView = findViewById(R.id.change);
        // Initialize hidden layout and ShowGal ImageView
        hiddenLayout = findViewById(R.id.savepop);

        textView27 = findViewById(R.id.textView27);
        textView14 = findViewById(R.id.textView14);

        saveTextView = findViewById(R.id.savetx);

        newListing = getIntent().getBooleanExtra("newListing", false);

        if(newListing) {
            isNeed = getIntent().getBooleanExtra("isNeed", true);
            if(isNeed) {
                inNeedPage();
            } else {
                inOfferPage();
            }
        } else {
            listingId = getIntent().getStringExtra("listingId");
            textView14.setText("");
            textView27.setText("");
            loadListingData();
        }

        // ONLCICK LISTENERS
        if (fromAdapter) {
            changeTextView.setVisibility(View.GONE);
        }
        // OnClickListener to view the image in full screen
        selectedImageView.setOnClickListener(v -> {
            imgFullScreen();
        });

        listingdesc.setOnClickListener(v -> handleDescClick());
        listingdesc.setOnFocusChangeListener((v, hasFocus) -> {
            handleDescClick();
        });

        ShowGal.setOnClickListener(v -> {
            boolean allowShow = handleNotRequiredFieldClick();
            if(allowShow) {
                openGallery();
            }
        });
        ShowGal.setOnFocusChangeListener((v, hasFocus) -> {
            boolean allowShow = handleNotRequiredFieldClick();
            if(allowShow) {
                openGallery();
            }
        });

        listvalue.setOnClickListener(v -> handleNotRequiredFieldClick());
        listvalue.setOnFocusChangeListener((v, hasFocus) -> {
            handleNotRequiredFieldClick();
        });
        listvalue.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Remove the formatting when the user is typing
                if (!s.toString().equals(current)) {
                    listvalue.removeTextChangedListener(this); // Prevent infinite loop
                    try {
                        String cleanString = s.toString().replaceAll("[^\\d]", ""); // Remove all non-numeric characters
                        double parsed = Double.parseDouble(cleanString);
                        String formatted = NumberFormat.getCurrencyInstance(new Locale("en", "PH")).format(parsed / 100);
                        current = formatted;

                        listvalue.setText(formatted);
                        listvalue.setSelection(formatted.length()); // Set cursor to the end
                    } catch (NumberFormatException e) {
                        // Handle number parsing error
                        e.printStackTrace();
                    }
                    listvalue.addTextChangedListener(this); // Re-attach listener
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        inexchange.setOnClickListener(v -> {
            boolean allowShow = handleNotRequiredFieldClick();
            if(allowShow) {
                fetchInExchangeOptions(fireBUserID);
            }
        });
        inexchange.setOnFocusChangeListener((v, hasFocus) -> {
            boolean allowShow = handleNotRequiredFieldClick();
            if(allowShow) {
                fetchInExchangeOptions(fireBUserID);
            }
        });

        // Save
        changeTextView.setOnClickListener(v -> {
                showDropdownMenu(v);
        });

        // Find the TextView for saving and set OnClickListener
        saveTextView.setOnClickListener(v -> {
            boolean allowShow = handleNotRequiredFieldClick();
            if(allowShow) {
                showHiddenLayout();
            }
        });

        // select "Item" category by default
        handleImageClick(itemsImageView, R.drawable.itemsel_add, R.drawable.item_add);

        // Set OnClickListener for each ImageView
        skillsImageView.setOnClickListener(v -> handleImageClick(skillsImageView, R.drawable.skillsel_add, R.drawable.skill_add));
        favorsImageView.setOnClickListener(v -> handleImageClick(favorsImageView, R.drawable.favorsel_add, R.drawable.favor_add));
        itemsImageView.setOnClickListener(v -> handleImageClick(itemsImageView, R.drawable.itemsel_add, R.drawable.item_add));

        // Set padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    // Separate methods to handle each field's action
    private void handleDescClick() {

        if (listingTitle.getText().toString().trim().isEmpty()) {
            // Set red border when validation fails
            listingTitle.setBackgroundResource(R.drawable.edittext_red_border);
            listingTitle.requestFocus();
            listingdesc.clearFocus();
            errorTv.setVisibility(View.VISIBLE);
        } else {
            // Reset to default background after valid input
            listingTitle.setBackground(defaultBgET);
            errorTv.setVisibility(View.INVISIBLE);
        }
    }

    private boolean handleNotRequiredFieldClick() {
        if (listingdesc.getText().toString().trim().isEmpty()) {
            // Set red border when validation fails
            listingdesc.setBackgroundResource(R.drawable.edittext_red_border);
            listingdesc.requestFocus();
            errorTv.setVisibility(View.VISIBLE);
            return false;
        } else {
            listingdesc.setBackground(defaultBgET);
            errorTv.setVisibility(View.INVISIBLE);
            return true;
        }
    }

    // Function to open gallery
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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

        Intent fullScreenIntent = new Intent(MyNeeds.this, FullScreenImageActivity.class);
        if (imageUri != null) {
            // Pass imageUri for newly selected images
            fullScreenIntent.putExtra("imageUri", imageUri.toString());
        } else if (imageUrl != null) {
            // Pass imageUrl for images loaded from Firestore
            fullScreenIntent.putExtra("imageUrl", imageUrl);
        }
        startActivity(fullScreenIntent);
    }

    private void showHiddenLayout() {
        // Show the hidden layout
        hiddenLayout.setVisibility(View.VISIBLE);
        hiddenLayout.bringToFront();

        // Change the background of the main layout to gray
        findViewById(R.id.main).setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));

        // Request a layout update
        hiddenLayout.requestLayout();

        // Set up button listeners for save and cancel
        Button saveButton = hiddenLayout.findViewById(R.id.savebt);
        Button cancelButton = hiddenLayout.findViewById(R.id.cancelbt);

        // Decrease the opacity of each ImageView
        ImageView[] imageViews = {
                findViewById(R.id.favors),
                findViewById(R.id.skills),
                findViewById(R.id.items),
                findViewById(R.id.selectedImageView),
                findViewById(R.id.ShowGal)// Add other ImageViews as needed
        };
        TextView[] textViews = {
                findViewById(R.id.textView23),
                findViewById(R.id.textView24),
                findViewById(R.id.textView25),
                findViewById(R.id.savetx),
                findViewById(R.id.change),
                findViewById(R.id.textView14),
                findViewById(R.id.textView22),
                findViewById(R.id.textView26),
                findViewById(R.id.textView27)
        };
        EditText[] editTexts = {
                findViewById(R.id.inexchange),
                findViewById(R.id.listvalue),
                findViewById(R.id.searchBar),
                findViewById(R.id.requestNote)
        };

        for (ImageView imageView : imageViews) {
            if (imageView != null) {
                imageView.setAlpha(0.2f); // Set to 50% opacity (0.0 to 1.0)
            }
        }
        for (TextView textView : textViews) {
            if (textView != null) {
                textView.setAlpha(0.4f); // Set to 50% opacity (0.0 to 1.0)
            }
        }
        for (EditText editText : editTexts) {
            if (editText != null) {
                editText.setAlpha(0.4f); // 40% opacity
            }
        }

        // Save button click listener
        saveButton.setOnClickListener(v -> {
            hiddenLayout.setVisibility(View.GONE);
            findViewById(R.id.main).setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent)); // Reset background

            // Reset opacity of each ImageView after hiding the layout
            for (ImageView imageView : imageViews) {
                if (imageView != null) {
                    imageView.setAlpha(1.0f); // Reset to 100% opacity
                }
            }
            for (TextView textView : textViews) {
                if (textView != null) {
                    textView.setAlpha(1.0f); // Reset to 100% opacity
                }
            }


    // Reset opacity of each EditText
    for (EditText editText : editTexts) {
        if (editText != null) {
            editText.setAlpha(1.0f); // Reset to 100% opacity
        }
    }
            createListing(fireBUserID);
        });

        // Cancel button click listener
        cancelButton.setOnClickListener(v -> {
            hiddenLayout.setVisibility(View.GONE);
            findViewById(R.id.main).setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent)); // Reset background

            // Reset opacity of each ImageView on cancel
            for (ImageView imageView : imageViews) {
                if (imageView != null) {
                    imageView.setAlpha(1.0f); // Reset to 100% opacity
                }
            }
            for (TextView textView : textViews) {
                if (textView != null) {
                    textView.setAlpha(1.0f); // Reset to 100% opacity
                }
            }


            // Reset opacity of each EditText
            for (EditText editText : editTexts) {
                if (editText != null) {
                    editText.setAlpha(1.0f); // Reset to 100% opacity
                }
            }
        });
    }


    private void showDropdownMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onMenuItemClick);
        popupMenu.show();
    }

    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.myneeds) {
            inNeedPage();
            return true;
        } else if (item.getItemId() == R.id.myoffers) {
            inOfferPage();
            return true;
        } else {
            return false; // Return false for unhandled menu items
        }
    }

    private void inNeedPage() {
        listingType = "Need";
        counterListingType ="Offer";
        textView14.setText("My Need");
        textView27.setText("Enter your need details.");
    }

    private void inOfferPage() {
        listingType = "Offer";
        counterListingType = "Need";
        textView14.setText("My Offer");
        textView27.setText("Enter your offer details.");
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

    private void fetchInExchangeOptions(String currentUserId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Listings")
                .whereEqualTo("listingType", counterListingType)
                .whereEqualTo("storedIn", "Active")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> titleAndTypeList = new ArrayList<>();
                    List<String> listingIdList = new ArrayList<>();

                    // Add "Clear Selection" option at the beginning
                    titleAndTypeList.add("Clear Selection");
                    listingIdList.add(null); // Placeholder for no selection

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String title = document.getString("title");
                        String category = document.getString("listingCategory");
                        String listingId = document.getId();

                        String titleAndType = title + " - " + category;
                        titleAndTypeList.add(titleAndType);
                        listingIdList.add(listingId);
                    }

                    // Show dialog to select inExchange option
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Select In Exchange")
                            .setItems(titleAndTypeList.toArray(new String[0]), (dialog, which) -> {
                                if (which == 0) { // "Clear Selection" option
                                    selectedTitleAndType = null;
                                    selectedListingId = null;

                                    // Clear the inExchange field
                                    inexchange.setText("");
                                    CustomToast.show(this, "Selection cleared.");
                                } else {
                                    // Regular selection
                                    selectedTitleAndType = titleAndTypeList.get(which);
                                    selectedListingId = listingIdList.get(which);

                                    // Assign selected values to inExchange fields
                                    inexchange.setText(selectedTitleAndType);
                                }
                            })
                            .show();
                })
                .addOnFailureListener(e -> {
                    CustomToast.show(this, "Error fetching listings: " + e.getMessage());
                });
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
                        listingType = documentSnapshot.getString("listingType");
                        listingCategory = documentSnapshot.getString("listingCategory");
                        listingDescription = documentSnapshot.getString("listingDescription");
                        listingValue = documentSnapshot.getString("listingValue");
                        selectedListingId = documentSnapshot.getString("inExchange");
                        fetchTitleAndCategoryById(selectedListingId);

                        if(listingType.equals("Need")) {
                            inNeedPage();
                        } else if(listingType.equals("Offer")) {
                            inOfferPage();
                        }

                        // Populate UI fields
                        listingTitle.setText(title);
                        listingdesc.setText(listingDescription);

                        // Format listingValue as currency
                        if (listingValue != null && !listingValue.isEmpty()) {
                            listvalue.setText(listingValue);
                        } else {
                            // If listingValue is empty, display "₱0.00"
                            double parsedValue = 0;
                            String formattedValue = NumberFormat.getCurrencyInstance(new Locale("en", "PH")).format(parsedValue); // Format as PHP
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

    // Generate keywords for partial matching
    private List<String> generateKeywords(String text) {
        List<String> keywords = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return keywords;
        }

        // Split text into individual words
        String[] words = text.toLowerCase().split(" ");

        // Generate all prefixes for each word
        for (String word : words) {
            StringBuilder prefix = new StringBuilder();
            for (char c : word.toCharArray()) {
                prefix.append(c);
                keywords.add(prefix.toString());
            }
        }

        return keywords;
    }



    // save listing to firebase method
    public void createListing(String fireBUserID) {
        title = listingTitle.getText().toString().trim();
        listingDescription = listingdesc.getText().toString().trim();
        listingValue = listvalue.getText().toString().replaceAll("[^\\d.]", ""); // Remove currency symbols

        if (fireBUserID == null || fireBUserID.isEmpty()) {
            return;
        }

        // Generate keywords
        List<String> keywords = generateKeywords(title);

        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Use existing listingId for updates or generate a new one
        String idToUse = (listingId != null) ? listingId : db.collection("Listings").document().getId();

        // Create a map to hold the fields for the listing
        Map<String, Object> listingData = new HashMap<>();
        listingData.put("title", title);
        listingData.put("listingType", listingType);
        listingData.put("listingCategory", listingCategory);
        listingData.put("listingDescription", listingDescription);
        listingData.put("listingValue", listingValue);
        listingData.put("inExchange", selectedListingId);
        listingData.put("storedIn", "Active");
        listingData.put("userId", fireBUserID);
        listingData.put("createdTimestamp", FieldValue.serverTimestamp()); // Add timestamp field
        listingData.put("keywords", keywords);

        // Check if image is updated
        if (imageUri != null) {
            // Upload image and update Firestore with image URL
            StorageReference storageRef = storage.getReference().child("images/listing/" + idToUse + ".jpg");
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                            .addOnSuccessListener(downloadUri -> {
                                listingData.put("listingImage", downloadUri.toString());
                                updateListingInFirestore(db, idToUse, listingData);
                            })
                            .addOnFailureListener(e -> {
                                CustomToast.show(this, "Error getting image URL: " + e.getMessage());
                                progressBar.setVisibility(View.GONE);
                            }))
                    .addOnFailureListener(e -> {
                        CustomToast.show(this, "Error uploading image: " + e.getMessage());
                        progressBar.setVisibility(View.GONE);
                    });
        } else {
            // Do not include "listingImage" in updates if no new image is provided
            updateListingInFirestore(db, idToUse, listingData);
        }
    }

    private void updateListingInFirestore(FirebaseFirestore db, String idToUse, Map<String, Object> listingData) {
        db.collection("Listings").document(idToUse)
                .update(listingData) // Update existing fields, without overwriting unspecified fields
                .addOnSuccessListener(aVoid -> {
                    CustomToast.show(this, "Listing created successfully!");
                    progressBar.setVisibility(View.GONE);
                    finish();
                })
                .addOnFailureListener(e -> {
                    CustomToast.show(this, "Error saving listing: " + e.getMessage());
                    progressBar.setVisibility(View.GONE);
                });
    }

}
