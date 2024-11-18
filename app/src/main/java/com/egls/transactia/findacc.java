package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class findacc extends AppCompatActivity {

    // Define ProgressBar at the top
    ProgressBar progressBar;

    Button search, cancel;
    EditText emailAdd;

    String email;
    boolean isLoggedIn;

    UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Enable Edge-to-Edge mode
        EdgeToEdge.enable(this);

        // Set the content view
        setContentView(R.layout.activity_findacc);

        progressBar = findViewById(R.id.progressBar);

        // Find the buttons and layouts
        search = findViewById(R.id.searchh);
        cancel = findViewById(R.id.cancel);
        emailAdd = findViewById(R.id.facc);

        isLoggedIn = getIntent().getBooleanExtra("isLoggedIn", false);

        if(isLoggedIn) {
            dbHelper = new UserDatabaseHelper(findacc.this);
            String[] userDetails = dbHelper.getUserDetails();
            if (userDetails != null) {
                email = userDetails[0].toString().trim();
                emailAdd.setText(email);
                emailAdd.setEnabled(false);
            }
        }

        // Set click listener for the search button

        // Set click listener for the search button
        search.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String emailAddress = emailAdd.getText().toString().trim();

            // Show the ProgressBar when the process starts
            progressBar.setVisibility(View.VISIBLE);

            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(task -> {
                        // Hide the ProgressBar once the task completes
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            CustomToast.show(this, "Password reset email sent to " + emailAddress);

                            // Delay the intent by 3 seconds (3000 milliseconds)
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {

                                goBackToPrevScreen();

                            }, 3000);

                        } else {
                            CustomToast.show(this, "Error: " + task.getException().getMessage());
                        }
                    });
        });


        cancel.setOnClickListener(v -> {
            goBackToPrevScreen();
        });

        // Handle window insets for Edge-to-Edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void goBackToPrevScreen() {
        if(isLoggedIn) {
            Intent intent = new Intent(this, Account_Settings.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBackToPrevScreen();
    }

}
