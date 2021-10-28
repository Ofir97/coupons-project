package com.ofir.coupons.beans;

import com.ofir.coupons.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Company {
    private int id;
    private String name;
    private String email;
    private String password;
    private List<Coupon> coupons;

    //empty constructor
    public Company() {}

    //partial constructor
    public Company(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    //partial constructor
    public Company(String name, String email, String password, List<Coupon> coupons) {
        this(name, email, password);
        this.coupons = coupons;
    }

    //partial constructor
    public Company(int id, String name, String email, String password) {
        this(name, email, password);
        this.id = id;
    }

    //full constructor
    public Company(int id, String name, String email, String password, List<Coupon> coupons) {
        this(id, name, email, password);
        this.coupons = coupons;
    }

    public Company(Builder builder) {
        this(builder.id, builder.name, builder.email, builder.password, builder.coupons);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return String.format("Company- id: %d, name: %s, email: %s, password: %s, coupons: %s",
                id, name, email, password, Utils.getCouponsAsStr(coupons));
    }

    public static class Builder {
        private int id;
        private String name;
        private String email;
        private String password;
        private List<Coupon> coupons = new ArrayList<Coupon>();

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
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

        public Company build() {
            if (name == null)
                throw new IllegalStateException(Utils.getCurrentTime() +
                        " | company build failed: cannot set a company without a name");
            if (email == null)
                throw new IllegalStateException(Utils.getCurrentTime() +
                        " | company build failed: cannot set a company without an email");
            if (password == null)
                throw new IllegalStateException(Utils.getCurrentTime() +
                        " | company build failed: cannot set a company without a password");

            return new Company(this);
        }
    }
}
