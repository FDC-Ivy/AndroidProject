package com.example.androidproject.Singleton;

public class SignInSingleton {
    private static SignInSingleton instance;
    private String mauthUserId;
    private String mauthUserTyoe;

    private SignInSingleton() {
        // Private constructor to prevent instantiation from outside the class
    }

    public static SignInSingleton getInstance() {
        if (instance == null) {
            synchronized (SignInSingleton.class) {
                if (instance == null) {
                    instance = new SignInSingleton();
                }
            }
        }
        return instance;
    }

    public void setAuthUserId(String authUserId) {
        this.mauthUserId = authUserId;
    }

    public String getAuthUserId() {
        return mauthUserId;
    }

    public String getMauthUserType() {
        return mauthUserTyoe;
    }

    public void setMauthUserType(String mauthUserTyoe) {
        this.mauthUserTyoe = mauthUserTyoe;
    }
}

