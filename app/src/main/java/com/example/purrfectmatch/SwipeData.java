package com.example.purrfectmatch;

public class SwipeData {

    int age, weight, adoptionFee;
    char sex;

    String foodPreference, bio, temperament, breed, name, compatibleWith, catId, catImage, temperament2;
    boolean isBookmarked, isNeutered;

    public SwipeData(int age, int weight, int adoptionFee,  int neuteredImage, int vaccinationImage, int litterTrainedImage,
                     String catImage, char sex, String foodPreference, String bio, String temperament, String temperament2, String breed,
                     String name, String catId, String compatibleWith, boolean isNeutered) {
        this.age = age;
        this.weight = weight;
        this.adoptionFee = adoptionFee;
        this.catImage = catImage;
        this.sex = sex;
        this.foodPreference = foodPreference;
        this.bio = bio;
        this.temperament = temperament;
        this.temperament2 = temperament2;
        this.breed = breed;
        this.name = name;
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
