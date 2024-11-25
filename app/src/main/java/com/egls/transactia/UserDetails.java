package com.egls.transactia;

import com.google.firebase.firestore.FieldValue;

import java.util.Map;

public class UserDetails {
    private String name;
    private String sex;
    private String bio;
    private String contactInfo;
    private String birthdate;
    private String location;  // Keep the original location string
    private Map<String, String> locationMap;  // New location map for hierarchical data
    private String imageUrl;
    private double ratings; // New field
    private long numberofratings;
    private String userId;// New field
    private Object dateJoined;  // Use Object to handle FieldValue and Timestamp interchangeably



    // Default constructor for Firestore serialization
    public UserDetails() {
    }

    // Constructor with all fields, including the new locationMap
    public UserDetails( String name, String sex, String bio, String contactInfo, String birthdate,
                       String location, Map<String, String> locationMap, String imageUrl, double ratings, long numberofratings, String userId, Object dateJoined) {
        this.name = name;
        this.sex = sex;
        this.bio = bio;
        this.contactInfo = contactInfo;
        this.birthdate = birthdate;
        this.location = location;
        this.locationMap = locationMap;
        this.imageUrl = imageUrl;
        this.ratings = ratings;
        this.numberofratings = numberofratings;
        this.userId = userId;
        this.dateJoined = dateJoined;
    }

    // Constructor without ratings and number of ratings (for compatibility)
    public UserDetails(String name, String sex, String bio, String contactInfo, String birthdate,
                       String location, Map<String, String> locationMap, String profileImageUrl) {
        this(name, sex, bio, contactInfo, birthdate, location, locationMap, profileImageUrl, 0.0, 0, "", FieldValue.serverTimestamp());
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Map<String, String> getLocationMap() {
        return locationMap;
    }

    public void setLocationMap(Map<String, String> locationMap) {
        this.locationMap = locationMap;
    }

    // Getter and setter for imageUrl
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getRatings() {
        return ratings;
    }

    public void setRatings(double ratings) {
        this.ratings = ratings;
    }

    public long getNumberOfRatings() {
        return numberofratings;
    }

    public void setNumberOfRatings(long numberOfRatings) {
        this.numberofratings = numberOfRatings;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getdateJoined() {
        return userId;
    }

    public void setdateJoined(Object dateJoined) {
        this.dateJoined = dateJoined;
    }
}
