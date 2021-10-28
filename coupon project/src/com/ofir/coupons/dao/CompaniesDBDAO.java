package com.ofir.coupons.dao;

import com.ofir.coupons.beans.Company;
import com.ofir.coupons.beans.Coupon;
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

public class CompaniesDBDAO implements CompaniesDAO {
	
	private ConnectionPool connectionPool = ConnectionPool.getInstance();

    @Override
    public boolean isCompanyExists(String email, String password) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        ResultSet result = null; boolean isExist = false;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "select * from companies where email=? and password=?";
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
    public void addCompany(Company company) throws DBErrorException {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null; PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "insert into companies (name, email, password) values(?,?,?)";
            preparedStatement = connection.prepareStatement(sqlStatement);
            setDataToPreparedStatement(preparedStatement, company);
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
    public void updateCompany(Company company) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "update companies set name=?, email=?, password=? where id=?";
            preparedStatement = connection.prepareStatement(sqlStatement);
            setDataToPreparedStatement(preparedStatement, company);

            //applying company id to the question mark
            preparedStatement.setInt(4, company.getId());
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
    public void deleteCompany(int companyID) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "delete from companies where id=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying companyID to the question mark
            preparedStatement.setInt(1, companyID);
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
    public List<Company> getAllCompanies() throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        ResultSet result = null; List<Company> companies = new ArrayList<Company>();
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "select companies.id, name, email, password, " +
                    "coupons.id as coupon_id, company_id, category_id, title, description, " +
                    "start_date, end_date, amount, price, image from companies " +
                    "left join coupons " +
                    "on companies.id = coupons.company_id " +
                    "order by companies.id";
            preparedStatement = connection.prepareStatement(sqlStatement);
            result = preparedStatement.executeQuery();

            //check if result set has a record
            if (result.next())
                companies = generateCompaniesFromResultSet(result);

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        }
        finally {
            connectionPool.restoreConnection(connection);
            connectionPool.closeResources(preparedStatement, result);
        }
        return companies;
    }

    @Override
    public Company getOneCompany(int companyID) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        ResultSet result = null; Company company = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "select companies.id, name, email, password, " +
                    "coupons.id as coupon_id, company_id, category_id, title, description, " +
                    "start_date, end_date, amount, price, image from companies " +
                    "left join coupons " +
                    "on companies.id = coupons.company_id " +
                    "where companies.id=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying companyID to the question mark
            preparedStatement.setInt(1, companyID);
            result = preparedStatement.executeQuery();

            //check if result set has a record
            if (result.next())
                company = generateCompanyFromResultSet(result);

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        }
        finally {
            connectionPool.restoreConnection(connection);
            connectionPool.closeResources(preparedStatement, result);
        }
        return company;
    }

    public List<Coupon> getCompanyCoupons(int companyID) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        List<Coupon> coupons = new ArrayList<Coupon>(); ResultSet result = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "select id as coupon_id, company_id, category_id, title, description," +
                    " start_date, end_date, amount, price, image from coupons where company_id=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying companyID to the question mark
            preparedStatement.setInt(1, companyID);
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

    public List<Coupon> getCompanyCoupons(int companyID, Category category) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        List<Coupon> coupons = new ArrayList<Coupon>(); ResultSet result = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "select id as coupon_id, company_id, category_id, title, description, " +
                    "start_date, end_date, amount, price, image from coupons where company_id=? and category_id=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying companyID and category id to the question marks by order-
            preparedStatement.setInt(1, companyID);
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

    public List<Coupon> getCompanyCoupons(int companyID, double maxPrice) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        List<Coupon> coupons = new ArrayList<Coupon>(); ResultSet result = null;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "select id as coupon_id, company_id, category_id, title, description, " +
                    "start_date, end_date, amount, price, image from coupons where company_id=? and price<=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying companyID and maxPrice to the question marks by order-
            preparedStatement.setInt(1, companyID);
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
     * @param 	companyId is the id of the company that logged in
     * @param 	title is the title of the coupon the company tries to add
     * @return 	true if the coupon title already exists in company's coupons, otherwise false
     */
    public boolean isCouponTitleExistsInCompanyCoupons(int companyId, String title) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        ResultSet result = null; boolean isTitleExists = false;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "select * from coupons where company_id=? and title=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying data to the question marks by order-
            preparedStatement.setInt(1, companyId);
            preparedStatement.setString(2, title);
            result = preparedStatement.executeQuery();

            //check if result set has a record
            if (result.next())
                isTitleExists = true;

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
        return isTitleExists;
    }

    /**
     * @param 	email is the email of the company
     * @param	password is the password of the company
     * @return	company id of the record with the specified email and password
     */
    public int getCompanyID(String email, String password) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        ResultSet result = null; int companyID = -1;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "select * from companies where email=? and password=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying company id to the question mark
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            result = preparedStatement.executeQuery();

            //check if result set has a record
            if (result.next())
                companyID = result.getInt("id");

        } catch (SQLException exception) {
            Date currentTime = Utils.getCurrentTime();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DBErrorException(currentTime, getClass().getName(), methodName, exception.getMessage());
        }

        finally {
            connectionPool.restoreConnection(connection);
            connectionPool.closeResources(preparedStatement, result);
        }
        return companyID;
    }

    /**
     * @param 	name is the company name of the new company to be added
     * @param	email is the company email of the new company to be added
     * @return  true if company name or email exists in DB, otherwise false
     */
    public boolean isCompanyNameOrEmailExists(String name, String email) throws DBErrorException {
        Connection connection = null; PreparedStatement preparedStatement = null;
        ResultSet result = null; boolean isExists = false;
        try {
            connection = connectionPool.getConnection();
            String sqlStatement = "select * from companies where name=? or email=?";
            preparedStatement = connection.prepareStatement(sqlStatement);

            //applying company name and email to question marks
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
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
            if (connection != null) {
                connectionPool.restoreConnection(connection);
                connectionPool.closeResources(preparedStatement, result);
            }
        }
        return isExists;
    }

    /**
     * @param 	preparedStatement is the preparedStatement object
     * @param 	company is the company object with the data that needs to be set to the question marks
     * @throws 	SQLException
     */
    private void setDataToPreparedStatement(PreparedStatement preparedStatement, Company company) throws SQLException {
        preparedStatement.setString(1, company.getName());
        preparedStatement.setString(2, company.getEmail());
        preparedStatement.setString(3, company.getPassword());
    }

    /**
     * @param 	result is the result set
     * @return	list of all companies in DB
     * @throws 	SQLException
     */
    private List<Company> generateCompaniesFromResultSet(ResultSet result) throws SQLException {
        List<Company> companies = new ArrayList<Company>();

        while (!result.isAfterLast()) { //if result is before last record or last record
            companies.add(generateCompanyFromResultSet(result));
        }
        return companies;
    }

    /**
     * @param 	result is the result set
     * @return	company object extracted from the result set
     * @throws 	SQLException
     */
    private Company generateCompanyFromResultSet(ResultSet result) throws SQLException {
        CouponsDBDAO couponsDBDAO = new CouponsDBDAO();
        Company company = new Company();
        company.setId(result.getInt("id"));
        company.setName(result.getString("name"));
        company.setEmail(result.getString("email"));
        company.setPassword(result.getString("password"));
        company.setCoupons(couponsDBDAO.generateCouponsFromResultSet(result, company.getId()));
        return company;
    }
}
