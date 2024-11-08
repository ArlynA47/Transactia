package com.egls.transactia;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Request extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Enables Edge-to-Edge support for system bars
        setContentView(R.layout.activity_request);  // Inflate the layout

        // Set up window insets to adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());  // Get insets for system bars (status bar, navigation bar)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);  // Apply padding to avoid overlap
            return insets;
        });

        // Find the TextViews and ConstraintLayout
        TextView requestBt = findViewById(R.id.requestbt);  // Request button (TextView)
        ConstraintLayout confirmationBt = findViewById(R.id.confirmationbt);  // Confirmation layout
        TextView backBt = findViewById(R.id.backbt);  // TextView for "back" button
        TextView conBt = findViewById(R.id.conbt);  // TextView for "confirm" button

        // Initially hide the confirmation layout
        confirmationBt.setVisibility(View.GONE);  // Make confirmation layout invisible at first

        // Set up click listener for the request button (TextView)
        requestBt.setOnClickListener(v -> toggleConfirmationLayoutVisibility(confirmationBt));

        // Show toast and hide confirmation layout for cancellation when backBt is clicked
        backBt.setOnClickListener(v -> {
            Toast.makeText(Request.this, "Request is cancelled", Toast.LENGTH_SHORT).show();
            confirmationBt.setVisibility(View.GONE);  // Hide confirmation layout
        });

        // Show toast and hide confirmation layout for successful request when conBt is clicked
        conBt.setOnClickListener(v -> {
            Toast.makeText(Request.this, "Request sent successfully", Toast.LENGTH_SHORT).show();
            confirmationBt.setVisibility(View.GONE);  // Hide confirmation layout
        });
    }

    // Method to toggle the visibility of the confirmation layout
    private void toggleConfirmationLayoutVisibility(ConstraintLayout confirmationBt) {
        if (confirmationBt.getVisibility() == View.GONE) {
            confirmationBt.setVisibility(View.VISIBLE);  // Show confirmation layout
            confirmationBt.bringToFront();  // Bring confirmation layout to the front
        } else {
            confirmationBt.setVisibility(View.GONE);  // Hide confirmation layout
        }
    }
}
