package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;

public class ConfirmEmail extends AppCompatActivity {

    private TextView verificationStatusText;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private boolean verificationChecked = false;
    private AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_email);

        verificationStatusText = findViewById(R.id.verification_status_text); // Assuming you have a TextView for displaying status

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Retrieve the FirebaseUser instance from the intent
        user = getIntent().getParcelableExtra("firebaseUser");

        if (user == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        verificationStatusText.setText("Waiting for email verification...");

        // Real-time listener for changes in the user's email verification status
        setupAuthStateListener();
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
                            // Move to the next activity
                                Intent intent = new Intent(ConfirmEmail.this, signuptwo.class);
                                intent.putExtra("user", currentUser);
                                startActivity(intent);
                                finish();
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
                        // Proceed to the next activity
                        Intent intent = new Intent(ConfirmEmail.this, signuptwo.class);
                        intent.putExtra("userId", user.getUid());
                        Toast.makeText(ConfirmEmail.this, "ID" + user.getUid(), Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
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
