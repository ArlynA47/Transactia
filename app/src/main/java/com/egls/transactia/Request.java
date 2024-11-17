package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class Request extends AppCompatActivity {

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


        setValues();











        // Find the TextViews and ConstraintLayout
         requestBt = findViewById(R.id.requestbt);  // Request button (TextView)
         confirmationBt = findViewById(R.id.confirmationbt);  // Confirmation layout
         backBt = findViewById(R.id.backbt);  // TextView for "back" button
         conBt = findViewById(R.id.conbt);  // TextView for "confirm" button

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
            listingImage.setImageResource(R.drawable.pfpdef);
        }

        ownerName.setText(ownerNameStr);
        location.setText(ownerLocationStr);
        listingType.setText(listingTypeStr);
        listingTitle.setText(listingTitleStr);
        listingDesc.setText(listingDescriptionStr);
        listingCateg.setText(listingCategoryStr);
        valueOwner.setText(listingValueStr);
        inExchangeOwner.setText(listingInExchangeStr);
        timestamp.setText(timestampStr);

        loadMoreFragment();
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
