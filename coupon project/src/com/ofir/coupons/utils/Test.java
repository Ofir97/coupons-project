package com.ofir.coupons.utils;

import com.ofir.coupons.beans.Company;
import com.ofir.coupons.beans.Coupon;
import com.ofir.coupons.beans.Customer;
import com.ofir.coupons.custom_exceptions.DBErrorException;
import com.ofir.coupons.dao.CouponsDBDAO;
import com.ofir.coupons.enums.Category;
import com.ofir.coupons.enums.ClientType;
import com.ofir.coupons.facade.AdminFacade;
import com.ofir.coupons.facade.CompanyFacade;
import com.ofir.coupons.facade.CustomerFacade;
import com.ofir.coupons.job.CouponExpirationDailyJob;
import com.ofir.coupons.logs.Logger;

import java.sql.Date;

public class Test {

    public static void testAll() {
        try {
        	CouponExpirationDailyJob job = new CouponExpirationDailyJob();
            Thread dailyJob = new Thread(job);
            dailyJob.start();
            
            testAdmin(LoginManager.getInstance());
            testCompany(LoginManager.getInstance());
            testCustomer(LoginManager.getInstance());

            job.stop();
            //dailyJob.interrupt();

        } catch (Exception e) {
            Utils.writeToExceptionLogAndPrint(e.getMessage());
        } 
        finally {
            try {
                ConnectionPool.getInstance().closeAllConnections();
            } catch (DBErrorException e) {
                Utils.writeToExceptionLogAndPrint(e.getMessage());
            }
        }
    }

    public static void testAdmin(LoginManager loginManager) throws Exception {
         AdminFacade adminFacade = (AdminFacade) loginManager.login("admin@admin.com", "admin", ClientType.ADMINISTRATOR);

        /*
        adminFacade.addCompany(new Company.Builder()
                .name("BBB")
                .email("bbb@gmail.com")
                .password("burgers11")
                .build());
        */
        
        /*
        adminFacade.updateCompany(new Company.Builder()
                .id(13)
                .name("sheraton")
                .email("sheraton@gmail.com")
                .password("sher55")
                .build());
        */

        // adminFacade.deleteCompany(100);

        // adminFacade.getAllCompanies().forEach(company -> System.out.println(company));

        // System.out.println(adminFacade.getOneCompany(14));

        /*
       adminFacade.addCustomer(new Customer.Builder()
               .firstName("Rafi")
               .lastName("Peretz")
               .password("111")
               .email("rafi@gmail.com")
               .build());
        */

        /*
        adminFacade.updateCustomer(new Customer.Builder()
                .id(15)
                .firstName("Dudu")
                .lastName("Gordfen")
                .password("55555")
                .email("dudu@walla.com")
                .build());
        */

        // adminFacade.deleteCustomer(9);

        // CouponsDBDAO couponsDBDAO = new CouponsDBDAO();
        // couponsDBDAO.getAllCoupons().forEach(coupon -> System.out.println(coupon));

        // adminFacade.getAllCustomers().forEach(customer -> System.out.println(customer));

        // System.out.println(adminFacade.getOneCustomer(18));

    }

    public static void testCompany(LoginManager loginManager) throws Exception {
        // CompanyFacade companyFacade = (CompanyFacade) loginManager.login("bbb@gmail.com", "burgers11", ClientType.COMPANY);
        
        /*
        companyFacade.addCoupon(new Coupon.Builder()
                .category(Category.VACATION)
                .title("discount on french fries")
                .description("15% discount on fries!")
                .startDate(new Date(System.currentTimeMillis()))
                .endDate(Date.valueOf("2021-11-4"))
                .amount(0)
                .price(900)
                .image("https://d2bgjx2gb489de.cloudfront.net/gbb-blogs/wp-content/uploads/2017/05/16213722/Berlin_city_viewXL.jpg")
                .build());
        */

        /*
        companyFacade.updateCoupon(new Coupon.Builder()
                .id(29)
                .companyID(14)
                .category(Category.FOOD)
                .title("discount for all burgers")
                .description("45% discount for all burgers!")
                .startDate(new Date(System.currentTimeMillis()))
                .endDate(Date.valueOf("2021-10-15"))
                .amount(10)
                .price(250)
                .image("https://d2bgjx2gb489de.cloudfront.net/gbb-blogs/wp-content/uploads/2017/05/16213722/Berlin_city_viewXL.jpg")
                .build());
        */

        // companyFacade.deleteCoupon(289);

        // companyFacade.getCompanyCoupons().forEach(coupon -> System.out.println(coupon));

        // companyFacade.getCompanyCoupons(Category.FOOD).forEach(coupon -> System.out.println(coupon));

        // companyFacade.getCompanyCoupons(250).forEach(coupon -> System.out.println(coupon));

        // System.out.println(companyFacade.getCompanyDetails());

    }

    public static void testCustomer(LoginManager loginManager) throws Exception {
         CustomerFacade customerFacade = (CustomerFacade) loginManager.login("adam11@gmail.com", "adam333", ClientType.CUSTOMER);

        // customerFacade = (CustomerFacade) loginManager.login("steven3@gmail.com", "steve2", ClientType.Customer);

        // customerFacade.purchaseCoupon(29);

        // customerFacade.getCustomerCoupons().forEach(coupon -> System.out.println(coupon));

        // customerFacade.getCustomerCoupons(Category.FOOD).forEach(coupon -> System.out.println(coupon));

        // customerFacade.getCustomerCoupons(100).forEach(coupon -> System.out.println(coupon));

        // System.out.println(customerFacade.getCustomerDetails());

    }


}
