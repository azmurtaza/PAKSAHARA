package com.example.paksahara.dao;

import com.example.paksahara.model.Customer;
import java.util.List;

public interface CustomerDAO {
    void addCustomer(Customer customer);
    Customer findCustomerById(int userID);
    // existing methods…
    Customer findCustomerByEmail(String email);

    List<Customer> findAllCustomers();
    void updateCustomer(Customer customer);
    void deleteCustomer(int userID);
}
