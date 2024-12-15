package com.example.afinal.objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {
    private String name, email, userId;

    List<String> topicStudyId;

    public User() {
    }
    public User(String name, String email, String userId, List<String> topicStudyId) {
        this.name = name;
        this.email = email;
        this.userId = userId;
        this.topicStudyId = topicStudyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getTopicStudyId() {
        return topicStudyId;
    }

    public void setTopicStudyId(List<String> topicStudyId) {
        this.topicStudyId = topicStudyId;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", name);
        userInfo.put("email", email);
        userInfo.put("userId", userId);
        userInfo.put("topicStudyId", topicStudyId);
        return userInfo;
    }
}
