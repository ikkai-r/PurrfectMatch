package com.example.purrfectmatch;

public class SwipeData {

    int age, weight, adoptionFee, neuteredImage, vaccinationImage, litterTrainedImage;
    int[] catImages;
    char sex;
    String foodPreference, bio, temperament, breed, medicalHistory, name, birthday, contactInformation;

    public SwipeData(int age, int weight, int adoptionFee,  int neuteredImage, int vaccinationImage, int litterTrainedImage,
                     int[] catImages, char sex, String foodPreference, String bio, String temperament, String breed, String medicalHistory, String name, String birthday, String contactInformation) {
        this.age = age;
        this.weight = weight;
        this.adoptionFee = adoptionFee;
        this.contactInformation = contactInformation;
        this.neuteredImage = neuteredImage;
        this.vaccinationImage = vaccinationImage;
        this.litterTrainedImage = litterTrainedImage;
        this.catImages = catImages;
        this.sex = sex;
        this.foodPreference = foodPreference;
        this.bio = bio;
        this.temperament = temperament;
        this.breed = breed;
        this.medicalHistory = medicalHistory;
        this.name = name;
        this.birthday = birthday;
    }
}
