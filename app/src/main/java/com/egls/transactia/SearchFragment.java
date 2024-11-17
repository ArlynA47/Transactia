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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchFragment extends Fragment {

    public boolean isFromRequest = false;

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

    ConstraintLayout constraintLayout;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        constraintLayout = view.findViewById(R.id.constraintLayout);
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

        // Find Spinners by their IDs
        listingTypeFltr = view.findViewById(R.id.spinner1);
        listingCategFltr = view.findViewById(R.id.spinner2);

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
                StartSearch();
            }
        });

        if(isFromRequest) {
            publicSearch();
        } else {
            getSelectedCountry();
            StartSearch(); // start by initially searching the listings in the user's country
        }

        return view;
    }

    private void StartSearch() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference listingsRef = db.collection("Listings");

        // Step 1: Apply search text filter as a priority
        Query query = listingsRef;
        String searchText = searchBar.getText().toString().trim().toLowerCase();
        if (!searchText.isEmpty()) {
            query = query.whereArrayContains("keywords", searchText); // Use keywords array for flexible matching
        }

        // Step 2: Apply "storedIn" filter to search for listings with "Active" status
        query = query.whereEqualTo("storedIn", "Active");

        // Apply listing type filter if specified
        if (slt != null && !slt.equals("All Types")) {
            query = query.whereEqualTo("listingType", slt);
        }

        // Apply listing category filter if specified
        if (slc != null && !slc.equals("All Categories")) {
            query = query.whereEqualTo("listingCategory", slc);
        }

        // Step 3: Execute Listings query first to narrow results, then filter users based on location
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ListingWithUserDetails> listingsWithUserDetails = new ArrayList<>();
                List<Listing> initialResults = new ArrayList<>();

                // Step 1: Fetch listings along with their document IDs
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Listing listing = document.toObject(Listing.class);
                    String listingId = document.getId(); // Firestore's document ID
                    listing.setListingId(listingId); // Optionally set listingId in the Listing object (if you add it as a field)
                    initialResults.add(listing);
                }

                if (initialResults.isEmpty()) {
                    displayNoResultsFound();
                    return;
                }

                // Step 2: Filter initial results by location in UserDetails
                CollectionReference userDetailsRef = db.collection("UserDetails");
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

                userQuery.get().addOnCompleteListener(userTask -> {
                    if (userTask.isSuccessful()) {
                        List<String> userIds = new ArrayList<>();
                        for (QueryDocumentSnapshot userDoc : userTask.getResult()) {
                            userIds.add(userDoc.getId());
                        }

                        // Filter initial results by matching user IDs from UserDetails
                        List<Listing> finalResults = new ArrayList<>();
                        for (Listing listing : initialResults) {
                            if (userIds.contains(listing.getUserId())) {
                                finalResults.add(listing);
                            }
                        }

                        if (finalResults.isEmpty()) {
                            displayNoResultsFound();
                        } else {
                            fetchUserDetailsAndUpdateRecyclerView(finalResults);
                            searchrv.setVisibility(View.VISIBLE);
                            noResultsTextView.setVisibility(View.GONE);
                        }
                    } else {
                        displayNoResultsFound();
                        Log.d("SearchFragment", "No users found matching location filters.");
                    }
                });
            } else {
                Log.e("SearchFragment", "Error getting listings: ", task.getException());
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

    public void publicSearch() {

        filteritems.setVisibility(View.GONE);
        searchType.setVisibility(View.GONE);
        filterrg.setVisibility(View.GONE);
        noResultsTextView.setVisibility(View.GONE);
        searchBar.setVisibility(View.GONE);
        filteritems.setVisibility(View.GONE);
        filterDisp.setVisibility(View.GONE);
        constraintLayout.setVisibility(View.GONE);

        getSelectedCountry();

        // Get the current layout parameters of the RecyclerView
        ViewGroup.LayoutParams layoutParams = searchrv.getLayoutParams();

        // Set the height to match the parent
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // Apply the updated layout parameters
        searchrv.setLayoutParams(layoutParams);

        StartSearch(); // start by initially searching the listings in the user's country

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

    private void getSelectedCountry() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String fireBUserID = user.getUid();

        // Fetch the document where the userId matches fireBUserID
        db.collection("UserDetails")
                .document(fireBUserID) // Assuming userId is the document ID
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Access the locationMap and retrieve the country
                        Map<String, String> locationMap = (Map<String, String>) documentSnapshot.get("locationMap");
                        if (locationMap != null && locationMap.containsKey("country")) {
                            selectedCountry = locationMap.get("country");
                            // Use the selectedCountry as needed
                        } else {
                            Log.d("SelectedCountry", "Country field is missing in locationMap.");
                        }
                    } else {
                        Log.d("SelectedCountry", "No document found for userId: " + fireBUserID);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("SelectedCountry", "Error retrieving selectedCountry: " + e.getMessage(), e);
                });
    }



}
