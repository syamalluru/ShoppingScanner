package com.practise.neo.shoppingscanner.Model;

public class ItemModel {
    public ItemModel(String itemName, String price, String quantity) {
        this.productName = itemName;
        this.price = price;
        this.quantity = quantity;
    }

    public ItemModel()
    {

    }
    String barcodeNumber;

    public ItemModel(String productName, String barcodeNumber, String quantity, String price) {
        this.barcodeNumber = barcodeNumber;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public String getBarcodeNumber() {
        return barcodeNumber;
    }

    public void setBarcodeNumber(String barcodeNumber) {
        this.barcodeNumber = barcodeNumber;
    }

    String productName;
    String price;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    String quantity;




}
