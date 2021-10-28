package com.ofir.coupons.dao;

import com.ofir.coupons.beans.Coupon;
import com.ofir.coupons.custom_exceptions.DBErrorException;
import com.ofir.coupons.custom_exceptions.IdNotFoundException;
import com.ofir.coupons.enums.Category;
import com.ofir.coupons.utils.ConnectionPool;
import com.ofir.coupons.utils.Utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CouponsDBDAO implements CouponsDAO {
	
	private ConnectionPool connectionPool = ConnectionPool.getInstance();

    @Override
    public void addCoupon(Coupon coupon) throws DBErrorException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "insert into coupons (company_id, category_id, title, description, start_date, end_date, amount, price, image)" +
                    " values(?,?,?,?,?,?,?,?,?)";
            preparedStatement = connection.prepareStatement(sqlStatement);
            setDataToPreparedStatement(preparedStatement, coupon);
            preparedStatement.executeUpdate();

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());

        } finally {
            connectionPool.restoreConnection(connection);
            connectionPool.closeResources(preparedStatement);
        }
    }

    @Override
    public void updateCoupon(Coupon coupon) throws DBErrorException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "update coupons set company_id=?, category_id=?, title=?, description=?, start_date=?" +
                    ", end_date=?, amount=?, price=?, image=? where id=?";
            preparedStatement = connection.prepareStatement(sqlStatement);
            setDataToPreparedStatement(preparedStatement, coupon);

            //applying coupon id to the question mark
            preparedStatement.setInt(10, coupon.getId());
            preparedStatement.executeUpdate();    

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        } finally {
            connectionPool.restoreConnection(connection);
            connectionPool.closeResources(preparedStatement);
        }
    }

    @Override
    public void deleteCoupon(int couponID) throws DBErrorException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "delete from coupons where id=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying couponID to the question mark
            preparedStatement.setInt(1, couponID);
            preparedStatement.executeUpdate();
            
        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        } finally {
            connectionPool.restoreConnection(connection);
            connectionPool.closeResources(preparedStatement);
        }
    }

    @Override
    public List<Coupon> getAllCoupons() throws DBErrorException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        List<Coupon> coupons = null;
        ResultSet result = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "select id as coupon_id, company_id, category_id, title, description, " +
                    "start_date, end_date, amount, price, image from coupons";
            preparedStatement = connection.prepareStatement(sqlStatement);
            result = preparedStatement.executeQuery();
            coupons = generateCouponsFromResultSet(result);

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        } finally {
            connectionPool.restoreConnection(connection);
            connectionPool.closeResources(preparedStatement, result);
        }
        return coupons;
    }

    @Override
    public Coupon getOneCoupon(int couponID) throws DBErrorException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        Coupon coupon = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "select id as coupon_id, company_id, category_id, title, description, " +
                    "start_date, end_date, amount, price, image from coupons where id=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying couponID to the question mark
            preparedStatement.setInt(1, couponID);
            result = preparedStatement.executeQuery();

            //check if result set has a record
            if (result.next())
                coupon = generateCouponFromResultSet(result);

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        } finally {
            connectionPool.restoreConnection(connection);
            connectionPool.closeResources(preparedStatement, result);
        }
        return coupon;
    }

    @Override
    public void addCouponPurchase(int customerID, int couponID) throws DBErrorException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "insert into customers_vs_coupons (customer_id, coupon_id) values(?,?)";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying customerID and couponID to question marks by order
            preparedStatement.setInt(1, customerID);
            preparedStatement.setInt(2, couponID);
            preparedStatement.executeUpdate();

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        } finally {
            connectionPool.restoreConnection(connection);
            connectionPool.closeResources(preparedStatement);
        }
    }

    @Override
    public void deleteCouponPurchase(int customerID, int couponID) throws DBErrorException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "delete from customers_vs_coupons where customer_id=? and coupon_id=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying customerID and couponID to question marks by order
            preparedStatement.setInt(1, customerID);
            preparedStatement.setInt(2, couponID);
            int result = preparedStatement.executeUpdate();
            if (result == 0)
                throw new IdNotFoundException(String.format("delete failed: customer id %d and coupon id %d do not exist", customerID, couponID));
            System.out.println(String.format("coupon purchase deleted successfully."));

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        } catch (IdNotFoundException exception) {
            System.out.println(exception.getMessage());
        } finally {
            connectionPool.restoreConnection(connection);
            connectionPool.closeResources(preparedStatement);
        }
    }

    /**
     * this method decreases coupon amount by 1
     * @param	couponId is the id of the coupon to be purchased by the customer
     */
    public void decreaseCouponAmount(int couponId) throws DBErrorException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "update coupons set amount=amount-1 where id=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying couponId to the question mark-
            preparedStatement.setInt(1, couponId);
            preparedStatement.executeUpdate();

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        } finally {
            connectionPool.restoreConnection(connection);
            connectionPool.closeResources(preparedStatement);
        }
    }

    /**
     * this method deletes all coupons that are expired from coupons table. 
     * (all coupons with end_date before current time)
     * 
     * also, all customer purchases of the expired coupons will be deleted too.
     * (customers_vs_coupons table: cascade for coupon_id_fk)
     */
    public void removeAllExpiredCoupons() throws DBErrorException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "delete from coupons where end_date<now()";
            preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.executeUpdate();

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        } finally {
            connectionPool.restoreConnection(connection);
            connectionPool.closeResources(preparedStatement);
        }
    }

    /** this method is used by getAllCoupons(), getCompanyCoupons(), getCustomerCoupons()
     * 
     * @param 	result is the result set 
     * @return	list of all coupons extracted from result set
     * @throws	SQLException
     */
    public List<Coupon> generateCouponsFromResultSet(ResultSet result) throws SQLException {
        List<Coupon> coupons = new ArrayList<Coupon>();
        while (result.next()) {
            coupons.add(generateCouponFromResultSet(result));
        }
        return coupons;
    }

    /**
     * this method adds all coupons that belong to customer/company to coupons list.
     * the method is responsible for advancing the result set cursor to the next record.
     * it is used by generateCompanyFromResultSet() in CompaniesDBDAO, and generateCustomerFromResultSet() in CustomersDBDAO
     * 
     * @param 	result is the result set
     * @param 	id represents either company id or customer id
     * @return	list of all coupons that belong to the company/customer with the id parameter
     * @throws 	SQLException
     */
    public List<Coupon> generateCouponsFromResultSet(ResultSet result, int id) throws SQLException {
        List<Coupon> coupons = new ArrayList<Coupon>();
        if (result.getInt("coupon_id") == 0) { // if company/customer has no coupons -
            result.next(); //moves the cursor to next record.
            return coupons;
        }
        do {
            coupons.add(generateCouponFromResultSet(result));
            //if the next record has the same customer/company id - loop over again.
        } while (result.next() && result.getInt("id") == id);

        return coupons;
    }

    /**
     * @param 	result is the result set from the select query
     * @return	coupon object with all the data from the result set
     * @throws 	SQLException
     */
    public Coupon generateCouponFromResultSet(ResultSet result) throws SQLException {
        Coupon coupon = new Coupon();
        coupon.setId(result.getInt("coupon_id"));
        coupon.setCompanyID(result.getInt("company_id"));
        coupon.setCategory(Category.getCategoryById(result.getInt("category_id")));
        coupon.setTitle(result.getString("title"));
        coupon.setDescription(result.getString("description"));
        coupon.setStartDate(result.getDate("start_date"));
        coupon.setEndDate(result.getDate("end_date"));
        coupon.setAmount(result.getInt("amount"));
        coupon.setPrice(result.getDouble("price"));
        coupon.setImage(result.getString("image"));
        return coupon;
    }

    /**
     * @param 	preparedStatement is the preparedStatement object
     * @param 	coupon is the coupon object with the data that needs to be set to the question marks
     * @throws 	SQLException
     */
    private void setDataToPreparedStatement(PreparedStatement preparedStatement, Coupon coupon) throws SQLException {
        preparedStatement.setInt(1, coupon.getCompanyID());
        preparedStatement.setInt(2, Category.getIdByCategory(coupon.getCategory()));
        preparedStatement.setString(3, coupon.getTitle());
        preparedStatement.setString(4, coupon.getDescription());
        preparedStatement.setDate(5, coupon.getStartDate());
        preparedStatement.setDate(6, coupon.getEndDate());
        preparedStatement.setInt(7, coupon.getAmount());
        preparedStatement.setDouble(8, coupon.getPrice());
        preparedStatement.setString(9, coupon.getImage());
    }

}
