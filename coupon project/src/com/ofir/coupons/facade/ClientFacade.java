package com.ofir.coupons.facade;

import com.ofir.coupons.custom_exceptions.DBErrorException;
import com.ofir.coupons.dao.*;

public abstract class ClientFacade {
	
    protected CompaniesDAO companiesDAO;
    protected CustomersDAO customersDAO;
    protected CouponsDAO couponsDAO;
    
    public ClientFacade() {
    	companiesDAO = new CompaniesDBDAO();
    	customersDAO = new CustomersDBDAO();
    	couponsDAO = new CouponsDBDAO();
    }

    public abstract boolean login(String email, String password) throws DBErrorException;

}
