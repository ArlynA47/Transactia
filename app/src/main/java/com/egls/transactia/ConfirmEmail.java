package com.egls.transactia;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler; // Import Handler
import android.os.Looper;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ConfirmEmail extends AppCompatActivity {

    private TextView verificationStatusText;
    private ImageView loadingImageView;  // Reference to the loading ImageView
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private boolean verificationChecked = false;
    private FirebaseAuth.AuthStateListener authListener;
    private AnimatorSet animatorSet;  // To control the animation set
    String emailAuth ="", passAuth ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_email);

        verificationStatusText = findViewById(R.id.verification_status_text);
        loadingImageView = findViewById(R.id.loading);  // Link to the loading ImageView

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();

        // Retrieve the FirebaseUser instance from the intent
        user = intent.getParcelableExtra("firebaseUser");
        if (user == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        verificationStatusText.setText("Waiting for email verification...");

        // Start the rotation animation
        startLoadingAnimation();

        // Real-time listener for changes in the user's email verification status
        setupAuthStateListener();
    }

    private void startLoadingAnimation() {
        // Create the clockwise rotation animation (0 -> 360)
        ObjectAnimator rotateClockwise = ObjectAnimator.ofFloat(loadingImageView, "rotation", 0f, 360f);
        rotateClockwise.setDuration(2000);  // Clockwise rotation duration (2 seconds)
        rotateClockwise.setInterpolator(new LinearInterpolator());  // Smooth animation
        rotateClockwise.setRepeatCount(ObjectAnimator.INFINITE);  // Infinite rotation

        // Create the counterclockwise rotation animation (360 -> 0)
        ObjectAnimator rotateCounterClockwise = ObjectAnimator.ofFloat(loadingImageView, "rotation", 360f, 0f);
        rotateCounterClockwise.setDuration(4000);  // Slower counterclockwise rotation (4 seconds)
        rotateCounterClockwise.setInterpolator(new LinearInterpolator());
        rotateCounterClockwise.setRepeatCount(ObjectAnimator.INFINITE);  // Infinite rotation

        // Create an AnimatorSet to loop both animations infinitely
        animatorSet = new AnimatorSet();
        animatorSet.playSequentially(rotateClockwise, rotateCounterClockwise);
        animatorSet.start();  // Start the animation sequence
    }

    private void returnToOriginalPosition(Runnable onComplete) {
        // Create an animation that smoothly returns the image to 0 degrees (original position)
        ObjectAnimator returnToPosition = ObjectAnimator.ofFloat(loadingImageView, "rotation", loadingImageView.getRotation(), 0f);
        returnToPosition.setDuration(1000);  // Set a smooth transition duration (1 second)
        returnToPosition.setInterpolator(new LinearInterpolator());  // Smooth transition

        // When the rotation is complete, execute the onComplete runnable (to update the image and move to the next page)
        returnToPosition.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                onComplete.run();
            }
        });

        returnToPosition.start();  // Start the transition back to the original position
    }

    private void setupAuthStateListener() {
        authListener = firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null && !verificationChecked) {
                currentUser.reload().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (currentUser.isEmailVerified()) {
                            verificationChecked = true;
                            emailIsConfirmedIndicate();

                            // Stop the infinite animation and smoothly return the ImageView to 0 degrees
                            animatorSet.cancel();  // Stop the infinite rotation

                            // Return to original position, then update the image and move to the next activity
                            returnToOriginalPosition(() -> {
                                // Update the image to verified icon
                                loadingImageView.setImageResource(R.drawable.chik);

                                // Delay before moving to the next activity (e.g., 2 seconds)
                                new Handler().postDelayed(() -> {
                                    // Move to the next activity after the image update
                                    Intent intent = new Intent(ConfirmEmail.this, UserAccountDetailSignup.class);
                                    intent.putExtra("firebaseUser", currentUser);
                                    startActivity(intent);
                                    // Delay the intent by 2 seconds (2000 milliseconds)
                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        finish();
                                    }, 4000); // 2000 milliseconds = 2 seconds
                                    finish();
                                }, 2000); // 2000ms = 2 seconds delay
                            });
                        }
                    } else {
                        Log.e("TAG", "Error reloading user", task.getException());
                    }
                });
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Attach the real-time listener
        mAuth.addAuthStateListener(authListener);

        // Also check if email verification happened while the app was in the background
        if (user != null && !verificationChecked) {
            user.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (user.isEmailVerified()) {
                        verificationChecked = true;
                        emailIsConfirmedIndicate();

                        // Stop the infinite animation and smoothly return the ImageView to 0 degrees
                        animatorSet.cancel();  // Stop the infinite rotation

                        // Return to original position, then update the image and move to the next activity
                        returnToOriginalPosition(() -> {
                            // Update the image to verified icon
                            loadingImageView.setImageResource(R.drawable.chik);

                            // Delay before moving to the next activity (e.g., 2 seconds)
                            new Handler().postDelayed(() -> {
                                // Move to the next activity after the image update
                                Intent intent = new Intent(ConfirmEmail.this, UserAccountDetailSignup.class);
                                intent.putExtra("firebaseUser", user.getUid());
                                startActivity(intent);
                                // Delay the intent by 2 seconds (2000 milliseconds)
                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    finish();
                                }, 4000); // 2000 milliseconds = 2 seconds
                            }, 2000); // 2000ms = 2 seconds delay
                        });
                    } else {
                        verificationStatusText.setText("Please verify your email and return to the app.");
                    }
                } else {
                    Log.e("TAG", "Error reloading user", task.getException());
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Detach the listener when the activity goes to the background
        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }
    }

    private void emailIsConfirmedIndicate() {
        verificationStatusText.setText("Email successfully verified.");
    }
}
