package com.egls.transactia;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Request extends AppCompatActivity {

    String fireBUserID;
    boolean newRequest;

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

    ProgressBar progressBar;

    TextView errorTv;

    String timestampStr;

    String refListingType;

    EditText inexchange, listvalue, requestNote;

    RadioButton paymebt, nopaymentbt, paytraderbt;
    RadioGroup paymentOption;

    Drawable defaultBgET;

    // Holders
    String selectedTitleAndType;
    String selectedListingId;

    String transactionTitle = "Unnamed";
    String senderID, receiverId;
    String senderListing, receiverListing;
    String status = "Request";
    String paymentTransaction = "0";
    String paymentMode;
    String senderNote = "";

    String selectedTransactionId;
    String transactionid;

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

        FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        progressBar = findViewById(R.id.progressBar);

        errorTv = findViewById(R.id.errorTv);

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

        defaultBgET = listvalue.getBackground();

        // Find the TextViews and ConstraintLayout
        requestBt = findViewById(R.id.cancelreqbt);  // Request button (TextView)
        confirmationBt = findViewById(R.id.confirmationbt);  // Confirmation layout
        backBt = findViewById(R.id.backbt);  // TextView for "back" button
        conBt = findViewById(R.id.conbt);  // TextView for "confirm" button

        // Retrieve the intent
        Intent intent = getIntent();

        newRequest = intent.getBooleanExtra("newRequest", true);

            // Get user details
            ownerUserId = intent.getStringExtra("ownerUserId");
            listingId = intent.getStringExtra("listingId");

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
                        handleMissingInfo();
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

                paymentMode = selectedText;
            }
        });


        // Initially hide the confirmation layout
        confirmationBt.setVisibility(View.GONE);  // Make confirmation layout invisible at first

        // Set up click listener for the request button (TextView)
        requestBt.setOnClickListener(v -> {

            boolean missinginfo = handleMissingInfo();

            if(!missinginfo) {
                toggleConfirmationLayoutVisibility(confirmationBt);
            }
        });

        // Show toast and hide confirmation layout for cancellation when backBt is clicked
        backBt.setOnClickListener(v -> {
            CustomToast.show(this, "Request is cancelled.");
            confirmationBt.setVisibility(View.GONE);  // Hide confirmation layout
        });

        if(!newRequest) {
            transactionid = intent.getStringExtra("transactionid");
            selectedTransactionId = transactionid;
            requestBt.setText("SAVE UPDATE");
        }

        // Show toast and hide confirmation layout for successful request when conBt is clicked
        conBt.setOnClickListener(v -> {

            confirmationBt.setVisibility(View.GONE);  // Hide confirmation layout
            // Create an EditText for user input
            EditText input = new EditText(this);
            input.setHint("Enter transaction name");

            // Set layout parameters for centering horizontally without taking full width
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, // Width wraps content
                    LinearLayout.LayoutParams.WRAP_CONTENT  // Height wraps content
            );
            params.gravity = Gravity.CENTER_HORIZONTAL; // Center horizontally
            params.setMargins(30, 20, 30, 20); // Optional: Add some margins
            input.setLayoutParams(params);

            // Use a container to manage layout more easily
            LinearLayout container = new LinearLayout(this);
            container.setOrientation(LinearLayout.VERTICAL);
            container.addView(input);


            if(newRequest) {
                // Build the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialogTheme));
                builder.setTitle("Transaction Name")
                        .setMessage("Please enter the transaction name:")
                        .setView(container) // Add the container to the dialog
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Get the input text
                            transactionTitle = input.getText().toString().trim();

                            // Validate the input
                            if (!transactionTitle.isEmpty()) {
                                // CREATE THE TRANSACTION REQUEST
                                createTransactionRequest();
                            } else {
                                builder.setMessage("Transaction name is required.");

                            }
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss(); // Close the dialog
                        });

                // Show the dialog
                builder.show();
            } else {
                updateTransactionDetails();
            }

        });

        fetchListingDetails(db, listingId,true);
        fetchUserDetails(db, ownerUserId);

        if(!newRequest) {
            loadTransactionDetails(transactionid);
        }
    }

    private void fetchListingDetails(FirebaseFirestore db, String listingId, boolean isReceiverListing) {

        progressBar.setVisibility(View.VISIBLE);
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

                            listingImageUrl = listingImage;
                            listingTitleStr = listingTitle;
                            listingTypeStr = listingType;
                            listingDescriptionStr = listingDescription;
                            listingCategoryStr = listingCategory;
                            listingInExchangeStr = inExchange;


                            refListingType = listingTypeStr;

                            if(timestamp!=null) {
                                long timestampMillis = timestamp.getSeconds() * 1000; // Firestore Timestamp to milliseconds
                                Date date = new Date(timestampMillis);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                                String formattedDate = dateFormat.format(date);
                                timestampStr = formattedDate;
                            }

                            listingValueStr = priceValue;
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
                        ownerImageUrl = profileImage;
                        ownerNameStr = name;
                        ownerLocationStr = location;
                        setValues();
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> Log.e("UserDetails", "Error fetching user details: " + e.getMessage()));
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


                            if(paymentMode.equals("No payment")) {
                                paymentOption.check(nopaymentbt.getId());
                            } else if(paymentMode.equals("Pay me")) {
                                paymentOption.check(paymebt.getId());
                            } else if(paymentMode.equals("Pay trader")) {
                                paymentOption.check(paytraderbt.getId());
                            }

                        if(senderListing!=null) {
                            selectedListingId = senderListing;
                            setListingDetails(senderListing, senderID, receiverID);
                        }

                        listvalue.setText(paymentTransaction);
                        requestNote.setText(senderNote);
                    } else {
                        Log.e("TransactionDetails", "No transaction found with transactionId: " + transactionId);
                    }
                })
                .addOnFailureListener(e -> Log.e("TransactionDetails", "Error fetching transaction: " + e.getMessage()));
    }

    private void setListingDetails(String listingId, String currentUserId, String traderId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch the listing by its ID
        db.collection("Listings")
                .document(listingId) // Fetch document with the specific listing ID
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve details from the document
                        String title = documentSnapshot.getString("title");
                        String category = documentSnapshot.getString("listingCategory");
                        String userId = documentSnapshot.getString("userId");

                        if (title != null && category != null && userId != null) {
                            // Determine ownership and format the title and type
                            String titleAndType;
                            if (userId.equals(currentUserId)) {
                                titleAndType = "You:         " + title + " - " + category;
                            } else if (userId.equals(traderId)) {
                                titleAndType = "Trader:     " + title + " - " + category;
                            } else {
                                titleAndType = title + " - " + category;
                            }

                            // Set the title and type into the EditText
                            inexchange.setText(titleAndType); // Replace with your title EditText
                        } else {
                            CustomToast.show(this, "Listing details are incomplete.");
                        }
                    } else {
                        CustomToast.show(this, "Listing not found.");
                    }
                })
                .addOnFailureListener(e -> {
                    CustomToast.show(this, "Error fetching listing: " + e.getMessage());
                });
    }



    // Separate methods to handle each field's action
    private boolean handleMissingInfo() {

        if (inexchange.getText().toString().trim().isEmpty() && listvalue.getText().toString().trim().isEmpty() && requestNote.getText().toString().trim().isEmpty()) {
            // Set red border when validation fails
            inexchange.setBackgroundResource(R.drawable.edittext_red_border);
            listvalue.setBackgroundResource(R.drawable.edittext_red_border);
            requestNote.setBackgroundResource(R.drawable.edittext_red_border);
            errorTv.setVisibility(View.VISIBLE);

            return true;
        } else {
            // Reset to default background after valid input
            inexchange.setBackground(defaultBgET);
            listvalue.setBackground(defaultBgET);
            requestNote.setBackground(defaultBgET);
            errorTv.setVisibility(View.INVISIBLE);
            return false;
        }
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
                String formattedValue = NumberFormat.getCurrencyInstance(new Locale("en", "PH")).format(parsedValue); // Divide by 100 to handle fractional values
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

        db.collection("Listings")
                .whereEqualTo("listingType", refListingType)
                .whereEqualTo("storedIn", "Active")
                .whereIn("userId", Arrays.asList(currentUserId, traderId)) // Fetch listings for both users
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> titleAndTypeList = new ArrayList<>();
                    List<String> listingIdList = new ArrayList<>();

                    // Add "Clear Selection" option at the beginning of the list
                    titleAndTypeList.add("Clear Selection");
                    listingIdList.add(null); // Placeholder for no selection

                    // Iterate through the listings fetched
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String title = document.getString("title");
                        String category = document.getString("listingCategory");
                        String listingId = document.getId();
                        String userId = document.getString("userId");

                        // Format titleAndType based on ownership
                        String titleAndType;
                        if (userId.equals(currentUserId)) {
                            titleAndType = "You:         " + title + " - " + category;
                        } else if (userId.equals(traderId)) {
                            titleAndType = "Trader:     " + title + " - " + category;
                        } else {
                            titleAndType = title + " - " + category;
                        }

                        // Add the formatted title and listing ID to the lists
                        titleAndTypeList.add(titleAndType);
                        listingIdList.add(listingId);
                    }

                    // Show dialog with options for inExchange
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Select In Exchange")
                            .setItems(titleAndTypeList.toArray(new String[0]), (dialog, which) -> {
                                if (which == 0) { // First item is "Clear Selection"
                                    selectedTitleAndType = null;
                                    selectedListingId = null;

                                    // Clear the inExchange field
                                    inexchange.setText("");
                                    CustomToast.show(this, "Selection cleared.");
                                } else {
                                    // Regular selection
                                    selectedTitleAndType = titleAndTypeList.get(which);
                                    selectedListingId = listingIdList.get(which);

                                    // Assign selected values to inExchange field
                                    inexchange.setText(selectedTitleAndType);
                                    handleMissingInfo(); // Perform validation or further actions
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Request.this, MyRequests.class);
        startActivity(intent);
        finish();
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


    private void createTransactionRequest() {
        progressBar.setVisibility(View.VISIBLE);

        senderID = fireBUserID;
        receiverId = ownerUserId;
        senderListing = selectedListingId;
        receiverListing = listingId;
        status = "Request";
        paymentTransaction = listvalue.getText().toString().replaceAll("[^\\d.]", "");
        senderNote = requestNote.getText().toString();

        TransactionManager transactionManager = new TransactionManager();
        transactionManager.createTransaction(
                transactionTitle,
                senderID,
                receiverId,
                senderListing,
                receiverListing,
                status,
                paymentTransaction,
                paymentMode,
                senderNote,
                new TransactionManager.OnTransactionCompleteListener() {
                    @Override
                    public void onTransactionSuccess() {
                        progressBar.setVisibility(View.GONE);
                        CustomToast.show(Request.this, "Transaction created successfully!");

                        Intent intent = new Intent(Request.this, MyRequests.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onTransactionFailure(String errorMessage) {
                        progressBar.setVisibility(View.GONE);
                        CustomToast.show(Request.this, "Error: " + errorMessage);
                    }
                }
        );
    }


    private void updateTransactionDetails() {
        progressBar.setVisibility(View.VISIBLE);

        String transactionId = transactionid; // The transactionId field within the document
        String updatedSenderListing = selectedListingId;
        String updatedPaymentTransaction = listvalue.getText().toString().replaceAll("[^\\d.]", "");
        String updatedPaymentMode = paymentMode;
        String updatedSenderNote = requestNote.getText().toString();

        TransactionManager transactionManager = new TransactionManager();
        transactionManager.updateTransaction(
                transactionId,
                updatedSenderListing,
                updatedPaymentTransaction,
                updatedPaymentMode,
                updatedSenderNote, // Pass the updated senderNote
                new TransactionManager.OnTransactionUpdateListener() {
                    @Override
                    public void onTransactionUpdateSuccess() {
                        progressBar.setVisibility(View.GONE);
                        CustomToast.show(Request.this, "Transaction updated successfully!");

                        // Delay the intent by 2 seconds (2000 milliseconds)
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            Intent intent = new Intent(Request.this, ManageRequest.class);
                            intent.putExtra("transactionid", transactionid); // Pass the correct transactionId
                            startActivity(intent);
                            finish();
                        }, 2000); // 2000 milliseconds = 2 seconds

                    }

                    @Override
                    public void onTransactionUpdateFailure(String errorMessage) {
                        progressBar.setVisibility(View.GONE);
                        CustomToast.show(Request.this, "Error: " + errorMessage);
                    }
                }
        );
    }





}

