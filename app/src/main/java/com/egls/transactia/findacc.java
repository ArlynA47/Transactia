package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class findacc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable Edge-to-Edge mode
        EdgeToEdge.enable(this);

        // Set the content view
        setContentView(R.layout.activity_findacc);

        // Find the buttons and layouts
        Button search = findViewById(R.id.searchh);
        Button cancel = findViewById(R.id.cancel);
        EditText emailAdd = findViewById(R.id.facc);



        // Set click listener for the search button
        search.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String emailAddress = emailAdd.getText().toString().trim(); // replace this with the user's email

            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Successfully sent reset email
                            CustomToast.show(this, "Password reset email sent.");
                        } else {
                            // Failed to send reset email
                            CustomToast.show(this, "Error: " + task.getException().getMessage());
                        }
                    });

        });

        cancel.setOnClickListener(v -> {
            // Create an intent to start the MainActivity
            Intent back = new Intent(findacc.this, MainActivity.class);
            startActivity(back);
            finish();
        });

        // Handle window insets for Edge-to-Edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }}
