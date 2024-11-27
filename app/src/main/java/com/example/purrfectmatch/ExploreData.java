package com.example.purrfectmatch;

public class ExploreData {
    private int age, adoptionFee;
    private String catImage, name, sex, breed, id;
    private boolean isNeutered;

    public ExploreData(){}

    public ExploreData(String id, String catImage, String name, int age, String sex, String breed,
                       boolean isNeutered, int adoptionFee) {
        this.id = id;
        this.catImage = catImage;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.breed = breed;
        this.isNeutered = isNeutered;
        this.adoptionFee = adoptionFee;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getCatImage() {
        return catImage;
    }

    public void setCatImage(String imageResId) {
        this.catImage = imageResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) { this.age = age; }

    public String getSex() { return sex; }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public boolean getIsNeutered() {
        return isNeutered;
    }

    public void setIsNeutered(boolean isNeutered) {
        this.isNeutered = isNeutered;
    }

    public int getAdoptionFee() { return adoptionFee; }

    public void setAdoptionFee(int adoptionFee) { this.adoptionFee = adoptionFee; }
}