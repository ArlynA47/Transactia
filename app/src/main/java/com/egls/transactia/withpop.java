package com.egls.transactia;



import android.animation.ObjectAnimator;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class withpop extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withpop);

        // Find the views
        LinearLayout pop = findViewById(R.id.pop);
        ConstraintLayout page = findViewById(R.id.page);
        LinearLayout holder = findViewById(R.id.holder);
        TextView search = findViewById(R.id.search);
        Button startedButton = findViewById(R.id.started);
        ImageView homemain = findViewById(R.id.homemain); // Finding the homemain ImageView
        ImageView prof = findViewById(R.id.prof); // Find the prof ImageView

        // Set click listener on the homemain ImageView to load the hfrag fragment
        homemain.setOnClickListener(v -> {
            homemain.setImageResource(R.drawable.ghome);
            prof.setImageResource(R.drawable.profile);
            Fragment hfrag = new hfrag(); // Replace with your hfrag fragment class
            loadFragment(hfrag);
        });

        // Set click listener on the prof ImageView to load the accfrag fragment
        prof.setOnClickListener(v -> {
            prof.setImageResource(R.drawable.pselect);
            homemain.setImageResource(R.drawable.home);

            Fragment accfrag = new accfrag(); // Replace with your accfrag fragment class
            loadFragment(accfrag);
        });

        // Animate pop layout to move from the bottom to the center
        pop.post(() -> {
            // Start position: pop is off-screen at the bottom
            pop.setTranslationY(page.getHeight());

            // Calculate the Y-axis position for the center of page
            float centerY = (page.getHeight() - pop.getHeight()) / 11.0f;

            // Create the ObjectAnimator for translation on Y-axis
            ObjectAnimator animator = ObjectAnimator.ofFloat(pop, "translationY", page.getHeight(), centerY);
            animator.setDuration(2000); // Animation duration in milliseconds
            animator.setInterpolator(new AccelerateDecelerateInterpolator());

            // Listener to change background colors once animation is complete
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // Change the background color of the surrounding layout (page) and holder to gray
                    page.setBackgroundResource(R.drawable.bgpop);
                    holder.setBackgroundResource(R.drawable.graybot);
                    search.setBackgroundResource(R.drawable.graysearch);
                    search.setBackgroundResource(R.drawable.searchpopp);
                }
            });

            // Start the animation
            animator.start();
        });

        // Set click listener on the "Get started" button
        startedButton.setOnClickListener(v -> {
            // Remove the pop layout by setting it to GONE
            pop.setVisibility(View.GONE);

            // Change the background color of page to white
            page.setBackgroundColor(Color.WHITE);

            // Set the background of holder to the drawable resource bottombox
            holder.setBackgroundResource(R.drawable.bottombox);
            search.setBackgroundResource(R.drawable.searchtx);
        });
    }

    // Method to load fragments
    private void loadFragment(Fragment fragment) {
        // Create a FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Create a FragmentTransaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Replace the fragment in the fragment container (main layout or another container)
        fragmentTransaction.replace(R.id.main, fragment); // Assuming R.id.main is your fragment container
        fragmentTransaction.addToBackStack(null); // Add to back stack if you want to allow back navigation
        fragmentTransaction.commit();
    }
}
