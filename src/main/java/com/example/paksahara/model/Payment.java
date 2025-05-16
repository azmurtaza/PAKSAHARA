package com.example.paksahara.model;

import java.util.Date;

public class Payment {
    private int paymentID;
    private Order order;
    private Date paymentDate;

    public void setPaymentID(int paymentID) {
        this.paymentID = paymentID;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getPaymentID() {
        return paymentID;
    }

    public Order getOrder() {
        return order;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    private double amount;
    private String paymentMethod;

    public Payment(int paymentID, Order order, Date paymentDate, double amount, String paymentMethod) {
        this.paymentID = paymentID;
        this.order = order;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public boolean processPayment(){
        if (order != null && amount >= order.getTotalAmount()) {
            System.out.println("Payment of " + amount + " processed successfully for Order ID: " + order.getOrderID());
            order.updateStatus("Paid");
            return true;
        } else {
            System.out.println("Payment failed: Invalid order or insufficient amount.");
            return false;
        }
    }

}
