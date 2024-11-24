package com.example.purrfectmatch;

public class User {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String country;
    private String region;
    private String city;
    private String userType;
    private int age;
    private String gender;
    private String householdMembers;
    private String otherPets;
    private String preferences1;
    private String preferences2;
    private String bio;
    private String profileimg;

    // Constructor, getters, and setters
    public User(String username, String firstName, String lastName, String email, String phoneNumber, String country,
                String region, String city, int age, String gender, String householdMembers,
                String otherPets, String preferences1, String preferences2, String userType,
                String bio, String profileimg) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.region = region;
        this.city = city;
        this.age = age;
        this.gender = gender;
        this.householdMembers = householdMembers;
        this.otherPets = otherPets;
        this.preferences1 = preferences1;
        this.preferences2 = preferences2;
        this.userType = userType;
        this.bio = bio;
        this.profileimg = profileimg;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHouseholdMembers() {
        return householdMembers;
    }

    public void setHouseholdMembers(String householdMembers) {
        this.householdMembers = householdMembers;
    }

    public String getOtherPets() {
        return otherPets;
    }

    public void setOtherPets(String otherPets) {
        this.otherPets = otherPets;
    }

    public String getPreferences1() {
        return preferences1;
    }

    public void setPreferences1(String preferences1) {
        this.preferences1 = preferences1;
    }

    public String getPreferences2() {
        return preferences2;
    }

    public void setPreferences2(String preferences2) {
        this.preferences2 = preferences2;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfileimg() {
        return profileimg;
    }

    public void setProfileimg(String profileimg) {
        this.profileimg = profileimg;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
