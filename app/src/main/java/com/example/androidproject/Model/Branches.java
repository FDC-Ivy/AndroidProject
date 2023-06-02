package com.example.androidproject.Model;

public class Branches {
    private String mBranchID;
    private String mBranchName;
    private String mBranchAddress;
    private int mBranchImage;

    private String clickedBranchID;

    public Branches( String branchID, String branchName, String branchAddress, int branchImage){
        this.mBranchID = branchID;
        this.mBranchName = branchName;
        this.mBranchAddress = branchAddress;
        this.mBranchImage = branchImage;
    }


    public String getmBranchID() {
        return mBranchID;
    }

    public void setmBranchID(String mBranchID) {
        this.mBranchID = mBranchID;
    }

    public String getmBranchName() {
        return mBranchName;
    }

    public void setmBranchName(String mBranchName) {
        this.mBranchName = mBranchName;
    }

    public String getmBranchAddress() {
        return mBranchAddress;
    }

    public void setmBranchAddress(String mBranchAddress) {
        this.mBranchAddress = mBranchAddress;
    }

    public int getmBranchImage() {
        return mBranchImage;
    }

    public void setmBranchImage(int mBranchImage) {
        this.mBranchImage = mBranchImage;
    }

    public String getClickedBranchID() {
        return clickedBranchID;
    }

    public void setClickedBranchID(String clickedBranchID) {
        this.clickedBranchID = clickedBranchID;
    }
}
