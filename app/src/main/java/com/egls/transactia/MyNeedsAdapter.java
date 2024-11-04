package com.egls.transactia;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

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
        TextView titleTxt, descriptionTxt, categTxt;
        ImageView listingImage, deleteImage;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.title_txt);
            descriptionTxt = itemView.findViewById(R.id.description_txt);
            categTxt = itemView.findViewById(R.id.categ_txt);
            listingImage = itemView.findViewById(R.id.listingImage);
            deleteImage = itemView.findViewById(R.id.delete_image); // Add delete image
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
            intent.putExtra("fromAdapter", true);
            context.startActivity(intent);
        });

        // Handle delete button click
        holder.deleteImage.setOnClickListener(v -> {
            showDeleteConfirmationDialog(position);
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

        return limitedDescription.toString().trim(); // Remove trailing space and return
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    // Method to show delete confirmation dialog
    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this listing?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteListing(position);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss(); // User canceled, do nothing
                })
                .create()
                .show();
    }

    // Method to delete listing
    private void deleteListing(int position) {
        // Remove the listing from the list and notify the adapter
        listings.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listings.size());

        // Show a toast message for confirmation
        Toast.makeText(context, "Listing deleted successfully.", Toast.LENGTH_SHORT).show();

        // Here you might want to perform actual deletion from the database or backend
        // Example: myDatabase.deleteListingById(listings.get(position).getListingId());
    }
}
