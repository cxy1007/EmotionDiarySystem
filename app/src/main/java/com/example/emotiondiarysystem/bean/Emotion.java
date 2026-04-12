package com.example.emotiondiarysystem.bean;

public class Emotion {
    // 情感记录ID
    private int emotionId;
    // 用户ID
    private int userId;
    // 情感分数
    private float emotionScore;
    // 统计时间
    private String statisticsTime;

    public Emotion() {}

    public Emotion(int emotionId, int userId, float emotionScore, String statisticsTime) {
        this.emotionId = emotionId;
        this.userId = userId;
        this.emotionScore = emotionScore;
        this.statisticsTime = statisticsTime;
    }

    public int getEmotionId() { return emotionId; }
    public void setEmotionId(int emotionId) { this.emotionId = emotionId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public float getEmotionScore() { return emotionScore; }
    public void setEmotionScore(float emotionScore) { this.emotionScore = emotionScore; }
    public String getStatisticsTime() { return statisticsTime; }
    public void setStatisticsTime(String statisticsTime) { this.statisticsTime = statisticsTime; }
}