package com.ysh.demo.util;

import android.text.TextUtils;

import com.blankj.utilcode.util.GsonUtils;
import com.tencent.mmkv.MMKV;
import com.ysh.demo.base.CacheKey;
import com.ysh.demo.bean.UserInfoBean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CacheUtil {
    private static final MMKV mmkv = MMKV.defaultMMKV();

    /**
     * @return 获取token
     */
    @Nullable
    public static String getToken() {
        return mmkv.getString(CacheKey.TOKEN, null);
    }

    /**
     * @param token 缓存token
     */
    public static void setToken(@NotNull String token) {
        mmkv.putString(CacheKey.TOKEN, token);
    }


    /**
     * @return 密码
     */
    @Nullable
    public static String getPsd() {
        return mmkv.getString(CacheKey.PSD, null);
    }

    /**
     * @return 账号
     */
    @Nullable
    public static String getAccount() {
        return mmkv.getString(CacheKey.ACCOUNT, null);
    }

    /**
     * @param psd 设置密码
     */
    public static void setPsd(@NotNull String psd) {
        mmkv.putString(CacheKey.PSD, psd);
    }

    /**
     * @param account 设置账号
     */
    public static void setAccount(@NotNull String account) {
        mmkv.putString(CacheKey.ACCOUNT, account);
    }

    /**
     * @param key 删除key
     */
    public static void remove(@NotNull String key) {
        mmkv.remove(key);
    }

    /**
     * 删除账号信息
     */
    public static void removeLogin() {
        remove(CacheKey.ACCOUNT);
        remove(CacheKey.PSD);
        remove(CacheKey.TOKEN);
        remove(CacheKey.UserInfo);
    }


    @NotNull
    public static String getStrByKey(@NotNull String key, @NotNull String s) {
        return mmkv.getString(key, s);
    }

    public static void putStr2SP(@NotNull String key, @NotNull String value) {
        mmkv.putString(key, value);
    }

    public static void setUserInfo(@NotNull UserInfoBean userInfo) {
        mmkv.putString(CacheKey.UserInfo, GsonUtils.toJson(userInfo));
    }

    @Nullable
    public static UserInfoBean getUserInfo() {
        String userInfo = mmkv.getString(CacheKey.UserInfo, "");
        if (TextUtils.isEmpty(userInfo)) return null;
        return GsonUtils.fromJson(userInfo, UserInfoBean.class);
    }
}
