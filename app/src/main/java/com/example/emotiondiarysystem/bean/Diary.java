package com.example.emotiondiarysystem.bean;

/**
 * 日记实体类
 */
public class Diary {

    /**
     * 日记id
     */
    private int diaryId;

    /**
     * 用户id
     */
    private int userId;

    /**
     * 日记内容
     */
    private String content;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 情感类型
     */
    private String emotionType;

    /**
     * 无参构造方法
     */
    public Diary() {
    }

    /**
     * 全参构造方法
     *
     * @param diaryId 日记id
     * @param userId 用户id
     * @param content 日记内容
     * @param createTime 创建时间
     * @param emotionType 情感类型
     */
    public Diary(int diaryId, int userId, String content, String createTime, String emotionType) {
        this.diaryId = diaryId;
        this.userId = userId;
        this.content = content;
        this.createTime = createTime;
        this.emotionType = emotionType;
    }

    public int getDiaryId() {
        return diaryId;
    }

    public void setDiaryId(int diaryId) {
        this.diaryId = diaryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEmotionType() {
        return emotionType;
    }

    public void setEmotionType(String emotionType) {
        this.emotionType = emotionType;
    }
}
