package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        Button submit = findViewById(R.id.nextbt);
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        ConstraintLayout otpLayout = findViewById(R.id.otp);




        // Set click listener for the search button
        search.setOnClickListener(v -> {
            // Make the OTP layout visible and bring it to the front
            linearLayout.setVisibility(View.GONE);

            otpLayout.setVisibility(View.VISIBLE);
            otpLayout.bringToFront();
        });

        submit.setOnClickListener(v -> {
            // Create an intent to start the newpass activity
            Intent intent = new Intent(findacc.this, newpass.class);
            startActivity(intent);
        });

        cancel.setOnClickListener(v -> {
            // Create an intent to start the MainActivity
            Intent back = new Intent(findacc.this, MainActivity.class);
            startActivity(back);
        });

        // Handle window insets for Edge-to-Edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }}
