package com.ofir.coupons.dao;

import com.ofir.coupons.beans.Coupon;
import com.ofir.coupons.beans.Customer;
import com.ofir.coupons.custom_exceptions.DBErrorException;
import com.ofir.coupons.enums.Category;

import java.util.List;

public interface CustomersDAO {
    boolean isCustomerExists(String email, String password) throws DBErrorException;
    void addCustomer(Customer customer) throws DBErrorException; // CREATE (insert)
    void updateCustomer(Customer customer) throws DBErrorException; // UPDATE
    void deleteCustomer(int customerID) throws DBErrorException; // DELETE
    List<Customer> getAllCustomers() throws DBErrorException; // READ
    Customer getOneCustomer(int customerID) throws DBErrorException; // READ

    List<Coupon> getCustomerCoupons(int customerID) throws DBErrorException;
    List<Coupon> getCustomerCoupons(int customerId, Category category) throws DBErrorException;
    List<Coupon> getCustomerCoupons(int customerId, double maxPrice) throws DBErrorException;
    int getCustomerID(String email, String password) throws DBErrorException;
    boolean isCustomerEmailExists(String email) throws DBErrorException;
    boolean isCouponIdExistsForCustomerId(int customerId, int couponId) throws DBErrorException;
}
