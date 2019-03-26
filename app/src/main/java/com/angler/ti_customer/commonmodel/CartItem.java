package com.angler.ti_customer.commonmodel;

/**
 * Created by ravi on 26/09/17.
 */

public class CartItem {
    private String id;
    private String name;
    private String description;
    private String price;


    public CartItem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
