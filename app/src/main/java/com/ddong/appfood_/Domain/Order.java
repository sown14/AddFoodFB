package com.ddong.appfood_.Domain;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderId;
    private String customer_Name;
    private String customer_Address;
    private String customer_Phone;
    private String customer_Email;
    private ArrayList<String> items; // Sử dụng Map để lưu trữ danh sách các mặt hàng
    private double totalPrice;
    private String orderTime;

    private List<Item> itemList;

    public Order() {

    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomer_Name() {
        return customer_Name;
    }

    public void setCustomer_Name(String customer_Name) {
        this.customer_Name = customer_Name;
    }

    public String getCustomer_Address() {
        return customer_Address;
    }

    public void setCustomer_Address(String customer_Address) {
        this.customer_Address = customer_Address;
    }

    public String getCustomer_Phone() {
        return customer_Phone;
    }

    public void setCustomer_Phone(String customer_Phone) {
        this.customer_Phone = customer_Phone;
    }

    public String getCustomer_Email() {
        return customer_Email;
    }

    public void setCustomer_Email(String customer_Email) {
        this.customer_Email = customer_Email;
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }
}
