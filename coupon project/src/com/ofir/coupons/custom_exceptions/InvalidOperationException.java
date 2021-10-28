package com.ofir.coupons.custom_exceptions;

import com.ofir.coupons.utils.Utils;

/**
 * 	this exception is thrown if an illegal operation has been performed by Admin/Company/Customer.
 */

public class InvalidOperationException extends Exception {

	public InvalidOperationException(String message) {
        super(String.format("%s | %s", Utils.getCurrentTime(), message));
    }
}
