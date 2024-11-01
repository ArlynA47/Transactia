package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseUser;


public class mainHome extends AppCompatActivity {

    boolean newLogin;

        private boolean isFragmentTransitioning = false;

        FirebaseUser currUser;

        // Declare ImageViews
        private ImageView homeMain2;
        private ImageView add2;
        private ImageView message;
        private ImageView prof2;

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

            newLogin = getIntent().getBooleanExtra("newLogin", false);

            if(newLogin) {
                // Retrieve the FirebaseUser instance from the intent
                currUser = getIntent().getParcelableExtra("firebaseUser");
                // Save the user id into sqlite db
                UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);
                dbHelper.saveUserId(currUser.getUid());
            }


            // Initialize ImageViews
            homeMain2 = findViewById(R.id.homemain2);
            add2 = findViewById(R.id.add2);
            message = findViewById(R.id.message);
            prof2 = findViewById(R.id.prof2);

            // Set up click listeners for fragment switching and image updating
            homeMain2.setOnClickListener(view -> {
                if (!isFragmentTransitioning) {
                    updateSelectedImage(homeMain2, "selecthome");
                    loadFragment(new HomeFragment());
                }
            });
            add2.setOnClickListener(view -> {
                if (!isFragmentTransitioning) {
                    updateSelectedImage(add2, "selectsearch");
                    loadFragment(new SearchFragment());
                }
            });
            message.setOnClickListener(view -> {
                if (!isFragmentTransitioning) {
                    updateSelectedImage(message, "selectmessage");
                    loadFragment(new MessageFragment());


                    // makeshift log out
                    UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);
                    dbHelper.deleteUserId();
                    Intent intent = new Intent(mainHome.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            });
            prof2.setOnClickListener(view -> {
                if (!isFragmentTransitioning) {
                    updateSelectedImage(prof2, "selectprof");
                    loadFragment(new ProfileFragment());


                    // UPDATE USER DETAILS TESTING
                    //BackendTest backendTest = new BackendTest();
                    //backendTest.createListing(currUser);

                }
            });
        }

        // Method to load fragments
        private void loadFragment(Fragment fragment) {
            isFragmentTransitioning = true;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
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

            // Set the selected image to its active state
            selectedImageView.setImageResource(getResources().getIdentifier(selectedImageResource, "drawable", getPackageName()));
        }
    }