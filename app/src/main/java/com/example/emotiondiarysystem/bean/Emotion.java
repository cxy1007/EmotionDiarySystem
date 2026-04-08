package com.example.emotiondiarysystem.bean;

/**
 * 情感实体类
 */
public class Emotion {

    /**
     * 情感id
     */
    private int emotionId;

    /**
     * 用户id
     */
    private int userId;

    /**
     * 情感分数
     */
    private float emotionScore;

    /**
     * 统计时间
     */
    private String statisticsTime;

    /**
     * 无参构造方法
     */
    public Emotion() {
    }

    /**
     * 全参构造方法
     *
     * @param emotionId 情感id
     * @param userId 用户id
     * @param emotionScore 情感分数
     * @param statisticsTime 统计时间
     */
    public Emotion(int emotionId, int userId, float emotionScore, String statisticsTime) {
        this.emotionId = emotionId;
        this.userId = userId;
        this.emotionScore = emotionScore;
        this.statisticsTime = statisticsTime;
    }

    public int getEmotionId() {
        return emotionId;
    }

    public void setEmotionId(int emotionId) {
        this.emotionId = emotionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public float getEmotionScore() {
        return emotionScore;
    }

    public void setEmotionScore(float emotionScore) {
        this.emotionScore = emotionScore;
    }

    public String getStatisticsTime() {
        return statisticsTime;
    }

    public void setStatisticsTime(String statisticsTime) {
        this.statisticsTime = statisticsTime;
    }
}
