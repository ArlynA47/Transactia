package com.egls.transactia;

import com.google.firebase.firestore.FieldValue;

import java.util.Map;

public class UserDetails {
    private String name;
    private String sex;
    private String bio;
    private String contactInfo;
    private String birthdate;
    private String location;  // Original location string
    private Map<String, String> locationMap;  // Hierarchical location map
    private String imageUrl;
    private String status;
    private double ratings;  // Ratings field
    private long numberofratings;  // Number of ratings
    private String userId;  // User ID
    private Object dateJoined;  // Field for FieldValue or Timestamp

    // Default constructor for Firestore serialization
    public UserDetails() {}

    // Full constructor with all fields
    public UserDetails(String name, String sex, String bio, String contactInfo, String birthdate,
                       String location, Map<String, String> locationMap, String imageUrl, String status, double ratings,
                       long numberofratings,Object dateJoined, String userId) {
        this.name = name;
        this.sex = sex;
        this.bio = bio;
        this.contactInfo = contactInfo;
        this.birthdate = birthdate;
        this.location = location;
        this.locationMap = locationMap;
        this.imageUrl = imageUrl;
        this.status = status;
        this.ratings = ratings;
        this.numberofratings = numberofratings;
        this.userId = userId;
        this.dateJoined = dateJoined;
    }

    // Compatibility constructor without ratings and number of ratings
    public UserDetails(String name, String sex, String bio, String contactInfo, String birthdate,
                       String location, Map<String, String> locationMap, String profileImageUrl, String status) {
        this(name, sex, bio, contactInfo, birthdate, location, locationMap, profileImageUrl, status, 0.0, 0,FieldValue.serverTimestamp(), "");
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Object getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(Object dateJoined) {
        this.dateJoined = dateJoined;
    }
}
