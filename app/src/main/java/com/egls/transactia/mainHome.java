package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class mainHome extends AppCompatActivity {

    ConstraintLayout mainLay;

    int displayed = 1;

    private RecyclerView recyclerView;
    private MyNeedsAdapter adapter;
    private List<Listing> listings = new ArrayList<>();

    FragmentContainerView fragmentContainerView;
    FragmentContainerView fragmentContainerHome;
    FragmentContainerView fragmentContainerNotHome;

    boolean newLogin;
    boolean isNeed;

        private boolean isFragmentTransitioning = false;

        FirebaseUser currUser;
        String fireBUserID;

        String emailAuth = "", passAuth = "";

        UserDatabaseHelper dbHelper;

        // Declare ImageViews
        private ImageView homeMain2;
        private ImageView add2;
        private ImageView message;
        private ImageView prof2;
        FloatingActionButton listButton;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);  // Enable edge-to-edge support
            setContentView(R.layout.activity_main_home);

            recyclerView = findViewById(R.id.recyclerView);


            newLogin = getIntent().getBooleanExtra("newLogin", false);

            if(newLogin) {
                // Retrieve the FirebaseUser instance from the intent
                emailAuth = getIntent().getStringExtra("emailAuth");
                passAuth = getIntent().getStringExtra("passAuth");

                // Save the credentials into sqlite db
                UserDatabaseHelper dbHelper = new UserDatabaseHelper(mainHome.this);
                dbHelper.saveUserDetails(emailAuth, passAuth);
                AuthenticateUser();
            } else {
                dbHelper = new UserDatabaseHelper(mainHome.this);
                String[] userDetails = dbHelper.getUserDetails();
                if (userDetails != null) {
                    emailAuth = userDetails[0];
                    passAuth = userDetails[1];
                    AuthenticateUser();
                }
            }

        }

    @Override
    protected void onResume() {
        super.onResume();
        if(displayed==1) {
            showMyNeeds();
        }
    }

    private void AfterAuth() {
        mainLay = findViewById(R.id.main);

        // Home Fragments
        fragmentContainerHome = findViewById(R.id.fragmentContainerHome);
        fragmentContainerView = findViewById(R.id.fragmentContainerView);

        // Not Home Fragment
        fragmentContainerNotHome = findViewById(R.id.fragmentContainerNotHome);

        // Initialize ImageViews
        homeMain2 = findViewById(R.id.homemain2);
        add2 = findViewById(R.id.add2);
        message = findViewById(R.id.message);
        prof2 = findViewById(R.id.prof2);

        // Set up click listeners for fragment switching and image updating
        homeMain2.setOnClickListener(view -> {
            displayHome();
        });
        add2.setOnClickListener(view -> {
            if (!isFragmentTransitioning) {
                HideHome();
                updateSelectedImage(add2, "selectsearch");
                loadFragment(new SearchFragment());
            }
        });
        message.setOnClickListener(view -> {
            if (!isFragmentTransitioning) {
                HideHome();
                updateSelectedImage(message, "selectmessage");
                loadFragment(new MessageFragment());
            }
        });
        prof2.setOnClickListener(view -> {
            if (!isFragmentTransitioning) {
                HideHome();
                updateSelectedImage(prof2, "selectprof");
                loadFragment(new ProfileFragment());
                recyclerView.setVisibility(View.GONE);
            }
        });


        listButton = findViewById(R.id.List);

        // Set an OnClickListener for the floating button
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MyNeeds activity when the button is clicked
                Intent intent = new Intent(mainHome.this, MyNeeds.class);
                intent.putExtra("newListing", true);
                intent.putExtra("isNeed", isNeed);
                startActivity(intent);
            }
        });

        // Set up a listener for window insets to apply padding to the main layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;  // Return the insets to propagate them
        });
    }

    private void AuthenticateUser() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(emailAuth, passAuth)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User is authenticated
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        fireBUserID = user.getUid();  // This will be the authenticated user ID
                        AfterAuth();
                        loadInitialFragment();
                        showMyNeeds();
                    } else {
                        CustomToast.show(this, "Authentication failed.");
                        finish();
                    }
                });
    }

    private void displayHome() {
            if (!isFragmentTransitioning) {
                fragmentContainerNotHome.setVisibility(View.GONE);
                updateSelectedImage(homeMain2, "selecthome");
                fragmentContainerView.setVisibility(View.VISIBLE);
                fragmentContainerHome.setVisibility(View.VISIBLE);
                listButton.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }

    private void HideHome() {
            fragmentContainerView.setVisibility(View.GONE);
            fragmentContainerHome.setVisibility(View.GONE);
            listButton.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            fragmentContainerNotHome.setVisibility(View.VISIBLE);
    }

        public void onNeedsButtonClicked() {
            showMyNeeds(); // Call your existing showMyNeeds method
            isNeed = true;
        }

        public void onOffersButtonClicked() {
            showMyOffers();
            isNeed = false;
        }

    public void showMyNeeds() {
        // Only set the adapter if it's null
        if (adapter == null) {
            adapter = new MyNeedsAdapter(this, listings, fireBUserID);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
        recyclerView.setVisibility(View.VISIBLE); // Make RecyclerView visible
        loadListings("Need"); // Load "Need" listings
    }

    // Show My Offers
    public void showMyOffers() {
        // Only set the adapter if it's null
        if (adapter == null) {
            adapter = new MyNeedsAdapter(this, listings, fireBUserID);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
        recyclerView.setVisibility(View.VISIBLE); // Make RecyclerView visible
        loadListings("Offer"); // Load "Offer" listings
    }

        // Method to load fragments
        private void loadFragment(Fragment fragment) {
            isFragmentTransitioning = true;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerNotHome, fragment)
                    .addToBackStack(null)
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
            isFragmentTransitioning = false;  // Allow clicks again after transaction
        }

        // Method to update the selected image and reset others
        private void updateSelectedImage(ImageView selectedImageView, String selectedImageResource) {
            // Reset all images to their default state
            homeMain2.setImageResource(R.drawable.home);
            add2.setImageResource(R.drawable.hanap);
            message.setImageResource(R.drawable.message);
            prof2.setImageResource(R.drawable.profile);

            if(selectedImageResource.equals("selectsearch")) {
                displayed = 2;
            } else if(selectedImageResource.equals("selectmessage")) {
                displayed = 3;
            } else if(selectedImageResource.equals("selectprof")) {
                displayed = 4;
            } else {
                displayed = 1;
            }

            // Set the selected image to its active state
            selectedImageView.setImageResource(getResources().getIdentifier(selectedImageResource, "drawable", getPackageName()));
        }

    private void loadListings(String lType) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Listings")
                .whereEqualTo("listingType", lType)
                .whereEqualTo("storedIn", "Active")
                .whereEqualTo("userId", fireBUserID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listings.clear(); // Clear existing listings
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Listing listing = doc.toObject(Listing.class);
                        listing.setListingId(doc.getId());
                        listings.add(listing);
                    }
                    adapter.notifyDataSetChanged(); // Notify the adapter of data changes
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error loading listings", e));
    }


    private void loadInitialFragment() {
        hfrag fragment = new hfrag();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, fragment, "MY_FRAGMENT_TAG")
                .commit();
    }


}