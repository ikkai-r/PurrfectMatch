package com.example.purrfectmatch;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

@IgnoreExtraProperties
public class Cat implements Serializable {

    private String name;
    private int age;
    private int weight;
    private int adoptionFee;
    private int[] catPics;  // List of cat picture resources, replace with URLs if using Firebase Storage
    private String sex;
    private String foodPreference;
    private String description;
    private String temperament1;
    private String temperament2;
    private String breed;
    private String compatibleWith;
//    private String medicalHistory;
    private String ownerName;
    private String birthday;
    private String contactNumber;
    private boolean isAdopted;
    private List<String> pendingApplicationsId;

    // Default constructor required for calls to DataSnapshot.getValue(Cat.class)
    public Cat() {}

    public Cat(String name, int age, int weight, int adoptionFee, int[] catPics, String sex, String foodPreference, String description,
               String temperament1, String temperament2, String breed, String compatibleWith, String ownerName, String birthday,
               String contactNumber, boolean isAdopted, List<String> pendingApplicationsId) {
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.adoptionFee = adoptionFee;
        this.catPics = catPics;
        this.sex = sex;
        this.foodPreference = foodPreference;
        this.description = description;
        this.temperament1 = temperament1;
        this.temperament2 = temperament2;
        this.breed = breed;
        this.compatibleWith = compatibleWith;
        this.ownerName = ownerName;
        this.birthday = birthday;
        this.contactNumber = contactNumber;
        this.pendingApplicationsId = pendingApplicationsId;
        this.isAdopted = isAdopted;
    }

    public List<String> getPendingApplications() {
        return pendingApplicationsId;
    }

    public void setPendingApplications(List<String> pendingApplicationsId) {
        this.pendingApplicationsId = pendingApplicationsId;
    }

    public String getTemperament1() {
        return temperament1;
    }

    public void setTemperament1(String temperament1) {
        this.temperament1 = temperament1;
    }

    public String getTemperament2() {
        return temperament2;
    }

    public void setTemperament2(String temperament2) {
        this.temperament2 = temperament2;
    }

    // Getters and Setters
    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getAdoptionFee() {
        return adoptionFee;
    }

    public void setAdoptionFee(int adoptionFee) {
        this.adoptionFee = adoptionFee;
    }

    public int[] getCatPics() {
        return catPics;
    }

    public void setCatPics(int[] catPics) {
        this.catPics = catPics;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getFoodPreference() {
        return foodPreference;
    }

    public void setFoodPreference(String foodPreference) {
        this.foodPreference = foodPreference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getCompatibleWith() {
        return compatibleWith;
    }

    public void setCompatibleWith(String compatibleWith) {
        this.compatibleWith = compatibleWith;
    }

//    public String getMedicalHistory() {
//        return medicalHistory;
//    }
//
//    public void setMedicalHistory(String medicalHistory) {
//        this.medicalHistory = medicalHistory;
//    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public boolean isAdopted() {
        return isAdopted;
    }

    public void setAdopted(boolean adopted) {
        isAdopted = adopted;
    }
}
