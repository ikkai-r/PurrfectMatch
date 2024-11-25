package com.example.purrfectmatch;

import java.io.Serializable;
import com.google.firebase.Timestamp;

public class ApplicationData implements Serializable {
    private String applicationId;
    private String catId;
    private String userId;
    private String reason;
    private String status;
    private Timestamp applicationDate;

    // Default constructor (required for Firestore deserialization)
    public ApplicationData() {}

    // Constructor with all fields
    public ApplicationData(String applicationId, String catId, String userId, String reason, String status, Timestamp applicationDate) {
        this.applicationId = applicationId;
        this.catId = catId;
        this.userId = userId;
        this.reason = reason;
        this.status = status;
        this.applicationDate = applicationDate;
    }

    // Getters and Setters
    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Timestamp applicationDate) {
        this.applicationDate = applicationDate;
    }
}

