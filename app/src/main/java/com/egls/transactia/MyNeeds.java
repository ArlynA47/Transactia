package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MyNeeds extends AppCompatActivity {

    private ImageView currentlySelectedImageView = null;
    private View hiddenLayout; // Reference to the hidden layout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_needs);

        // Set padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize hidden layout
        hiddenLayout = findViewById(R.id.savepop);

        // Find the TextView by its ID and set an OnClickListener to show the dropdown menu
        TextView changeTextView = findViewById(R.id.change);
        changeTextView.setOnClickListener(v -> showDropdownMenu(v));

        // Find the TextView for saving and set OnClickListener
        TextView saveTextView = findViewById(R.id.savetx);
        saveTextView.setOnClickListener(v -> showHiddenLayout());

        // ImageViews for skills, favors, and items
        ImageView skillsImageView = findViewById(R.id.skills);
        ImageView favorsImageView = findViewById(R.id.favors);
        ImageView itemsImageView = findViewById(R.id.items);

        // Set OnClickListener for each ImageView
        skillsImageView.setOnClickListener(v -> handleImageClick(skillsImageView, R.drawable.skillsel_add, R.drawable.skill_add));
        favorsImageView.setOnClickListener(v -> handleImageClick(favorsImageView, R.drawable.favorsel_add, R.drawable.favor_add));
        itemsImageView.setOnClickListener(v -> handleImageClick(itemsImageView, R.drawable.itemsel_add, R.drawable.item_add));
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

            // Navigate to MyOffers Activity
            startActivity(new Intent(this, mainHome.class));
        });

        // Cancel button click listener
        cancelButton.setOnClickListener(v -> {
            // Display a toast message
            Toast.makeText(this, "Changes not saved", Toast.LENGTH_SHORT).show();

            // Optionally, you can hide the layout again
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
            return true; // Stay in MyNeeds Activity
        } else if (item.getItemId() == R.id.myoffers) {
            // Navigate to MyOffers Activity
            startActivity(new Intent(this, MyOffers.class));
            return true;
        } else {
            return false; // Return false for unhandled menu items
        }
    }

    private void handleImageClick(ImageView imageView, int selectedImageResId, int defaultImageResId) {
        // If there is an already selected ImageView, reset it to its default image
        if (currentlySelectedImageView != null && currentlySelectedImageView != imageView) {
            currentlySelectedImageView.setImageResource(getDefaultImageResource(currentlySelectedImageView));
        }

        // Set the clicked ImageView to the selected image
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
