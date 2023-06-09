package com.example.androidproject.Model;

public class AddToCart {
    private String cartID;
    private String cartProductName;
    private String cartProductPrice;
    private String cartProductQty;
    private String cartProductImage;
    private String cartProductID;

    public AddToCart(String cartID, String cartProductName, String cartProductPrice, String cartProductQty, String cartProductImage, String cartProductID){
        this.cartID = cartID;
        this.cartProductName = cartProductName;
        this.cartProductPrice = cartProductPrice;
        this.cartProductQty = cartProductQty;
        this.cartProductImage = cartProductImage;
        this.cartProductID = cartProductID;
    }

    public String getCartID() {
        return cartID;
    }

    public void setCartID(String cartID) {
        this.cartID = cartID;
    }

    public String getCartProductName() {
        return cartProductName;
    }

    public void setCartProductName(String cartProductName) {
        this.cartProductName = cartProductName;
    }

    public String getCartProductPrice() {
        return cartProductPrice;
    }

    public void setCartProductPrice(String cartProductPrice) {
        this.cartProductPrice = cartProductPrice;
    }

    public String getCartProductQty() {
        return cartProductQty;
    }

    public void setCartProductQty(String cartProductQty) {
        this.cartProductQty = cartProductQty;
    }

    public String getCartProductImage() {
        return cartProductImage;
    }

    public void setCartProductImage(String cartProductImage) {
        this.cartProductImage = cartProductImage;
    }

    public String getCartProductID() {
        return cartProductID;
    }

    public void setCartProductID(String cartProductID) {
        this.cartProductID = cartProductID;
    }
}
