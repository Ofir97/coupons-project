package com.ofir.coupons.beans;

import com.ofir.coupons.enums.Category;
import com.ofir.coupons.utils.Utils;

import java.sql.Date;

public class Coupon {

    private int id;
    private int companyID;
    private Category category;
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private int amount;
    private double price;
    private String image;

    public Coupon() {}

    //partial constructor
    public Coupon(Category category, String title, String description, Date startDate, Date endDate, int amount, double price, String image) {
        this.category = category;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.amount = amount;
        this.price = price;
        this.image = image;
    }

    //partial constructor
    public Coupon(int companyID, Category category, String title, String description, Date startDate, Date endDate, int amount, double price, String image) {
        this(category, title, description, startDate, endDate, amount, price, image);
        this.companyID = companyID;
    }

    //full constructor
    public Coupon(int id, int companyID, Category category, String title, String description, Date startDate, Date endDate, int amount, double price, String image) {
        this(companyID, category, title, description, startDate, endDate, amount, price, image);
        this.id = id;
    }

    public Coupon(Builder builder) {
        this(builder.id, builder.companyID, builder.category, builder.title, builder.description, builder.startDate,
                builder.endDate, builder.amount, builder.price, builder.image);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return String.format("Coupon- id: %d, company id: %d, category: %s, title: %s, description: %s, " +
                "start date: %s, end date: %s, amount: %d, price: %f, image: %s",
                id, companyID, Utils.convertEnumToString(category), title, description, startDate, endDate, amount, price, image);
    }

    public static class Builder {
        private int id;
        private int companyID;
        private Category category;
        private String title;
        private String description;
        private Date startDate;
        private Date endDate;
        private int amount;
        private double price;
        private String image;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder companyID(int companyID) {
            this.companyID = companyID;
            return this;
        }

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder startDate(Date startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(Date endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public Builder price(double price) {
            this.price = price;
            return this;
        }

        public Builder image(String image) {
            this.image = image;
            return this;
        }

        public Coupon build() {
            if (this.title==null)
                throw new IllegalStateException("cannot set a coupon without a title");
            return new Coupon(this);
        }
    }
}
