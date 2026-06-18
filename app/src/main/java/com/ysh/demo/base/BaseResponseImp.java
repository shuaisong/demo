package com.ysh.demo.base;

import androidx.annotation.NonNull;
import me.hgj.jetpackmvvm.network.BaseResponse;

public class BaseResponseImp<T> extends BaseResponse<T> {
    private final int code;
    private final String msg;
    private final T data;

    public BaseResponseImp(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    @Override
    public int getResponseCode() {
        return code;
    }

    @Override
    public T getResponseData() {
        return data;
    }

    @NonNull
    @Override
    public String getResponseMsg() {
        return msg;
    }

    @Override
    public boolean isSucces() {
        return code == 0;
    }
}
