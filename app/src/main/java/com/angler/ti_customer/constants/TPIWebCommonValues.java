package com.angler.ti_customer.constants;

/**
 * Created by mahalingam on 28-10-2017.
 * TIDCWebCommonValues
 */

public interface TPIWebCommonValues {

    String COMMON_RESPONSE_NOT_RECEIVE_MSG = "Server not reachable try again later";

    String ALERT_NETWORK_NOT_AVAILABLE = "Network is not available. Please try again later";

    String ALERT_SOMETHING_WENT_WRONG = "Something went wrong. Please try again later";


    String ALERT_LOADING = "Please wait...";

    String RESPONSE = "Response";

    String RESPONSE_CODE = "response_code";

    String RESPONSE_MSG = "response_message";

    String RESPONSE_CODE_CAP = "Response_Code";
    String RESPONSE_MSG_CAP = "Response_description";

    String RESPONSE_CODE_UPDATE_AVAILABLE = "1";
    String RESPONSE_CODE_FAILURE = "3";

    String RESPONSE_CODE_FAILURE_MESSAGE = "Server not reachable";


    String RESPONSE_CODE_NO_DATA = "0";
    String RESPONSE_CODE_SUCCESS = "1";
    String RESPONSE_CODE_EMPTY = "2";
    String RESPONSE_CODE_FAIL = "-1";
    String RESPONSE_CODE_INVALID = "-2";
    String RESPONSE_CODE_CONNECTION_FAILURE = "-100";

    String VERSION_UPDATE_DETAILS = "GetAppUpdate?";
    String APP_VERSION = "App_Version?";
    String APP_TRACK = "track?";
    String VALIDATELOGIN = "ValidateLogin?";
    String DASHBOARDDATA = "DashBoardData?";
    String ORDERDETAIL = "GetOrderScheduledQty?";
    String CUSTOMERITEMLIST = "CustomerItemList?";
    String CUSTOMERSITELIST = "CustomerSiteList?";
    String CARTINSERT = "SalesOrderCartInsert/get?";
    String DELETECART = "SalesOrderCartDelete/get?";
    String CONFIRMORDER = "SalesOrderCartConfirm/get?";
    String BOOKING_HISTORY = "TPI_Customer/PutCustomerReqList?";
    String BOOKING_HISTORY_DETAIL = "TPI_Customer/PutRequestItemList?";

    String VERSION_MSG = "A new version of Diamond-Directory is available.";
}
