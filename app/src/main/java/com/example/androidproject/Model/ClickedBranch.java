package com.example.androidproject.Model;

public class ClickedBranch {
    private String clickedBranchID;
    private boolean isSameBranch;

    public String getClickedBranchID() {
        return clickedBranchID;
    }

    public void setClickedBranchID(String clickedBranchID) {
        this.clickedBranchID = clickedBranchID;
    }

    public boolean isSameBranch() {
        return isSameBranch;
    }

    public void setSameBranch(boolean sameBranch) {
        isSameBranch = sameBranch;
    }
}
