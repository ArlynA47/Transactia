package com.egls.transactia;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent; // Import this for the Intent
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView; // Import TextView
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private ImageView tgwhite, tggreen;
    private ConstraintLayout contain; // Declare the ConstraintLayout
    private TextView signupbt; // Declare the TextView for sign-up button
    private Button loginbt;

    private EditText usernameEditText;
    private EditText passwordEditText;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



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
        TextView forgotPass = findViewById(R.id.forgotpass);

        usernameEditText = findViewById(R.id.usernametx);
        passwordEditText = findViewById(R.id.passtx);

        auth = FirebaseAuth.getInstance();

        // Set an OnClickListener on the signupbt TextView
        signupbt.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, signup.class);
            startActivity(intent);
        });
        loginbt.setOnClickListener(v -> {

            String email = usernameEditText.getText().toString();
            String pass = passwordEditText.getText().toString();

                    if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        if (!pass.isEmpty()) {
                            auth.signInWithEmailAndPassword(email, pass)
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(MainActivity.this, withpop.class));
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                        }
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
        forgotPass.setOnClickListener(v -> {
            // Create an intent to start the FindAcc activity
            Intent intent = new Intent(MainActivity.this, signuptwo.class);
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