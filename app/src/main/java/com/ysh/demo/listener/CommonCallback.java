package com.ysh.demo.listener;

public interface CommonCallback {
    void onSuccess();
    void onFail(String message);

    public static class SimpleCallback implements CommonCallback {
        @Override
        public void onSuccess() {

        }
        public void onSuccess(Object o) {

        }
        @Override
        public void onFail(String message) {

        }
    }
}


