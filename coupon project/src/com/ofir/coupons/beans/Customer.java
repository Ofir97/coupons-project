package com.ofir.coupons.beans;

import com.ofir.coupons.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<Coupon> coupons;

    //empty constructor
    public Customer() {}

    //partial constructor
    public Customer(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    //partial constructor
    public Customer(String firstName, String lastName, String email, String password, List<Coupon> coupons) {
        this(firstName, lastName, email, password);
        this.coupons = coupons;
    }

    //partial constructor
    public Customer(int id, String firstName, String lastName, String email, String password) {
        this(firstName, lastName, email, password);
        this.id = id;
    }

    //full constructor
    public Customer(int id, String firstName, String lastName, String email, String password, List<Coupon> coupons) {
        this(id, firstName, lastName, email, password);
        this.coupons = coupons;
    }

    public Customer(Builder builder) {
        this(builder.id, builder.firstName, builder.lastName, builder.email, builder.password, builder.coupons);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
    }

    @Override
    public String toString() {
        return String.format("Customer- id: %d, first name: %s, last name: %s, email: %s, " +
                "password: %s, coupons: %s", id, firstName, lastName, email, password, Utils.getCouponsAsStr(coupons));
    }

    public static class Builder {
        private int id;
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private List<Coupon> coupons = new ArrayList<Coupon>();

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder coupons(List<Coupon> coupons) {
            this.coupons = coupons;
            return this;
        }

        public Customer build() {
            if (firstName == null)
                throw new IllegalStateException(Utils.getCurrentTime() +
                        " | customer build failed: cannot set a customer without first name.");
            if (email == null)
                throw new IllegalStateException(Utils.getCurrentTime() +
                        " | customer build failed: cannot set a customer without an email.");
            if (password == null)
                throw new IllegalStateException(Utils.getCurrentTime() +
                        "| customer build failed: cannot set a customer without a password.");

            return new Customer(this);
        }
    }
}
