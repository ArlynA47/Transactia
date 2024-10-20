package com.egls.transactia;

public class UserDetails {
    private String name;
    private String sex;
    private String bio;
    private String contactInfo;
    private String birthdate;
    private String location;

    // Default constructor for Firestore serialization
    public UserDetails() {
    }

    // Constructor with all fields
    public UserDetails(String name, String sex, String bio, String contactInfo, String birthdate, String location) {
        this.name = name;
        this.sex = sex;
        this.bio = bio;
        this.contactInfo = contactInfo;
        this.birthdate = birthdate;
        this.location = location;
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
}
