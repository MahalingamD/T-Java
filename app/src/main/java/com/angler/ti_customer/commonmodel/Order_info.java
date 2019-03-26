package com.angler.ti_customer.commonmodel;

/**
 * Created by mahalingam on 07-02-2018.
 */

public class Order_info {

    private String order_id;
    private String order_model_name;
    private String order_balance_qty;
    private String order_qty;
    private String order_date;
    private boolean IsActive;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_model_name() {
        return order_model_name;
    }

    public void setOrder_model_name(String order_model_name) {
        this.order_model_name = order_model_name;
    }

    public String getOrder_balance_qty() {
        return order_balance_qty;
    }

    public void setOrder_balance_qty(String order_balance_qty) {
        this.order_balance_qty = order_balance_qty;
    }

    public String getOrder_qty() {
        return order_qty;
    }

    public void setOrder_qty(String order_qty) {
        this.order_qty = order_qty;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public boolean isActive() {
        return IsActive;
    }

    public void setActive(boolean active) {
        IsActive = active;
    }
}
