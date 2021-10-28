# Coupons Project

This system allows companies to create coupons that can be purchased by customers.

The system has 3 client types: administrator, company and customer:

Each client type can login to the system, but each can perform different operations.

(for example - administrator can view all customers registered to the system, 
and customer can purchase a coupon)

This system is capable of deleting expired coupons from DB every 24 hours.
(task that is done by CouponExpirationDailyJob that must run on parallel with the execution of the program, 
so users can still use the application while the job removes all expired coupons at the same time)

The moment the application starts its execution - connection pool is created and holds 10 connections that are ready to use.
(eager singleton). each time a method requires a connection - it will be removed from the pool and when finished with the connection will be restored back to the pool. 
All connections are closed by the end of the program execution.

---

## _installation requirements_

- Eclipse IDE Version: 2020-12 

- JDK-11.0.11

- MySQL Server 8.0.25

- MySQL Workbench 8.0.25 (MySQL Server GUI)

- Connector/J 8.0.25 (in MySQL Installer)

- MySQL connector J - jar 8.0.25 (for java application: via Build Path -> Add External JARs..)

---

## _SQL Statements for Tables Creation_

### Categories Table

CREATE TABLE `categories` (
`id` int NOT NULL AUTO_INCREMENT,
`name` varchar(60) NOT NULL,
PRIMARY KEY (`id`))

- id and category name cannot be null.
category name can have 60 characters max.

-------------------------------------------------------------------------

### Companies Table

CREATE TABLE `companies` (
`id` int NOT NULL AUTO_INCREMENT,
`name` varchar(100) NOT NULL,
`email` varchar(150) NOT NULL,
`password` varchar(200) NOT NULL,
PRIMARY KEY (`id`))

- all fields in companies table cannot be null.
name should be up to 100 characters, 150 characters for email,
and 200 for password(in case of encryption)

ALTER TABLE `coupon_system`.`companies`
ADD UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE;

- email field has 'unique constraint' so company cannot update its email to an existing one in DB. 

-------------------------------------------------------------------------

### Coupons Table

CREATE TABLE `coupons` (
`id` int NOT NULL AUTO_INCREMENT,
`company_id` int NOT NULL,
`category_id` int NOT NULL,
`title` varchar(200) DEFAULT NULL,
`description` varchar(800) DEFAULT NULL,
`start_date` date NOT NULL,
`end_date` date NOT NULL,
`amount` int DEFAULT NULL,
`price` double DEFAULT NULL,
`image` varchar(700) DEFAULT NULL,
PRIMARY KEY (`id`),
KEY `company_id_idx` (`company_id`),
KEY `category_id_idx` (`category_id`),
CONSTRAINT `category_id_fk` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
CONSTRAINT `company_id_fk` FOREIGN KEY (`company_id`) REFERENCES `companies` (`id`) ON DELETE CASCADE)

- company id and category id cannot be null, and should point to existing PKs.
title and description may be null in case the company doesn't want to enter title/description for the coupon.
  
###### both category id and company id are foreign keys:
  - category id is defined as 'RESTRICT' for both delete and update.(because we don't want to delete a category from categories,
  or update category id from categories table).
  - company id is defined as 'RESTRICT' for update because company id cannot be updated. 
  company id is defined as 'CASCADE' for delete because if a company is deleted - all its coupons should be removed as well.

-------------------------------------------------------------------------

### Customers Table

CREATE TABLE `customers` (
`id` INT NOT NULL AUTO_INCREMENT,
`first_name` VARCHAR(100) NOT NULL,
`last_name` VARCHAR(100) NOT NULL,
`email` VARCHAR(150) NOT NULL,
`password` VARCHAR(200) NOT NULL,
PRIMARY KEY (`id`));

- all fields in customer table cannot be null.
first name and last name can have no more than 100 characters.
email can be no more than 150 characters,
and password can be up to 200 characters in case of password encryption.

ALTER TABLE `coupon_system`.`customers`
ADD UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE;
- email field has 'unique constraint' so customer cannot update its email to an existing one in DB.

-------------------------------------------------------------------------

### Customers_VS_Coupons Table

CREATE TABLE `customers_vs_coupons` (
`customer_id` int NOT NULL,
`coupon_id` int NOT NULL,
PRIMARY KEY (`customer_id`,`coupon_id`),
KEY `coupon_id_fk_idx` (`coupon_id`),
CONSTRAINT `coupon_id_fk` FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`id`) ON DELETE CASCADE,
CONSTRAINT `customer_id_fk` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE CASCADE)

 ###### both customer id and coupon id are PKs and FKs:
- customer id is defined as 'RESTRICT' for update, because customer id cannot be updated.
customer id is defined as 'CASCADE' for delete, because if a customer is removed - all his coupons purchases will be removed as well.


- coupon id is defined as 'RESTRICT' for update, because coupon id cannot be updated.
coupon id is defined as 'CASCADE' for delete, because if a coupon is removed - all its purchases by customers will be removed as well.

-------------------------------------------------------------------------

## _Changes to instructions_ 

#### In CustomerFacade, method -public void purchaseCoupon(int couponId)

- instead of getting Coupon object as an argument - this method can get coupon id and make a purchase

#### In Coupon class, toString()

- instead of returning the Category enum(with capital letters), the enum is converted to String
with small letters except for the first letter. (Utils.convertEnumToString(Enum myEnum))
  
#### In Company class, toString() and Customer class, toString()
- Utils.getCouponsAsStr(coupons) is used for a better String representation of the list of coupons.

#### Log Files
- there are 2 log files: one for documenting all exceptions that have been thrown(exception_log.txt)\
and another for documenting all successful operations(operations_log.txt)

#### In getAllCoupons(), getAllCustomers(), getAllCompanies()
- since coupon id is defined as 'id' in DB, it was necessary to add alias to id as 'coupon_id'. 
(so it will not be confused with company/customer id in generateCouponsFromResultSet(ResultSet result, int id) in CouponsDBDAO). 
It was also possible to change the coupon id field in DB.

#### In LoginManager - login(String email, String password, ClientType clientType)
- in case of login fail (because of bad email or password)- instead of returning null as requested, BadLoginException is thrown to the user with a detailed and clear message. (instead of handling NullPointerException in main method and throwing this exception from main)
 