package com.egls.transactia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_full_screen_image);

        // Set up window insets for proper padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the image URI from the intent
        Intent intent = getIntent();
        String imageUri = intent.getStringExtra("imageUri");
        String imageUrl = intent.getStringExtra("imageUrl");

        // Debugging: log the image URL to check its value
        Log.d("FullScreenImageActivity", "Image URL: " + imageUrl);

        // Find the ImageView and load the image using Glide
        ImageView fullImageView = findViewById(R.id.full_image_view);

        // Use Glide to load the image
        if (imageUri != null) {
            Uri imageU = Uri.parse(imageUri);
            Glide.with(this).load(imageUri).into(fullImageView);
        } else if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(fullImageView);
        }
    }
}
