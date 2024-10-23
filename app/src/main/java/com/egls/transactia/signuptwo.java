    package com.egls.transactia;

    import android.app.DatePickerDialog;
    import android.content.Intent;
    import android.net.Uri;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.ArrayAdapter;
    import android.widget.AutoCompleteTextView;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.activity.EdgeToEdge;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;

    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.google.firebase.storage.FirebaseStorage;
    import com.google.firebase.storage.StorageReference;

    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.List;
    import java.util.Locale;

    import androidx.appcompat.app.AlertDialog;


    public class signuptwo extends AppCompatActivity {

        String userID;
        FirebaseUser currUser;

        private static final int PICK_IMAGE_REQUEST = 1;
        private Uri imageUri;
        private FirebaseStorage storage;

        String[] item = {"Male", "Female", "Other", "Preferred not to say"};
        AutoCompleteTextView autoCompleteTextView;
        ArrayAdapter<String> adapterItems;
        EditText birthdateTx;

        ImageView addIMG; // to add an image
        ImageView pfp; // to display the selected image
        EditText nametx; // user full name
        EditText contacttx; // user phone number
        EditText biotx; //userbio
        EditText loctx; // user location


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_signuptwo);

            // Retrieve the FirebaseUser instance from the intent
            currUser = getIntent().getParcelableExtra("firebaseUser");

            // initialize Firebase Storage
            storage = FirebaseStorage.getInstance();

            addIMG  = findViewById(R.id.addIMG);
            pfp  = findViewById(R.id.pfp);
            nametx  = findViewById(R.id.nametx);
            contacttx  = findViewById(R.id.contacttx);
            biotx  = findViewById(R.id.biotx);
            loctx  = findViewById(R.id.loctx);

            // location onclick listener / set location
            loctx.setOnClickListener(v -> {
                showLocationPickerDialog();
            });
            // OnClickListener for addIMG to open the gallery
            addIMG.setOnClickListener(v -> openGallery());

            pfp.setOnClickListener(v -> {
                if (imageUri != null) {
                    Intent fullScreenIntent = new Intent(signuptwo.this, FullScreenImageActivity.class);
                    fullScreenIntent.putExtra("imageUri", imageUri.toString());
                    startActivity(fullScreenIntent);
                }
            });

            // Set up gender AutoCompleteTextView
            autoCompleteTextView = findViewById(R.id.auto_complete_txt);
            adapterItems = new ArrayAdapter<>(this, R.layout.genderlist, item);
            autoCompleteTextView.setAdapter(adapterItems);
            autoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> {
            });

            // Initialize the birthdate EditText
            birthdateTx = findViewById(R.id.birthdatetx);
            birthdateTx.setOnClickListener(v -> showDatePickerDialog());

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

                        // Set the formatted date to the EditText
                        birthdateTx.setText(formattedDate, TextView.BufferType.EDITABLE);
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
                                        String selectedCountry = countries.get(which);
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
                                        String selectedRegion = regions.get(which);
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
                                        String selectedState = states.get(which);
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
                                        String selectedCity = cities.get(which);
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
                Toast.makeText(this, "User ID is null, unable to save details", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = nametx.getText().toString().trim();
            String sex = autoCompleteTextView.getText().toString().trim();
            String bio = biotx.getText().toString().trim();
            String contactInfo = contacttx.getText().toString().trim();
            String birthdate = birthdateTx.getText().toString().trim();
            String location = loctx.getText().toString().trim();

            // Check if the required fields are filled
            if(name.isEmpty() || sex.isEmpty() || contactInfo.isEmpty() || birthdate.isEmpty() || location.isEmpty()) {

                Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_LONG).show();
            } else {

                // check if the user is at least 16 years old
                AgeCheck ageCheck = new AgeCheck();

                if (ageCheck.isAtLeast16YearsOld(birthdate)) {
                    // Proceed with registration

                    // Firestore instance
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    // Check if there's an image to upload
                    if (imageUri != null) {
                        // Firebase Storage reference
                        StorageReference storageRef = storage.getReference().child("images/" + currUser.getUid() + ".jpg");

                        // Upload image to Firebase Storage
                        storageRef.putFile(imageUri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    // Get the image's download URL
                                    storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                                        // Create a new UserDetails object including image URL
                                        UserDetails userDetails = new UserDetails(name, sex, bio, contactInfo, birthdate, location, downloadUri.toString());

                                        // Save user details along with image URL to Firestore
                                        saveUserDetailsToFirestore(db, userDetails);
                                        Toast.makeText(this, "Account details added successfully.", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(signuptwo.this, mainHome.class);
                                        startActivity(intent);
                                    }).addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error getting image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // If no image is provided, set null as the image URL
                        UserDetails userDetails = new UserDetails(name, sex, bio, contactInfo, birthdate, location, null);

                        // Save user details without image URL to Firestore
                        saveUserDetailsToFirestore(db, userDetails);
                    }
                } else {
                    // Show an error message
                    Toast.makeText(this, "You must be at least 16 years old to register.", Toast.LENGTH_LONG).show();
                }
            }
        }

        // Helper method to save user details to Firestore
        private void saveUserDetailsToFirestore(FirebaseFirestore db, UserDetails userDetails) {
            db.collection("UserDetails")
                    .document(currUser.getUid())
                    .set(userDetails)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "User details saved successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error saving user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

    }