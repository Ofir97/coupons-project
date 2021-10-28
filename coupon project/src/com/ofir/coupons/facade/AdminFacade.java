package com.ofir.coupons.facade;

import com.ofir.coupons.beans.Company;
import com.ofir.coupons.beans.Customer;
import com.ofir.coupons.custom_exceptions.*;
import com.ofir.coupons.utils.Utils;

import java.util.List;

public class AdminFacade extends ClientFacade {

    public AdminFacade() {
        super();
    }

    @Override
    public boolean login(String email, String password) {
        return (email.equals("admin@admin.com") && password.equals("admin"));
    }

    /**
     * @param 	company is the company object to be added to DB
     * @throws 	DBErrorException
     * @throws 	InvalidOperationException if company name or email already exist in DB and thus cannot be added
     */
    public void addCompany(Company company) throws DBErrorException, InvalidOperationException {
        if (companiesDAO.isCompanyNameOrEmailExists(company.getName(), company.getEmail()))
            throw new InvalidOperationException(String.format("company add failed: company name '%s' or email '%s' already exists.",
                    company.getName(), company.getEmail()));

        companiesDAO.addCompany(company);
        Utils.writeToOperationLogAndPrint(String.format("%s | company %s added successfully.",
                Utils.getCurrentTime(), company.getName()));
    }

    /**
     * company name cannot be updated.
     * company email cannot be updated to an existing email in DB. (Unique Constraint on email field)
     * 
     * @param 	updatedCompany is the company object to be updated in DB
     * @throws 	DBErrorException
     * @throws 	InvalidOperationException if updatedCompany object has a different name from the name in DB and thus cannot be updated
     * @throws 	IdNotFoundException if updatedCompany object has an id that does not exist in DB and thus cannot be updated
     */
    public void updateCompany(Company updatedCompany) throws DBErrorException, InvalidOperationException, IdNotFoundException {
        Company company = companiesDAO.getOneCompany(updatedCompany.getId());
        if (company == null) // if company does not exist
            throw new IdNotFoundException(String.format("company update failed: company id %d does not exist.", updatedCompany.getId()));
        if (!company.getName().equals(updatedCompany.getName())) // if updated company name different from name in db -
            throw new InvalidOperationException(String.format("company update failed: name of company %s cannot be updated.", company.getId()));

        companiesDAO.updateCompany(updatedCompany);
        Utils.writeToOperationLogAndPrint(String.format("%s | company %d updated successfully.",
                Utils.getCurrentTime(), company.getId()));
    }

       /**
        * when a company gets deleted - all its corresponding coupons will be deleted too,
        * as well as coupon purchases made by customers:
        * 		- coupons table: on delete = cascade for company_id_fk
        * 		- customers_vs_coupons: on delete = cascade for coupon_id_fk
        * 
        * @param 	companyID is the id of the company to be deleted from DB
        * @throws 	DBErrorException
        * @throws 	IdNotFoundException if company id does not exist in DB and thus cannot be deleted
        */
    public void deleteCompany(int companyID) throws DBErrorException, IdNotFoundException {
        if (companiesDAO.getOneCompany(companyID) == null) // if company does not exist
            throw new IdNotFoundException(String.format("company delete failed: company id %d does not exist.", companyID));

        companiesDAO.deleteCompany(companyID);
        Utils.writeToOperationLogAndPrint(String.format("%s | company %d deleted successfully.",
                Utils.getCurrentTime(), companyID));
    }

    /**
     * @return	 list of all companies in DB.
     * @throws 	 DBErrorException
     */
    public List<Company> getAllCompanies() throws DBErrorException {
        List<Company> companies = companiesDAO.getAllCompanies();
        return companies;
    }

    /**
     *   @param		companyID is the id of the company that exists in DB 
     *   @return 	company object with the id that is passes as parameter
     *   @throws	DBErrorException
     *   @throws	IdNotFoundException if company id does not exist in DB
     */
    public Company getOneCompany(int companyID) throws DBErrorException, IdNotFoundException {
        Company company = companiesDAO.getOneCompany(companyID);
        if (company == null) // if company does not exist
            throw new IdNotFoundException(String.format("company id %d does not exist.", companyID));

        return company;
    }

    /**  
     *  @param		customer object to be added to DB
     *  @throws		DBErrorException 
     *  @throws		InvalidOperationException if customer email already exists in DB and thus cannot be added
     */
    public void addCustomer(Customer customer) throws DBErrorException, InvalidOperationException {
        if (customersDAO.isCustomerEmailExists(customer.getEmail()))
            throw new InvalidOperationException(
            		String.format("customer add failed: customer with email %s already exists!", customer.getEmail()));

        customersDAO.addCustomer(customer);
        Utils.writeToOperationLogAndPrint(String.format("%s | customer %s added successfully.",
                Utils.getCurrentTime(), customer.getFirstName()));
    }

    /**
     *  customer object must contain an id in order to be updated.
     *  customer email cannot be updated to an existing email in DB. (Unique Constraint on email field) 
     *  
     *  @param		customer object to be updated in DB
     *  @throws		DBErrorException 
     *  @throws		IdNotFoundException if customer id does not exist in DB and thus cannot be updated 
     */
    public void updateCustomer(Customer customer) throws DBErrorException, IdNotFoundException {
        if (customersDAO.getOneCustomer(customer.getId()) == null) // if customer does not exist
            throw new IdNotFoundException(String.format("customer update failed: customer id %d does not exist.", customer.getId()));

        customersDAO.updateCustomer(customer);
        Utils.writeToOperationLogAndPrint(String.format("%s | customer %d updated successfully.",
                Utils.getCurrentTime(), customer.getId()));
    }

    /**
     *   when a customer gets deleted - all his coupon purchases will be deleted too. 
     *   	- customers_vs_coupons table: on delete = cascade for customer_id_fk
     *   
     *   @param		customerID is the id of the customer that is to be removed from DB
     *   @throws	DBErrorException 
     *   @throws	IdNotFoundException if customer id does not exist in DB and thus cannot be deleted 
     */
    
    public void deleteCustomer(int customerID) throws DBErrorException, IdNotFoundException {
        if (customersDAO.getOneCustomer(customerID) == null) // if customer does not exist
            throw new IdNotFoundException(String.format("customer delete failed: customer id %d does not exist.", customerID));

        customersDAO.deleteCustomer(customerID);
        Utils.writeToOperationLogAndPrint(String.format("%s | customer %d deleted successfully.",
                Utils.getCurrentTime(), customerID));
    }

    /**
     * @return 	  list of all customers in DB
     * @throws 	  DBErrorException 
     */
    public List<Customer> getAllCustomers() throws DBErrorException {
        List<Customer> customers = customersDAO.getAllCustomers();
        return customers;
    }

    /**
     * @param 	customerID is the id of the customer that exists in DB
     * @return	customer object with the id that is passes as parameter
     * @throws 	DBErrorException
     * @throws 	IdNotFoundException if customer id does not exist in DB
     */
    public Customer getOneCustomer(int customerID) throws DBErrorException, IdNotFoundException {
        Customer customer = customersDAO.getOneCustomer(customerID);
        if (customer == null) // if customer does not exist
            throw new IdNotFoundException(String.format("customer id %d does not exist.", customerID));

        return customer;
    }
}
