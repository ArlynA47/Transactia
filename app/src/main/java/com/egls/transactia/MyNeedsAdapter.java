package com.egls.transactia;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyNeedsAdapter extends RecyclerView.Adapter<MyNeedsAdapter.MyViewHolder> {
    private Context context;
    private List<Listing> listings;
    private String fireBUserID;

    public MyNeedsAdapter(Context context, List<Listing> listings, String fireBUserID) {
        this.context = context;
        this.listings = listings;
        this.fireBUserID = fireBUserID;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, descriptionTxt, categTxt, timestamptx;
        ImageView listingImage, deleteImage;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.title_txt);
            descriptionTxt = itemView.findViewById(R.id.description_txt);
            categTxt = itemView.findViewById(R.id.categ_txt);
            listingImage = itemView.findViewById(R.id.listingImage);
            deleteImage = itemView.findViewById(R.id.delete_image);
            timestamptx = itemView.findViewById(R.id.titletx);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.need_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Listing listing = listings.get(position);
        holder.titleTxt.setText(listing.getTitle());
        holder.descriptionTxt.setText(limitWords(listing.getListingDescription(), 10));
        holder.categTxt.setText(listing.getListingCategory());

        // Set the timestamp, format it if necessary
        if (listing.getCreatedTimestamp() != null) {
            long timestampMillis = listing.getCreatedTimestamp().getSeconds() * 1000; // Firestore Timestamp to milliseconds
            Date date = new Date(timestampMillis);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            String formattedDate = dateFormat.format(date);
            holder.timestamptx.setText(formattedDate);
        } else {
            holder.timestamptx.setText("No Timestamp available");
        }


        // Load image using Glide
        if (listing.getListingImage() != null) {
            Glide.with(context).load(listing.getListingImage()).into(holder.listingImage);
        } else {
            holder.listingImage.setImageResource(R.drawable.addimage_profile); // Placeholder image
        }

        // Handle CardView click to navigate to MyNeeds activity
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MyNeeds.class);
            intent.putExtra("newListing", false);
            intent.putExtra("listingId", listing.getListingId());
            context.startActivity(intent);
        });

        // Apply animation
        Animation translateAnim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
        int delay = position * 150; // Adjust this value for the speed of the cascade
        translateAnim.setStartOffset(delay); // Add delay based on the position
        holder.cardView.startAnimation(translateAnim);

        // Handle delete button click
        holder.deleteImage.setOnClickListener(v -> {
            showArchiveConfirmationDialog(position);
        });

    }

    // Method to show delete confirmation dialog
    private void showArchiveConfirmationDialog(int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Listing listingToArchive = listings.get(position);
        String listingId = listingToArchive.getListingId();

        // Check if this listingId exists in the "inExchange" field of other listings
        db.collection("Listings")
                .whereEqualTo("inExchange", listingId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    boolean isInExchange = !querySnapshot.isEmpty();

                    // Create the alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirm Archive");

                    // Check if the listing is used in other listings' inExchange fields
                    if (isInExchange) {
                        builder.setMessage("This listing is referenced as 'In Exchange' by another listing. Are you sure you want to archive it? The reference will remain, but the listing will only be visible to you and the user associated with the referenced listing.");
                    } else {
                        builder.setMessage("Are you sure you want to archive this listing?");
                    }

                    builder.setPositiveButton("Archive", (dialog, which) -> {

                        // Get a reference to the Firestore instance and the specific document
                        db.collection("Listings").document(listingId)
                                .update("storedIn", "Archive")
                                .addOnSuccessListener(aVoid -> {
                                    // Optional: remove the item from the list if you don't want to show archived listings
                                    listings.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, listings.size());
                                    CustomToast.show(context, "Listing archived successfully.");
                                })
                                .addOnFailureListener(e ->
                                        CustomToast.show(context, "Failed to archive listing: " + e.getMessage())
                                );
                    });

                    builder.setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss(); // User canceled, do nothing
                    });

                    // Show the dialog
                    builder.create().show();
                })
                .addOnFailureListener(e -> {
                    CustomToast.show(context, "Error checking listings: " + e.getMessage());
                });
    }


    // Method to limit the number of words
    private String limitWords(String description, int maxWords) {
        String[] words = description.split("\\s+"); // Split the description into words
        if (words.length <= maxWords) {
            return description; // Return the full description if within limit
        }

        // Join the first maxWords words and return
        StringBuilder limitedDescription = new StringBuilder();
        for (int i = 0; i < maxWords; i++) {
            limitedDescription.append(words[i]).append(" ");
        }

        return limitedDescription.toString().trim()+"..."; // Remove trailing space and return
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }
}
