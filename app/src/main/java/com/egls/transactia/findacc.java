package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class findacc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable Edge-to-Edge mode
        EdgeToEdge.enable(this);

        // Set the content view before finding views
        setContentView(R.layout.activity_findacc);

        // Find the buttons after setting the content view
        Button search = findViewById(R.id.searchh);
        Button cancel = findViewById(R.id.cancel);

        // Set click listener for the search button
        search.setOnClickListener(v -> {
            // Create an intent to start the newpass activity
            Intent intent = new Intent(findacc.this, newpass.class);
            startActivity(intent);
        });

        // Set click listener for the cancel button
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
    }
}
