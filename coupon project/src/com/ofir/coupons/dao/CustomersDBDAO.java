package com.ofir.coupons.dao;

import com.ofir.coupons.beans.Coupon;
import com.ofir.coupons.beans.Customer;
import com.ofir.coupons.custom_exceptions.DBErrorException;
import com.ofir.coupons.enums.Category;
import com.ofir.coupons.utils.ConnectionPool;
import com.ofir.coupons.utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomersDBDAO implements CustomersDAO {
	
	private ConnectionPool connectionPool = ConnectionPool.getInstance();

    @Override
    public boolean isCustomerExists(String email, String password) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        ResultSet result = null; boolean isExist = false;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "select * from customers where email=? and password=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying email and password to the question marks by order
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            result = preparedStatement.executeQuery();

            //check if result set is empty
            if (result.next())
                isExist = true;

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        }
        finally {
            connectionPool.restoreConnection(connection);
            connectionPool.closeResources(preparedStatement, result);
        }
        return isExist;
    }

    @Override
    public void addCustomer(Customer customer) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "insert into customers (first_name, last_name, email, password) values(?,?,?,?)";
            preparedStatement = connection.prepareStatement(sqlStatement);
            setDataToPreparedStatement(preparedStatement, customer);
            preparedStatement.executeUpdate();

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        }
        finally {
            connectionPool.restoreConnection(connection);
            connectionPool.closeResources(preparedStatement);
        }
    }

    @Override
    public void updateCustomer(Customer customer) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "update customers set first_name=?, last_name=?, email=?, password=? where id=?";
            preparedStatement = connection.prepareStatement(sqlStatement);
            setDataToPreparedStatement(preparedStatement, customer);

            //applying customer id to the question mark
            preparedStatement.setInt(5, customer.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        }

        finally {
            connectionPool.restoreConnection(connection);
            connectionPool.closeResources(preparedStatement);
        }
    }

    @Override
    public void deleteCustomer(int customerID) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "delete from customers where id=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying customerID to the question mark
            preparedStatement.setInt(1, customerID);
            preparedStatement.executeUpdate();

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        }

         finally {
            connectionPool.restoreConnection(connection);
            connectionPool.closeResources(preparedStatement);
        }
    }

    @Override
    public List<Customer> getAllCustomers() throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        List<Customer> customers = new ArrayList<Customer>(); ResultSet result = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "select customers.id, first_name, last_name, email, " +
                    "password, coupons.id as coupon_id, company_id, category_id, title, description, " +
                    "start_date, end_date, amount, price, image from customers " +
                    "left join customers_vs_coupons " +
                    "on customers.id=customer_id " +
                    "left join coupons " +
                    "on customers_vs_coupons.coupon_id=coupons.id " +
                    "order by customers.id";

            preparedStatement = connection.prepareStatement(sqlStatement);
            result = preparedStatement.executeQuery();

            //check if result set has a record
            if (result.next())
                customers = generateCustomersFromResultSet(result);

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        }
        finally {
            connectionPool.restoreConnection(connection);
            connectionPool.closeResources(preparedStatement, result);
        }
        return customers;
    }

    @Override
    public Customer getOneCustomer(int customerID) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        ResultSet result = null; Customer customer = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "select customers.id, first_name, last_name, email, password, coupons.id as coupon_id, company_id, category_id, title, description, " +
                    "start_date, end_date, amount, price, image from customers " +
                    "left join customers_vs_coupons " +
                    "on customers.id=customer_id " +
                    "left join coupons " +
                    "on coupon_id=coupons.id where customers.id=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying customerID to the question mark
            preparedStatement.setInt(1, customerID);
            result = preparedStatement.executeQuery();

            //check if result set has a record
            if (result.next())
                customer = generateCustomerFromResultSet(result);

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        }

        finally {
            connectionPool.restoreConnection(connection);
            connectionPool.closeResources(preparedStatement, result);
        }
        return customer;
    }

    public List<Coupon> getCustomerCoupons(int customerID) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        List<Coupon> coupons = new ArrayList<Coupon>(); ResultSet result = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "select * from coupons join customers_vs_coupons on" +
                    " coupons.id=customers_vs_coupons.coupon_id where customer_id=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying customerID to the question mark
            preparedStatement.setInt(1, customerID);
            result = preparedStatement.executeQuery();
            coupons = new CouponsDBDAO().generateCouponsFromResultSet(result);

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        }
        finally {
            connectionPool.restoreConnection(connection);
            connectionPool.closeResources(preparedStatement, result);
        }
        return coupons;
    }

    public List<Coupon> getCustomerCoupons(int customerId, Category category) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        List<Coupon> coupons = new ArrayList<Coupon>(); ResultSet result = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "select * from coupons join customers_vs_coupons on" +
                    " coupons.id=customers_vs_coupons.coupon_id where customer_id=? and category_id=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying customerID and category id to the question marks by order
            preparedStatement.setInt(1, customerId);
            preparedStatement.setInt(2, Category.getIdByCategory(category));
            result = preparedStatement.executeQuery();
            coupons = new CouponsDBDAO().generateCouponsFromResultSet(result);

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        }
        finally {
            if (connection != null) {
                connectionPool.restoreConnection(connection);
                connectionPool.closeResources(preparedStatement, result);
            }
        }
        return coupons;
    }

    public List<Coupon> getCustomerCoupons(int customerId, double maxPrice) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        List<Coupon> coupons = new ArrayList<Coupon>(); ResultSet result = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "select * from coupons join customers_vs_coupons on" +
                    " coupons.id=customers_vs_coupons.coupon_id where customer_id=? and price<=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying customerId and maxPrice to the question marks by order
            preparedStatement.setInt(1, customerId);
            preparedStatement.setDouble(2, maxPrice);
            result = preparedStatement.executeQuery();
            coupons = new CouponsDBDAO().generateCouponsFromResultSet(result);

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        }
        finally {
            if (connection != null) {
                connectionPool.restoreConnection(connection);
                connectionPool.closeResources(preparedStatement, result);
            }
        }
        return coupons;
    }

    /**
     * @param 	email is the email of the customer
     * @param	password is the password of the customer
     * @return 	customer id of the record with the specified email and password
     */
    public int getCustomerID(String email, String password) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        ResultSet result = null; int customerID = -1;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "select * from customers where email=? and password=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying email and password to the question marks by order
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            result = preparedStatement.executeQuery();

            //check if result set has a record
            if (result.next())
                customerID = result.getInt("id");

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        }

        finally {
            if (connection != null) {
                connectionPool.restoreConnection(connection);
                connectionPool.closeResources(preparedStatement, result);
            }
        }
        return customerID;
    }

    /**
     * @param	email is customer email
     * @return	true if customer email already exists in DB, otherwise false
     */
    public boolean isCustomerEmailExists(String email) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        ResultSet result = null; boolean isExists = false;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "select * from customers where email=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying email to the question mark
            preparedStatement.setString(1, email);
            result = preparedStatement.executeQuery();

            //check if result set has a record
            if (result.next())
                isExists = true;

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        }
        finally {
            connectionPool.restoreConnection(connection);
            connectionPool.closeResources(preparedStatement, result);
        }
        return isExists;
    }

    /**
     * @param	customerId is the id of the customer that logged in
     * @param	couponId is the id of the coupon to be purchased by the customer
     * @return	true if customer already purchased the coupon, otherwise false
     */
    public boolean isCouponIdExistsForCustomerId(int customerId, int couponId) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        ResultSet result = null; boolean isCouponPurchaseExists = false;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "select * from customers_vs_coupons where customer_id=? and coupon_id=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying customerId and couponId to question marks by order
            preparedStatement.setInt(1, customerId);
            preparedStatement.setInt(2, couponId);
            result = preparedStatement.executeQuery();

            //check if result set has a record
            if (result.next()) {
                isCouponPurchaseExists = true;
            }

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        }
        finally {
            if (connection != null) {
                connectionPool.restoreConnection(connection);
                connectionPool.closeResources(preparedStatement, result);
            }
        }
        return isCouponPurchaseExists;
    }

    /**
     * @param 	preparedStatement is the preparedStatement object
     * @param 	customer is the customer object with the data that needs to be set to the question marks
     * @throws 	SQLException
     */
    private void setDataToPreparedStatement(PreparedStatement preparedStatement, Customer customer) throws SQLException {
        preparedStatement.setString(1, customer.getFirstName());
        preparedStatement.setString(2, customer.getLastName());
        preparedStatement.setString(3, customer.getEmail());
        preparedStatement.setString(4, customer.getPassword());
    }

    /**
     * @param 	result is the result set
     * @return	list of all customers in DB
     * @throws  SQLException
     */
    private List<Customer> generateCustomersFromResultSet(ResultSet result) throws SQLException {
        List<Customer> customers = new ArrayList<Customer>();

        while (!result.isAfterLast()) { //if result is before or last record
            customers.add(generateCustomerFromResultSet(result));
        }
        return customers;
    }

    /**
     * @param 	result is the result set
     * @return	customer object extracted from the result set
     * @throws 	SQLException
     */
    private Customer generateCustomerFromResultSet(ResultSet result) throws SQLException {
        CouponsDBDAO couponsDBDAO = new CouponsDBDAO();
        Customer customer = new Customer();
        customer.setId(result.getInt("id"));
        customer.setFirstName(result.getString("first_name"));
        customer.setLastName(result.getString("last_name"));
        customer.setEmail(result.getString("email"));
        customer.setPassword(result.getString("password"));
        customer.setCoupons(couponsDBDAO.generateCouponsFromResultSet(result, customer.getId()));
        return customer;
    }
}
