package com.egls.transactia;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent; // Import this for the Intent
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView; // Import TextView

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private ImageView tgwhite, tggreen;
    private ConstraintLayout contain; // Declare the ConstraintLayout
    private TextView signupbt; // Declare the TextView for sign-up button
    private Button loginbt;

    private EditText usernameEditText;
    private EditText passwordEditText;

    private FirebaseAuth auth;

    TextView forgotPass;
    ImageView eyereveal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserDatabaseHelper dbh = new UserDatabaseHelper(MainActivity.this);

        // Delete unauthenticated user details
        dbh.deleteUnauthenticatedUser();

        UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);
        // If the user is already logged in
        String[] userDetails = dbHelper.getUserDetails();
        if (userDetails != null) {
            Intent intent = new Intent(MainActivity.this, MainHome.class);
            intent.putExtra("newLogin", false);
            startActivity(intent);
        }

        // Assuming you have an EdgeToEdge helper class, if not remove this line
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        View backgroundView = findViewById(R.id.bg);

        // Check if the view with ID 'bg' exists
        if (backgroundView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(backgroundView, (v, insets) -> {
                // Get system bar insets
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

                // Apply padding to the view based on system bars insets
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

                return WindowInsetsCompat.CONSUMED;
            });
        } else {
            System.out.println("View with ID 'bg' not found.");
        }

        tgwhite = findViewById(R.id.tgwhite);
        tggreen = findViewById(R.id.tggreen);
        contain = findViewById(R.id.contain); // Initialize the ConstraintLayout
        signupbt = findViewById(R.id.signupbt); // Initialize the sign-up button
        loginbt =  findViewById(R.id.loginbt);
        forgotPass = findViewById(R.id.forgotpass);
        eyereveal = findViewById(R.id.eyereveal);
        usernameEditText = findViewById(R.id.usernametx);
        passwordEditText = findViewById(R.id.passtx);

        auth = FirebaseAuth.getInstance();

        // Set an OnClickListener on the signupbt TextView
        signupbt.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, signup.class);
            startActivity(intent);
        });
        loginbt.setOnClickListener(v -> {
            // Get references to UI elements
            ProgressBar progressBar = findViewById(R.id.progressBar);

            // Get email and password
            String email = usernameEditText.getText().toString().trim();
            String pass = passwordEditText.getText().toString().trim();

            dbh.saveUnauthenticatedUser(email, pass);


            // Check if email is valid
            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (!pass.isEmpty()) {
                    // Show the progress bar
                    progressBar.setVisibility(View.VISIBLE);

                    auth.signInWithEmailAndPassword(email, pass)
                            .addOnSuccessListener(authResult -> {
                                FirebaseUser user = authResult.getUser();
                                if (user != null) {
                                    if (!user.isEmailVerified()) {
                                        // Show dialog box with options
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                        builder.setTitle("Email Verification");
                                        builder.setMessage("A verification email was sent when you registered. Would you like to send another verification email?");
                                        builder.setPositiveButton("Send another verification link", (dialog, which) -> {
                                            user.sendEmailVerification().addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    CustomToast.show(MainActivity.this, "New verification email sent. Please check your inbox.");
                                                    // Redirect to ConfirmEmail screen
                                                    Intent intent = new Intent(MainActivity.this, ConfirmEmail.class);
                                                    intent.putExtra("newLogin", true);
                                                    intent.putExtra("firebaseUser", user);
                                                    startActivity(intent);
                                                    // Delay the intent by 2 seconds (2000 milliseconds)
                                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                        finish();
                                                    }, 4000); // 2000 milliseconds = 2 seconds
                                                } else {
                                                    CustomToast.show(MainActivity.this, "Failed to send verification email: " + task.getException().getMessage());
                                                }
                                            });
                                        });
                                        builder.setNegativeButton("Use the old verification link", (dialog, which) -> {
                                            dialog.dismiss();
                                            Intent intent = new Intent(MainActivity.this, ConfirmEmail.class);
                                            intent.putExtra("firebaseUser", user);
                                            startActivity(intent);
                                            // Delay the intent by 2 seconds (2000 milliseconds)
                                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                finish();
                                            }, 4000); // 2000 milliseconds = 2 seconds
                                        });

                                        AlertDialog dialog = builder.create();
                                        dialog.show();

                                    } else {
                                        // Check if user has details in Firestore
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        DocumentReference userDoc = db.collection("UserDetails").document(user.getUid());

                                        userDoc.get().addOnSuccessListener(documentSnapshot -> {
                                            // Hide the progress bar before navigating
                                            progressBar.setVisibility(View.GONE);

                                            if (documentSnapshot.exists()) {
                                                // UserDetails exists, redirect to mainHome
                                                Intent intent = new Intent(MainActivity.this, MainHome.class);
                                                intent.putExtra("newLogin", true);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // No UserDetails, redirect to signuptwo
                                                CustomToast.show(MainActivity.this, "Please add your account details.");
                                                Intent intent = new Intent(MainActivity.this, UserAccountDetailSignup.class);
                                                intent.putExtra("firebaseUser", user);
                                                startActivity(intent);
                                                // Delay the intent by 2 seconds (2000 milliseconds)
                                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                    finish();
                                                }, 4000); // 2000 milliseconds = 2 seconds
                                            }
                                        }).addOnFailureListener(e -> {
                                            progressBar.setVisibility(View.GONE); // Hide on failure
                                            CustomToast.show(MainActivity.this, "Failed to check user details: " + e.getMessage());
                                        });
                                    }
                                }
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE); // Hide on failure
                                CustomToast.show(MainActivity.this, "Login Failed: " + e.getMessage());
                            });
                } else {
                    passwordEditText.setError("Empty fields are not allowed");
                }
            } else if (email.isEmpty()) {
                usernameEditText.setError("Empty fields are not allowed");
            } else {
                usernameEditText.setError("Please enter correct email");
            }
        });
        eyereveal.setTag("hidden");

eyereveal.setOnClickListener(v -> {

    if (eyereveal.getTag().equals("hidden")) {
        // Show the password
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        eyereveal.setImageResource(R.drawable.eye); // Change to "visible" icon
        eyereveal.setTag("visible"); // Update the tag
    } else {
        // Hide the password
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        eyereveal.setImageResource(R.drawable.eyeclose); // Change to "hidden" icon
        eyereveal.setTag("hidden"); // Update the tag
    }

});
        forgotPass.setOnClickListener(v -> {
            // Create an intent to start the FindAcc activity
            Intent intent = new Intent(MainActivity.this, findacc.class);
            intent.putExtra("isLoggedIn", false);
            startActivity(intent);
        });

        // Ensure layout has been drawn before animating
        tgwhite.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tgwhite.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Calculate center position
                float centerX = (tgwhite.getX() + tggreen.getX()) / 1; // Adjusted to divide by 2
                float centerY = (tgwhite.getY() + tggreen.getY()) / 1; // Adjusted to divide by 2

                // Move tgwhite to center
                ObjectAnimator moveTgwhiteX = ObjectAnimator.ofFloat(tgwhite, "x", centerX - (tgwhite.getWidth() / 2));
                ObjectAnimator moveTgwhiteY = ObjectAnimator.ofFloat(tgwhite, "y", centerY - (tgwhite.getHeight() / 2));

                // Move tggreen to center
                ObjectAnimator moveTggreenX = ObjectAnimator.ofFloat(tggreen, "x", centerX - (tggreen.getWidth() / 2));
                ObjectAnimator moveTggreenY = ObjectAnimator.ofFloat(tggreen, "y", centerY - (tggreen.getHeight() / 2));

                // Create AnimatorSet for moving to center
                AnimatorSet moveToCenterSet = new AnimatorSet();
                moveToCenterSet.playTogether(moveTgwhiteX, moveTgwhiteY, moveTggreenX, moveTggreenY);
                moveToCenterSet.setDuration(2000);

                // Move tgwhite and tggreen to the top after reaching center
                ObjectAnimator moveTgwhiteToTop = ObjectAnimator.ofFloat(tgwhite, "y", -50);
                ObjectAnimator moveTggreenToTop = ObjectAnimator.ofFloat(tggreen, "y", -50);

                // Create AnimatorSet for moving to the top
                AnimatorSet moveToTopSet = new AnimatorSet();
                moveToTopSet.playTogether(moveTgwhiteToTop, moveTggreenToTop);
                moveToTopSet.setDuration(1000);

                // Make transac appear after tgwhite and tggreen reach the top
                ImageView transac = findViewById(R.id.transac);
                transac.setVisibility(View.INVISIBLE); // or View.GONE

                // Fade in animation for transac
                ObjectAnimator appearTransac = ObjectAnimator.ofFloat(transac, "alpha", 0f, 1f);
                appearTransac.setDuration(1000);
                appearTransac.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        transac.setVisibility(View.VISIBLE);
                    }
                });

                // Fade in animation for contain
                ObjectAnimator appearContain = ObjectAnimator.ofFloat(contain, "alpha", 0f, 1f);
                appearContain.setDuration(1000);
                appearContain.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        contain.setVisibility(View.VISIBLE);
                    }
                });

                // Create a sequential AnimatorSet for all animations
                AnimatorSet sequentialAnimatorSet = new AnimatorSet();
                sequentialAnimatorSet.playSequentially(moveToCenterSet, moveToTopSet, appearTransac, appearContain);

                // Start the sequential animation
                sequentialAnimatorSet.start();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save necessary state here
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore necessary state here
    }
}