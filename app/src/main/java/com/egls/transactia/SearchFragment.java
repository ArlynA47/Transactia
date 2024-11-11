package com.egls.transactia;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchFragment extends Fragment {

    ConstraintLayout filteritems;
    RadioButton listingbt, Usersbt, filtersbtt;
    RadioGroup searchType, filterrg;
    TextView noResultsTextView;

    EditText searchBar;

    String selectedCountry, selectedState, selectedRegion, selectedCity;
    Spinner listingTypeFltr, listingCategFltr;
    EditText loctx;
    TextView filterDisp;

    String filters;
    int selectedType = 1;
    String selectedLt = null;
    String selectedLc = null;
    String selectedLoc = null;

    String slt = null, slc = null, sl = null;

    // Filter box buttons
    TextView applybt, clearbt;

    private boolean checkAlr = false;

    RecyclerView searchrv;

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
        loctx  = view.findViewById(R.id.loctx);
        filterDisp  = view.findViewById(R.id.filterbt);
        applybt  = view.findViewById(R.id.Applybt);
        clearbt  = view.findViewById(R.id.Clearbt);
        noResultsTextView = view.findViewById(R.id.noResultsTextView);

        searchBar  = view.findViewById(R.id.searchBar);

        searchrv = view.findViewById(R.id.searchrv);

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

        filtersbtt.setOnClickListener(v -> {
            if (checkAlr) {
                // If already checked, hide filteritems and uncheck filtersbtt
                filtersbtt.setChecked(false);
                filteritems.setVisibility(View.GONE);
                checkAlr = false;
            } else {
                // If not checked, check it, set the color, and show filteritems
                filtersbtt.setChecked(true);
                filtersbtt.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#33443C")));
                filteritems.setVisibility(View.VISIBLE);
                checkAlr = true;
            }
        });


        // Find Spinners by their IDs
        listingTypeFltr = view.findViewById(R.id.spinner1);
        listingCategFltr = view.findViewById(R.id.spinner2);

        // Define options for the spinners
        String[] spinnerOptions1 = {"All Types", "Need", "Offer"};
        String[] spinnerOptions2 = {"All Categories", "Item","Favor", "Skill"};

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


        // Set the first item as selected by default
        listingTypeFltr.setSelection(0);

        // Set an OnItemSelectedListener
        listingTypeFltr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item as a String
                selectedLt = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle the case where no item is selected
                selectedLt = null;
            }
        });

        // Set an OnItemSelectedListener
        listingCategFltr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item as a String
                selectedLc = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle the case where no item is selected
                selectedLc = null;
            }
        });

        loctx.setOnClickListener(v -> showLocationPickerDialog());
        loctx.setOnFocusChangeListener((v, hasFocus) -> {
            showLocationPickerDialog();
        });


        applybt.setOnClickListener(v -> ApplyFilters());
        clearbt.setOnClickListener(v -> ClearFilters());

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // You can handle any actions before text changes here if needed.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // This is called every time the text is changed.
                if (charSequence.length() > 0) {
                    // Call StartSearch() when text is not empty
                    StartSearch();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // This is called after the text has changed, in case you want to handle something post-change.
            }
        });

        return view;
    }

    private void StartSearch() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userDetailsRef = db.collection("UserDetails");

        // Step 1: Build the UserDetails query based on location filters
        Query userQuery = userDetailsRef;
        if (selectedCountry != null && !selectedCountry.equals("All Countries")) {
            userQuery = userQuery.whereEqualTo("locationMap.country", selectedCountry);
        }
        if (selectedRegion != null && !selectedRegion.equals("All Regions")) {
            userQuery = userQuery.whereEqualTo("locationMap.region", selectedRegion);
        }
        if (selectedState != null && !selectedState.equals("All States")) {
            userQuery = userQuery.whereEqualTo("locationMap.state", selectedState);
        }
        if (selectedCity != null && !selectedCity.equals("All Cities")) {
            userQuery = userQuery.whereEqualTo("locationMap.city", selectedCity);
        }

        // Step 2: Execute the UserDetails query to find matching user IDs
        userQuery.get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful() && !userTask.getResult().isEmpty()) {
                List<String> userIds = new ArrayList<>();
                for (QueryDocumentSnapshot userDoc : userTask.getResult()) {
                    userIds.add(userDoc.getId()); // Each document ID corresponds to a userId
                }

                // Step 3: Query the Listings collection using the retrieved user IDs
                CollectionReference listingsRef = db.collection("Listings");
                Query listingQuery = listingsRef.whereIn("userId", userIds);

                // Apply additional filters (search text, listing type, listing category)
                String searchText = searchBar.getText().toString().trim();
                if (!searchText.isEmpty()) {
                    listingQuery = listingQuery.whereGreaterThanOrEqualTo("title", searchText)
                            .whereLessThanOrEqualTo("title", searchText + "\uf8ff");
                }

                if (slt != null && !slt.equals("All Types")) {
                    listingQuery = listingQuery.whereEqualTo("listingType", slt);
                }

                if (slc != null && !slc.equals("All Categories")) {
                    listingQuery = listingQuery.whereEqualTo("listingCategory", slc);
                }

                // Fetch the filtered listings
                listingQuery.get().addOnCompleteListener(listingTask -> {
                    if (listingTask.isSuccessful()) {
                        List<Listing> searchResults = new ArrayList<>();
                        for (QueryDocumentSnapshot listingDoc : listingTask.getResult()) {
                            Listing listing = listingDoc.toObject(Listing.class);
                            searchResults.add(listing);
                        }

                        if (searchResults.isEmpty()) {
                            displayNoResultsFound();
                        } else {
                            fetchUserDetailsAndUpdateRecyclerView(searchResults);
                            searchrv.setVisibility(View.VISIBLE);
                            noResultsTextView.setVisibility(View.GONE);
                        }
                    } else {
                        Log.e("SearchFragment", "Error getting listings: ", listingTask.getException());
                    }
                });
            } else {
                // No matching users found, show no results
                displayNoResultsFound();
                Log.d("SearchFragment", "No users found matching location filters.");
            }
        });
    }



    private void displayNoResultsFound() {
        // Hide the RecyclerView
        searchrv.setVisibility(View.GONE);
        noResultsTextView.setVisibility(View.VISIBLE);
        noResultsTextView.setText("No results found.");
    }


    private void fetchUserDetailsAndUpdateRecyclerView(List<Listing> searchResults) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<ListingWithUserDetails> listingsWithUserDetails = new ArrayList<>();
        final int totalListings = searchResults.size();
        final AtomicInteger counter = new AtomicInteger(0); // To track completed user detail fetches

        for (Listing listing : searchResults) {
            String userId = listing.getUserId();
            db.collection("UserDetails").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot userDoc = task.getResult();
                            UserDetails userDetails = userDoc.toObject(UserDetails.class);

                            ListingWithUserDetails listingWithUserDetails = new ListingWithUserDetails(listing, userDetails);
                            listingsWithUserDetails.add(listingWithUserDetails);

                            if (counter.incrementAndGet() == totalListings) {
                                // Update the RecyclerView only when all user details have been fetched
                                getActivity().runOnUiThread(() -> updateRecyclerView(listingsWithUserDetails));
                            }
                        } else {
                            Log.e("SearchFragment", "Error fetching user details", task.getException());
                        }
                    });
        }
    }


    private void updateRecyclerView(List<ListingWithUserDetails> listingsWithUserDetails) {
        ListingResultAdapter adapter = new ListingResultAdapter(listingsWithUserDetails);
        searchrv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void ApplyFilters() {
        slt = selectedLt;
        slc = selectedLc;
        sl = selectedLoc;

        if(slt ==null) {
            slt = "All Types";
        }
        if(slt ==null) {
            slc = "All Categories";
        }

        StringBuilder filterStr = new StringBuilder();
        if (!(slt==null)) filterStr.append(slt);
        if (!(slc==null)) filterStr.append(" | ").append(slc);
        if (!(sl==null)) filterStr.append(" | ").append(sl);

        filterDisp.setText(filterStr.toString());
        filterDisp.setVisibility(View.VISIBLE);
        filteritems.setVisibility(View.GONE);
        checkAlr = false;
        StartSearch();
    }

    private void ClearFilters() {
        slt = null;
        slc = null;
        sl = null;
        loctx.setText("");
        selectedCountry = null;
        selectedState = null;
        selectedRegion = null;
        selectedCity = null;;

        filterDisp.setText("Filters");
        filterDisp.setVisibility(View.GONE);
        filteritems.setVisibility(View.GONE);
        checkAlr = false;
        StartSearch();
    }

    private void showLocationPickerDialog() {
        // Start with picking the country
        loctx.setText("");
        selectedCountry = null;
        selectedState = null;
        selectedRegion = null;
        selectedCity = null;;
        pickCountry();
    }

    private void pickCountry() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Countries")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> countries = new ArrayList<>();
                        countries.add("All Countries"); // Add "All" option at the beginning
                        for (DocumentSnapshot document : task.getResult()) {
                            countries.add(document.getId());
                        }

                        // Create dialog for selecting country
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Select Country")
                                .setItems(countries.toArray(new String[0]), (dialog, which) -> {
                                    selectedCountry = countries.get(which);
                                    if (selectedCountry.equals("All Countries")) {
                                        displaySelectedLocation("", "", "", "All Countries");
                                    } else {
                                        pickRegion(selectedCountry);
                                    }
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
                        regions.add("All Regions"); // Add "All" option at the beginning
                        for (DocumentSnapshot document : task.getResult()) {
                            regions.add(document.getId());
                        }

                        // Create dialog for selecting region
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Select Region")
                                .setItems(regions.toArray(new String[0]), (dialog, which) -> {
                                    selectedRegion = regions.get(which);
                                    if (selectedRegion.equals("All Regions")) {
                                        displaySelectedLocation("", "", "All Regions", country);
                                    } else {
                                        pickState(country, selectedRegion);
                                    }
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
                        states.add("All States"); // Add "All" option at the beginning
                        for (DocumentSnapshot document : task.getResult()) {
                            states.add(document.getId());
                        }

                        // Create dialog for selecting state
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Select State")
                                .setItems(states.toArray(new String[0]), (dialog, which) -> {
                                    selectedState = states.get(which);
                                    if (selectedState.equals("All States")) {
                                        displaySelectedLocation("", "All States", region, country);
                                    } else {
                                        pickCity(country, region, selectedState);
                                    }
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
                        cities.add("All Cities"); // Add "All" option at the beginning
                        for (DocumentSnapshot document : task.getResult()) {
                            cities.add(document.getId());
                        }

                        // Create dialog for selecting city
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Select City")
                                .setItems(cities.toArray(new String[0]), (dialog, which) -> {
                                    selectedCity = cities.get(which);
                                    if (selectedCity.equals("All Cities")) {
                                        displaySelectedLocation("All Cities", state, region, country);
                                    } else {
                                        displaySelectedLocation(selectedCity, state, region, country);
                                    }
                                })
                                .show();
                    }
                });
    }

    private void displaySelectedLocation(String city, String state, String region, String country) {
        StringBuilder location = new StringBuilder();
        if (!city.isEmpty()) location.append(city).append(", ");
        if (!state.isEmpty()) location.append(state).append(", ");
        if (!region.isEmpty()) location.append(region).append(", ");
        if (!country.isEmpty()) location.append(country);

        loctx.setText(location.toString());
        selectedLoc = location.toString();
    }


}
