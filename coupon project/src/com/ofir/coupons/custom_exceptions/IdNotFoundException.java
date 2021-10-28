package com.ofir.coupons.custom_exceptions;

import com.ofir.coupons.utils.Utils;

/**
	this exception is thrown when the user tries to perform an operation on a row with an id that does not exist in DB.
 */
public class IdNotFoundException extends Exception{

    public IdNotFoundException(String message) {
        super(String.format("%s | %s", Utils.getCurrentTime(), message));
    }

}
