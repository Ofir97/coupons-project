package com.ofir.coupons.dao;

import com.ofir.coupons.beans.Company;
import com.ofir.coupons.beans.Coupon;
import com.ofir.coupons.custom_exceptions.DBErrorException;
import com.ofir.coupons.custom_exceptions.IdNotFoundException;
import com.ofir.coupons.enums.Category;

import java.util.List;

public interface CompaniesDAO {
    boolean isCompanyExists(String email, String password) throws DBErrorException;
    void addCompany(Company company) throws DBErrorException; // CREATE (insert)
    void updateCompany(Company company) throws DBErrorException, IdNotFoundException; // UPDATE
    void deleteCompany(int companyID) throws DBErrorException; // DELETE
    List<Company> getAllCompanies() throws DBErrorException; // READ
    Company getOneCompany(int companyID) throws DBErrorException; // READ

    List<Coupon> getCompanyCoupons(int companyID) throws DBErrorException;
    List<Coupon> getCompanyCoupons(int companyID, Category category) throws DBErrorException;
    List<Coupon> getCompanyCoupons(int companyID, double maxPrice) throws DBErrorException;
    boolean isCouponTitleExistsInCompanyCoupons(int companyId, String title) throws DBErrorException;
    int getCompanyID(String email, String password) throws DBErrorException;
    boolean isCompanyNameOrEmailExists(String name, String email) throws DBErrorException;
}
