package com.egls.transactia;

import android.net.Uri;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class BackendTest {


    // For testing use details update
    public void TestUpdate(FirebaseUser currUser) {
        if (currUser == null || currUser.getUid() == null) {
            //Toast.makeText(this, "User ID is null, unable to update details", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = "Bumble Boots";
        String sex = "Female";
        String bio = "This is bambam's bio";
        String contactInfo = "09508629480";
        String birthdate = "13/01/2000";
        String location = "Limay, Bataan, Central Luzon, Philippines";

        // Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a map of the fields to update
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("sex", sex);
        updates.put("bio", bio);
        updates.put("contactInfo", contactInfo);
        updates.put("birthdate", birthdate);
        updates.put("location", location);

        // Update the user details in Firestore
        db.collection("UserDetails")
                .document(currUser.getUid())
                .set(updates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    //Toast.makeText(this, "User details updated successfully.", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    //Toast.makeText(this, "Error updating user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    public void createListing(FirebaseUser currUser) {


        String title = "This is a listing 1 title";
        String listingType = "Need";
        String listingCategory = "Item";
        Uri listingImageUri = null;
        String listingDescription = "this is the listing 1 description";
        double listingValue = 2000;
        String inExchangeId = "QKvhapxRuqmyVgIimoQQ";

        if (currUser == null || currUser.getUid() == null) {
            //Toast.makeText(this, "User ID is null, unable to create listing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a unique ID for the listing
        String listingId = db.collection("Listings").document().getId();

        // Create a map of the listing fields
        Map<String, Object> listingData = new HashMap<>();
        listingData.put("title", title);
        listingData.put("listingType", listingType);
        listingData.put("listingCategory", listingCategory);
        listingData.put("listingDescription", listingDescription);
        listingData.put("listingValue", listingValue);
        listingData.put("inExchange", inExchangeId);
        listingData.put("userId", currUser.getUid()); // Store the user ID in the document

        // Handle image upload if there is a listing image
        if (listingImageUri != null) {
            /*StorageReference storageRef = storage.getReference().child("listing_images/" + listingId + ".jpg");

            // Upload the listing image to Firebase Storage
            storageRef.putFile(listingImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the image's download URL
                        storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            // Add the image URL to the listing data
                            listingData.put("listingImage", downloadUri.toString());

                            // Save the listing data to Firestore
                            saveListingToFirestore(db, listingId, listingData);
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, "Error getting listing image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error uploading listing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

             */
        } else {
            // If no image is provided, set the image URL to null
            listingData.put("listingImage", null);

            // Save the listing data to Firestore without image URL
            saveListingToFirestore(db, listingId, listingData);
        }
    }

    private void saveListingToFirestore(FirebaseFirestore db, String listingId, Map<String, Object> listingData) {
        db.collection("Listings")
                .document(listingId)
                .set(listingData)
                .addOnSuccessListener(aVoid -> {
                    //Toast.makeText(this, "Listing created successfully.", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    //Toast.makeText(this, "Error creating listing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }




}
