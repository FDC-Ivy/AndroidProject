package com.example.androidproject.Model;

public class Products {

    private String mProductID;
    private String mProductName;
    private String mProductPrice;
    private String mProductDescription;
    private int mProductImage;
    private String mProductBranchID;

    public Products(String productID, String productName, String productPrice, String productDescription, int productImage, String productBranchID){
        this.mProductID = productID;
        this.mProductName = productName;
        this.mProductPrice = productPrice;
        this.mProductDescription = productDescription;
        this.mProductImage = productImage;
        this.mProductBranchID = productBranchID;
    }

    public String getmProductID() {
        return mProductID;
    }

    public void setmProductID(String mProductID) {
        this.mProductID = mProductID;
    }

    public String getmProductName() {
        return mProductName;
    }

    public void setmProductName(String mProductName) {
        this.mProductName = mProductName;
    }

    public String getmProductPrice() {
        return mProductPrice;
    }

    public void setmProductPrice(String mProductPrice) {
        this.mProductPrice = mProductPrice;
    }

    public String getmProductDescription() {
        return mProductDescription;
    }

    public void setmProductDescription(String mProductDescription) {
        this.mProductDescription = mProductDescription;
    }

    public int getmProductImage() {
        return mProductImage;
    }

    public void setmProductImage(int mProductImage) {
        this.mProductImage = mProductImage;
    }

    public String getmProductBranchID() {
        return mProductBranchID;
    }

    public void setmProductBranchID(String mProductBranchID) {
        this.mProductBranchID = mProductBranchID;
    }
}
