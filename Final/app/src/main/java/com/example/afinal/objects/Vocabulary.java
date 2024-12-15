package com.example.afinal.objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Vocabulary implements Serializable {

    String topicId, vocabId, vocabWord, vocabMeaning, status;
    boolean star;

    public Vocabulary() {
    }

    public Vocabulary(String topicId, String vocabId, String vocabWord, String vocabMeaning, String status, boolean star) {
        this.topicId = topicId;
        this.vocabId = vocabId;
        this.vocabWord = vocabWord;
        this.vocabMeaning = vocabMeaning;
        this.status = status;
        this.star = star;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getVocabId() {
        return vocabId;
    }

    public void setVocabId(String vocabId) {
        this.vocabId = vocabId;
    }

    public String getVocabWord() {
        return vocabWord;
    }

    public void setVocabWord(String vocabWord) {
        this.vocabWord = vocabWord;
    }

    public String getVocabMeaning() {
        return vocabMeaning;
    }

    public void setVocabMeaning(String vocabMeaning) {
        this.vocabMeaning = vocabMeaning;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isStar() {
        return star;
    }

    public void setStar(boolean star) {
        this.star = star;
    }
    public Map<String, Object> toMap() {
        Map<String, Object> vocabInfo = new HashMap<>();
        vocabInfo.put("topicId", topicId);
        vocabInfo.put("vocabId", vocabId);
        vocabInfo.put("vocabWord", vocabWord);
        vocabInfo.put("vocabMeaning", vocabMeaning);
        vocabInfo.put("status", status);
        vocabInfo.put("star", star);
        return vocabInfo;
    }
}
