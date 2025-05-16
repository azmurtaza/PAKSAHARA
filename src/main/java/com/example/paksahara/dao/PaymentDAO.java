package com.example.paksahara.dao;

import com.example.paksahara.model.Payment;
import java.util.List;

public interface PaymentDAO {
    void addPayment(Payment payment);
    Payment findPaymentById(int paymentID);
    List<Payment> findAllPayments();
    void updatePayment(Payment payment);
    void deletePayment(int paymentID);
}
