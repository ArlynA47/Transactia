package com.egls.transactia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class MyOffers extends AppCompatActivity {

    private ImageView currentlySelectedImageView = null;
    private ConstraintLayout hiddenLayout;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView ShowGal;
    private ImageView selectedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_offers);

        // Set padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize hidden layout and ShowGal ImageView
        hiddenLayout = findViewById(R.id.savepop);
        ShowGal = findViewById(R.id.ShowGal);

        // ImageView to display the selected image from the gallery
        selectedImageView = findViewById(R.id.selectedImageView);

        // Set OnClickListener for ShowGal to open the gallery
        ShowGal.setOnClickListener(v -> openGallery());

        // Find the TextView by its ID and set an OnClickListener to show the dropdown menu
        TextView changeTextView = findViewById(R.id.change);
        changeTextView.setOnClickListener(v -> showDropdownMenu(v));

        // Find the TextView for saving and set OnClickListener
        TextView saveTextView = findViewById(R.id.savetx);
        saveTextView.setOnClickListener(v -> showHiddenLayout());

        // ImageViews for options in MyOffers
        ImageView skillsImageView = findViewById(R.id.skills);
        ImageView favorsImageView = findViewById(R.id.favors);
        ImageView itemsImageView = findViewById(R.id.items);

        // Set OnClickListener for each ImageView
        skillsImageView.setOnClickListener(v -> handleImageClick(skillsImageView, R.drawable.skillsel_add, R.drawable.skill_add));
        favorsImageView.setOnClickListener(v -> handleImageClick(favorsImageView, R.drawable.favorsel_add, R.drawable.favor_add));
        itemsImageView.setOnClickListener(v -> handleImageClick(itemsImageView, R.drawable.itemsel_add, R.drawable.item_add));
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                // Get the selected image as a Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                // Set the Bitmap to the ImageView
                selectedImageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showHiddenLayout() {
        // Show the hidden layout
        hiddenLayout.setVisibility(View.VISIBLE);
        hiddenLayout.bringToFront();

        // Change the background of the main layout to gray
        findViewById(R.id.main).setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));

        // Request a layout update
        hiddenLayout.requestLayout();

        // Set up button listeners for save and cancel
        Button saveButton = hiddenLayout.findViewById(R.id.savebt);
        Button cancelButton = hiddenLayout.findViewById(R.id.cancelbt);

        // Save button click listener
        saveButton.setOnClickListener(v -> {
            // Display a toast message
            Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();

            // Navigate to mainHome Activity
            startActivity(new Intent(this, MainHome.class));
        });

        // Cancel button click listener
        cancelButton.setOnClickListener(v -> {
            // Display a toast message
            Toast.makeText(this, "Changes not saved", Toast.LENGTH_SHORT).show();

            // Optionally, hide the layout again
            hiddenLayout.setVisibility(View.GONE);
            findViewById(R.id.main).setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent)); // Reset background
        });
    }

    private void showDropdownMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onMenuItemClick);
        popupMenu.show();
    }

    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.myneeds) {
            startActivity(new Intent(this, MyNeeds.class));
            return true;
        } else if (item.getItemId() == R.id.myoffers) {
            return true; // Stay in MyOffers Activity
        } else {
            return false; // Return false for unhandled menu items
        }
    }

    private void handleImageClick(ImageView imageView, int selectedImageResId, int defaultImageResId) {
        if (currentlySelectedImageView != null && currentlySelectedImageView != imageView) {
            currentlySelectedImageView.setImageResource(getDefaultImageResource(currentlySelectedImageView));
        }

        imageView.setImageResource(selectedImageResId);
        currentlySelectedImageView = imageView;
    }

    private int getDefaultImageResource(ImageView imageView) {
        if (imageView.getId() == R.id.skills) {
            return R.drawable.skill_add;
        } else if (imageView.getId() == R.id.favors) {
            return R.drawable.favor_add;
        } else if (imageView.getId() == R.id.items) {
            return R.drawable.item_add;
        }
        return 0; // Default or error case
    }
}
