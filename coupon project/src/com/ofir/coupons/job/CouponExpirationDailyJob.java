package com.ofir.coupons.job;

import com.ofir.coupons.custom_exceptions.DBErrorException;
import com.ofir.coupons.dao.CouponsDAO;
import com.ofir.coupons.dao.CouponsDBDAO;
import com.ofir.coupons.utils.Utils;

public class CouponExpirationDailyJob implements Runnable {

	private CouponsDAO couponsDAO;
	private boolean quit;
	private static final long TWENTY_FOUR_HOURS = 1000 * 60 * 60 * 24;

	public CouponExpirationDailyJob() {
		couponsDAO = new CouponsDBDAO();
		quit = false;
	}

	@Override
	public void run() {
		while (!quit) {
			try {
				couponsDAO.removeAllExpiredCoupons();
			} catch (DBErrorException e) {
				Utils.writeToExceptionLogAndPrint(e.getMessage());
			}
			try {
				Thread.sleep(TWENTY_FOUR_HOURS); // sleep for 24 hours
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public void stop() {
		quit = true;
	}
}
