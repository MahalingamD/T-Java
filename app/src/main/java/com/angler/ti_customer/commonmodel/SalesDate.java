package com.angler.ti_customer.commonmodel;

/**
 * Created by mahalingam on 26-03-2018.
 */

public class SalesDate {

    String displayDate="";
    String serverDate="";
    boolean Active=false;

    public boolean isActive() {
        return Active;
    }

    public void setActive(boolean active) {
        Active = active;
    }

    public String getDisplayDate() {
        return displayDate;
    }

    public void setDisplayDate(String displayDate) {
        this.displayDate = displayDate;
    }

    public String getServerDate() {
        return serverDate;
    }

    public void setServerDate(String serverDate) {
        this.serverDate = serverDate;
    }
}
