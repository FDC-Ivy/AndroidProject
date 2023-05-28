package com.example.androidproject.Model;

import com.example.androidproject.Enum.UserType;

public class Sellers {

    private int mSellerID;
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private String mPassword;
    private String mSellerImage;
    private UserType mSellerUserType;

    public int getmSellerID() {
        return mSellerID;
    }

    public void setmSellerID(int mSellerID) {
        this.mSellerID = mSellerID;
    }

    public String getmFirstName() {
        return mFirstName;
    }

    public void setmFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getmLastName() {
        return mLastName;
    }

    public void setmLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getmSellerImage() {
        return mSellerImage;
    }

    public void setmSellerImage(String mSellerImage) {
        this.mSellerImage = mSellerImage;
    }

    public UserType getmSellerUserType() {
        return mSellerUserType;
    }

    public void setmSellerUserType(UserType mSellerUserType) {
        this.mSellerUserType = mSellerUserType;
    }
}
