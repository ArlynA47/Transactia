package com.egls.transactia;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;


public class MainHome extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    ConstraintLayout mainLay;
        ConstraintLayout viewSentRequests, myReceivedRequests, viewInProgress;


        int displayed = 1;

        private RecyclerView recyclerView;
        private RecyclerView rvTransaction;
        private MyNeedsAdapter adapter;
        private List<Listing> listings = new ArrayList<>();

        FragmentContainerView fragmentContainerView;
        FragmentContainerView fragmentContainerHome;
        FragmentContainerView fragmentContainerNotHome;

        boolean newLogin;
        boolean isNeed;

        int homeScreenNum = 1;

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

        ProgressBar progressBar;

        boolean isInHome = true;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);  // Enable edge-to-edge support
            setContentView(R.layout.activity_main_home);

            instance = this;

            progressBar = findViewById(R.id.progressBar);

            recyclerView = findViewById(R.id.recyclerView);
            rvTransaction = findViewById(R.id.rvTransaction);

            viewSentRequests = findViewById(R.id.viewSentRequests);
            myReceivedRequests = findViewById(R.id.myReceivedRequests);
            viewInProgress = findViewById(R.id.viewInProgress);

            Intent intent = getIntent();

                newLogin = intent.getBooleanExtra("newLogin", true);

                if(newLogin) {
                    UserDatabaseHelper dbHelper = new UserDatabaseHelper(MainHome.this);

                    // Retrieve the saved unauthenticated user details
                    String[] userDetails = dbHelper.getUnauthenticatedUser();
                    if (userDetails != null) {
                        emailAuth = userDetails[0];
                        passAuth = userDetails[1];
                        // Save the credentials into sqlite db
                        dbHelper.saveUserDetails(emailAuth, passAuth);

                        // Delay the intent by 2 seconds (2000 milliseconds)
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            AuthenticateUser();
                        }, 2000); // 2000 milliseconds = 2 seconds

                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
                        }
                        fetchAndSaveToken();

                    }
                } else {
                    BioMetAuth();
                }



        }

        private void BioMetAuth() {
            // Check biometric compatibility
            BiometricManager biometricManager = BiometricManager.from(this);
            switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
                case BiometricManager.BIOMETRIC_SUCCESS:
                    showBiometricPrompt();
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    ContinueHome();
                    break;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    finish();
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    ContinueHome();
                    break;
                default:
                    finish();
            }
        }

        private void ContinueHome() {
            dbHelper = new UserDatabaseHelper(MainHome.this);
            String[] userDetails = dbHelper.getUserDetails();
            if (userDetails != null) {
                emailAuth = userDetails[0];
                passAuth = userDetails[1];
                AuthenticateUser();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
                }
                fetchAndSaveToken();

            }
    }

    private void fetchAndSaveToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult();
                        FirestoreHelper.saveTokenToFirestore(token); // Use the helper method
                    } else {
                        Log.e("FCM Token", "Failed to fetch token", task.getException());
                    }
                });
    }


    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

                // Check if the user canceled the authentication
                if (errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                    CustomToast.show(MainHome.this, "Authentication canceled by the user");
                    // Handle any specific flow for cancellation here, e.g., prompt for retry, show a different UI, etc.
                    // For example, you could return to the home screen or prompt again:
                    finishAffinity();  // Or handle as needed
                } else {
                    CustomToast.show(MainHome.this, "Authentication error: " + errString);
                    BioMetAuth();  // Or handle as needed
                }
            }


            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                CustomToast.show(MainHome.this, "Authentication succeeded!");
                ContinueHome();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                CustomToast.show(MainHome.this, "Authentication failed");
                BioMetAuth();
            }

        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint Authentication")
                .setSubtitle("Authenticate using your fingerprint")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with sending notifications
                // You can now send notifications.
            } else {
                // Permission denied, show a message to the user explaining why the permission is necessary
                // Handle the case where the user denies the permission
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(displayed==1) {

            if(homeScreenNum==1) {
                showMyNeeds();
            } else if(homeScreenNum==2) {
                showMyOffers();
            } else if(homeScreenNum==3) {
                fetchTransactions();
            } else if (homeScreenNum==4) {
                fetchCompletedTransactions();
            } else if(homeScreenNum==5) {
                fetchNotifications();
            }

        }
    }

    // Static accessor for activity instance
    private static MainHome instance;

    public static MainHome getInstance() {
        return instance; // Return the active instance
    }

    private int backPressCount = 0; // Counter for back presses
    private static final int EXIT_DELAY = 3000; // Time delay in milliseconds
    private Handler handler = new Handler();

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        backPressCount++;

        if (backPressCount == 1) {
            if (displayed != 1) {
                displayHome();
                loadInitialFragment();
                showMyNeeds();
            } else {
                backPressCount = 2; // Skip directly to second press behavior
            }
        }

        if (backPressCount == 2) {
            // Second back press: Show a toast
            CustomToast.show(MainHome.this, "Press once again to exit.");
        } else if (backPressCount == 3) {
            // Close the app
            finishAffinity(); // Closes all activities and exits the app
        }

        // Reset the counter after the delay
        handler.removeCallbacks(resetBackPressCount); // Avoid multiple callbacks
        handler.postDelayed(resetBackPressCount, EXIT_DELAY);
    }


    // Custom method to reset the back press count
    private Runnable resetBackPressCount = new Runnable() {
        @Override
        public void run() {
            backPressCount = 0;
        }
    };


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
        prof2 = findViewById(R.id.sendmsgbt);

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
                Intent intent = new Intent(MainHome.this, MyNeeds.class);
                intent.putExtra("newListing", true);
                intent.putExtra("isNeed", isNeed);
                startActivity(intent);
            }
        });

        viewSentRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MyNeeds activity when the button is clicked
                Intent intent = new Intent(MainHome.this, MyRequests.class);
                startActivity(intent);
            }
        });

        viewInProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MyNeeds activity when the button is clicked
                Intent intent = new Intent(MainHome.this, TransactionsInProgress.class);
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
                        listButton.setVisibility(View.VISIBLE);
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
            }
        }

    private void HideHome() {
            hideHomeRv();
            fragmentContainerView.setVisibility(View.GONE);
            fragmentContainerHome.setVisibility(View.GONE);
            listButton.setVisibility(View.GONE);
            fragmentContainerNotHome.setVisibility(View.VISIBLE);
            hideViewRequestSent();
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

            switchrvExchange();

        // Only set the adapter if it's null
        if (adapter == null) {
            adapter = new MyNeedsAdapter(this, listings, fireBUserID);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
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
                loadInitialFragment();
                showMyNeeds();
            }

            // Set the selected image to its active state
            selectedImageView.setImageResource(getResources().getIdentifier(selectedImageResource, "drawable", getPackageName()));
        }

    private void loadListings(String lType) {

        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Listings")
                .whereEqualTo("listingType", lType)
                .whereIn("storedIn", Arrays.asList("Active", "Flagged", "Notified"))  // OR logic for "storedIn"
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
                progressBar.setVisibility(View.GONE);
    }


    private void loadInitialFragment() {
        hfrag fragment = new hfrag();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, fragment, "MY_FRAGMENT_TAG")
                .commit();
    }

    public void showViewRequestSent() {
        viewSentRequests.setVisibility(View.VISIBLE);
        myReceivedRequests.setVisibility(View.VISIBLE);
        viewInProgress.setVisibility(View.VISIBLE);
    }

    public void hideViewRequestSent() {
        viewSentRequests.setVisibility(View.GONE);
        myReceivedRequests.setVisibility(View.GONE);
        viewInProgress.setVisibility(View.GONE);
    }

    public void fetchTransactions() {
        String currentUserId = fireBUserID;
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore.getInstance()
                .collection("Transactions")
                .whereEqualTo("receiverID", currentUserId)
                .whereEqualTo("status", "Request")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Transaction> transactionList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Transaction transaction = document.toObject(Transaction.class);
                        transactionList.add(transaction);
                    }
                    rvTransaction.setAdapter(null);

                    // Set up RecyclerView with adapter
                    TransactionAdapter tAdapter = new TransactionAdapter(this, transactionList, currentUserId);
                    rvTransaction.setAdapter(tAdapter);
                })
                .addOnFailureListener(e -> {
                    CustomToast.show(this, "Error fetching transactions: " + e.getMessage());
                });
        progressBar.setVisibility(View.GONE);
    }

    public void fetchCompletedTransactions() {
        String currentUserId = fireBUserID;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query for transactions where the current user is the sender
        Query senderQuery = db.collection("Transactions")
                .whereEqualTo("senderID", currentUserId)
                .whereEqualTo("status", "Completed");

        // Query for transactions where the current user is the receiver
        Query receiverQuery = db.collection("Transactions")
                .whereEqualTo("receiverID", currentUserId)
                .whereEqualTo("status", "Completed");

        // Execute both queries and merge the results
        senderQuery.get().addOnSuccessListener(senderSnapshot -> {
            receiverQuery.get().addOnSuccessListener(receiverSnapshot -> {
                List<Transaction> transactionList = new ArrayList<>();

                // Add transactions where the user is the sender
                for (DocumentSnapshot document : senderSnapshot) {
                    Transaction transaction = document.toObject(Transaction.class);
                    transactionList.add(transaction);
                }

                // Add transactions where the user is the receiver
                for (DocumentSnapshot document : receiverSnapshot) {
                    Transaction transaction = document.toObject(Transaction.class);
                    transactionList.add(transaction);
                }
                rvTransaction.setAdapter(null);

                // Set up RecyclerView with adapter
                TransactionAdapter tAdapter = new TransactionAdapter(this, transactionList, currentUserId);
                rvTransaction.setAdapter(tAdapter);
            }).addOnFailureListener(e -> {
                CustomToast.show(this, "Error fetching receiver transactions: " + e.getMessage());
            });
        }).addOnFailureListener(e -> {
            CustomToast.show(this, "Error fetching sender transactions: " + e.getMessage());
        });
    }

    public void fetchNotifications()
    {
        switchrvTransaction();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Notification> notificationList = new ArrayList<>();
        NotificationAdapter adapter = new NotificationAdapter(notificationList);

        rvTransaction.setAdapter(null);

        rvTransaction.setLayoutManager(new LinearLayoutManager(this));
        rvTransaction.setAdapter(adapter);

        db.collection("UserNotifications")
                .whereEqualTo("userId", fireBUserID)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    notificationList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Notification notification = doc.toObject(Notification.class);
                        notification.setDocumentId(doc.getId()); // Store the document ID
                        notificationList.add(notification);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching notifications: " + e.getMessage()));

    }
    public void switchrvExchange() {
        recyclerView.setVisibility(View.VISIBLE);
        rvTransaction.setVisibility(View.GONE);
    }

    public void switchrvTransaction() {
        recyclerView.setVisibility(View.GONE);
        rvTransaction.setVisibility(View.VISIBLE);
    }

    public void hideHomeRv() {
        recyclerView.setVisibility(View.GONE);
        rvTransaction.setVisibility(View.GONE);
    }

    public void whatHomeScreen(int hsnum) {
        homeScreenNum = hsnum;
    }

}