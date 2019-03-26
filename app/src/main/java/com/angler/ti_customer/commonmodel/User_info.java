package com.angler.ti_customer.commonmodel;

/**
 * Created by mahalingam on 11-11-2017.
 * User_info
 */

public class User_info {

    private String USER_ID;
    private String USER_TYPE;
    private String USER_NAME;
    private String CUSTOMER_ID;
    private String VALID;


    public String getUSER_TYPE() {
        return USER_TYPE;
    }

    public void setUSER_TYPE(String USER_TYPE) {
        this.USER_TYPE = USER_TYPE;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getUSER_NAME() {
        return USER_NAME;
    }

    public void setUSER_NAME(String USER_NAME) {
        this.USER_NAME = USER_NAME;
    }

    public String getCUSTOMER_ID() {
        return CUSTOMER_ID;
    }

    public void setCUSTOMER_ID(String CUSTOMER_ID) {
        this.CUSTOMER_ID = CUSTOMER_ID;
    }

    public String getVALID() {
        return VALID;
    }

    public void setVALID(String IsActive) {
        this.VALID = IsActive;
    }


}
