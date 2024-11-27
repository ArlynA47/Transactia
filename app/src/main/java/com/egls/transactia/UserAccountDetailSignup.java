package com.egls.transactia;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;


public class UserAccountDetailSignup extends AppCompatActivity {

    FirebaseUser currUser;

    // user details string holders
    String name, sex, bio, birthdate, contactInfo, location;
    String selectedCountry, selectedState, selectedRegion, selectedCity;

    String emailAuth ="", passAuth ="";

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private FirebaseStorage storage;

    String[] item = {"Male", "Female", "Prefer not to say"};
    TextInputLayout sextxCont;
    AutoCompleteTextView sextx;
    ArrayAdapter<String> adapterItems;
    EditText birthdateTx;

    ImageView addIMG; // to add an image
    ImageView pfp; // to display the selected image
    EditText nametx; // user full name
    EditText contacttx; // user phone number
    EditText biotx; //userbio
    EditText loctx; // user location

    // Error text for unfilled fields
    TextView errorTv;

    // Save the default backgrounds to revert back (if no error)
    Drawable defaultBgET;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signuptwo);

        Intent intent = getIntent();

        // Retrieve the FirebaseUser instance from the intent
        currUser = intent.getParcelableExtra("firebaseUser");

        // initialize Firebase Storage
        storage = FirebaseStorage.getInstance();

        addIMG  = findViewById(R.id.addIMG); // imageview
        pfp  = findViewById(R.id.pfp); // imageview icon
        errorTv  = findViewById(R.id.errorTv); // TextView
        nametx  = findViewById(R.id.nametx); // EditText
        sextx = findViewById(R.id.sexAutoComp_txt); // AutoCompleteTextView
        sextxCont = findViewById(R.id.sextx); // Sex EditText Container
        birthdateTx = findViewById(R.id.birthdatetx); // EditText
        loctx  = findViewById(R.id.loctx); // EditText
        contacttx  = findViewById(R.id.contacttx); //EditText
        biotx  = findViewById(R.id.biotx); // EditText

        // Background drawables
        defaultBgET = nametx.getBackground();

        // ----------------------- ONCLICK LISTENERS ------------------------------------

        // OnClickListener for addIMG to open the gallery
        addIMG.setOnClickListener(v -> openGallery());

        // OnClickListener to view the image in full screen
        pfp.setOnClickListener(v -> {
            imgFullScreen();
        });

        // Set up gender AutoCompleteTextView
        ArrayAdapter<String> adapterItems = new ArrayAdapter<>(this, R.layout.genderlist, item);
        sextx.setAdapter(adapterItems);

        // Set the OnClickListener for sextx and call the new method
        sextx.setOnClickListener(v -> handleSexClick());
        sextx.setOnFocusChangeListener((v, hasFocus) -> {
            handleSexClick();
        });

        // Set the OnClickListener for birthdateTx and call the new method
        birthdateTx.setOnClickListener(v -> handleBirthdateClick());
        birthdateTx.setOnFocusChangeListener((v, hasFocus) -> {
            handleBirthdateClick();
        });

        // Set the OnClickListener for loctx and call the new method
        loctx.setOnClickListener(v -> handleLocationClick());
        loctx.setOnFocusChangeListener((v, hasFocus) -> {
            handleLocationClick();
        });

        // Set the OnClickListener for contacttx and call the new method
        contacttx.setOnClickListener(v -> handleContactClick());
        contacttx.setOnFocusChangeListener((v, hasFocus) -> {
            handleContactClick();
        });

        // Set the OnClickListener for biotx and call the new method
        biotx.setOnClickListener(v -> handleBioClick());
        biotx.setOnFocusChangeListener((v, hasFocus) -> {
            handleBioClick();
        });

        // Sign up button click listener
        Button signbt = findViewById(R.id.signbt);
        signbt.setOnClickListener(v -> {
            saveUserDetails();

        });

        // Set window insets for main layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    // Separate methods to handle each field's action
    private void handleSexClick() {

        sextx.dismissDropDown();
        // Check if nametx is empty
        if (nametx.getText().toString().trim().isEmpty()) {
            sextx.dismissDropDown();
            // Set red border when validation fails
            nametx.setBackgroundResource(R.drawable.edittext_red_border);
            nametx.requestFocus();
            errorTv.setVisibility(View.VISIBLE);
        } else {
            // Reset to default background after valid input
            nametx.setBackground(defaultBgET);
            errorTv.setVisibility(View.INVISIBLE);
            sextx.showDropDown();  // Show dropdown if conditions are met
        }
    }


    private void handleBirthdateClick() {
        if (sextx.getText().toString().trim().isEmpty()) {
            // Set red border when validation fails
            sextxCont.setBackgroundResource(R.drawable.edittext_red_border);
            handleSexClick();
            birthdateTx.clearFocus();
            errorTv.setVisibility(View.VISIBLE);
        } else {
            sextxCont.setBackground(defaultBgET);
            showDatePickerDialog();
            errorTv.setVisibility(View.INVISIBLE);
        }
    }

    private void handleLocationClick() {
        if (birthdateTx.getText().toString().trim().isEmpty()) {
            // Set red border when validation fails
            birthdateTx.setBackgroundResource(R.drawable.edittext_red_border);
            handleBirthdateClick();
            loctx.clearFocus();
            errorTv.setVisibility(View.VISIBLE);
            errorTv.setVisibility(View.INVISIBLE);
        } else {
            birthdateTx.setBackground(defaultBgET);
            showLocationPickerDialog();
            errorTv.setVisibility(View.INVISIBLE);
        }
    }

    private void handleContactClick() {
        if (loctx.getText().toString().trim().isEmpty()) {
            // Set red border when validation fails
            loctx.setBackgroundResource(R.drawable.edittext_red_border);
            loctx.requestFocus();
            contacttx.clearFocus();
            errorTv.setVisibility(View.VISIBLE);
        } else {
            loctx.setBackground(defaultBgET);
            errorTv.setVisibility(View.INVISIBLE);
        }
    }

    private void handleBioClick() {
        if (contacttx.getText().toString().trim().isEmpty()) {
            // Set red border when validation fails
            contacttx.setBackgroundResource(R.drawable.edittext_red_border);
            contacttx.requestFocus();
            biotx.clearFocus();
            errorTv.setVisibility(View.VISIBLE);
        } else {
            contacttx.setBackground(defaultBgET);
            errorTv.setVisibility(View.INVISIBLE);
        }
    }


    // Function to open gallery
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Handle the result of the gallery selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            pfp.setImageURI(imageUri); // Display the selected image in pfp ImageView
        }
    }

    // Show Image on full screen
    private void imgFullScreen() {
        if (imageUri != null) {
            Intent fullScreenIntent = new Intent(UserAccountDetailSignup.this, FullScreenImageActivity.class);
            fullScreenIntent.putExtra("imageUri", imageUri.toString());
            startActivity(fullScreenIntent);
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, R.style.Datetheme,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Create a new calendar instance for the selected date
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    // Format the selected date using SimpleDateFormat
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String formattedDate = dateFormat.format(selectedDate.getTime());

                    // check if the user is at least 16 years old
                    AgeCheck ageCheck = new AgeCheck();

                    if (ageCheck.isAtLeast16YearsOld(formattedDate)) {
                        // Set the formatted date to the EditText
                        birthdateTx.setText(formattedDate, TextView.BufferType.EDITABLE);
                    } else {
                        birthdateTx.setText("");
                        // Show an error message
                        CustomToast.show(this, "You must be at least 16 years old to register.");
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showLocationPickerDialog() {
        // Start with picking the country
        pickCountry();
    }

    private void pickCountry() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Countries")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> countries = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            countries.add(document.getId());
                        }

                        // Create dialog for selecting country
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Select Country")
                                .setItems(countries.toArray(new String[0]), (dialog, which) -> {
                                    selectedCountry = countries.get(which);
                                    pickRegion(selectedCountry);
                                })
                                .show();
                    }
                });
    }

    private void pickRegion(String country) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Countries").document(country).collection("Regions")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> regions = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            regions.add(document.getId());
                        }

                        // Create dialog for selecting region
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Select Region")
                                .setItems(regions.toArray(new String[0]), (dialog, which) -> {
                                    selectedRegion = regions.get(which);
                                    pickState(country, selectedRegion);
                                })
                                .show();
                    }
                });
    }

    private void pickState(String country, String region) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Countries").document(country).collection("Regions").document(region).collection("States")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> states = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            states.add(document.getId());
                        }

                        // Create dialog for selecting state
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Select State")
                                .setItems(states.toArray(new String[0]), (dialog, which) -> {
                                    selectedState = states.get(which);
                                    pickCity(country, region, selectedState);
                                })
                                .show();
                    }
                });
    }

    private void pickCity(String country, String region, String state) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Countries").document(country).collection("Regions").document(region).collection("States").document(state).collection("Cities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> cities = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            cities.add(document.getId());
                        }

                        // Create dialog for selecting city
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Select City")
                                .setItems(cities.toArray(new String[0]), (dialog, which) -> {
                                    selectedCity = cities.get(which);
                                    displaySelectedLocation(selectedCity, state, region, country);
                                })
                                .show();
                    }
                });
    }

    private void displaySelectedLocation(String city, String state, String region, String country) {
        String location = city + ", " + state + ", " + region + ", " + country;
        loctx.setText(location);
    }

    private void saveUserDetails() {
        if (currUser == null || currUser.getUid() == null) {
            CustomToast.show(this, "User ID is null, unable to save details");
            return;
        }

        // Get the strings from the edit texts
        name = nametx.getText().toString().trim();
        sex = sextx.getText().toString().trim();
        bio = biotx.getText().toString().trim();
        contactInfo = contacttx.getText().toString().trim();
        birthdate = birthdateTx.getText().toString().trim();

        // Create the location map
        Map<String, String> locationMap = new HashMap<>();
        locationMap.put("country", selectedCountry);
        locationMap.put("region", selectedRegion);
        locationMap.put("state", selectedState);
        locationMap.put("city", selectedCity);

        // You can also store the location as a single string if needed, e.g. "City, State, Country"
        String location = selectedCity + ", " + selectedState + ", " + selectedCountry;

        // Check if the required fields are filled
        if (name.isEmpty() || sex.isEmpty() || contactInfo.isEmpty() || birthdate.isEmpty() || location.isEmpty()) {
            handleBioClick();
            CustomToast.show(this, "Please fill in all required fields.");
        } else {
            // Firestore instance
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Reference to the ProgressBar
            ProgressBar progressBar = findViewById(R.id.progressBar);

            String udid = db.collection("UserDetails").document().getId();

            // Check if there's an image to upload
            if (imageUri != null) {
                // Show the progress bar
                progressBar.setVisibility(View.VISIBLE);

                // Firebase Storage reference with listing ID subfolder
                StorageReference storageRef = storage.getReference().child("images/pfp/" + udid + "/image.jpg");

                // Upload image to Firebase Storage
                storageRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Get the image's download URL
                            storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                                // Create a new UserDetails object including image URL
                                UserDetails userDetails = new UserDetails(name, sex, bio, contactInfo, birthdate, location, locationMap, downloadUri.toString(), "not verified");

                                // Save user details along with image URL to Firestore
                                saveUserDetailsToFirestore(db, userDetails);
                            }).addOnFailureListener(e -> {
                                CustomToast.show(this, "Error getting image URL: " + e.getMessage());
                            }).addOnCompleteListener(task -> {
                                // Hide the progress bar when done
                                progressBar.setVisibility(View.GONE);
                            });
                        })
                        .addOnFailureListener(e -> {
                            CustomToast.show(this, "Error uploading image: " + e.getMessage());
                            progressBar.setVisibility(View.GONE); // Hide the progress bar on failure
                        });
            } else {
                // If no image is provided, set null as the image URL
                UserDetails userDetails = new UserDetails(name, sex, bio, contactInfo, birthdate, location, locationMap, null, "Not Verified");

                // Show the progress bar for saving user details
                progressBar.setVisibility(View.VISIBLE);

                // Save user details without image URL to Firestore
                saveUserDetailsToFirestore(db, userDetails);
                progressBar.setVisibility(View.GONE); // Hide after saving
            }
        }
    }


    // Helper method to save user details to Firestore
    private void saveUserDetailsToFirestore(FirebaseFirestore db, UserDetails userDetails) {
        db.collection("UserDetails")
                .document(currUser.getUid())
                .set(userDetails)
                .addOnSuccessListener(aVoid -> {
                    CustomToast.show(this, "User details saved successfully!");
                    Intent intent = new Intent(UserAccountDetailSignup.this, MainHome.class);
                    intent.putExtra("newLogin", true);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    CustomToast.show(this, "Error saving user details: " + e.getMessage());
                });
    }

}