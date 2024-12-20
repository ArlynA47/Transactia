package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signup extends AppCompatActivity {

    private FirebaseAuth auth;

    EditText emailEditText;
    EditText passwordEditText;
    EditText confirmPasswordEditText;
    Button signupbt;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        //firebase auth
        auth = FirebaseAuth.getInstance();

        // Get necessary views
        emailEditText = findViewById(R.id.usertx);
        passwordEditText = findViewById(R.id.passstx2);
        confirmPasswordEditText = findViewById(R.id.cfptx);
        signupbt = findViewById(R.id.bt);
        checkBox = findViewById(R.id.checkBox);


        signupbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get reference to the ProgressBar
                ProgressBar progressBar = findViewById(R.id.progressBar);

                String email = emailEditText.getText().toString().trim();
                String p1 = passwordEditText.getText().toString().trim();
                String p2 = confirmPasswordEditText.getText().toString().trim();

                // Check if the email field is not empty and valid
                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    // Check if the password field is not empty
                    if (!p1.isEmpty()) {
                        if (!p2.isEmpty()) {
                            // Check if the passwords match
                            if (p1.equals(p2)) {
                                String password = p1;
                                int lengthPass = password.length();

                                if (lengthPass > 6) {
                                    // Show the progress bar
                                    progressBar.setVisibility(View.VISIBLE);

                                    // Create a new user with email verification enabled
                                    auth.createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    // Hide the progress bar once the task is complete
                                                    progressBar.setVisibility(View.GONE);

                                                    if (task.isSuccessful()) {
                                                        // User creation successful
                                                        FirebaseUser user = auth.getCurrentUser();
                                                        if (user != null) {
                                                            // Send email verification link
                                                            user.sendEmailVerification()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                // Email verification link sent successfully
                                                                                CustomToast.show(signup.this, "Verification email sent. Please check your inbox.");
                                                                                // Redirect to a verification page to wait for user confirmation
                                                                                Intent intent = new Intent(signup.this, ConfirmEmail.class);
                                                                                // Pass the FirebaseUser instance to the intent
                                                                                intent.putExtra("firebaseUser", user);
                                                                                startActivity(intent);
                                                                                finish();
                                                                            } else {
                                                                                // Handle error sending verification email
                                                                                CustomToast.show(signup.this, "Failed to send verification email: " + task.getException().getMessage());
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    } else {
                                                        // Handle error creating user
                                                        CustomToast.show(signup.this, "SignUp Failed: " + task.getException().getMessage());
                                                    }
                                                }
                                            });
                                } else {
                                    passwordEditText.setError("Password should be more than 6 characters.");
                                }
                            } else {
                                confirmPasswordEditText.setError("Passwords do not match.");
                            }
                        } else {
                            confirmPasswordEditText.setError("Field cannot be empty.");
                        }
                    } else {
                        passwordEditText.setError("Field cannot be empty.");
                    }
                } else {
                    emailEditText.setError("Please enter a correct email.");
                }
            }
        });


        // Apply window insets for the main layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show password
                confirmPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                // Hide password
                confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            // Move the cursor to the end of the text
            confirmPasswordEditText.setSelection(confirmPasswordEditText.getText().length());
            passwordEditText.setSelection(passwordEditText.getText().length());
        });
    }
}
