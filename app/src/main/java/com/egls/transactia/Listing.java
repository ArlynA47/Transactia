package com.egls.transactia;

import com.google.firebase.Timestamp;

public class Listing {
    private String listingId; // Unique identifier for the listing
    private String title;
    private String listingDescription;
    private String listingCategory;
    private String listingImage; // URL of the image stored in Firestore
    private String listingType;
    private String listingValue;
    private String userId; // ID of the user who created the listing
    private String inExchange; // The ID of the listing in exchange, if applicable
    private Timestamp createdTimestamp;

    // Default constructor required for Firestore
    public Listing() {
    }

    // Parameterized constructor
    public Listing(String listingId, String title, String listingDescription, String listingCategory,
                   String listingImage, String listingType, String listingValue,
                   String userId, String inExchange, Timestamp createdTimestamp) {
        this.listingId = listingId;
        this.title = title;
        this.listingDescription = listingDescription;
        this.listingCategory = listingCategory;
        this.listingImage = listingImage;
        this.listingType = listingType;
        this.listingValue = listingValue;
        this.userId = userId;
        this.inExchange = inExchange; // Add this if you want to track exchanges
        this.createdTimestamp = createdTimestamp;
    }

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for the timestamp
    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getListingDescription() {
        return listingDescription;
    }

    public void setListingDescription(String listingDescription) {
        this.listingDescription = listingDescription;
    }

    public String getListingCategory() {
        return listingCategory;
    }

    public void setListingCategory(String listingCategory) {
        this.listingCategory = listingCategory;
    }

    public String getListingImage() {
        return listingImage;
    }

    public void setListingImage(String listingImage) {
        this.listingImage = listingImage;
    }

    public String getListingType() {
        return listingType;
    }

    public void setListingType(String listingType) {
        this.listingType = listingType;
    }

    public String getListingValue() {
        return listingValue;
    }

    public void setListingValue(String listingValue) {
        this.listingValue = listingValue;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInExchange() {
        return inExchange;
    }

    public void setInExchange(String inExchange) {
        this.inExchange = inExchange;
    }
}
