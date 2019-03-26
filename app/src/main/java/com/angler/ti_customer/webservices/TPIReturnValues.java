package com.angler.ti_customer.webservices;

import com.angler.ti_customer.commonmodel.Booking;
import com.angler.ti_customer.commonmodel.CustomerSelectedItem;
import com.angler.ti_customer.commonmodel.CustomerSiteList;
import com.angler.ti_customer.commonmodel.Dashboard_info;
import com.angler.ti_customer.commonmodel.OrderScheduledInfo;
import com.angler.ti_customer.commonmodel.ServerResponse;
import com.angler.ti_customer.commonmodel.User_info;
import com.angler.ti_customer.commonmodel.Version;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahalingam on 18-01-2018.
 */

public class TPIReturnValues {

    private String ResponseCode = "";
    private String ResponseMessage = "";

    private String Return_Description = "";
    private String Return_Code = "";
    private String RequestNumber = "";

    public String getRequestNumber() {
        return RequestNumber;
    }

    public void setRequestNumber(String requestNumber) {
        RequestNumber = requestNumber;
    }

    public String getReturn_Description() {
        return Return_Description;
    }

    public void setReturn_Description(String return_Description) {
        Return_Description = return_Description;
    }

    public String getReturn_Code() {
        return Return_Code;
    }

    public void setReturn_Code(String return_Code) {
        Return_Code = return_Code;
    }

    public String getResponseCode() {
        return ResponseCode;
    }

    public void setResponseCode(String responseCode) {
        ResponseCode = responseCode;
    }


    public String getResponseMessage() {
        return ResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        ResponseMessage = responseMessage;
    }


    private User_info myUserInfoResponse;

    public User_info getMyUserInfoResponse() {
        return myUserInfoResponse;
    }

    public void setMyUserInfoResponse(User_info myUserInfoResponse) {
        this.myUserInfoResponse = myUserInfoResponse;
    }


    private Version myVersionResponse;

    public Version getMyVersionResponse() {
        return myVersionResponse;
    }

    public void setMyVersionResponse(Version myVersionResponse) {
        this.myVersionResponse = myVersionResponse;
    }


    private ServerResponse myCommonServerResponse;

    public ServerResponse getMyCommonServerResponse() {
        return myCommonServerResponse;
    }

    public void setMyCommonServerResponse(ServerResponse myCommonServerResponse) {
        this.myCommonServerResponse = myCommonServerResponse;
    }

    ArrayList<Dashboard_info> myDashboardList;

    public ArrayList<Dashboard_info> getMyDashboardList() {
        return myDashboardList;
    }

    public void setMyDashboardList(ArrayList<Dashboard_info> myDashboardList) {
        this.myDashboardList = myDashboardList;
    }


    ArrayList<OrderScheduledInfo> myOrderScheduleList;

    public ArrayList<OrderScheduledInfo> getMyOrderScheduleList() {
        return myOrderScheduleList;
    }

    public void setMyOrderScheduleList(ArrayList<OrderScheduledInfo> myOrderScheduleList) {
        this.myOrderScheduleList = myOrderScheduleList;
    }

    ArrayList<CustomerSelectedItem> myCustomerSelectedList;

    public ArrayList<CustomerSelectedItem> getMyCustomerSelectedList() {
        return myCustomerSelectedList;
    }

    public void setMyCustomerSelectedList(ArrayList<CustomerSelectedItem> myCustomerSelectedList) {
        this.myCustomerSelectedList = myCustomerSelectedList;
    }

    ArrayList<CustomerSiteList> myCustomerSiteList;

    public ArrayList<CustomerSiteList> getMyCustomerSiteList() {
        return myCustomerSiteList;
    }

    public void setMyCustomerSiteList(ArrayList<CustomerSiteList> myCustomerSiteList) {
        this.myCustomerSiteList = myCustomerSiteList;
    }

    public List<Booking> bookingList = new ArrayList<>();
}
