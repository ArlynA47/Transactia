package com.egls.transactia;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ConstraintLayout rqsttrade = view.findViewById(R.id.rqsttrade);
        ConstraintLayout filteritems = view.findViewById(R.id.Filtersitems);
        ConstraintLayout Displayprev = view.findViewById(R.id.displaypreview);
        ConstraintLayout Listingsdisplay = view.findViewById(R.id.Listingdisplay);
        RadioButton listingbt = view.findViewById(R.id.Listingbt);
        RadioButton Usersbt = view.findViewById(R.id.Usersbt);
        RadioButton filtersbtt = view.findViewById(R.id.Filtersbtt);
        RadioGroup radioGroup = view.findViewById(R.id.Radiogroup);

        rqsttrade.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Request.class);  // Request is your target activity
            startActivity(intent);
        });

        // Set up radio button listener for the Listingbt to handle showing Listingsdisplay
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == listingbt.getId()) {
                // If listingbt is selected, show Listingsdisplay and hide filteritems if visible
                Listingsdisplay.setVisibility(View.VISIBLE); // Show Listingsdisplay
                filteritems.setVisibility(View.GONE); // Hide the filter view
                Displayprev.setVisibility(View.GONE); // Hide Displayprev
                // Set the button tint for the selected listingbt
                listingbt.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#33443C")));
            } else {
                // Hide Listingsdisplay if Listingbt is not selected
                Listingsdisplay.setVisibility(View.GONE);
            }
            if (checkedId == Usersbt.getId()) {

                Usersbt.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#33443C")));
            } else {
                // Hide Listingsdisplay if Listingbt is not selected
                Listingsdisplay.setVisibility(View.GONE);
            }




            if (checkedId == filtersbtt.getId()) {
                // If filtersbtt is selected, show filteritems and hide Listingsdisplay
                filteritems.setVisibility(View.VISIBLE); // Show the filter view
                Displayprev.setVisibility(View.VISIBLE); // Show Displayprev
                Listingsdisplay.setVisibility(View.GONE); // Hide Listingsdisplay
                // Set the button tint for the selected filtersbtt
                filtersbtt.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#33443C")));
            } else if (checkedId == listingbt.getId()) {
                // If listingbt is selected again, show Listingsdisplay and hide filteritems
                Listingsdisplay.setVisibility(View.VISIBLE);
                filteritems.setVisibility(View.GONE);
                Displayprev.setVisibility(View.GONE);
            } else {
                // Ensure all are hidden if no radio button is selected
                Listingsdisplay.setVisibility(View.GONE);
                filteritems.setVisibility(View.GONE);
                Displayprev.setVisibility(View.GONE);
            }
        });

        // Find Spinners by their IDs
        Spinner spinner1 = view.findViewById(R.id.spinner1);
        Spinner spinner2 = view.findViewById(R.id.spinner2);
        Spinner spinner3 = view.findViewById(R.id.spinner3);

        // Define options for the spinners
        String[] spinnerOptions1 = {"NEED", "OFFER", "ALL"};
        String[] spinnerOptions2 = {"FAVOR", "SKILL", "ALL"};
        String[] spinnerOptions3 = {"BY REGION", "BY PROVINCE", "BY CITY/MUNICIPALITY"};

        // Custom ArrayAdapter to set all text color to black for Spinner 1
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerOptions1) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK); // Set text color to black for selected item
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK); // Set text color to black for dropdown items
                return view;
            }
        };
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        // Custom ArrayAdapter to set all text color to black for Spinner 2
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerOptions2) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK); // Set text color to black for selected item
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK); // Set text color to black for dropdown items
                return view;
            }
        };
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        // Custom ArrayAdapter to set all text color to black for Spinner 3
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerOptions3) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK); // Set text color to black for selected item
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK); // Set text color to black for dropdown items
                return view;
            }
        };
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter3);

        return view;
    }
}
