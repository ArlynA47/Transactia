package com.egls.transactia;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class signuptwo extends AppCompatActivity {

    String[] item = {"Male", "Female", "Preferred not to say"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    EditText birthdateTx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signuptwo);

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
            Intent intent = new Intent(signuptwo.this, MainActivity.class);
            startActivity(intent);
        });

        // Set window insets for main layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up image click to show holder layout
        ImageView addIMG = findViewById(R.id.addIMG);
        LinearLayout holder = findViewById(R.id.holder);
        holder.setVisibility(View.GONE);
        addIMG.setOnClickListener(v -> {
            holder.setVisibility(View.VISIBLE);
            holder.bringToFront();
        });

        // Cancel button click listener
        Button remob = findViewById(R.id.remob);
        remob.setOnClickListener(v -> holder.setVisibility(View.GONE));
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,R.style.Datetheme,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    birthdateTx.setText(date);
                },
                year, month, day);

        datePickerDialog.show();
    }
}