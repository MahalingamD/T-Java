package com.angler.ti_customer.commonmodel;

import com.google.gson.Gson;

/**
 * Created by mahalingam on 23-03-2018.
 */

public class CustomerSiteList {

    private String CUSTOMER_USER_ID;
    private String CUSTOMER_NAME;
    private String CUSTOMER_NUMBER;
    private String CUSTOMER_ID;
    private String SHIP_TO_LOCATION_CODE;
    private String SHIP_TO_LOCATION_ID;

    public static CustomerSiteList objectFromData(String str) {

        return new Gson().fromJson(str, CustomerSiteList.class);
    }

    public String getCUSTOMER_USER_ID() {
        return CUSTOMER_USER_ID;
    }

    public void setCUSTOMER_USER_ID(String CUSTOMER_USER_ID) {
        this.CUSTOMER_USER_ID = CUSTOMER_USER_ID;
    }

    public String getCUSTOMER_NAME() {
        return CUSTOMER_NAME;
    }

    public void setCUSTOMER_NAME(String CUSTOMER_NAME) {
        this.CUSTOMER_NAME = CUSTOMER_NAME;
    }

    public String getCUSTOMER_NUMBER() {
        return CUSTOMER_NUMBER;
    }

    public void setCUSTOMER_NUMBER(String CUSTOMER_NUMBER) {
        this.CUSTOMER_NUMBER = CUSTOMER_NUMBER;
    }

    public String getCUSTOMER_ID() {
        return CUSTOMER_ID;
    }

    public void setCUSTOMER_ID(String CUSTOMER_ID) {
        this.CUSTOMER_ID = CUSTOMER_ID;
    }

    public String getSHIP_TO_LOCATION_CODE() {
        return SHIP_TO_LOCATION_CODE;
    }

    public void setSHIP_TO_LOCATION_CODE(String SHIP_TO_LOCATION_CODE) {
        this.SHIP_TO_LOCATION_CODE = SHIP_TO_LOCATION_CODE;
    }

    public String getSHIP_TO_LOCATION_ID() {
        return SHIP_TO_LOCATION_ID;
    }

    public void setSHIP_TO_LOCATION_ID(String SHIP_TO_LOCATION_ID) {
        this.SHIP_TO_LOCATION_ID = SHIP_TO_LOCATION_ID;
    }
}
