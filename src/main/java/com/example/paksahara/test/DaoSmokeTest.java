package com.example.paksahara.test;

import com.example.paksahara.dao.*;
import com.example.paksahara.model.*;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DaoSmokeTest {
    public static void main(String[] args) throws SQLException {
        // 1. ProductDAO
        ProductDAO productDao = new ProductDAOImpl();
        // now passing 6 args: id, name, desc, price, stock, imagePath
        Product p = new Product(101, "Soccer Ball", "Size 5", 19.99, 50, "");
        productDao.addProduct(p);
        Product loadedP = productDao.getProductById(101);
        System.out.println("Loaded Product: " + loadedP.getName());

        p.setPrice(17.99);
        productDao.updateProduct(p);
        System.out.println("Updated Price: " + productDao.getProductById(101).getPrice());

        productDao.deleteProduct(101);
        System.out.println("After Delete, all products: " + productDao.getAllProducts().size());

        // 2. CustomerDAO
        CustomerDAO custDao = new CustomerDAOImpl();
        Customer c = new Customer(201, "Alice", "alice@mail.com", "pass123", "123 Street", "555-0101");
        custDao.addCustomer(c);
        List<Customer> customers = custDao.findAllCustomers();
        System.out.println("Customers count: " + customers.size());
        c = customers.get(0);
        c = new Customer(c.getUserID(), c.getName(), c.getEmail(), c.getPassword(), "456 New St", "555-0202");
        custDao.updateCustomer(c);

        // 3. OrderDAO
        OrderDAO orderDao = new OrderDAOImpl();
        Order o = new Order(301, c, new Date(), 100.0, "Pending", new HashMap<>());
        orderDao.addOrder(o);
        System.out.println("Orders: " + orderDao.findAllOrders().size());
        o.setStatus("Paid");
        orderDao.updateOrder(o);

        // 4. PaymentDAO
        PaymentDAO payDao = new PaymentDAOImpl();
        Payment pay = new Payment(401, o, new Date(), 100.0, "Card");
        payDao.addPayment(pay);
        System.out.println("Payments: " + payDao.findAllPayments().size());
        pay.setAmount(95.0);
        payDao.updatePayment(pay);
        payDao.deletePayment(pay.getPaymentID());

        // cleanup
        orderDao.deleteOrder(o.getOrderID());
        custDao.deleteCustomer(c.getUserID());

        System.out.println("=== DAO Smoke Test Complete ===");
    }
}
