package com.egls.transactia;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Update_User_Details extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView ShowGal;
    private ImageView selectedImageView;
    private View hiddenLayout;
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
    Drawable defaultBgET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_user_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ShowGal = findViewById(R.id.ShowGal);
        selectedImageView = findViewById(R.id.selectedImageView);
        hiddenLayout = findViewById(R.id.savepop);
        TextView saveTextView = findViewById(R.id.savetxx);
        errorTv = findViewById(R.id.errorTv);
        nametx = findViewById(R.id.nametx);
        sextx = findViewById(R.id.sexAutoComp_txt);
        sextxCont = findViewById(R.id.sextx);
        birthdateTx = findViewById(R.id.birthdatetx);
        loctx = findViewById(R.id.loctx);
        contacttx = findViewById(R.id.contacttx);
        biotx = findViewById(R.id.biotx);

        ShowGal.setOnClickListener(v -> openGallery());
        saveTextView.setOnClickListener(v -> showHiddenLayout());

        ImageView accsets = findViewById(R.id.accsets);
        accsets.setOnClickListener(v -> {
            Intent intent = new Intent(Update_User_Details.this, Account_Settings.class);
            startActivity(intent);
            finish();
        });

        adapterItems = new ArrayAdapter<>(this, R.layout.genderlist, item);
        sextx.setAdapter(adapterItems);

        sextx.setOnClickListener(v -> handleSexClick());
        sextx.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) handleSexClick();
        });

        birthdateTx.setOnClickListener(v -> showDatePickerDialog());
        birthdateTx.setOnFocusChangeListener((v, hasFocus) -> {

        });

        loctx.setOnClickListener(v -> handleLocationClick());
        loctx.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) handleLocationClick();
        });

        contacttx.setOnClickListener(v -> handleContactClick());
        contacttx.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) handleContactClick();
        });

        biotx.setOnClickListener(v -> handleBioClick());
        biotx.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) handleBioClick();
        });


    }


    private void showHiddenLayout() {
        hiddenLayout.setVisibility(View.VISIBLE);
        hiddenLayout.bringToFront();
        findViewById(R.id.main).setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));

        Button saveButton = hiddenLayout.findViewById(R.id.savebt);
        Button cancelButton = hiddenLayout.findViewById(R.id.cancelbt);

        saveButton.setOnClickListener(v -> {
            Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ProfileFragment.class));
        });

        cancelButton.setOnClickListener(v -> {
            Toast.makeText(this, "Changes not saved", Toast.LENGTH_SHORT).show();
            hiddenLayout.setVisibility(View.GONE);
            findViewById(R.id.main).setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                selectedImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleSexClick() {
        sextx.dismissDropDown();
        if (nametx.getText().toString().trim().isEmpty()) {
            sextx.dismissDropDown();
            nametx.setBackgroundResource(R.drawable.edittext_red_border);
            nametx.requestFocus();
            errorTv.setVisibility(View.VISIBLE);
        } else {
            sextx.showDropDown();
        }
    }




    private void handleLocationClick() {
        List<String> countries = Arrays.asList("USA", "Canada", "UK"); // Sample static data
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Country")
                .setItems(countries.toArray(new String[0]), (dialog, which) -> loctx.setText(countries.get(which)))
                .show();
    }

    private void handleContactClick() {
        if (loctx.getText().toString().trim().isEmpty()) {
            loctx.setBackgroundResource(R.drawable.edittext_red_border);
            errorTv.setVisibility(View.VISIBLE);
        } else {
            errorTv.setVisibility(View.INVISIBLE);
        }
    }

    private void handleBioClick() {
        if (contacttx.getText().toString().trim().isEmpty()) {
            contacttx.setBackgroundResource(R.drawable.edittext_red_border);
            errorTv.setVisibility(View.VISIBLE);
        } else {
            errorTv.setVisibility(View.INVISIBLE);
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

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    private void saveUserDetails() {
        if (nametx.getText().toString().trim().isEmpty()) {
            errorTv.setText("Please fill out all required fields.");
            errorTv.setVisibility(View.VISIBLE);
        } else {
            errorTv.setVisibility(View.INVISIBLE);
            // Save data or navigate to another activity
        }
    }
}
