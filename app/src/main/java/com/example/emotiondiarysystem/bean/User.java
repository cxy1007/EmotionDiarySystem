package com.example.emotiondiarysystem.bean;

public class User {
    // 用户ID（主键，唯一）
    private int userId;
    // 账号（登录用）
    private String account;
    // 密码
    private String password;
    // 昵称（展示用）
    private String nickname;
    // 头像路径
    private String avatar;
    // 创建时间
    private String createTime;

    public User() {}

    public User(int userId, String account, String password, String nickname, String avatar, String createTime) {
        this.userId = userId;
        this.account = account;
        this.password = password;
        this.nickname = nickname;
        this.avatar = avatar;
        this.createTime = createTime;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}