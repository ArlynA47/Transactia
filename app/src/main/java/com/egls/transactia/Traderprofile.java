package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Traderprofile extends AppCompatActivity {
    View reportPromptLayout;
    ConstraintLayout reportPrompt;
    View msgNav;
    TextView reportcfbt, reportbackbt;

    String userID;

    ImageView star1, star2, star3, star4, star5;

    RecyclerView offerrv, needrv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_traderprofile);
        // Handle the system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        reportPromptLayout = findViewById(R.id.layout_reportprompttt); // layout where the report prompt is defined
        reportPrompt = findViewById(R.id.reportpromptt); // layout that triggers the report prompt visibility
        msgNav = findViewById(R.id.msgnav); // Floating Action Button for navigation
        reportcfbt = findViewById(R.id.reportcfbt);
        reportbackbt = findViewById(R.id.reportbackbt);

        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);

        offerrv = findViewById(R.id.offerrv);
        needrv = findViewById(R.id.needrv);


        Intent intentStart = getIntent();
        userID = intentStart.getStringExtra("userId");
        fetchUserDetails(userID);


        // Set click listener for reportPrompt
        reportPrompt.setOnClickListener(v -> {
            // Make report prompt visible and bring it to the front
            reportPromptLayout.setVisibility(View.VISIBLE);
            reportPromptLayout.bringToFront();
        });
        reportcfbt.setOnClickListener(v -> {
            Toast.makeText(Traderprofile.this, "Report Sent Succesfully", Toast.LENGTH_SHORT).show();

            reportPromptLayout.setVisibility(View.GONE);
        });
        reportbackbt.setOnClickListener(v -> {


            reportPromptLayout.setVisibility(View.GONE);
        });
        // Set click listener for msgNav (Floating Action Button)
        msgNav.setOnClickListener(v -> {
            // Navigate to Inbox activity
            Intent intent = new Intent(Traderprofile.this, Inbox.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        });

        StartSearch("Offer");
        StartSearch("Need");
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Apply the transition when back button is pressed
        overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
    }

    private void fetchUserDetails(String userId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("UserDetails").document(userId)
                .get()
                .addOnSuccessListener(userSnapshot -> {
                    if (userSnapshot.exists()) {
                        String profileImage = userSnapshot.getString("imageUrl");
                        String name = userSnapshot.getString("name");
                        String sex = userSnapshot.getString("sex");
                        String birthdate = userSnapshot.getString("birthdate");
                        String contactInfo = userSnapshot.getString("contactInfo");
                        String bio = userSnapshot.getString("bio");
                        Long numOfRatings = userSnapshot.contains("numberofratings") ? userSnapshot.getLong("numberofratings") : null;
                        Double ratings = userSnapshot.contains("ratings") ? userSnapshot.getDouble("ratings") : null;

                        // Check for null
                        if (numOfRatings == null) {
                            numOfRatings = 0L; // Default value if needed
                        }

                        if (ratings == null) {
                            ratings = 0.0; // Default value if needed
                        }

                        setStars(ratings);

                        String rateLabel = String.format(Locale.getDefault(), "%.1f Stars | %d Ratings",
                                ratings, numOfRatings);

                        Map<String, String> locationMap = (Map<String, String>) userSnapshot.get("locationMap");

                        // Format the location string
                        String location = "";
                        if (locationMap != null) {
                            location = String.format("%s, %s, %s",
                                    locationMap.get("city"),
                                    locationMap.get("state"),
                                    locationMap.get("country"));
                        }

                        int age = 0;
                        // Calculate and set age
                        if (birthdate != null) {
                            age = calculateAge(birthdate);
                        }

                        String sexAndAge = sex + "     |     " + age;


                        // Populate user-specific views
                        loadImageIntoView(profileImage, R.id.profileUser2);
                        ((TextView) findViewById(R.id.endusername)).setText(name);
                        ((TextView) findViewById(R.id.endusergender)).setText(sexAndAge);
                        ((TextView) findViewById(R.id.userloc)).setText(location);
                        ((TextView) findViewById(R.id.enduserphonenumber)).setText(contactInfo);
                        ((TextView) findViewById(R.id.enduserbio)).setText(bio);
                        ((TextView) findViewById(R.id.rateLabel)).setText(rateLabel);



                    }
                })
                .addOnFailureListener(e -> Log.e("UserDetails", "Error fetching user details: " + e.getMessage()));
    }

    private void loadImageIntoView(String imageUrl, int imageViewId) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            ImageView imageView = findViewById(imageViewId);
            Glide.with(this).load(imageUrl).into(imageView);
        }
    }

    public int calculateAge(String birthdate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            // Parse the birthdate
            Date birthDate = dateFormat.parse(birthdate);
            Calendar birthDay = Calendar.getInstance();
            birthDay.setTime(birthDate);

            // Get current date
            Calendar today = Calendar.getInstance();

            // Calculate age
            int age = today.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);

            // Adjust age if today's date is before the birthday this year
            if (today.get(Calendar.DAY_OF_YEAR) < birthDay.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return age;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Return -1 if there's an error in parsing
        }
    }

    private void setStars(double ratingUser) {

        star1.setImageResource(R.drawable.staruf);
        star2.setImageResource(R.drawable.staruf);
        star3.setImageResource(R.drawable.staruf);
        star4.setImageResource(R.drawable.staruf);
        star5.setImageResource(R.drawable.staruf);

        if(ratingUser==0) {

        }
        else if(ratingUser<2) {
            star1.setImageResource(R.drawable.starf);
            if(ratingUser>1.00) {
                star2.setImageResource(R.drawable.starhf);
            }
        } else if(ratingUser<3) {
            star1.setImageResource(R.drawable.starf);
            star2.setImageResource(R.drawable.starf);
            if(ratingUser>2.00) {
                star3.setImageResource(R.drawable.starhf);
            }
        } else if(ratingUser<4) {
            star1.setImageResource(R.drawable.starf);
            star2.setImageResource(R.drawable.starf);
            star3.setImageResource(R.drawable.starf);
            if(ratingUser>3.00) {
                star4.setImageResource(R.drawable.starhf);
            }
        } else if(ratingUser<5) {
            star1.setImageResource(R.drawable.starf);
            star2.setImageResource(R.drawable.starf);
            star3.setImageResource(R.drawable.starf);
            star4.setImageResource(R.drawable.starf);
            if(ratingUser>4.00) {
                star5.setImageResource(R.drawable.starhf);
            }
        } else if(ratingUser==5) {
            star1.setImageResource(R.drawable.starf);
            star2.setImageResource(R.drawable.starf);
            star3.setImageResource(R.drawable.starf);
            star4.setImageResource(R.drawable.starf);
            star5.setImageResource(R.drawable.starf);
        }
    }


    private void StartSearch(String ltype) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference listingsRef = db.collection("Listings");

        // Step 1: Apply search text filter as a priority
        Query query = listingsRef;

        // Step 2: Apply "storedIn" filter to search for listings with "Active" status
        query = query.whereEqualTo("storedIn", "Active");
        query = query.whereEqualTo("userId", userID);
        query = query.whereEqualTo("listingType", ltype);

        // Step 3: Execute Listings query first to narrow results, then filter users based on location
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ListingWithUserDetails> listingsWithUserDetails = new ArrayList<>();
                List<Listing> initialResults = new ArrayList<>();

                // Step 1: Fetch listings along with their document IDs
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Listing listing = document.toObject(Listing.class);
                    String listingId = document.getId(); // Firestore's document ID
                    listing.setListingId(listingId); // Optionally set listingId in the Listing object (if you add it as a field)
                    initialResults.add(listing);
                }

                if (initialResults.isEmpty()) {
                    return;
                }

                // Step 2: Filter initial results by location in UserDetails
                CollectionReference userDetailsRef = db.collection("UserDetails");
                Query userQuery = userDetailsRef;

                userQuery.get().addOnCompleteListener(userTask -> {
                    if (userTask.isSuccessful()) {
                        List<String> userIds = new ArrayList<>();
                        for (QueryDocumentSnapshot userDoc : userTask.getResult()) {
                            userIds.add(userDoc.getId());
                        }

                        // Filter initial results by matching user IDs from UserDetails
                        List<Listing> finalResults = new ArrayList<>();
                        for (Listing listing : initialResults) {
                            if (userIds.contains(listing.getUserId())) {
                                finalResults.add(listing);
                            }
                        }

                        if (finalResults.isEmpty()) {

                        } else {

                            if(ltype.equals("Offer")) {
                                fetchUserDetailsAndUpdateRecyclerView(finalResults, true);
                            }
                            else {
                                fetchUserDetailsAndUpdateRecyclerView(finalResults, false);
                            }

                        }
                    } else {
                        Log.d("SearchFragment", "No users found matching location filters.");
                    }
                });
            } else {
                Log.e("SearchFragment", "Error getting listings: ", task.getException());
            }
        });
    }


    private void fetchUserDetailsAndUpdateRecyclerView(List<Listing> searchResults, boolean isOffer) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<ListingWithUserDetails> listingsWithUserDetails = new ArrayList<>();
        final int totalListings = searchResults.size();
        final AtomicInteger counter = new AtomicInteger(0); // To track completed user detail fetches

        for (Listing listing : searchResults) {
            String userId = listing.getUserId();
            db.collection("UserDetails").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot userDoc = task.getResult();
                            UserDetails userDetails = userDoc.toObject(UserDetails.class);

                            ListingWithUserDetails listingWithUserDetails = new ListingWithUserDetails(listing, userDetails);
                            listingsWithUserDetails.add(listingWithUserDetails);

                            if (counter.incrementAndGet() == totalListings) {
                                if(isOffer) {
                                    // Update the RecyclerView only when all user details have been fetched
                                    this.runOnUiThread(() -> updateOfferRecyclerView(listingsWithUserDetails));
                                } else {
                                    this.runOnUiThread(() -> updateNeedRecyclerView(listingsWithUserDetails));
                                }
                            }
                        } else {
                            Log.e("SearchFragment", "Error fetching user details", task.getException());
                        }
                    });
        }
    }


    private void updateOfferRecyclerView(List<ListingWithUserDetails> listingsWithUserDetails) {

        offerrv.setAdapter(null);

        ListingResultAdapter adapter = new ListingResultAdapter(listingsWithUserDetails);
        offerrv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void updateNeedRecyclerView(List<ListingWithUserDetails> listingsWithUserDetails) {

        needrv.setAdapter(null);

        ListingResultAdapter adapter = new ListingResultAdapter(listingsWithUserDetails);
        needrv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }









}


