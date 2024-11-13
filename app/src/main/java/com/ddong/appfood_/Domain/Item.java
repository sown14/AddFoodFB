package com.ddong.appfood_.Domain;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Date;

public class Item {
    private String title;
    private int numberInCart; // Số lượng
    private double price;

    private double timeValue;

    // Constructor mặc định và các getter và setter
    public Item() { }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumberInCart() {
        return numberInCart;
    }

    public void setNumberInCart(int numberInCart) {
        this.numberInCart = numberInCart;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(double timeValue) {
        this.timeValue = timeValue;
    }

}
