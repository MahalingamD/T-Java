package com.angler.ti_customer.commonmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by mahalingam on 23-03-2018.
 */

public class CustomerSelectedItem implements Parcelable {


    private String CUSTOMER_USER_ID;
    private String CUSTOMER_NUMBER;
    private String CUSTOMER_ID;
    private String CUSTOMER_NAME;
    private String INVENTORY_ITEM_CODE;
    private String INVENTORY_ITEM_DESC;
    private String PRIMARY_UOM_CODE;
    private String INVENTORY_ITEM_ID;
    private String MODEL_NAME;
    private String MODEL_DESCRIPTION;
    private String PART_NUMBER;
    private String SHIP_TO_LOCATION_CODE;
    private String SHIP_TO_LOCATION_ID;
    private String ADDRESS1;
    private String ADDRESS2;
    private String ADDRESS3;
    private String Address4;
    private String ADDRESS5;
    private String SCHEDULE_QUANTITY;
    private String SCHEDULE_DATE;
    private String Pending_Qty;

    public String getPending_Qty() {
        return Pending_Qty;
    }

    public void setPending_Qty(String pending_Qty) {
        Pending_Qty = pending_Qty;
    }

    public CustomerSelectedItem() {

    }


    public String getCUSTOMER_USER_ID() {
        return CUSTOMER_USER_ID;
    }

    public void setCUSTOMER_USER_ID(String CUSTOMER_USER_ID) {
        this.CUSTOMER_USER_ID = CUSTOMER_USER_ID;
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

    public String getCUSTOMER_NAME() {
        return CUSTOMER_NAME;
    }

    public void setCUSTOMER_NAME(String CUSTOMER_NAME) {
        this.CUSTOMER_NAME = CUSTOMER_NAME;
    }

    public String getINVENTORY_ITEM_CODE() {
        return INVENTORY_ITEM_CODE;
    }

    public void setINVENTORY_ITEM_CODE(String INVENTORY_ITEM_CODE) {
        this.INVENTORY_ITEM_CODE = INVENTORY_ITEM_CODE;
    }

    public String getINVENTORY_ITEM_DESC() {
        return INVENTORY_ITEM_DESC;
    }

    public void setINVENTORY_ITEM_DESC(String INVENTORY_ITEM_DESC) {
        this.INVENTORY_ITEM_DESC = INVENTORY_ITEM_DESC;
    }

    public String getPRIMARY_UOM_CODE() {
        return PRIMARY_UOM_CODE;
    }

    public void setPRIMARY_UOM_CODE(String PRIMARY_UOM_CODE) {
        this.PRIMARY_UOM_CODE = PRIMARY_UOM_CODE;
    }

    public String getINVENTORY_ITEM_ID() {
        return INVENTORY_ITEM_ID;
    }

    public void setINVENTORY_ITEM_ID(String INVENTORY_ITEM_ID) {
        this.INVENTORY_ITEM_ID = INVENTORY_ITEM_ID;
    }

    public String getMODEL_NAME() {
        return MODEL_NAME;
    }

    public void setMODEL_NAME(String MODEL_NAME) {
        this.MODEL_NAME = MODEL_NAME;
    }

    public String getMODEL_DESCRIPTION() {
        return MODEL_DESCRIPTION;
    }

    public void setMODEL_DESCRIPTION(String MODEL_DESCRIPTION) {
        this.MODEL_DESCRIPTION = MODEL_DESCRIPTION;
    }

    public String getPART_NUMBER() {
        return PART_NUMBER;
    }

    public void setPART_NUMBER(String PART_NUMBER) {
        this.PART_NUMBER = PART_NUMBER;
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

    public String getADDRESS1() {
        return ADDRESS1;
    }

    public void setADDRESS1(String ADDRESS1) {
        this.ADDRESS1 = ADDRESS1;
    }

    public String getADDRESS2() {
        return ADDRESS2;
    }

    public void setADDRESS2(String ADDRESS2) {
        this.ADDRESS2 = ADDRESS2;
    }

    public String getADDRESS3() {
        return ADDRESS3;
    }

    public void setADDRESS3(String ADDRESS3) {
        this.ADDRESS3 = ADDRESS3;
    }

    public String getAddress4() {
        return Address4;
    }

    public void setAddress4(String Address4) {
        this.Address4 = Address4;
    }

    public String getADDRESS5() {
        return ADDRESS5;
    }

    public void setADDRESS5(String ADDRESS5) {
        this.ADDRESS5 = ADDRESS5;
    }

    public String getSCHEDULE_QUANTITY() {
        return SCHEDULE_QUANTITY;
    }

    public void setSCHEDULE_QUANTITY(String SCHEDULE_QUANTITY) {
        this.SCHEDULE_QUANTITY = SCHEDULE_QUANTITY;
    }

    public String getSCHEDULE_DATE() {
        return SCHEDULE_DATE;
    }

    public void setSCHEDULE_DATE(String SCHEDULE_DATE) {
        this.SCHEDULE_DATE = SCHEDULE_DATE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.CUSTOMER_USER_ID);
        dest.writeString(this.CUSTOMER_NUMBER);
        dest.writeString(this.CUSTOMER_ID);
        dest.writeString(this.CUSTOMER_NAME);

        dest.writeString(this.INVENTORY_ITEM_CODE);
        dest.writeString(this.INVENTORY_ITEM_DESC);
        dest.writeString(this.PRIMARY_UOM_CODE);
    }


    public CustomerSelectedItem(Parcel in) {
        this.CUSTOMER_USER_ID = in.readString();
        this.CUSTOMER_NUMBER = in.readString();
        this.CUSTOMER_ID = in.readString();
        this.CUSTOMER_NAME = in.readString();

        this.INVENTORY_ITEM_CODE = in.readString();
        this.INVENTORY_ITEM_DESC = in.readString();
        this.PRIMARY_UOM_CODE = in.readString();
    }

    public static final Creator<CustomerSelectedItem> CREATOR = new Creator<CustomerSelectedItem>() {
        @Override
        public CustomerSelectedItem createFromParcel(Parcel source) {
            return new CustomerSelectedItem(source);
        }

        @Override
        public CustomerSelectedItem[] newArray(int size) {
            return new CustomerSelectedItem[size];
        }
    };
}
