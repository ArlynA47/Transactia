package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Enduserinfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enduserinfo);

        // Set padding for edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize ImageView and set click listener
        ImageView traderprof = findViewById(R.id.traderprof);
        traderprof.setOnClickListener(v -> goToTraderProfile());
        TextView reportcfbt = findViewById(R.id.reportcfbt);
        TextView reportbackbt = findViewById(R.id.reportbackbt);
        // Initialize report button and report prompt layout
        ImageView reportbt = findViewById(R.id.reportbt);
        View reportPromptLayout = findViewById(R.id.layout_reportprompt);

        // Check if reportPromptLayout is found to avoid NullPointerException
        if (reportPromptLayout == null) {
            throw new NullPointerException("layout_reportprompt_include not found in the layout.");
        }

        // Set click listener for the report button
        reportbt.setOnClickListener(v -> {
            // Make the report prompt visible and bring it to the front
            reportPromptLayout.setVisibility(View.VISIBLE);
            reportPromptLayout.bringToFront();
        });

        reportcfbt.setOnClickListener(v -> {
            Toast.makeText(Enduserinfo.this, "Report Send Succesfully", Toast.LENGTH_SHORT).show();

            reportPromptLayout.setVisibility(View.GONE);
        });
        reportbackbt.setOnClickListener(v -> {


            reportPromptLayout.setVisibility(View.GONE);
        });
    }


    // Function to navigate to Traderprofile activity
    private void goToTraderProfile() {
        Intent intent = new Intent(this, Traderprofile.class);
        startActivity(intent);
    }
}
