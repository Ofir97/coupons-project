package com.ofir.coupons.facade;

import com.ofir.coupons.beans.Coupon;
import com.ofir.coupons.beans.Customer;
import com.ofir.coupons.custom_exceptions.DBErrorException;
import com.ofir.coupons.custom_exceptions.IdNotFoundException;
import com.ofir.coupons.custom_exceptions.InvalidOperationException;
import com.ofir.coupons.enums.Category;
import com.ofir.coupons.utils.Utils;

import java.util.List;

public class CustomerFacade extends ClientFacade {

	public CustomerFacade() {
		super();
	}

	private int customerID; // represents the customer id that logged in

	/**
     * @param	email - the customer's email
     * @param	password - the customer's password
     * @return 	true if the email and password exist in customers table, otherwise false
     */
	@Override
	public boolean login(String email, String password) throws DBErrorException {
		if (customersDAO.isCustomerExists(email, password)) {
			customerID = customersDAO.getCustomerID(email, password); // sets customer id to the customer id that logged in.
			return true;
		}
		return false;
	}

	/**
	 * this method makes a coupon purchase to the customer that logged in,
	 * responsible to check whether the customer can make that purchase
	 * and if so, it will decrease the coupon amount by 1 and make the purchase.
	 *  
	 * @param 	couponId represents the id of the coupon to be purchased by the customer
	 * @throws 	DBErrorException
	 * @throws 	InvalidOperationException for one of the following reasons:
	 * 					- customer already purchased the coupon
	 * 					- coupon is sold out (amount=0)
	 * 					- coupon is expired (end date is before the current time)
	 * @throws 	IdNotFoundException if coupon id does not exist in DB
	 */
	public void purchaseCoupon(int couponId) throws DBErrorException, InvalidOperationException, IdNotFoundException {
		Coupon coupon = couponsDAO.getOneCoupon(couponId);
		if (coupon == null) // if coupon ID does not exist -
			throw new IdNotFoundException("coupon purchase failed: coupon id " + couponId + " does not exist.");

		if (customersDAO.isCouponIdExistsForCustomerId(customerID, couponId))
			throw new InvalidOperationException(String
					.format("coupon purchase failed: customer %d already purchased coupon %d.", customerID, couponId));

		if (coupon.getAmount() == 0) // if coupon is out of stock
			throw new InvalidOperationException(
					String.format("coupon purchase failed: coupon %d is sold out.", couponId));
		if (coupon.getEndDate().before(Utils.getCurrentTime())) // if coupon is expired
			throw new InvalidOperationException(
					String.format("coupon purchase failed: coupon %d has expired.", couponId));

		// coupon can be purchased by the customer -
		couponsDAO.decreaseCouponAmount(couponId);
		couponsDAO.addCouponPurchase(customerID, couponId);
		Utils.writeToOperationLogAndPrint(String.format("%s | coupon %d for customer %d purchased successfully.",
				Utils.getCurrentTime(), couponId, customerID));
	}

	/**
	 * @return	list of all coupons purchased by the customer that logged in
	 * @throws 	DBErrorException
	 */
	public List<Coupon> getCustomerCoupons() throws DBErrorException {
		List<Coupon> coupons = customersDAO.getCustomerCoupons(customerID);
		return coupons;
	}

	/**
	 * @param 	category indicates that only coupons from the specific category will be included in the list
	 * @return	list of all coupons that belong to the customer that logged in from the specified category
	 * @throws 	DBErrorException
	 */
	public List<Coupon> getCustomerCoupons(Category category) throws DBErrorException {
		List<Coupon> coupons = customersDAO.getCustomerCoupons(customerID, category);
		return coupons;
	}

	/**
	 * @param 	maxPrice indicates that only coupons until the specified maxPrice will be included in the list
	 * @return	list of all coupons of the customer that logged in until maxPrice
	 * @throws 	DBErrorException
	 */
	public List<Coupon> getCustomerCoupons(double maxPrice) throws DBErrorException {
		List<Coupon> coupons = customersDAO.getCustomerCoupons(customerID, maxPrice);
		return coupons;
	}

	/**
	 * @return 	customer object with all details of the customer that logged in
	 * @throws	DBErrorException
	 */
	public Customer getCustomerDetails() throws DBErrorException {
		Customer customer = customersDAO.getOneCustomer(customerID);
		return customer;
	}
}
