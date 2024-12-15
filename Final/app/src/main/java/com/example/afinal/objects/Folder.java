package com.example.afinal.objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Folder implements Serializable {
    String folderName, folderId, userId, folderDescription, userName;

    public Folder() {
    }

    public Folder(String folderName, String folderId, String userId, String folderDescription, String userName) {
        this.folderName = folderName;
        this.folderId = folderId;
        this.userId = userId;
        this.folderDescription = folderDescription;
        this.userName = userName;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFolderDescription() {
        return folderDescription;
    }

    public void setFolderDescription(String folderDescription) {
        this.folderDescription = folderDescription;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> folderInfo = new HashMap<>();
        folderInfo.put("folderName", folderName);
        folderInfo.put("folderId", folderId);
        folderInfo.put("userId", userId);
        folderInfo.put("userName", userName);
        folderInfo.put("description", folderDescription);
        return folderInfo;
    }
}
