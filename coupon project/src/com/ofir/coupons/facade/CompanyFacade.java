package com.ofir.coupons.facade;

import com.ofir.coupons.beans.Company;
import com.ofir.coupons.beans.Coupon;
import com.ofir.coupons.custom_exceptions.DBErrorException;
import com.ofir.coupons.custom_exceptions.IdNotFoundException;
import com.ofir.coupons.custom_exceptions.InvalidOperationException;
import com.ofir.coupons.enums.Category;
import com.ofir.coupons.utils.Utils;

import java.util.List;

public class CompanyFacade extends ClientFacade {

    public CompanyFacade() {
        super();
    }

    private int companyID; //represents the company id that logged in

    /**
     * @param	email - the company's email
     * @param	password - the company's password
     * @return 	true if the email and password exist in companies table, otherwise false
     */
    @Override
    public boolean login(String email, String password) throws DBErrorException {
        if (companiesDAO.isCompanyExists(email, password)) {
            companyID = companiesDAO.getCompanyID(email, password); //sets company id to the company id that logged in.
            return true;
        } 
        return false;
    }

    /**
     * this method adds a coupon to coupons table.
     * (coupon object parameter doesn't have to contain companyId since it will be set to the company id that logged in) 
     * 
     * @param 	coupon is the coupon object to be added to coupons table
     * @throws 	DBErrorException
     * @throws 	InvalidOperationException if coupon title already exists in the coupons of the company that logged in
     */
    public void addCoupon(Coupon coupon) throws DBErrorException, InvalidOperationException {
        if (companiesDAO.isCouponTitleExistsInCompanyCoupons(companyID, coupon.getTitle()))
            throw new InvalidOperationException(String.format("coupon add failed: coupon title '%s' already exists in company %d coupons!",
                    coupon.getTitle(), companyID));

        coupon.setCompanyID(companyID); //sets coupon company id to the company id that logged in.
        couponsDAO.addCoupon(coupon);
        Utils.writeToOperationLogAndPrint(String.format("%s | coupon '%s' for company %s added successfully.",
                Utils.getCurrentTime(), coupon.getTitle(), coupon.getCompanyID()));
    }

    /**
     * this method updates coupon which belongs to the company that logged in.
     * (the company that logged in can update its own coupons only)
     * 
     * @param 	coupon object (must contain coupon id and company id in order to be updated)
     * @throws 	DBErrorException
     * @throws 	InvalidOperationException for two reasons:
     * 				- the company updates the company id of the coupon
     * 			 	- coupon id does not exist or does not belong to company's coupons 
     */
    public void updateCoupon(Coupon updatedCoupon) throws DBErrorException, InvalidOperationException {
        if (updatedCoupon.getCompanyID() != companyID) // if updatedCoupon has a different company id -
        	throw new InvalidOperationException("coupon update failed: company id of coupon cannot be updated.");
        
        Coupon coupon = couponsDAO.getOneCoupon(updatedCoupon.getId());
        if (coupon == null || coupon.getCompanyID() != companyID) // if coupon does not exist or does not belong to the company that logged in
        	throw new InvalidOperationException(String.format("coupon update failed: coupon id %d does not exist for company id %s.",
        			updatedCoupon.getId(), updatedCoupon.getCompanyID()));
        	
        couponsDAO.updateCoupon(updatedCoupon);
        Utils.writeToOperationLogAndPrint(String.format("%s | coupon %d of company %d updated successfully.",
                Utils.getCurrentTime(), updatedCoupon.getId(), updatedCoupon.getCompanyID()));
    }

    /**
     * this method deletes the coupon with the id that is passed as parameter.
     * (the company that logged in can delete its own coupons only)
     * once a coupon gets deleted - all its customer purchases will be deleted too: 
     * 		- customers_vs_coupons table: on delete = cascade for coupon_id_fk
     * 
     * @param 	couponID is the id of the coupon that is to be deleted from coupons table
     * @throws 	DBErrorException 
     * @throws 	IdNotFoundException if the coupon does not exist, or the coupon does not belong to the company that logged in 
     */
    public void deleteCoupon(int couponID) throws DBErrorException, IdNotFoundException {
        Coupon coupon = couponsDAO.getOneCoupon(couponID);
        
        if (coupon == null || coupon.getCompanyID() != companyID)
            throw new IdNotFoundException(String.format("coupon delete failed: coupon id %d does not exist for company id %s.",
                    couponID, companyID));

        couponsDAO.deleteCoupon(couponID);
        Utils.writeToOperationLogAndPrint(String.format("%s | coupon %d for company %d deleted successfully.",
                Utils.getCurrentTime(), couponID, companyID));
    }

    /**
     * @return	 list of all coupons that belong to the company that logged in
     * @throws	 DBErrorException
     */
    public List<Coupon> getCompanyCoupons() throws DBErrorException {
        List<Coupon> coupons = companiesDAO.getCompanyCoupons(companyID);
        return coupons;
    }

    /**
     * @param 	category indicates that only coupons from the specific category will be included in the list
     * @return	list of all coupons that belong to the company that logged in from the specified category
     * @throws	DBErrorException
     */
    public List<Coupon> getCompanyCoupons(Category category) throws DBErrorException {
        List<Coupon> coupons = companiesDAO.getCompanyCoupons(companyID, category);
        return coupons;
    }

    /**
     * @param 	maxPrice indicates that only coupons until the specified maxPrice will be included in the list 
     * @return	list of all coupons of the company that logged in until maxPrice
     * @throws 	DBErrorException
     */
    public List<Coupon> getCompanyCoupons(double maxPrice) throws DBErrorException {
        List<Coupon> coupons = companiesDAO.getCompanyCoupons(companyID, maxPrice);
        return coupons;
    }

    /**
     * @return 	company object with all details of the company that logged in
     * @throws DBErrorException
     */
    public Company getCompanyDetails() throws DBErrorException {
        Company company = companiesDAO.getOneCompany(companyID);
        return company;
    }
}
