package com.egls.transactia;

import android.animation.ObjectAnimator;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
}
