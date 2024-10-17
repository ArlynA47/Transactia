package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class newpass extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable Edge-to-Edge mode
        EdgeToEdge.enable(this);

        // Set the content view
        setContentView(R.layout.activity_newpass);

        // Find the confirm button after setting the content view
        Button confirm = findViewById(R.id.confirm);

        // Set click listener for the confirm button
        confirm.setOnClickListener(v -> {
            // Perform your confirmation logic here
            // For example, navigate to another activity or show a message
            // If you want to go back to the previous activity, use finish()

            // Example: navigate to the main activity (change MainActivity to your target activity)
            Intent intent = new Intent(newpass.this, MainActivity.class);
            startActivity(intent);
        });

        // Handle window insets for Edge-to-Edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
