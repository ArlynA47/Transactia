package com.egls.transactia;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

public class Update_User_Details extends AppCompatActivity {

    FirebaseUser user;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private FirebaseStorage storage;

    String fireBUserID;  // Firestore user ID (must be passed to the activity)
    String selectedCountry, selectedState, selectedRegion, selectedCity;

    String[] item = {"Male", "Female", "Prefer not to say"};
    TextInputLayout sextxCont;
    AutoCompleteTextView sextx;
    ArrayAdapter<String> adapterItems;
    EditText birthdateTx;

    ImageView addIMG;
    ImageView pfp;
    EditText nametx;
    EditText contacttx;
    EditText biotx;
    EditText loctx;
    TextView errorTv;

    boolean isLocChanged = false;

    Drawable defaultBgET;
    // Set up button for updating user details
    TextView updateBtn;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_user_details);

        user = FirebaseAuth.getInstance().getCurrentUser();
        fireBUserID = user.getUid();

        storage = FirebaseStorage.getInstance();

        // Initialize UI elements
        addIMG = findViewById(R.id.addIMG);
        updateBtn = findViewById(R.id.savetxx);
        pfp = findViewById(R.id.pfp);
        errorTv = findViewById(R.id.errorTv);
        nametx = findViewById(R.id.nametx);
        sextx = findViewById(R.id.sexAutoComp_txt);
        sextxCont = findViewById(R.id.sextx);
        birthdateTx = findViewById(R.id.birthdatetx);
        loctx = findViewById(R.id.loctx);
        contacttx = findViewById(R.id.contacttx);
        biotx = findViewById(R.id.biotx);

        defaultBgET = nametx.getBackground();

        // Set up gender AutoCompleteTextView
        ArrayAdapter<String> adapterItems = new ArrayAdapter<>(this, R.layout.genderlist, item);
        sextx.setAdapter(adapterItems);

        // Load user details
        loadUserDetails();

        addIMG.setOnClickListener(v -> openGallery());

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

        updateBtn.setOnClickListener(v -> updateUserDetails());

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
            Intent fullScreenIntent = new Intent(Update_User_Details.this, FullScreenImageActivity.class);
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
                                    displaySelectedLocation(selectedCity, region, country);
                                })
                                .show();
                    }
                });
    }

    private void displaySelectedLocation(String city, String state, String country) {
        String location = city + ", " + state + ", " + country;
        loctx.setText(location);
        isLocChanged = true;
    }

    private void loadUserDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("UserDetails").document(fireBUserID);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    nametx.setText(document.getString("name"));
                    sextx.setText(document.getString("sex"));
                    biotx.setText(document.getString("bio"));
                    contacttx.setText(document.getString("contactInfo"));
                    birthdateTx.setText(document.getString("birthdate"));
                    loctx.setText(document.getString("location"));

                    String imageUrl = document.getString("imageUrl");
                    if (imageUrl != null) {
                        // Load profile image with Glide
                        Glide.with(this).load(imageUrl).into(pfp);
                    }
                } else {
                    errorTv.setText("User details not found.");
                    errorTv.setVisibility(View.VISIBLE);
                }
            } else {
                errorTv.setText("Error loading user details: " + task.getException().getMessage());
                errorTv.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateUserDetails() {
        String name = nametx.getText().toString().trim();
        String sex = sextx.getText().toString().trim();
        String bio = biotx.getText().toString().trim();
        String contactInfo = contacttx.getText().toString().trim();
        String birthdate = birthdateTx.getText().toString().trim();

        // Create the location map and location string only if isLocChanged is true
        Map<String, String> locationMap = null;
        String location = null;

        if (isLocChanged) {
            locationMap = new HashMap<>();
            locationMap.put("country", selectedCountry);
            locationMap.put("region", selectedRegion);
            locationMap.put("state", selectedState);
            locationMap.put("city", selectedCity);
            location = selectedCity + ", " + selectedState + ", " + selectedCountry;
        }

        if (name.isEmpty() || sex.isEmpty() || contactInfo.isEmpty() || birthdate.isEmpty() || (isLocChanged && location.isEmpty())) {
            errorTv.setText("Please fill in all required fields.");
            errorTv.setVisibility(View.VISIBLE);
            return;
        }

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("UserDetails").document(fireBUserID);

        // Create a map to hold only the fields that need updating
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("sex", sex);
        updates.put("bio", bio);
        updates.put("contactInfo", contactInfo);
        updates.put("birthdate", birthdate);

        // Add location fields only if location has changed
        if (isLocChanged) {
            updates.put("location", location);
            updates.put("locationMap", locationMap);
        }

        if (imageUri != null) {
            // Upload image and get URL
            StorageReference storageRef = storage.getReference().child("images/pfp/" + fireBUserID + "/image.jpg");
            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    updates.put("imageUrl", downloadUri.toString());
                    userRef.update(updates).addOnSuccessListener(aVoid -> {
                        progressBar.setVisibility(View.GONE);
                        finish();
                    }).addOnFailureListener(e -> {
                        CustomToast.show(this, "Error updating user details: " + e.getMessage());
                        progressBar.setVisibility(View.GONE);
                    });
                });
            }).addOnFailureListener(e -> {
                CustomToast.show(this, "Error uploading image: " + e.getMessage());
                progressBar.setVisibility(View.GONE);
            });
        } else {
            // Update Firestore without image
            userRef.update(updates).addOnSuccessListener(aVoid -> {
                progressBar.setVisibility(View.GONE);
                finish();
            }).addOnFailureListener(e -> {
                CustomToast.show(this, "Error updating user details: " + e.getMessage());
                progressBar.setVisibility(View.GONE);
            });
        }
    }


}
