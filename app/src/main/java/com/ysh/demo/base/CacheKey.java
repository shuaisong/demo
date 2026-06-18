package com.ysh.demo.base;

import org.jetbrains.annotations.NotNull;

public class CacheKey {
    public static final String ACCOUNT = "account";//登录账号
    public static final String PSD = "psd";//登录密码
    @NotNull
    public static final String TOKEN = "token";
    @NotNull
    public static final String UserInfo = "UserInfo";
}
