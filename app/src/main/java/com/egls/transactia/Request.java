package com.egls.transactia;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Request extends AppCompatActivity {

    String fireBUserID;

    TextView requestBt, conBt, backBt;
    ConstraintLayout confirmationBt;

    TextView ownerName, location;
    TextView listingTitle, listingDesc, timestamp, listingType, listingCateg, inExchangeOwner, valueOwner;
    ImageView ownerImage, listingImage;


    String ownerUserId, ownerImageUrl, ownerNameStr, ownerLocationStr;
    String listingId,
    listingTitleStr, listingTypeStr,
    listingDescriptionStr,
    listingCategoryStr ,
    listingValueStr,
    listingInExchangeStr,
    listingImageUrl;

    String timestampStr;

    String counterListingType;

    EditText inexchange, listvalue, requestNote;

    RadioButton paymebt, nopaymentbt, paytraderbt;
    RadioGroup paymentOption;

    // Holders
    String selectedTitleAndType;
    String selectedListingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Enables Edge-to-Edge support for system bars
        setContentView(R.layout.activity_request);  // Inflate the layout

        // Set up window insets to adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());  // Get insets for system bars (status bar, navigation bar)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);  // Apply padding to avoid overlap
            return insets;
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        fireBUserID = user.getUid();

        ownerImage = findViewById(R.id.imageView2);
        listingImage = findViewById(R.id.imageView);

        ownerName = findViewById(R.id.tradername);
        location = findViewById(R.id.userlocation);
        listingTitle = findViewById(R.id.listingTitle);
        listingDesc = findViewById(R.id.listingDescription);
        timestamp = findViewById(R.id.timestamp);
        listingType = findViewById(R.id.listingtype);
        listingCateg = findViewById(R.id.listingCateg);
        inExchangeOwner = findViewById(R.id.Exchangefor);
        valueOwner = findViewById(R.id.Pricevalue);

        inexchange = findViewById(R.id.inexchange);
        listvalue = findViewById(R.id.listvalue);
        requestNote = findViewById(R.id.requestNote);

        paymentOption = findViewById(R.id.paymentOption);
        paymebt = findViewById(R.id.paymebt);
        nopaymentbt = findViewById(R.id.nopaymentbt);
        paytraderbt = findViewById(R.id.paytraderbt);

        nopaymentbt.setChecked(true);
        nopaymentbt.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#33443C")));
        listvalue.setText("");

        // Retrieve the intent
        Intent intent = getIntent();

        // Get user details
        ownerUserId = intent.getStringExtra("ownerUserId");
        ownerImageUrl = intent.getStringExtra("ownerImage");
        ownerNameStr = intent.getStringExtra("ownerName");
        ownerLocationStr = intent.getStringExtra("ownerLocation");

        // Get listing details
        listingId = intent.getStringExtra("listingId");
        listingTitleStr = intent.getStringExtra("listingTitle");
        listingTypeStr = intent.getStringExtra("listingType");
        listingDescriptionStr = intent.getStringExtra("listingDescription");
        listingCategoryStr = intent.getStringExtra("listingCategory");
        listingValueStr = intent.getStringExtra("listingValue");
        listingInExchangeStr = intent.getStringExtra("listingInExchange");
        listingImageUrl = intent.getStringExtra("listingImage");

        // Get timestamp
        timestampStr = intent.getStringExtra("listingTimestamp");

        if(listingTypeStr.equals("Need")) {
            counterListingType = "Offer";
        } else if(listingTypeStr.equals("Offer")) {
            counterListingType = "Need";
        }


        setValues();

        // Find the TextViews and ConstraintLayout
         requestBt = findViewById(R.id.requestbt);  // Request button (TextView)
         confirmationBt = findViewById(R.id.confirmationbt);  // Confirmation layout
         backBt = findViewById(R.id.backbt);  // TextView for "back" button
         conBt = findViewById(R.id.conbt);  // TextView for "confirm" button


        // AFTER DECLARATIONS

        inexchange.setOnClickListener(v -> {
            fetchInExchangeOptions(fireBUserID, ownerUserId);
        });
        inexchange.setOnFocusChangeListener((v, hasFocus) -> {
            fetchInExchangeOptions(fireBUserID, ownerUserId);
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


        // Set the OnCheckedChangeListener to detect changes
        paymentOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Get the selected RadioButton by its ID
                RadioButton selectedRadioButton = findViewById(checkedId);
                String selectedText = selectedRadioButton.getText().toString();

                selectedRadioButton.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#33443C")));

                if(selectedText.equals("No payment")) {
                    listvalue.setText("");
                    listvalue.setEnabled(false);
                } else if(selectedText.equals("Pay me")) {
                    listvalue.setEnabled(true);
                } else if(selectedText.equals("Pay trader")) {
                    listvalue.setEnabled(true);
                }
            }
        });



        // Initially hide the confirmation layout
        confirmationBt.setVisibility(View.GONE);  // Make confirmation layout invisible at first

        // Set up click listener for the request button (TextView)
        requestBt.setOnClickListener(v -> toggleConfirmationLayoutVisibility(confirmationBt));

        // Show toast and hide confirmation layout for cancellation when backBt is clicked
        backBt.setOnClickListener(v -> {
            Toast.makeText(Request.this, "Request is cancelled", Toast.LENGTH_SHORT).show();
            confirmationBt.setVisibility(View.GONE);  // Hide confirmation layout
        });

        // Show toast and hide confirmation layout for successful request when conBt is clicked
        conBt.setOnClickListener(v -> {
            Toast.makeText(Request.this, "Request sent successfully", Toast.LENGTH_SHORT).show();
            confirmationBt.setVisibility(View.GONE);  // Hide confirmation layout
        });
    }


    private void setValues() {

        // Set the images
        if (ownerImageUrl != null && !ownerImageUrl.isEmpty()) {
            Glide.with(Request.this)
                    .load(ownerImageUrl) // This should be the URL string from Firebase Storage
                    .into(ownerImage);
        } else {
            ownerImage.setImageResource(R.drawable.pfpdef);
        }

        if (listingImageUrl != null && !listingImageUrl.isEmpty()) {
            Glide.with(Request.this)
                    .load(listingImageUrl) // This should be the URL string from Firebase Storage
                    .into(listingImage);
        } else {
            listingImage.setImageResource(R.drawable.gallery_message);
        }

        ownerName.setText(ownerNameStr);
        location.setText(ownerLocationStr);
        listingType.setText(listingTypeStr);
        listingTitle.setText(listingTitleStr);
        fetchTitleAndCategoryById(listingInExchangeStr);
        listingDesc.setText(listingDescriptionStr);
        listingCateg.setText(listingCategoryStr);
        timestamp.setText(timestampStr);

        // Format listingValue as currency
        if (listingValueStr != null && !listingValueStr.isEmpty()) {
            try {
                double parsedValue = Double.parseDouble(listingValueStr.replaceAll("[^\\d.]", "")); // Remove non-numeric characters
                String formattedValue = NumberFormat.getCurrencyInstance(new Locale("en", "PH")).format(parsedValue / 100); // Divide by 100 to handle fractional values
                valueOwner.setText(formattedValue);
            } catch (NumberFormatException e) {
                // Handle invalid number format (e.g., empty or non-numeric input)
                e.printStackTrace();
            }
        } else {
            // If listingValue is empty, display "₱0.00"
            double parsedValue = 0;
            String formattedValue = NumberFormat.getCurrencyInstance(new Locale("en", "PH")).format(parsedValue); // Format as PHP
            valueOwner.setText(formattedValue);
        }

        loadMoreFragment();
    }

    private void fetchInExchangeOptions(String currentUserId, String traderId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Use .whereIn() to match either currentUserId or traderId in userId
        db.collection("Listings")
                .whereEqualTo("listingType", counterListingType)
                .whereEqualTo("storedIn", "Active")
                .whereIn("userId", Arrays.asList(currentUserId, traderId)) // Change to whereIn for multiple IDs
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> titleAndTypeList = new ArrayList<>();
                    List<String> listingIdList = new ArrayList<>();

                    // Iterate through the listings fetched
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String title = document.getString("title");
                        String category = document.getString("listingCategory");
                        String listingId = document.getId();
                        String userId = document.getString("userId"); // Get the userId of the listing

                        // Build the titleAndType based on whether it's the current user's listing or the other trader's listing
                        String titleAndType;
                        if (userId.equals(currentUserId)) {
                            titleAndType = "You:         " +title + " - " + category;  // Add "- You" for current user's listing
                        } else if (userId.equals(traderId)) {
                            titleAndType = "Trader:     " +title + " - " + category;  // Add "- Trader" for the other trader's listing
                        } else {
                            titleAndType = title + " - " + category; // Default case (shouldn't occur if userId is currentUserId or traderId)
                        }

                        // Add the formatted title and listing ID to the lists
                        titleAndTypeList.add(titleAndType);
                        listingIdList.add(listingId);
                    }

                    // Show dialog with options for inExchange
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Select In Exchange")
                            .setItems(titleAndTypeList.toArray(new String[0]), (dialog, which) -> {
                                selectedTitleAndType = titleAndTypeList.get(which);
                                selectedListingId = listingIdList.get(which);

                                // Assign selected values to inExchange field
                                inexchange.setText(selectedTitleAndType);
                            })
                            .show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching listings: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



    private void fetchTitleAndCategoryById(String listingId) {
        // Set listingId to empty string if it is null
        if (listingId == null) {
            listingId = ""; // Set to empty string
        }

        // Check if the listingId is still empty before proceeding
        if (listingId.isEmpty()) {
            inExchangeOwner.setText(""); // Optionally clear the EditText if no ID is available
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
                        inExchangeOwner.setText(titleAndType);
                    } else {
                        // Case 01A: If the document doesn't exist, you might want to handle this case
                        inExchangeOwner.setText(""); // Clear the EditText if the listing is not found
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the error gracefully
                    Toast.makeText(this, "Error fetching listing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    inExchangeOwner.setText(""); // Clear the EditText in case of an error
                });
    }


    // Method to toggle the visibility of the confirmation layout
    private void toggleConfirmationLayoutVisibility(ConstraintLayout confirmationBt) {
        if (confirmationBt.getVisibility() == View.GONE) {
            confirmationBt.setVisibility(View.VISIBLE);  // Show confirmation layout
            confirmationBt.bringToFront();  // Bring confirmation layout to the front
        } else {
            confirmationBt.setVisibility(View.GONE);  // Hide confirmation layout
        }
    }

    private void loadMoreFragment() {
        SearchFragment fragment = new SearchFragment();
        fragment.isFromRequest = true;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, fragment, "MY_FRAGMENT_TAG")
                .commit();
    }
}
