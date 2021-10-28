package com.ofir.coupons.custom_exceptions;

import java.util.Date;

/**
	this exception is thrown when SQL Exception occurs.
 */
public class DBErrorException extends Exception{

	/**
	 * @param 	time represents when the exception occurred
	 * @param 	className represents in which class the exception occurred
	 * @param 	methodName represents in which method the exception occurred
	 * @param 	message represents the details of the SQL exception
	 */
    public DBErrorException(Date time, String className, String methodName, String message) {
        super(String.format("%s | DB Error: at %s.%s(). \nerror details: %s",
                time, className, methodName, message));
    }
}
