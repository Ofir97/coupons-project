package com.ofir.coupons.utils;

import com.ofir.coupons.custom_exceptions.BadLoginException;
import com.ofir.coupons.custom_exceptions.DBErrorException;
import com.ofir.coupons.enums.ClientType;
import com.ofir.coupons.facade.AdminFacade;
import com.ofir.coupons.facade.ClientFacade;
import com.ofir.coupons.facade.CompanyFacade;
import com.ofir.coupons.facade.CustomerFacade;

public class LoginManager {
	
	private static LoginManager instance = new LoginManager();  //eager

	private LoginManager() {
	}

	public static LoginManager getInstance() {
		return instance;
	}

	/**
	 * @param 	email is the email of the admin/company/customer that wants to log in
	 * @param 	password is the password of the admin/company/customer that wants to log in
	 * @param 	clientType is the type of client that wants to login(types are: admin, company and customer)
	 * @return	the matching ClientFacade if login is successful, otherwise throws BadLoginException
	 * @throws 	BadLoginException if login failed so the user can be informed about the error
	 * @throws 	DBErrorException
	 */
	public ClientFacade login(String email, String password, ClientType clientType) throws BadLoginException, DBErrorException {
		ClientFacade clientFacade = null;
		
		switch (clientType) {
		case ADMINISTRATOR:
			clientFacade = new AdminFacade();
			break;
		case COMPANY:
			clientFacade = new CompanyFacade();
			break;
		case CUSTOMER:
			clientFacade = new CustomerFacade();
		}

		if (clientFacade != null && clientFacade.login(email, password))
			return clientFacade;

		throw new BadLoginException(email, password, clientType);
	}
}
