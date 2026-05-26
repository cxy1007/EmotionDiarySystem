package com.example.emotiondiarysystem.bean;

public class ChatMessage {
    public static final int TYPE_SYSTEM = 0;
    public static final int TYPE_USER = 1;
    public static final int TYPE_AI = 2;

    private int type;
    private String content;
    private boolean selected;

    public ChatMessage() {
        // 无参构造函数，供 FastJSON 反序列化使用
    }

    public ChatMessage(int type, String content) {
        this.type = type;
        this.content = content;
        this.selected = false;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
