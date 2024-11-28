package com.egls.transactia;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class About_Transactia extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    LinearLayout violationsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_abou_transactia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        violationsLayout = findViewById(R.id.violationsLayout);

        // Fetch and display violations
        fetchAndDisplayViolations("User");
        fetchAndDisplayViolations("Listing");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Apply the transition when back button is pressed
        overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
    }

    private void fetchAndDisplayViolations(String type) {
        db.collection("Violations").document(type).collection("Categories").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot categoryDoc : task.getResult()) {
                            String categoryName = categoryDoc.getId();

                            // Display category as a header
                            TextView categoryTextView = new TextView(this);
                            categoryTextView.setText(type + " - " + categoryName);
                            categoryTextView.setTextSize(18);
                            categoryTextView.setTypeface(null, Typeface.BOLD);
                            categoryTextView.setPadding(0, 16, 0, 8); // Add spacing above categories
                            violationsLayout.addView(categoryTextView);

                            // Create a container layout for subcategories
                            LinearLayout subCategoryContainer = new LinearLayout(this);
                            subCategoryContainer.setOrientation(LinearLayout.VERTICAL);
                            subCategoryContainer.setPadding(16, 0, 0, 0); // Indent for subcategories
                            violationsLayout.addView(subCategoryContainer);

                            // Fetch and display subcategories
                            fetchAndDisplaySubCategories(type, categoryName, subCategoryContainer);
                        }
                    }
                });
    }

    private void fetchAndDisplaySubCategories(String type, String categoryName, LinearLayout subCategoryContainer) {
        db.collection("Violations").document(type)
                .collection("Categories").document(categoryName)
                .collection("SubCategories").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot subCategoryDoc : task.getResult()) {
                            String subCategoryName = subCategoryDoc.getId();
                            String description = subCategoryDoc.getString("desc");

                            // Display subcategory name
                            TextView subCategoryTextView = new TextView(this);
                            subCategoryTextView.setText("- " + subCategoryName);
                            subCategoryTextView.setTextSize(16);
                            subCategoryTextView.setTypeface(null, Typeface.BOLD);
                            subCategoryTextView.setPadding(0, 8, 0, 4); // Space between subcategories
                            subCategoryContainer.addView(subCategoryTextView);

                            // Display violation description
                            TextView descriptionTextView = new TextView(this);
                            descriptionTextView.setText("  • " + description);
                            descriptionTextView.setTextSize(14);
                            descriptionTextView.setPadding(16, 0, 0, 8); // Indent and add spacing
                            subCategoryContainer.addView(descriptionTextView);
                        }
                    }
                });
    }
}
