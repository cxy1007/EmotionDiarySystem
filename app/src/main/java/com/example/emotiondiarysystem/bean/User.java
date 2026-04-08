package com.example.emotiondiarysystem.bean;

/**
 * 用户实体类
 */
public class User {

    /**
     * 用户id
     */
    private int userId;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 无参构造方法
     */
    public User() {
    }

    /**
     * 全参构造方法
     *
     * @param userId 用户id
     * @param account 账号
     * @param password 密码
     * @param createTime 创建时间
     */
    public User(int userId, String account, String password, String createTime) {
        this.userId = userId;
        this.account = account;
        this.password = password;
        this.createTime = createTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
