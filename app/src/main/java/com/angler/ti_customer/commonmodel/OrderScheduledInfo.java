package com.angler.ti_customer.commonmodel;

/**
 * Created by mahalingam on 07-02-2018.
 */

public class OrderScheduledInfo {

    private String MODEL_NAME;
    private String MODEL_DESCRIPTION;
    private String PART_NUMBER;
    private String SCHEDULED_QUANTITY;

    private String ITEM_CODE;
    private String ITEM_DESC;
    private String PRIMARY_UOM_CODE;
    private String INVENTORY_ITEM_ID;

    private boolean IsActive;
    private String Order_Qty;
    private String Remaining_Qty;

    public String getITEM_CODE() {
        return ITEM_CODE;
    }

    public void setITEM_CODE(String ITEM_CODE) {
        this.ITEM_CODE = ITEM_CODE;
    }

    public String getITEM_DESC() {
        return ITEM_DESC;
    }

    public void setITEM_DESC(String ITEM_DESC) {
        this.ITEM_DESC = ITEM_DESC;
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

    public String getRemaining_Qty() {
        return Remaining_Qty;
    }

    public void setRemaining_Qty(String remaining_Qty) {
        Remaining_Qty = remaining_Qty;
    }

    public String getOrder_Qty() {
        return Order_Qty;
    }

    public void setOrder_Qty(String order_Qty) {
        Order_Qty = order_Qty;
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

    public String getSCHEDULED_QUANTITY() {
        return SCHEDULED_QUANTITY;
    }

    public void setSCHEDULED_QUANTITY(String SCHEDULED_QUANTITY) {
        this.SCHEDULED_QUANTITY = SCHEDULED_QUANTITY;
    }

    public boolean isActive() {
        return IsActive;
    }

    public void setActive(boolean active) {
        IsActive = active;
    }
}
