package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class mainHome extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyNeedsAdapter adapter;
    private List<Listing> listings = new ArrayList<>();

    private boolean isFragmentTransitioning = false;
    private FirebaseUser currUser;
    private String fireBUserID;
    private UserDatabaseHelper dbHelper;

    // Declare ImageViews
    private ImageView homeMain2, add2, message, prof2;
    private FloatingActionButton listButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Enable edge-to-edge support
        setContentView(R.layout.activity_main_home);

        // Set up a listener for window insets to apply padding to the main layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;  // Return the insets to propagate them
        });

        initializeFields();
        handleIncomingIntent();
        setupImageViewListeners();
        setupFloatingButton();
        recyclerView.setVisibility(View.GONE);
    }

    private void initializeFields() {
        homeMain2 = findViewById(R.id.homemain2);
        add2 = findViewById(R.id.add2);
        message = findViewById(R.id.message);
        prof2 = findViewById(R.id.prof2);
        recyclerView = findViewById(R.id.recyclerView);
        listButton = findViewById(R.id.List);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setVisibility(View.GONE);

    }

    private void handleIncomingIntent() {
        boolean newLogin = getIntent().getBooleanExtra("newLogin", false);
        dbHelper = new UserDatabaseHelper(this);
        if (newLogin) {
            currUser = getIntent().getParcelableExtra("firebaseUser");
            dbHelper.saveUserId(currUser.getUid());
        }
        fireBUserID = dbHelper.getUserId();
    }

    private void setupImageViewListeners() {
        homeMain2.setOnClickListener(view -> {
            if (!isFragmentTransitioning) {
                listButton.setVisibility(View.VISIBLE);
                displayHome();
            }
        });

        add2.setOnClickListener(view -> {
            switchFragment(new SearchFragment(), add2, "selectsearch");
            recyclerView.setVisibility(View.GONE); // Hide RecyclerView when Add is clicked
            listButton.setVisibility(View.GONE);
        });

        message.setOnClickListener(view -> {
            switchFragment(new MessageFragment(), message, "selectmessage");
            recyclerView.setVisibility(View.GONE); // Hide RecyclerView when Message is clicked
            listButton.setVisibility(View.GONE);
        });

        prof2.setOnClickListener(view -> {
            switchFragment(new ProfileFragment(), prof2, "selectprof");
            recyclerView.setVisibility(View.GONE); // Hide RecyclerView when Profile is clicked
            listButton.setVisibility(View.GONE);
        });
    }

    private void setupFloatingButton() {
        listButton.setOnClickListener(v -> {
            // Start MyNeeds activity when the button is clicked
            Intent intent = new Intent(mainHome.this, MyNeeds.class);
            intent.putExtra("newListing", true);
            startActivity(intent);
        });
    }

    private void displayHome() {
        if (!isFragmentTransitioning) {
            updateSelectedImage(homeMain2, "selecthome");
            recyclerView.setVisibility(View.VISIBLE); // Show RecyclerView when Home is clicked
            loadFragment(new HomeFragment());
        }
    }

    private void switchFragment(Fragment fragment, ImageView selectedImageView, String selectedImageResource) {
        if (!isFragmentTransitioning) {
            updateSelectedImage(selectedImageView, selectedImageResource);
            loadFragment(fragment);
        }
    }

    private void loadFragment(Fragment fragment) {
        isFragmentTransitioning = true;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
        isFragmentTransitioning = false;  // Allow clicks again after transaction
    }

    private void updateSelectedImage(ImageView selectedImageView, String selectedImageResource) {
        homeMain2.setImageResource(R.drawable.home);
        add2.setImageResource(R.drawable.hanap);
        message.setImageResource(R.drawable.message);
        prof2.setImageResource(R.drawable.profile);

        selectedImageView.setImageResource(getResources().getIdentifier(selectedImageResource, "drawable", getPackageName()));
    }

    // Method to show needs listings
    public void onNeedsButtonClicked() {
        showMyNeeds(); // Call showMyNeeds when needs button is clicked
    }

    // Method to show offers listings
    public void onOffersButtonClicked() {
        showMyOffers(); // Call showMyOffers when offers button is clicked
    }

    public void showMyNeeds() {
        setupRecyclerView("Need");
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void showMyOffers() {
        setupRecyclerView("Offer");
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void setupRecyclerView(String listingType) {
        // Ensure the adapter is initialized
        if (adapter == null) {
            adapter = new MyNeedsAdapter(this, listings, fireBUserID);
            recyclerView.setAdapter(adapter);
        }

        // Make RecyclerView visible and load listings
        recyclerView.setVisibility(View.GONE); // Ensure visibility
        loadListings(listingType); // Load listings based on type
    }

    private void loadListings(String listingType) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Listings")
                .whereEqualTo("listingType", listingType)
                .whereEqualTo("userId", fireBUserID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listings.clear(); // Clear existing listings
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("Firestore", "No listings found for type: " + listingType);
                    } else {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Listing listing = doc.toObject(Listing.class);
                            listing.setListingId(doc.getId());
                            listings.add(listing);
                        }
                        Log.d("Firestore", "Listings loaded: " + listings.size());
                    }
                    adapter.notifyDataSetChanged(); // Notify the adapter of data changes
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error loading listings", e));

    }

}
