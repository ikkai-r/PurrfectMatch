package com.example.purrfectmatch;

import java.io.Serializable;
import com.google.firebase.Timestamp;

public class ApplicationData implements Serializable {
    private String applicationId;
    private String catId, userId, reason, status, feedback, acknowledged;
    private String startDate, endDate, startTime, endTime, finalDate, finalTime;
    private Timestamp applicationDate;

    public ApplicationData() {}

    public ApplicationData(String applicationId, String catId, String userId, String reason,
                           String acknowledged, String status, String feedback, Timestamp applicationDate,
                           String startDate, String endDate, String startTime, String endTime,
                           String finalDate, String finalTime) {
        this.applicationId = applicationId;
        this.catId = catId;
        this.userId = userId;
        this.reason = reason;
        this.status = status;
        this.applicationDate = applicationDate;
        this.feedback = feedback;
        this.acknowledged = acknowledged;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.finalDate = finalDate;
        this.finalTime = finalTime;
    }

    public String getApplicationId() { return applicationId; }

    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getCatId() { return catId; }

    public void setCatId(String catId) { this.catId = catId; }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getReason() { return reason; }

    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getFeedback() { return feedback; }

    public void setFeedback(String feedback) { this.feedback = feedback; }

    public Timestamp getApplicationDate() { return applicationDate; }

    public void setApplicationDate(Timestamp applicationDate) { this.applicationDate = applicationDate; }

    public String getStartDate() { return startDate; }

    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }

    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getStartTime() { return startTime; }

    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }

    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getFinalDate() { return finalDate; }

    public void setFinalDate(String finalDate) { this.finalDate = finalDate; }

    public String getFinalTime() { return finalTime; }

    public void setFinalTime(String finalTime) { this.finalTime = finalTime; }
}
