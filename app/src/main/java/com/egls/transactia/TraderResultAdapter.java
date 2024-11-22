package com.egls.transactia;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

public class TraderResultAdapter extends RecyclerView.Adapter<TraderResultAdapter.TraderViewHolder> {
    private List<UserDetails> userList;

    public TraderResultAdapter(List<UserDetails> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public TraderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trader_row, parent, false);
        return new TraderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TraderViewHolder holder, int position) {
        UserDetails user = userList.get(position);

        holder.name.setText(user.getName());
        holder.location.setText(user.getLocation()); // Set location string

        String rateLabel = String.format(Locale.getDefault(), "%.1f | %d",
                user.getRatings(), user.getNumberOfRatings());

        holder.rateLabel.setText(rateLabel);

        // Load profile image if available
        if (user.getImageUrl() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(user.getImageUrl())
                    .into(holder.profileUser);
        } else {
            holder.profileUser.setImageResource(R.drawable.pfpdef); // Default image
        }

        double ratingUser =  user.getRatings();


        holder.star1.setImageResource(R.drawable.staruf);
        holder.star2.setImageResource(R.drawable.staruf);
        holder.star3.setImageResource(R.drawable.staruf);
        holder.star4.setImageResource(R.drawable.staruf);
        holder.star5.setImageResource(R.drawable.staruf);

        if(ratingUser==0) {

        }
        else if(ratingUser<2) {
            holder.star1.setImageResource(R.drawable.starf);
            if(ratingUser>1.00) {
                holder.star2.setImageResource(R.drawable.starhf);
            }
        } else if(ratingUser<3) {
            holder.star1.setImageResource(R.drawable.starf);
            holder.star2.setImageResource(R.drawable.starf);
            if(ratingUser>2.00) {
                holder.star3.setImageResource(R.drawable.starhf);
            }
        } else if(ratingUser<4) {
            holder.star1.setImageResource(R.drawable.starf);
            holder.star2.setImageResource(R.drawable.starf);
            holder.star3.setImageResource(R.drawable.starf);
            if(ratingUser>3.00) {
                holder.star4.setImageResource(R.drawable.starhf);
            }
        } else if(ratingUser<5) {
            holder.star1.setImageResource(R.drawable.starf);
            holder.star2.setImageResource(R.drawable.starf);
            holder.star3.setImageResource(R.drawable.starf);
            holder.star4.setImageResource(R.drawable.starf);
            if(ratingUser>4.00) {
                holder.star5.setImageResource(R.drawable.starhf);
            }
        } else if(ratingUser==5) {
            holder.star1.setImageResource(R.drawable.starf);
            holder.star2.setImageResource(R.drawable.starf);
            holder.star3.setImageResource(R.drawable.starf);
            holder.star4.setImageResource(R.drawable.starf);
            holder.star5.setImageResource(R.drawable.starf);
        }


        // Set onClickListener for the row
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Traderprofile.class);
            intent.putExtra("userId", user.getUserId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class TraderViewHolder extends RecyclerView.ViewHolder {
        ImageView profileUser;
        TextView location, name, rateLabel;
        ImageView star1, star2, star3, star4, star5;

        public TraderViewHolder(@NonNull View itemView) {
            super(itemView);
            profileUser = itemView.findViewById(R.id.profileUser);
            location = itemView.findViewById(R.id.UserLoc);
            name = itemView.findViewById(R.id.UserName);
            rateLabel = itemView.findViewById(R.id.rateLabel);
            star1 = itemView.findViewById(R.id.star1);
            star2 = itemView.findViewById(R.id.star2);
            star3 = itemView.findViewById(R.id.star3);
            star4 = itemView.findViewById(R.id.star4);
            star5 = itemView.findViewById(R.id.star5);
        }
    }
}

