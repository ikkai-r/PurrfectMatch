package com.example.purrfectmatch;

public class SwipeData {

    int age, weight, adoptionFee;
    int[] catImages;
    char sex;
    String foodPreference, bio, temperament, breed, name, contactInformation, compatibleWith, catId;
    boolean isBookmarked, isNeutered;

    public SwipeData(int age, int weight, int adoptionFee,  int neuteredImage, int vaccinationImage, int litterTrainedImage,
                     int[] catImages, char sex, String foodPreference, String bio, String temperament, String breed,
                     String name, String contactInformation, String catId, String compatibleWith, boolean isNeutered) {
        this.age = age;
        this.weight = weight;
        this.adoptionFee = adoptionFee;
        this.contactInformation = contactInformation;
        this.catImages = catImages;
        this.sex = sex;
        this.foodPreference = foodPreference;
        this.bio = bio;
        this.temperament = temperament;
        this.breed = breed;
//        this.medicalHistory = medicalHistory;
        this.name = name;
//        this.birthday = birthday;
        this.isBookmarked = false;
        this.catId = catId;
        this.compatibleWith = compatibleWith;
        this.isNeutered = isNeutered;
    }

    public String getCatId() {
        return catId;
    }


    public int getAge() {
        return age;
    }

    
    public int getAdoptionFee() {
        return adoptionFee;
    }

    
    public char getSexS() {
        return sex;
    }

    public void setBookmarked(Boolean isBookmarked) {
        this.isBookmarked = isBookmarked;
    }
    


}
