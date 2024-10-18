package com.egls.transactia;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
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
        ImageView homeImageView = findViewById(R.id.homemain);

        // Set up click listener for ImageView to navigate to mainHome activity
        homeImageView.setOnClickListener(v -> {
            Intent intent = new Intent(withpop.this, mainHome.class);
            startActivity(intent);
        });

        // Animate pop layout to move from the bottom to the center
        pop.post(() -> {
            pop.setTranslationY(page.getHeight());
            float centerY = (page.getHeight() - pop.getHeight()) / 11.0f;
            ObjectAnimator animator = ObjectAnimator.ofFloat(pop, "translationY", page.getHeight(), centerY);
            animator.setDuration(2000);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    page.setBackgroundResource(R.drawable.bgpop);
                    holder.setBackgroundResource(R.drawable.graybot);
                    search.setBackgroundResource(R.drawable.searchpopp);
                }
            });

            animator.start();
        });

        // Set click listener on the "Get started" button
        startedButton.setOnClickListener(v -> {
            pop.setVisibility(View.GONE);
            page.setBackgroundColor(Color.WHITE);
            holder.setBackgroundResource(R.drawable.bottombox);
            search.setBackgroundResource(R.drawable.searchtx);
        });
    }
}