package com.example.afinal.objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Ranking implements Serializable {

    String rankId, oldRankTopic, topicId, userId, userName, times, joinTime;
    int timeStudied, score;

    public Ranking() {
    }

    public Ranking(String rankId, String oldRankTopic, String topicId, String userId, String userName, String times, String joinTime, int timeStudied, int score) {
        this.rankId = rankId;
        this.oldRankTopic = oldRankTopic;
        this.topicId = topicId;
        this.userId = userId;
        this.userName = userName;
        this.times = times;
        this.joinTime = joinTime;
        this.timeStudied = timeStudied;
        this.score = score;
    }

    public String getRankId() {
        return rankId;
    }

    public void setRankId(String rankId) {
        this.rankId = rankId;
    }

    public String getOldRankTopic() {
        return oldRankTopic;
    }

    public void setOldRankTopic(String oldRankTopic) {
        this.oldRankTopic = oldRankTopic;
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

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(String joinTime) {
        this.joinTime = joinTime;
    }

    public int getTimeStudied() {
        return timeStudied;
    }

    public void setTimeStudied(int timeStudied) {
        this.timeStudied = timeStudied;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> rankInfo = new HashMap<>();
        rankInfo.put("rankId", rankId);
        rankInfo.put("topicId", topicId);
        rankInfo.put("oldRankTopicId", oldRankTopic);
        rankInfo.put("userId", userId);
        rankInfo.put("userName", userName);
        rankInfo.put("times", times);
        rankInfo.put("timeStudied", timeStudied);
        rankInfo.put("score", score);
        rankInfo.put("joinTime", joinTime);
        return rankInfo;
    }
}
