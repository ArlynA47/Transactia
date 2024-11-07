package com.egls.transactia;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Archive extends AppCompatActivity {

    String fireBUserID;
    UserDatabaseHelper dbHelper;

    private RecyclerView needrv, offerrv;
    private MyNeedArchiveAdapter adapter1;
    private MyOfferArchiveAdapter adapter2;
    private List<Listing> needsListings = new ArrayList<>();
    private List<Listing> offersListings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_archive);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

            dbHelper = new UserDatabaseHelper(this);
            fireBUserID = dbHelper.getUserId();

            needrv = findViewById(R.id.needrv);
            offerrv= findViewById(R.id.offerrv);


            showMyNeeds();
            showMyOffers();

    }

    public void showMyNeeds() {
        // Only set the adapter if it's null
        if (adapter1 == null) {
            adapter1 = new MyNeedArchiveAdapter(this, needsListings, fireBUserID);
            needrv.setLayoutManager(new LinearLayoutManager(this));
            needrv.setAdapter(adapter1);
        }
        needrv.setVisibility(View.VISIBLE); // Make RecyclerView visible
        loadNeed(); // Load "Offer" listings
    }

    public void showMyOffers() {
        // Only set the adapter if it's null
        if (adapter2 == null) {
            adapter2 = new MyOfferArchiveAdapter(this, offersListings, fireBUserID);
            offerrv.setLayoutManager(new LinearLayoutManager(this));
            offerrv.setAdapter(adapter2);
        }
        offerrv.setVisibility(View.VISIBLE); // Make RecyclerView visible
        loadOffer();
    }

    private void loadNeed() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Listings")
                .whereEqualTo("listingType", "Need")
                .whereEqualTo("storedIn", "Archive")
                .whereEqualTo("userId", fireBUserID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    needsListings.clear(); // Clear existing listings
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Listing listing = doc.toObject(Listing.class);
                        listing.setListingId(doc.getId());
                        needsListings.add(listing);
                    }
                    adapter1.notifyDataSetChanged(); // Notify the adapter of data changes
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error loading listings", e));
    }

    private void loadOffer() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Listings")
                .whereEqualTo("listingType", "Offer")
                .whereEqualTo("storedIn", "Archive")
                .whereEqualTo("userId", fireBUserID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    offersListings.clear(); // Clear existing listings
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Listing listing = doc.toObject(Listing.class);
                        listing.setListingId(doc.getId());
                        offersListings.add(listing);
                    }
                    adapter2.notifyDataSetChanged(); // Notify the adapter of data changes
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error loading listings", e));
    }
}