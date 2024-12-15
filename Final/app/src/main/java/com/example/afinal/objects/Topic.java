package com.example.afinal.objects;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Topic implements Serializable {
    String topicName, topicId, userId, userName, userIdStudy, oldTopicId;

    List<String> folderId;
    boolean visible;

    public Topic() {
    }

    public Topic(String topicName, String topicId, String userId, String userName, String userIdStudy, String oldTopicId, List<String> folderId, boolean visible) {
        this.topicName = topicName;
        this.topicId = topicId;
        this.userId = userId;
        this.userName = userName;
        this.userIdStudy = userIdStudy;
        this.oldTopicId = oldTopicId;
        this.folderId = folderId;
        this.visible = visible;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIdStudy() {
        return userIdStudy;
    }

    public void setUserIdStudy(String userIdStudy) {
        this.userIdStudy = userIdStudy;
    }

    public String getOldTopicId() {
        return oldTopicId;
    }

    public void setOldTopicId(String oldTopicId) {
        this.oldTopicId = oldTopicId;
    }

    public List<String> getFolderId() {
        return folderId;
    }

    public void setFolderId(List<String> folderId) {
        this.folderId = folderId;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> topicInfo = new HashMap<>();
        topicInfo.put("topicName", topicName);
        topicInfo.put("userName", userName);
        topicInfo.put("topicId", topicId);
        topicInfo.put("userId", userId);
        topicInfo.put("userIdStudy", userIdStudy);
        topicInfo.put("oldTopicId", oldTopicId);
        topicInfo.put("folderId", folderId);
        topicInfo.put("visible", visible);

        return topicInfo;
    }
}
