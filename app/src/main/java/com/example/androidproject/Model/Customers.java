package com.example.androidproject.Model;

import com.example.androidproject.Enum.UserType;

public class Customers {

    private int mCustomerID;
    private String mCustomerFirstName;
    private String mCustomerLastName;
    private String mCustomerEmail;
    private String mCustomerPassword;
    private String mCustomerImage;
    private UserType mCustomerUserType;

    public int getmCustomerID() {
        return mCustomerID;
    }

    public void setmCustomerID(int mCustomerID) {
        this.mCustomerID = mCustomerID;
    }

    public String getmCustomerFirstName() {
        return mCustomerFirstName;
    }

    public void setmCustomerFirstName(String mCustomerFirstName) {
        this.mCustomerFirstName = mCustomerFirstName;
    }

    public String getmCustomerLastName() {
        return mCustomerLastName;
    }

    public void setmCustomerLastName(String mCustomerLastName) {
        this.mCustomerLastName = mCustomerLastName;
    }

    public String getmCustomerEmail() {
        return mCustomerEmail;
    }

    public void setmCustomerEmail(String mCustomerEmail) {
        this.mCustomerEmail = mCustomerEmail;
    }

    public String getmCustomerPassword() {
        return mCustomerPassword;
    }

    public void setmCustomerPassword(String mCustomerPassword) {
        this.mCustomerPassword = mCustomerPassword;
    }

    public String getmCustomerImage() {
        return mCustomerImage;
    }

    public void setmCustomerImage(String mCustomerImage) {
        this.mCustomerImage = mCustomerImage;
    }

    public UserType getmCustomerUserType() {
        return mCustomerUserType;
    }

    public void setmCustomerUserType(UserType mCustomerUserType) {
        this.mCustomerUserType = mCustomerUserType;
    }
}
