package com.ofir.coupons.custom_exceptions;

import com.ofir.coupons.enums.ClientType;
import com.ofir.coupons.utils.Utils;

/**
	this exception is thrown when the user(admin/company/customer) enters wrong email/password when tries to login.
 */
public class BadLoginException extends Exception {

    public BadLoginException(String email, String password, ClientType clientType) {
        super(String.format("%s | %s Login failed: Incorrect email(%s) or password(%s) entered.",
                Utils.getCurrentTime(), Utils.convertEnumToString(clientType), email, password));
    }
}
