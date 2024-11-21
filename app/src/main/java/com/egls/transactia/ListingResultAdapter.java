package com.egls.transactia;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListingResultAdapter extends RecyclerView.Adapter<ListingResultAdapter.ViewHolder> {
    private List<ListingWithUserDetails> listingsWithUserDetails;

    FirebaseUser user;
    String fireBUserID;


    public ListingResultAdapter(List<ListingWithUserDetails> listingsWithUserDetails) {
        this.listingsWithUserDetails = listingsWithUserDetails;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        user = FirebaseAuth.getInstance().getCurrentUser();
        fireBUserID = user.getUid();

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listingresult_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListingWithUserDetails item = listingsWithUserDetails.get(position);

        Listing listing = item.getListing();
        UserDetails userDetails = item.getUserDetails();

        // Set timestamp
        if (listing.getCreatedTimestamp() != null) {
            long timestampMillis = listing.getCreatedTimestamp().getSeconds() * 1000; // Firestore Timestamp to milliseconds
            Date date = new Date(timestampMillis);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            String formattedDate = dateFormat.format(date);
            holder.timestamptx.setText(formattedDate);
        } else {
            holder.timestamptx.setText("No Timestamp available");
        }

        // Set user info
        holder.UserName.setText(userDetails.getName());
        holder.UserLoc.setText(userDetails.getLocation());

        // Set listing info
        holder.title_txt.setText(listing.getTitle());
        holder.ltype.setText(listing.getListingType());
        holder.description_txt.setText(listing.getListingDescription());
        holder.categ_txt.setText(listing.getListingCategory());

        // Set images (You may want to load images using a library like Glide or Picasso)
        if (userDetails.getImageUrl() != null && !userDetails.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(userDetails.getImageUrl()) // This should be the URL string from Firebase Storage
                    .into(holder.profileUser);
        } else {
            holder.profileUser.setImageResource(R.drawable.pfpdef);
        }

        if (listing.getListingImage() != null && !listing.getListingImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(listing.getListingImage()) // Ensure this is a valid URL string
                    .into(holder.listingImage);
        } else {
            holder.listingImage.setImageResource(R.drawable.addimage_profile);
        }


        // Handle CardView click to navigate to Request activity
        holder.cardView.setOnClickListener(v -> {

            String listingOwner = listing.getUserId();
            String listingId = item.getListing().getListingId(); // Get the document ID

            if(listingOwner.equals(fireBUserID)) {
                // If the search result is the user's own listing
                Intent intent = new Intent(holder.itemView.getContext(), MyNeeds.class);
                intent.putExtra("newListing", false);
                intent.putExtra("listingId", listingId);
                holder.itemView.getContext().startActivity(intent);

            } else {
                Intent intent = new Intent(holder.itemView.getContext(), Request.class);

                intent.putExtra("newRequest", true);

                // Add user details to the intent
                intent.putExtra("ownerUserId", listingOwner); // Add owner User ID
                intent.putExtra("listingId", listingId); // Pass the listing ID (document ID)

                // Start the Request activity
                holder.itemView.getContext().startActivity(intent);
            }



        });
    }


    @Override
    public int getItemCount() {
        return listingsWithUserDetails.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView timestamptx, UserName, UserLoc, title_txt, description_txt, categ_txt, ltype;
        ImageView profileUser, listingImage;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            ltype = itemView.findViewById(R.id.ltype);
            timestamptx = itemView.findViewById(R.id.timestamptx);
            UserName = itemView.findViewById(R.id.UserName);
            UserLoc = itemView.findViewById(R.id.UserLoc);
            title_txt = itemView.findViewById(R.id.title_txt);
            description_txt = itemView.findViewById(R.id.description_txt);
            categ_txt = itemView.findViewById(R.id.categ_txt);
            profileUser = itemView.findViewById(R.id.profileUser);
            listingImage = itemView.findViewById(R.id.listingImage);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
