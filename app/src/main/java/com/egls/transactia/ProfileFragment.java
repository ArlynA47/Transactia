
package com.egls.transactia;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ProfileFragment extends Fragment {

    ImageView profileUser;
    TextView UserName, UserLoc, UserGender, UserAge, UserBio;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    UserDatabaseHelper dbHelper;

    String fireBUserID;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileUser = view.findViewById(R.id.profileUser);
        UserName = view.findViewById(R.id.UserName);
        UserLoc = view.findViewById(R.id.UserLoc);
        UserGender = view.findViewById(R.id.UserGender);
        UserAge = view.findViewById(R.id.UserAge);
        UserBio = view.findViewById(R.id.UserBio);

        dbHelper = new UserDatabaseHelper(getContext());
        fireBUserID = dbHelper.getUserId();

        loadUserDetails(); // Call method to load user details

        ImageView accsets = view.findViewById(R.id.accsets);
        accsets.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Account_Settings.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        });

        return view;
    }

    private void loadUserDetails() {
        db.collection("UserDetails").document(fireBUserID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String location = documentSnapshot.getString("location");
                        String sex = documentSnapshot.getString("sex");
                        String bio = documentSnapshot.getString("bio");
                        String birthdate = documentSnapshot.getString("birthdate");
                        String imageUrl = documentSnapshot.getString("imageUrl");

                        // Set data to TextViews
                        UserName.setText(name);
                        UserLoc.setText(location);
                        UserGender.setText(sex);
                        UserBio.setText(bio);

                        // Calculate and set age
                        if (birthdate != null) {
                            int age = calculateAge(birthdate);
                            UserAge.setText(String.valueOf(age));
                        }

                        // Load profile image
                        if (imageUrl != null) {
                            Glide.with(this).load(imageUrl).into(profileUser);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    e.printStackTrace();
                });

    }

    public int calculateAge(String birthdate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            // Parse the birthdate
            Date birthDate = dateFormat.parse(birthdate);
            Calendar birthDay = Calendar.getInstance();
            birthDay.setTime(birthDate);

            // Get current date
            Calendar today = Calendar.getInstance();

            // Calculate age
            int age = today.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);

            // Adjust age if today's date is before the birthday this year
            if (today.get(Calendar.DAY_OF_YEAR) < birthDay.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return age;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Return -1 if there's an error in parsing
        }
    }
}