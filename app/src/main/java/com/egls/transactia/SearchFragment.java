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

    ConstraintLayout filteritems;
    RadioButton listingbt, Usersbt, filtersbtt;
    RadioGroup searchType, filterrg;

    Spinner listingTypeFltr, listingCategFltr;

    String filters;
    int selectedType = 1;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        filteritems = view.findViewById(R.id.Filtersitems);
        listingbt = view.findViewById(R.id.Listingbt);
        Usersbt = view.findViewById(R.id.Usersbt);
        filtersbtt = view.findViewById(R.id.Filtersbtt);
        searchType = view.findViewById(R.id.Radiogroup);
        filterrg = view.findViewById(R.id.filterRadio);

        // Set up radio button listener for the Listingbt to handle showing Listingsdisplay
        searchType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == listingbt.getId()) {
                // Set the button tint for the selected listingbt
                listingbt.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#33443C")));
                selectedType = 1;

            } else if (checkedId == Usersbt.getId()) {
                Usersbt.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#33443C")));
                selectedType = 2;
            }
        });

        listingbt.setChecked(true);

/*
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
        }); */

        // Find Spinners by their IDs
        listingTypeFltr = view.findViewById(R.id.spinner1);
        listingCategFltr = view.findViewById(R.id.spinner2);

        // Define options for the spinners
        String[] spinnerOptions1 = {"Need", "Offer", "All"};
        String[] spinnerOptions2 = {"Favor", "Skill", "All"};

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
        listingTypeFltr.setAdapter(adapter1);

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
        listingCategFltr.setAdapter(adapter2);

        /*

        // Initialize the Spinner
        Spinner listingTypeFltr = findViewById(R.id.listingTypeFltr);

// Set the first item as selected by default
        listingTypeFltr.setSelection(0);

// Set an OnItemSelectedListener
        listingTypeFltr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                String selectedItem = parent.getItemAtPosition(position).toString();

                // Check which item is selected
                switch (position) {
                    case 0:
                        // First item selected
                        // Add logic here if needed
                        break;
                    case 1:
                        // Second item selected
                        // Add logic here if needed
                        break;
                    // Add more cases as needed for additional items
                    default:
                        break;
                }

                // You can also use selectedItem variable if you need the selected text
                Toast.makeText(parent.getContext(), "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle if no item is selected
            }
        });


*/







        return view;
    }

}
