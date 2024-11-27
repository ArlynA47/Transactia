
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    FirebaseUser user;

    ImageView profileUser, verified;
    TextView UserName, UserLoc, UserGender, UserAge, UserBio, dateJoined;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    UserDatabaseHelper dbHelper;

    String fireBUserID;

    ImageView star1, star2, star3, star4, star5;

    TextView rateLabel;

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
        verified = view.findViewById(R.id.verified);

        star1 =  view.findViewById(R.id.star1);
        star2 =  view.findViewById(R.id.star2);
        star3 =  view.findViewById(R.id.star3);
        star4 =  view.findViewById(R.id.star4);
        star5 =  view.findViewById(R.id.star5);

        dateJoined =  view.findViewById(R.id.dateJoined);

        rateLabel =  view.findViewById(R.id.rateLabel);

        user = FirebaseAuth.getInstance().getCurrentUser();
        fireBUserID = user.getUid();

        loadUserDetails(); // Call method to load user details

        ImageView accsets = view.findViewById(R.id.accsets);
        accsets.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Account_Settings.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        });

        return view;
    }

    private void setStars(double ratingUser) {

        star1.setImageResource(R.drawable.staruf);
        star2.setImageResource(R.drawable.staruf);
        star3.setImageResource(R.drawable.staruf);
        star4.setImageResource(R.drawable.staruf);
        star5.setImageResource(R.drawable.staruf);

        if(ratingUser==0) {

        }
        else if(ratingUser<2) {
            star1.setImageResource(R.drawable.starf);
            if(ratingUser>1.00) {
                star2.setImageResource(R.drawable.starhf);
            }
        } else if(ratingUser<3) {
            star1.setImageResource(R.drawable.starf);
            star2.setImageResource(R.drawable.starf);
            if(ratingUser>2.00) {
                star3.setImageResource(R.drawable.starhf);
            }
        } else if(ratingUser<4) {
            star1.setImageResource(R.drawable.starf);
            star2.setImageResource(R.drawable.starf);
            star3.setImageResource(R.drawable.starf);
            if(ratingUser>3.00) {
                star4.setImageResource(R.drawable.starhf);
            }
        } else if(ratingUser<5) {
            star1.setImageResource(R.drawable.starf);
            star2.setImageResource(R.drawable.starf);
            star3.setImageResource(R.drawable.starf);
            star4.setImageResource(R.drawable.starf);
            if(ratingUser>4.00) {
                star5.setImageResource(R.drawable.starhf);
            }
        } else if(ratingUser==5) {
            star1.setImageResource(R.drawable.starf);
            star2.setImageResource(R.drawable.starf);
            star3.setImageResource(R.drawable.starf);
            star4.setImageResource(R.drawable.starf);
            star5.setImageResource(R.drawable.starf);
        }
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
                        Timestamp dateJoinedStr = documentSnapshot.getTimestamp("dateJoined");
                        String status = documentSnapshot.getString("status");
                        Long numOfRatings = documentSnapshot.contains("numberofratings") ? documentSnapshot.getLong("numberofratings") : null;
                        Double ratings = documentSnapshot.contains("ratings") ? documentSnapshot.getDouble("ratings") : null;

                        // Set the timestamp, format it if necessary
                        if (dateJoinedStr != null) {
                            long timestampMillis = dateJoinedStr.getSeconds() * 1000; // Firestore Timestamp to milliseconds
                            Date date = new Date(timestampMillis);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                            String formattedDate = dateFormat.format(date);
                            dateJoined.setText("Date Joined: "+formattedDate);
                        } else {
                            dateJoined.setText("");
                        }

                        // Check for null
                        if (numOfRatings == null) {
                            numOfRatings = 0L; // Default value if needed
                        }

                        if (ratings == null) {
                            ratings = 0.0; // Default value if needed
                        }

                        setStars(ratings);

                        String rateLabelStr = String.format(Locale.getDefault(), "%.1f Stars | %d Ratings",
                                ratings, numOfRatings);

                        rateLabel.setText(rateLabelStr);

                        if(status.equals("Verified")) {
                            verified.setVisibility(View.VISIBLE);
                        } else {
                            verified.setVisibility(View.GONE);
                        }

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