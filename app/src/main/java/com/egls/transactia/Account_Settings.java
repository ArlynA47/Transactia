package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.Button; // Import Button
import androidx.constraintlayout.widget.ConstraintLayout;

public class Account_Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_settings);

        // Enable edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the ConstraintLayouts by their IDs
        ConstraintLayout myreportss = findViewById(R.id.myreportss);
        ConstraintLayout aboutus = findViewById(R.id.aboutus);
        ConstraintLayout updateuserdets = findViewById(R.id.updateuserdets);
        ConstraintLayout passwordreset = findViewById(R.id.passwordreset); // Assuming you have this layout
        ConstraintLayout passpop = findViewById(R.id.passpop); // The pop-up layout
        ConstraintLayout logoutlay = findViewById(R.id.logoutlay);
        ConstraintLayout archive = findViewById(R.id.archive);
        Button cancel2 = passpop.findViewById(R.id.cancel2); // Find the cancel2 button inside passpop

        // Set click listener for myreportss
        myreportss.setOnClickListener(v -> {
            Intent intent = new Intent(Account_Settings.this, My_Reports.class);
            startActivity(intent);
        });

        // Set click listener for aboutus
        aboutus.setOnClickListener(v -> {
            Intent intent = new Intent(Account_Settings.this, Abou_Transactia.class);
            startActivity(intent);
        });

        // Set click listener for updateuserdets
        updateuserdets.setOnClickListener(v -> {
            Intent intent = new Intent(Account_Settings.this, Update_User_Details.class);
            startActivity(intent);
        });

        // Set click listener for passwordreset
        passwordreset.setOnClickListener(v -> {
            // Make the passpop visible and bring it to the front

        });

        // Set click listener for cancel2 button
        cancel2.setOnClickListener(v -> {
            // Hide the passpop
            passpop.setVisibility(View.GONE);
        });

        archive.setOnClickListener(v -> {
            Intent intent = new Intent(Account_Settings.this, Archive.class);
            startActivity(intent);
        });

        // LOG OUT
        logoutlay.setOnClickListener(v -> {
            UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);
            dbHelper.deleteUserDetails();
            Intent intent = new Intent(Account_Settings.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
