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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Request extends AppCompatActivity {

    FragmentContainerView fragmentContainerView;
    View reportPromptLayout, reportSuccessLayout, dark_overlay;
    ConstraintLayout reportPrompt, reportSuccess;

    TextView reportcfbt, reportbackbt, violation, description, viewReports, ok;

    String fireBUserID;
    boolean newRequest;

    TextView requestBt, conBt, backBt;
    ConstraintLayout confirmationBt;

    TextView ownerName, location;
    TextView listingTitle, listingDesc, timestamp, listingType, listingCateg, inExchangeOwner, valueOwner;
    ImageView ownerImage, listingImage;


    TextView seeListing;


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

    String senderName;

    boolean isMyListing;
    String ownerStr;

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

        fragmentContainerView = findViewById(R.id.fragmentContainerView);
        // Initialize views
        reportPromptLayout = findViewById(R.id.layout_reportprompttt); // layout where the report prompt is defined
        reportPrompt = findViewById(R.id.reportpromptt); // layout that triggers the report prompt visibility
        reportcfbt = findViewById(R.id.reportcfbt);
        reportbackbt = findViewById(R.id.reportbackbt);
        violation = findViewById(R.id.violation);
        description = findViewById(R.id.description);

        seeListing = findViewById(R.id.seeListing);

        dark_overlay = findViewById(R.id.dark_overlay);

        reportSuccessLayout = findViewById(R.id.layout_reportSuccess);
        ok = findViewById(R.id.ok);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        fireBUserID = user.getUid();

        fetchUserName(fireBUserID);

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


        seeListing.setOnClickListener(v -> {
            if(ownerStr.equals(fireBUserID)) {
                // If the search result is the user's own listing
                Intent intentList = new Intent(this, MyNeeds.class);
                intentList.putExtra("newListing", false);
                intentList.putExtra("listingId", selectedListingId);
                startActivity(intentList);

            } else {
                Intent intentList = new Intent(this, Request.class);

                intentList.putExtra("newRequest", true);

                // Add user details to the intent
                intentList.putExtra("ownerUserId", ownerStr); // Add owner User ID
                intentList.putExtra("listingId", selectedListingId); // Pass the listing ID (document ID)

                // Start the Request activity
                startActivity(intentList);
            }
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

        // Load listing and user details
        fetchListingAndUserDetails(db, listingId, ownerUserId, true);

        // Load transaction details if not a new request
        if (!newRequest) {
            loadTransactionDetails(transactionid);
        }

        // Set click listener for reportPrompt
        reportPrompt.setOnClickListener(v -> {
            // Make report prompt visible and bring it to the front
            pickCategory();
        });
        ok.setOnClickListener(v -> {
            dark_overlay.setVisibility(View.GONE);
            reportSuccessLayout.setVisibility(View.GONE);
            finish();
        });

        ownerImage.setOnClickListener(v -> {
            Intent intentprof = new Intent(this, Traderprofile.class);
            intentprof.putExtra("userId", ownerUserId);
            startActivity(intentprof);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        });

        // ON CREATE END
    }

    private void fetchListingAndUserDetails(FirebaseFirestore db, String listingId, String ownerUserId, boolean isReceiverListing) {
        progressBar.setVisibility(View.VISIBLE);

        // Fetch Listing Details
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

                        if (isReceiverListing) {
                            listingImageUrl = listingImage;
                            listingTitleStr = listingTitle;
                            listingTypeStr = listingType;
                            listingDescriptionStr = listingDescription;
                            listingCategoryStr = listingCategory;
                            listingInExchangeStr = inExchange;


                            if (timestamp != null) {
                                long timestampMillis = timestamp.getSeconds() * 1000;
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                                timestampStr = dateFormat.format(new Date(timestampMillis));
                            }

                            listingValueStr = priceValue;
                        }
                    }

                    // Fetch User Details after successfully fetching Listing Details
                    db.collection("UserDetails").document(ownerUserId)
                            .get()
                            .addOnSuccessListener(userSnapshot -> {
                                if (userSnapshot.exists()) {
                                    String profileImage = userSnapshot.getString("imageUrl");
                                    String name = userSnapshot.getString("name");
                                    Map<String, String> locationMap = (Map<String, String>) userSnapshot.get("locationMap");

                                    String location = "";
                                    if (locationMap != null) {
                                        location = String.format("%s, %s, %s",
                                                locationMap.get("city"),
                                                locationMap.get("state"),
                                                locationMap.get("country"));
                                    }

                                    ownerImageUrl = profileImage;
                                    ownerNameStr = name;
                                    ownerLocationStr = location;
                                }

                                // Update the UI after all data is fetched
                                setValues();
                                progressBar.setVisibility(View.GONE);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("UserDetails", "Error fetching user details: " + e.getMessage());
                                progressBar.setVisibility(View.GONE);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("ListingDetails", "Error fetching listing details: " + e.getMessage());
                    progressBar.setVisibility(View.GONE);
                });
    }


    private void loadTransactionDetails(String transactionId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query Transactions collection
        db.collection("Transactions")
                .whereEqualTo("transactionid", transactionId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot transactionSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String senderID = transactionSnapshot.getString("senderID");
                        String receiverID = transactionSnapshot.getString("receiverID");
                        String senderListing = transactionSnapshot.getString("senderListing");
                        String receiverListing = transactionSnapshot.getString("receiverListing");
                        String paymentTransaction = transactionSnapshot.getString("paymentTransaction");
                        String paymentMode = transactionSnapshot.getString("paymentMode");
                        String senderNote = transactionSnapshot.getString("senderNote");

                        // Set payment option
                        if ("No payment".equals(paymentMode)) {
                            paymentOption.check(nopaymentbt.getId());
                        } else if ("Pay me".equals(paymentMode)) {
                            paymentOption.check(paymebt.getId());
                        } else if ("Pay trader".equals(paymentMode)) {
                            paymentOption.check(paytraderbt.getId());
                        }

                        if (senderListing != null) {
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
                .whereEqualTo("listingType", listingTypeStr)
                .whereEqualTo("storedIn", "Active")
                .whereIn("userId", Arrays.asList(currentUserId, traderId)) // Fetch listings for both users
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> titleAndTypeList = new ArrayList<>();
                    List<String> listingIdList = new ArrayList<>();
                    List<String> owner = new ArrayList<>();

                    // Add "Clear Selection" option at the beginning of the list
                    titleAndTypeList.add("Clear Selection");
                    listingIdList.add(null); // Placeholder for no selection
                    owner.add(null);

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
                        owner.add(userId);
                    }

                    // Show dialog with options for inExchange
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Select In Exchange")
                            .setItems(titleAndTypeList.toArray(new String[0]), (dialog, which) -> {
                                if (which == 0) { // First item is "Clear Selection"
                                    selectedTitleAndType = null;
                                    selectedListingId = null;
                                    ownerStr = null;

                                    // Clear the inExchange field
                                    inexchange.setText("");
                                    CustomToast.show(this, "Selection cleared.");
                                    seeListing.setVisibility(View.GONE);
                                } else {
                                    // Regular selection
                                    selectedTitleAndType = titleAndTypeList.get(which);
                                    selectedListingId = listingIdList.get(which);
                                    ownerStr = owner.get(which);

                                    // Assign selected values to inExchange field
                                    inexchange.setText(selectedTitleAndType);
                                    handleMissingInfo(); // Perform validation or further actions
                                    seeListing.setVisibility(View.VISIBLE);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialogTheme));
        builder.setTitle("Confirm Exit")
                .setMessage("Your changes will not be saved.")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss(); // Close the dialog
                });

        // Show the dialog
        builder.show();
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
        fragment.isFromRequest = true; // Pass your data
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, fragment, "MY_FRAGMENT_TAG") // Load fragment
                .commit();
    }

    private void hideMoreFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("MY_FRAGMENT_TAG");
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .hide(fragment) // Hides fragment but keeps it in memory
                    .commit();
        }
    }

    private void showMoreFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("MY_FRAGMENT_TAG");
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .show(fragment) // Makes the fragment visible again
                    .commit();
        }
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
                        sendNotification();
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

    private void sendNotification() {
        // Get a reference to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

// Create a map to represent the notification
        Map<String, Object> notification = new HashMap<>();
        notification.put("message", senderName + " sent you a transaction request for the listing "+ listingTitle + ". The transaction is named "+ transactionTitle + ".");
        notification.put("status", "unread");
        notification.put("timestamp", FieldValue.serverTimestamp());  // Automatically set timestamp to the current server time
        notification.put("title", "Transaction Request");
        notification.put("userId", ownerUserId);  // Replace with the actual user ID

// Add the notification to the UserNotifications collection
        db.collection("UserNotifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    // Notify that the document was added successfully
                    Log.d("Notification", "Notification added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // Handle error if document could not be added
                    Log.w("Notification", "Error adding notification", e);
                });

    }

    private void fetchUserName(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("UserDetails").document(userId)
                .get()
                .addOnSuccessListener(userSnapshot -> {
                    if (userSnapshot.exists()) {
                        senderName = userSnapshot.getString("name");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UserDetails", "Error fetching user details: " + e.getMessage());
                });
    }


    private void pickCategory() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Step 2: Fetch Categories (sub-collections under the type)
        db.collection("Violations").document("Listing").collection("Categories").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<String> categories = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    categories.add(document.getId());
                }

                // Show dialog for selecting a category
                showSelectionDialog("Select Category", categories, selectedCategory -> pickSubCategory("Listing", selectedCategory));
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
        String reportedListingId = listingId; // Replace this with the actual reported user ID
        // Timestamp for the report

        hideMoreFragment();
        dark_overlay.setVisibility(View.VISIBLE);
        reportPromptLayout.setVisibility(View.VISIBLE);
        reportPromptLayout.bringToFront();

        violation.setText(subCategory);
        this.description.setText(description);

        reportcfbt.setOnClickListener(v -> {
            reportPromptLayout.setVisibility(View.GONE);
            saveViolationReport(subCategory, description, reporterId, reportedListingId);
        });
        reportbackbt.setOnClickListener(v -> {
            reportPromptLayout.setVisibility(View.GONE);
            dark_overlay.setVisibility(View.GONE);
            showMoreFragment();
        });

    }

    private void saveViolationReport(String subCategory, String description, String reporterId, String reportedListingId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Data to be saved
        Map<String, Object> reportData = new HashMap<>();
        reportData.put("violationType", subCategory);
        reportData.put("description", description);
        reportData.put("reporterId", reporterId);
        reportData.put("reportedListingId", reportedListingId);
        reportData.put("reportedUserId", ownerUserId);
        reportData.put("timestamp", FieldValue.serverTimestamp());
        reportData.put("status", "Pending");

        // Save to Firestore under Reports/User
        db.collection("Reports").document("Listing").collection("ListingReports").add(reportData)
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

