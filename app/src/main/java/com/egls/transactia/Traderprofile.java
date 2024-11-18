package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Traderprofile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_traderprofile);

        // Initialize views
        View reportPromptLayout = findViewById(R.id.layout_reportprompttt); // layout where the report prompt is defined
        ConstraintLayout reportPrompt = findViewById(R.id.reportpromptt); // layout that triggers the report prompt visibility
        View msgNav = findViewById(R.id.msgnav); // Floating Action Button for navigation
        TextView reportcfbt = findViewById(R.id.reportcfbt);
        TextView reportbackbt = findViewById(R.id.reportbackbt);
        // Handle the system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set click listener for reportPrompt
        reportPrompt.setOnClickListener(v -> {
            // Make report prompt visible and bring it to the front
            reportPromptLayout.setVisibility(View.VISIBLE);
            reportPromptLayout.bringToFront();
        });
reportcfbt.setOnClickListener(v -> {
    Toast.makeText(Traderprofile.this, "Report Send Succesfully", Toast.LENGTH_SHORT).show();

    reportPromptLayout.setVisibility(View.GONE);
});
        reportbackbt.setOnClickListener(v -> {


            reportPromptLayout.setVisibility(View.GONE);
        });
        // Set click listener for msgNav (Floating Action Button)
        msgNav.setOnClickListener(v -> {
            // Navigate to Inbox activity
            Intent intent = new Intent(Traderprofile.this, Inbox.class);
            startActivity(intent);
        });
    }
}
