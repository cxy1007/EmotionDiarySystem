package com.example.emotiondiarysystem.bean;

public class Diary {
    // 日记ID
    private int diaryId;
    // 用户ID（关联是谁写的）
    private int userId;
    // 日记内容
    private String content;
    // 创建时间
    private String createTime;
    // 情感类型
    private String emotionType;
    // 是否删除（0：未删除，1：已删除）
    private int isDeleted;

    public Diary() {}

    public Diary(int diaryId, int userId, String content, String createTime, String emotionType) {
        this.diaryId = diaryId;
        this.userId = userId;
        this.content = content;
        this.createTime = createTime;
        this.emotionType = emotionType;
        this.isDeleted = 0;
    }

    public Diary(int diaryId, int userId, String content, String createTime, String emotionType, int isDeleted) {
        this.diaryId = diaryId;
        this.userId = userId;
        this.content = content;
        this.createTime = createTime;
        this.emotionType = emotionType;
        this.isDeleted = isDeleted;
    }

    public int getDiaryId() { return diaryId; }
    public void setDiaryId(int diaryId) { this.diaryId = diaryId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public String getEmotionType() { return emotionType; }
    public void setEmotionType(String emotionType) { this.emotionType = emotionType; }
    public int getIsDeleted() { return isDeleted; }
    public void setIsDeleted(int isDeleted) { this.isDeleted = isDeleted; }
    public boolean isDeleted() { return isDeleted == 1; }
}