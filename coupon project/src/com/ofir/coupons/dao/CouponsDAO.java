package com.ofir.coupons.dao;

import com.ofir.coupons.beans.Coupon;
import com.ofir.coupons.custom_exceptions.DBErrorException;

import java.util.List;

public interface CouponsDAO {
    void addCoupon(Coupon coupon) throws DBErrorException; // CREATE (Insert)
    void updateCoupon(Coupon coupon) throws DBErrorException; // UPDATE
    void deleteCoupon(int couponID) throws DBErrorException; // DELETE
    List<Coupon> getAllCoupons() throws DBErrorException; // READ
    Coupon getOneCoupon(int couponID) throws DBErrorException;
    void addCouponPurchase(int customerID, int couponID) throws DBErrorException;
    void deleteCouponPurchase(int customerID, int couponID) throws DBErrorException;

    void decreaseCouponAmount(int couponId) throws DBErrorException;
    void removeAllExpiredCoupons() throws DBErrorException;

}
