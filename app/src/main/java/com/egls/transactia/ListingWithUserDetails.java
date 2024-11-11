package com.egls.transactia;

public class ListingWithUserDetails {
    private Listing listing;
    private UserDetails userDetails;

    public ListingWithUserDetails(Listing listing, UserDetails userDetails) {
        this.listing = listing;
        this.userDetails = userDetails;
    }

    public Listing getListing() {
        return listing;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }
}

