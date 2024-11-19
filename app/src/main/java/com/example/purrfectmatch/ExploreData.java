package com.example.purrfectmatch;

public class ExploreData {
    private int imageResId;
    private String name;
    private String age;
    private String sex;
    private String breed;
    private boolean isNeutered;

    public ExploreData(int imageResId, String name, String age, String sex, String breed, boolean isNeutered) {
        this.imageResId = imageResId;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.breed = breed;
        this.isNeutered = isNeutered;
    }

    // Getters and Setters for each property
    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

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
}

