package com.angler.ti_customer.webservices;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.angler.ti_customer.BuildConfig;
import com.angler.ti_customer.app.AppController;
import com.angler.ti_customer.cachememory.TPIPreferences;
import com.angler.ti_customer.commonmodel.Booking;
import com.angler.ti_customer.commonmodel.CustomerSelectedItem;
import com.angler.ti_customer.commonmodel.CustomerSiteList;
import com.angler.ti_customer.commonmodel.Dashboard_info;
import com.angler.ti_customer.commonmodel.OrderScheduledInfo;
import com.angler.ti_customer.commonmodel.ServerResponse;
import com.angler.ti_customer.commonmodel.Version;
import com.angler.ti_customer.constants.TPICommonValues;
import com.angler.ti_customer.constants.TPIWebCommonValues;
import com.angler.ti_customer.listener.TPIWebServiceCallBack;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;


/**
 * Created by Mahalingam on 18-01-2018.
 * TPIWebService
 */

public class TPIWebService implements TPIWebCommonValues, TPICommonValues {

    public static String TAG = TPIWebService.class.getSimpleName();

    private Context myContext;
    private String MAIN_URL;
    private String AddCart_URL;
    private String APP_Update_URL;
    private String APP_Insert_URL;

    private TPIReturnValues myReturnValues;
    private TPIPreferences myPreferences;
    private RequestQueue myRequestQueue;

    private int MY_SOCKET_TIMEOUT_MS = 30000;
    private HttpURLConnection urlConnection = null;

    public TPIWebService(Context aContext) {
        myContext = aContext;
        myReturnValues = new TPIReturnValues();
        myPreferences = new TPIPreferences(myContext);
        myRequestQueue = Volley.newRequestQueue(myContext);
        //  myDBHelper = new TIDCDBHepler(myContext);
        MAIN_URL = BuildConfig.common_url;
        AddCart_URL = BuildConfig.AddCart_url;
        APP_Update_URL = BuildConfig.AppUpdate_url;
        APP_Insert_URL = BuildConfig.AppInsert_url;
    }


    /**
     * Function to get CommonResponse Status and Store in Pojo Class
     *
     * @param jsonObject JSONObject
     */
    public void getResponseStatus(JSONObject jsonObject) {
        try {
            myReturnValues.setResponseCode(jsonObject.getString(RESPONSE_CODE));
            myReturnValues.setResponseMessage(jsonObject.getString(RESPONSE_MSG));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void getResponseCapStatus(JSONObject jsonObject) {
        try {
            myReturnValues.setResponseCode(jsonObject.getString(RESPONSE_CODE_CAP));
            myReturnValues.setResponseMessage(jsonObject.getString(RESPONSE_MSG_CAP));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getResponseUpdateStatus(JSONObject jsonObject) {

        try {

            JSONObject aJSONSegObject = jsonObject.getJSONObject("Track");
            JSONObject aResponseJSON = aJSONSegObject.getJSONObject(RESPONSE);
            myReturnValues.setResponseCode(aResponseJSON
                    .getString(RESPONSE_CODE_CAP));
            myReturnValues.setResponseMessage(aResponseJSON
                    .getString(RESPONSE_MSG_CAP));
          /*  if ((aResponseJSON.has(RESPONSE_DETAILS)) && (aResponseJSON.getString(RESPONSE_DETAILS) != null) &&
                    (!aResponseJSON.getString(RESPONSE_DETAILS).equals(""))) {
                myReturnValues.setResponseDetails(aResponseJSON
                        .getString(RESPONSE_DETAILS));
            }*/

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public Boolean ValidJSON(JSONObject aJsonObj, String aValueSTR) {

        Boolean aBoolean = false;

        try {
            if ((aJsonObj.has(aValueSTR)) && (aJsonObj.getString(aValueSTR) != null) &&
                    (!aJsonObj.getString(aValueSTR).equals(""))) {

                aBoolean = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return aBoolean;
    }


    public void checkVersionUpdate(final String aInput, final TPIWebServiceCallBack callback) {
        myReturnValues = new TPIReturnValues();
        final String aUrl = APP_Update_URL + APP_VERSION + aInput;

        Log.e(TAG, "appVersionUpdate: " + aUrl);

        //final UserInfo aUserInfo = new UserInfo();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, aUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i(TAG, "onResponse appVersionUpdate: " + response);
                            JSONObject aBaseJsonObject = new JSONObject(response);
                            JSONObject aResponseJsonObject = aBaseJsonObject.getJSONObject(RESPONSE);
                            getResponseCapStatus(aResponseJsonObject);

                            if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {
                                JSONObject aVersionJsonObject = aBaseJsonObject.getJSONObject("APPVERSION");
                                Version aVersion = new Version();

                                aVersion.setMyCurrentVersion(aVersionJsonObject.getString("VERSION_CODE"));
                                aVersion.setMyStatus(aVersionJsonObject.getString("STATUS"));
                                aVersion.setMyUpdateType(aVersionJsonObject.getString("UPDATE_TYPE"));
                                aVersion.setMyMessage(aVersionJsonObject.getString("MESSAGE"));
                                aVersion.setMyUpdateURL(aVersionJsonObject.getString("URL"));
                                myReturnValues.setMyVersionResponse(aVersion);
                            }

                        } catch (Exception e) {
                            //   Crashlytics.logException(e);
                            myReturnValues.setResponseCode(RESPONSE_CODE_CONNECTION_FAILURE);
                            myReturnValues.setResponseMessage(ALERT_SOMETHING_WENT_WRONG);
                            Log.e(TAG, "appVersionUpdate: " + e.toString());
                        }
                        callback.onSuccess(myReturnValues);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onFailer(COMMON_RESPONSE_NOT_RECEIVE_MSG);
                        Log.e(TAG, "onErrorResponse loginRequest: " + error.toString());
                    }
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest, TAG);
    }


    public void insertUpdateVersionUpdate(String aInputStr, TPIWebServiceCallBack tpiWebServiceCallBack) {
        myReturnValues = new TPIReturnValues();

        final String aUrl = APP_Insert_URL + APP_TRACK + aInputStr;

        Log.e("VERSION_Insert", aUrl);

        StringRequest aStringRequest = new StringRequest(Request.Method.GET, aUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // JSON Response Object Initialization
                    JSONObject aJsonResponseObject;
                    aJsonResponseObject = new JSONObject(response);
                    getResponseUpdateStatus(aJsonResponseObject);

                    if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {
                    } else {

                    }

                    Log.e("InsertSuccess Response", response.toString());

                } catch (Exception e) {
                    myReturnValues.setResponseCode(RESPONSE_CODE_FAILURE);
                    myReturnValues.setResponseMessage(RESPONSE_CODE_FAILURE_MESSAGE);

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError aError) {
                Log.e("Error Response", aError.toString());

            }

        });


        aStringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(aStringRequest, TAG);

    }


    public void getLogin(final String aInput, final TPIWebServiceCallBack callback) {

        final String aUrl = MAIN_URL + VALIDATELOGIN + aInput;
        myReturnValues = new TPIReturnValues();

        Log.e(TAG, "ValidateOTP: " + aUrl);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, aUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(TAG, "Response: " + response);
                    JSONObject aBaseJsonObject = new JSONObject(response);
                    JSONObject aResponseJsonObject = aBaseJsonObject.getJSONObject(RESPONSE);
                    getResponseStatus(aResponseJsonObject);

                    if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {
                        Moshi aUserMoshi = new Moshi.Builder().build();
                        JsonAdapter<ServerResponse> aUserJsonAdapter = aUserMoshi.adapter(ServerResponse.class);
                        ServerResponse aUserInfoList = aUserJsonAdapter.fromJson(aBaseJsonObject.toString());

                        myReturnValues.setMyCommonServerResponse(aUserInfoList);
                    }
                } catch (Exception e) {
                    myReturnValues.setResponseCode(RESPONSE_CODE_CONNECTION_FAILURE);
                    myReturnValues.setResponseMessage(ALERT_SOMETHING_WENT_WRONG);
                    e.printStackTrace();
                    Log.e(TAG, "Login: " + e.toString());
                }
                callback.onSuccess(myReturnValues);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailer(COMMON_RESPONSE_NOT_RECEIVE_MSG);
                Log.e(TAG, "onErrorResponse loginRequest: " + error.toString());
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest, TAG);
    }


    public void getDashboardDetail(final String aInput, final TPIWebServiceCallBack callback) {

        final String aUrl = MAIN_URL + DASHBOARDDATA + aInput;
        myReturnValues = new TPIReturnValues();

        Log.e(TAG, "DASHBOARDDATA: " + aUrl);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, aUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(TAG, "Response: " + response);
                    JSONObject aBaseJsonObject = new JSONObject(response);
                    JSONObject aResponseJsonObject = aBaseJsonObject.getJSONObject(RESPONSE);
                    getResponseStatus(aResponseJsonObject);

                    ArrayList<Dashboard_info> aDashBoardList = new ArrayList<>();

                    if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {

                        JSONArray aDashBoardJSONArray = aBaseJsonObject.getJSONArray("Dashboard_info");

                        Log.e("TAG", "" + aDashBoardJSONArray.length());

                        for (int i = 0; i < aDashBoardJSONArray.length(); i++) {
                            JSONObject aJsonObj = aDashBoardJSONArray.getJSONObject(i);
                            Dashboard_info aDashBoard = new Dashboard_info();

                            aDashBoard.setSHIP_TO_SITE_ID(aJsonObj.getString("SHIP_TO_SITE_ID"));
                            aDashBoard.setSHIP_TO_SITE_NAME(aJsonObj.getString("SHIP_TO_SITE_NAME"));
                            aDashBoard.setADDRESS1(aJsonObj.getString("ADDRESS1"));
                            aDashBoard.setADDRESS2(aJsonObj.getString("ADDRESS2"));
                            aDashBoard.setADDRESS3(aJsonObj.getString("ADDRESS3"));
                            aDashBoard.setADDRESS4(aJsonObj.getString("ADDRESS4"));
                            aDashBoard.setADDRESS5(aJsonObj.getString("ADDRESS5"));
                            aDashBoard.setCUSTOMER_ID(aJsonObj.getString("CUSTOMER_ID"));
                            aDashBoard.setCUSTOMER_NAME(aJsonObj.getString("CUSTOMER_NAME"));
                            aDashBoard.setCUSTOMER_NUMBER(aJsonObj.getString("CUSTOMER_NUMBER"));

                            aDashBoardList.add(aDashBoard);
                        }

                        myReturnValues.setMyDashboardList(aDashBoardList);


                        //   myReturnValues.setMyCommonServerResponse(aUserInfoList);
                    }
                } catch (Exception e) {
                    myReturnValues.setResponseCode(RESPONSE_CODE_CONNECTION_FAILURE);
                    myReturnValues.setResponseMessage(ALERT_SOMETHING_WENT_WRONG);
                    e.printStackTrace();
                    Log.e(TAG, "Login: " + e.toString());
                }
                callback.onSuccess(myReturnValues);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailer(COMMON_RESPONSE_NOT_RECEIVE_MSG);
                Log.e(TAG, "onErrorResponse loginRequest: " + error.toString());
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest, TAG);
    }


    public void getOrderDetail(final String aInput, final TPIWebServiceCallBack callback) {

        final String aUrl = MAIN_URL + ORDERDETAIL + aInput;
        myReturnValues = new TPIReturnValues();

        Log.e(TAG, "DASHBOARDDATA: " + aUrl);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, aUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(TAG, "Response: " + response);
                    JSONObject aBaseJsonObject = new JSONObject(response);
                    JSONObject aResponseJsonObject = aBaseJsonObject.getJSONObject(RESPONSE);

                    getResponseStatus(aResponseJsonObject);

                    ArrayList<OrderScheduledInfo> aOrderInfoList = new ArrayList<>();

                    if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {

                        JSONArray aOrderInfoJSONArray = aBaseJsonObject.getJSONArray("OrderScheduledQty");

                        Log.e("TAG", "" + aOrderInfoJSONArray.length());

                        for (int i = 0; i < aOrderInfoJSONArray.length(); i++) {
                            JSONObject aJsonObj = aOrderInfoJSONArray.getJSONObject(i);
                            OrderScheduledInfo aOrderInfo = new OrderScheduledInfo();

                            aOrderInfo.setMODEL_NAME(aJsonObj.getString("MODEL_NAME"));
                            aOrderInfo.setMODEL_DESCRIPTION(aJsonObj.getString("MODEL_DESCRIPTION"));
                            aOrderInfo.setPART_NUMBER(aJsonObj.getString("PART_NUMBER"));
                            aOrderInfo.setSCHEDULED_QUANTITY(aJsonObj.getString("SCHEDULED_QUANTITY"));

                            aOrderInfo.setITEM_CODE(aJsonObj.getString("ITEM_CODE"));
                            aOrderInfo.setITEM_DESC(aJsonObj.getString("ITEM_DESC"));
                            aOrderInfo.setPRIMARY_UOM_CODE(aJsonObj.getString("PRIMARY_UOM_CODE"));
                            aOrderInfo.setINVENTORY_ITEM_ID(aJsonObj.getString("INVENTORY_ITEM_ID"));

                            aOrderInfoList.add(aOrderInfo);
                        }

                        myReturnValues.setMyOrderScheduleList(aOrderInfoList);


                        //   myReturnValues.setMyCommonServerResponse(aUserInfoList);
                    }
                } catch (Exception e) {
                    myReturnValues.setResponseCode(RESPONSE_CODE_CONNECTION_FAILURE);
                    myReturnValues.setResponseMessage(ALERT_SOMETHING_WENT_WRONG);
                    e.printStackTrace();
                    Log.e(TAG, "Login: " + e.toString());
                }
                callback.onSuccess(myReturnValues);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailer(COMMON_RESPONSE_NOT_RECEIVE_MSG);
                Log.e(TAG, "onErrorResponse loginRequest: " + error.toString());
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest, TAG);
    }

    public void getCustomerItemList(String aInputStr, String aType, final TPIWebServiceCallBack callback) {

        final String aUrl = MAIN_URL + CUSTOMERITEMLIST + aInputStr;
        myReturnValues = new TPIReturnValues();

        Log.e(TAG, "CUSTOMERITEMLIST: " + aUrl);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, aUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    Log.e(TAG, "Response: " + response);
                    JSONObject aBaseJsonObject = new JSONObject(response);
                    JSONObject aResponseJsonObject = aBaseJsonObject.getJSONObject(RESPONSE);

                    getResponseStatus(aResponseJsonObject);

                    ArrayList<CustomerSelectedItem> aOrderInfoList = new ArrayList<>();

                    if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {

                        JSONArray aOrderInfoJSONArray = aBaseJsonObject.getJSONArray("OrderScheduledQty");

                        Log.e("TAG", "" + aOrderInfoJSONArray.length());

                        for (int i = 0; i < aOrderInfoJSONArray.length(); i++) {
                            JSONObject aJsonObj = aOrderInfoJSONArray.getJSONObject(i);
                            CustomerSelectedItem aOrderInfo = new CustomerSelectedItem();

                            aOrderInfo.setCUSTOMER_USER_ID(aJsonObj.getString("CUSTOMER_USER_ID"));
                            aOrderInfo.setCUSTOMER_NUMBER(aJsonObj.getString("CUSTOMER_NUMBER"));
                            aOrderInfo.setCUSTOMER_ID(aJsonObj.getString("CUSTOMER_ID"));
                            aOrderInfo.setCUSTOMER_NAME(aJsonObj.getString("CUSTOMER_NAME"));
                            aOrderInfo.setINVENTORY_ITEM_CODE(aJsonObj.getString("INVENTORY_ITEM_CODE"));
                            aOrderInfo.setINVENTORY_ITEM_DESC(aJsonObj.getString("INVENTORY_ITEM_DESC"));
                            aOrderInfo.setINVENTORY_ITEM_ID(aJsonObj.getString("INVENTORY_ITEM_ID"));
                            aOrderInfo.setPRIMARY_UOM_CODE(aJsonObj.getString("PRIMARY_UOM_CODE"));

                            aOrderInfo.setMODEL_NAME(aJsonObj.getString("MODEL_NAME"));
                            aOrderInfo.setMODEL_DESCRIPTION(aJsonObj.getString("MODEL_DESCRIPTION"));
                            aOrderInfo.setPART_NUMBER(aJsonObj.getString("PART_NUMBER"));

                            aOrderInfo.setSHIP_TO_LOCATION_CODE(aJsonObj.getString("SHIP_TO_LOCATION_CODE"));
                            aOrderInfo.setSHIP_TO_LOCATION_ID(aJsonObj.getString("SHIP_TO_LOCATION_ID"));
                            aOrderInfo.setADDRESS1(aJsonObj.getString("ADDRESS1"));
                            aOrderInfo.setADDRESS2(aJsonObj.getString("ADDRESS2"));
                            aOrderInfo.setADDRESS3(aJsonObj.getString("ADDRESS3"));
                            aOrderInfo.setAddress4(aJsonObj.getString("Address4"));
                            aOrderInfo.setADDRESS5(aJsonObj.getString("ADDRESS5"));

                            aOrderInfo.setSCHEDULE_QUANTITY(aJsonObj.getString("SCHEDULE_QUANTITY"));
                            aOrderInfo.setSCHEDULE_DATE(aJsonObj.getString("SCHEDULE_DATE"));
                            aOrderInfo.setPending_Qty(aJsonObj.getString("Pending_Qty"));


                            aOrderInfoList.add(aOrderInfo);
                        }


                        myReturnValues.setMyCustomerSelectedList(aOrderInfoList);


                        //   myReturnValues.setMyCommonServerResponse(aUserInfoList);
                    }
                } catch (Exception e) {
                    myReturnValues.setResponseCode(RESPONSE_CODE_CONNECTION_FAILURE);
                    myReturnValues.setResponseMessage(ALERT_SOMETHING_WENT_WRONG);
                    e.printStackTrace();
                    Log.e(TAG, "Login: " + e.toString());
                }
                callback.onSuccess(myReturnValues);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailer(COMMON_RESPONSE_NOT_RECEIVE_MSG);
                Log.e(TAG, "onErrorResponse loginRequest: " + error.toString());
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest, TAG);
    }


    public void getCustomerSiteList(String aInputStr, final TPIWebServiceCallBack callback) {

        final String aUrl = MAIN_URL + CUSTOMERSITELIST + aInputStr;
        myReturnValues = new TPIReturnValues();


        Log.e(TAG, "CUSTOMERSITELIST: " + aUrl);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, aUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(TAG, "Response: " + response);
                    JSONObject aBaseJsonObject = new JSONObject(response);
                    JSONObject aResponseJsonObject = aBaseJsonObject.getJSONObject(RESPONSE);

                    getResponseStatus(aResponseJsonObject);

                    ArrayList<CustomerSiteList> aOrderInfoList = new ArrayList<>();

                    if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {

                        JSONArray aOrderInfoJSONArray = aBaseJsonObject.getJSONArray("CustomerSiteList");

                        Log.e("TAG", "" + aOrderInfoJSONArray.length());

                        for (int i = 0; i < aOrderInfoJSONArray.length(); i++) {
                            JSONObject aJsonObj = aOrderInfoJSONArray.getJSONObject(i);
                            CustomerSiteList aCustomerSite = new CustomerSiteList();

                            aCustomerSite.setCUSTOMER_ID(aJsonObj.getString("CUSTOMER_ID"));
                            aCustomerSite.setCUSTOMER_NAME(aJsonObj.getString("CUSTOMER_NAME"));
                            aCustomerSite.setCUSTOMER_NUMBER(aJsonObj.getString("CUSTOMER_NUMBER"));
                            aCustomerSite.setCUSTOMER_USER_ID(aJsonObj.getString("CUSTOMER_USER_ID"));
                            aCustomerSite.setSHIP_TO_LOCATION_CODE(aJsonObj.getString("SHIP_TO_LOCATION_CODE"));
                            aCustomerSite.setSHIP_TO_LOCATION_ID(aJsonObj.getString("SHIP_TO_LOCATION_ID"));

                            aOrderInfoList.add(aCustomerSite);
                        }


                        myReturnValues.setMyCustomerSiteList(aOrderInfoList);

                    }
                } catch (Exception e) {
                    myReturnValues.setResponseCode(RESPONSE_CODE_CONNECTION_FAILURE);
                    myReturnValues.setResponseMessage(ALERT_SOMETHING_WENT_WRONG);
                    e.printStackTrace();
                    Log.e(TAG, "CUSTOMERSITELIST: " + e.toString());
                }
                callback.onSuccess(myReturnValues);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailer(COMMON_RESPONSE_NOT_RECEIVE_MSG);
                Log.e(TAG, "onErrorResponse loginRequest: " + error.toString());
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest, TAG);
    }


    public void addTocartInsert(String aInputStr, final TPIWebServiceCallBack callback) {

        try {
            String aUrl = AddCart_URL + CARTINSERT + aInputStr;


            myReturnValues = new TPIReturnValues();

            Log.e(TAG, "CARTINSERT: " + aUrl);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, aUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.e(TAG, "Response: " + response);
                        JSONObject aBaseJsonObject = new JSONObject(response);

                        if (aBaseJsonObject.has("SalesOrderCart")) {

                            JSONObject OrderinfoObject = aBaseJsonObject.getJSONObject("SalesOrderCart");

                            if (OrderinfoObject.has("Response")) {
                                JSONObject ResponseObject = OrderinfoObject.getJSONObject("Response");
                                getResponseCapStatus(ResponseObject);
                            }

                            if (OrderinfoObject.has("Data")) {
                                JSONObject DataObject = OrderinfoObject.getJSONObject("Data");
                                myReturnValues.setReturn_Description(DataObject.getString("Return_Description"));
                                myReturnValues.setReturn_Code(DataObject.getString("Return_Code"));
                            }
                        } else {
                            myReturnValues.setResponseCode(RESPONSE_CODE_CONNECTION_FAILURE);
                            myReturnValues.setResponseMessage(ALERT_SOMETHING_WENT_WRONG);
                        }

                    } catch (Exception e) {
                        myReturnValues.setResponseCode(RESPONSE_CODE_CONNECTION_FAILURE);
                        myReturnValues.setResponseMessage(ALERT_SOMETHING_WENT_WRONG);
                        e.printStackTrace();
                        Log.e(TAG, "CUSTOMERSITELIST: " + e.toString());
                    }
                    callback.onSuccess(myReturnValues);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.onFailer(COMMON_RESPONSE_NOT_RECEIVE_MSG);
                    Log.e(TAG, "onErrorResponse loginRequest: " + error.toString());
                }
            });

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(stringRequest, TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void DeleteCart(String aInputStr, final TPIWebServiceCallBack callback) {
        try {
            String aUrl = AddCart_URL + DELETECART + aInputStr;


            myReturnValues = new TPIReturnValues();

            Log.e(TAG, "DELETECART: " + aUrl);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, aUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.e(TAG, "Response: " + response);
                        JSONObject aBaseJsonObject = new JSONObject(response);

                        if (aBaseJsonObject.has("SalesOrderCart")) {

                            JSONObject OrderinfoObject = aBaseJsonObject.getJSONObject("SalesOrderCart");

                            if (OrderinfoObject.has("Response")) {
                                JSONObject ResponseObject = OrderinfoObject.getJSONObject("Response");
                                getResponseCapStatus(ResponseObject);
                            }

                            if (OrderinfoObject.has("Data")) {
                                JSONObject DataObject = OrderinfoObject.getJSONObject("Data");
                                myReturnValues.setReturn_Description(DataObject.getString("Return_Description"));
                                myReturnValues.setReturn_Code(DataObject.getString("Return_Code"));
                            }
                        } else {
                            myReturnValues.setResponseCode(RESPONSE_CODE_CONNECTION_FAILURE);
                            myReturnValues.setResponseMessage(ALERT_SOMETHING_WENT_WRONG);
                        }

                    } catch (Exception e) {
                        myReturnValues.setResponseCode(RESPONSE_CODE_CONNECTION_FAILURE);
                        myReturnValues.setResponseMessage(ALERT_SOMETHING_WENT_WRONG);
                        e.printStackTrace();
                        Log.e(TAG, "DELETECART: " + e.toString());
                    }
                    callback.onSuccess(myReturnValues);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.onFailer(COMMON_RESPONSE_NOT_RECEIVE_MSG);
                    Log.e(TAG, "onErrorResponse DELETECART: " + error.toString());
                }
            });

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(stringRequest, TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void confirmOrder(String aInputStr, final TPIWebServiceCallBack callback) {
        try {
            String aUrl = AddCart_URL + CONFIRMORDER + aInputStr;


            myReturnValues = new TPIReturnValues();

            Log.e(TAG, "CONFIRMORDER: " + aUrl);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, aUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.e(TAG, "Response: " + response);
                        JSONObject aBaseJsonObject = new JSONObject(response);

                        if (aBaseJsonObject.has("SalesOrderCart")) {

                            JSONObject OrderinfoObject = aBaseJsonObject.getJSONObject("SalesOrderCart");
                            if (OrderinfoObject.has("Response")) {
                                JSONObject ResponseObject = OrderinfoObject.getJSONObject("Response");
                                getResponseCapStatus(ResponseObject);
                            }
                            if (OrderinfoObject.has("Data")) {
                                JSONObject DataObject = OrderinfoObject.getJSONObject("Data");
                                myReturnValues.setReturn_Description(DataObject.getString("Return_Description"));
                                myReturnValues.setReturn_Code(DataObject.getString("Return_Code"));
                                myReturnValues.setRequestNumber(DataObject.getString("RequestNumber"));
                            }
                        } else {
                            myReturnValues.setResponseCode(RESPONSE_CODE_CONNECTION_FAILURE);
                            myReturnValues.setResponseMessage(ALERT_SOMETHING_WENT_WRONG);
                        }

                    } catch (Exception e) {
                        myReturnValues.setResponseCode(RESPONSE_CODE_CONNECTION_FAILURE);
                        myReturnValues.setResponseMessage(ALERT_SOMETHING_WENT_WRONG);
                        e.printStackTrace();
                        Log.e(TAG, "CONFIRMORDER: " + e.toString());
                    }
                    callback.onSuccess(myReturnValues);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.onFailer(COMMON_RESPONSE_NOT_RECEIVE_MSG);
                    Log.e(TAG, "onErrorResponse CONFIRMORDER: " + error.toString());
                }
            });

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(stringRequest, TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void bookingHistory(String aInputStr, final TPIWebServiceCallBack callback) {
        try {
            String aUrl = AddCart_URL + BOOKING_HISTORY + aInputStr;


            myReturnValues = new TPIReturnValues();

            Log.e(TAG, "BOOKING_HISTORY: " + aUrl);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, aUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.e(TAG, "Response: " + response);
                        JSONObject aBaseJsonObject = new JSONObject(response);

                        JSONObject aResponseJsonObject = aBaseJsonObject.getJSONObject(RESPONSE);
                        getResponseStatus(aResponseJsonObject);

                        ArrayList<Booking> aOrderInfoList = new ArrayList<>();


                        if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {

                            JSONArray aOrderInfoJSONArray = aBaseJsonObject.getJSONArray("CustomerReqList");
                            Log.e("TAG", "" + aOrderInfoJSONArray.length());
                            for (int i = 0; i < aOrderInfoJSONArray.length(); i++) {
                                JSONObject aJsonObj = aOrderInfoJSONArray.getJSONObject(i);
                                Booking aBooking = new Booking();

                                aBooking.booking_sl_no = aJsonObj.getString("SLNo");
                                aBooking.booking_req_no = aJsonObj.getString("RequestNo");
                                aBooking.booking_date = aJsonObj.getString("CreatedDate");
                                aBooking.booking_total_qty = aJsonObj.getString("TotalQuantity");

                                aBooking.booking_location_id = aJsonObj.getString("ShiptoLocationId");
                                aBooking.booking_location_code = aJsonObj.getString("ShiptoLocationCode");
                                aBooking.booking_address1 = aJsonObj.getString("Address1");
                                aBooking.booking_address2 = aJsonObj.getString("Address2");
                                aBooking.booking_address3 = aJsonObj.getString("Address3");
                                aBooking.booking_address4 = aJsonObj.getString("Address4");
                                aBooking.booking_address5 = aJsonObj.getString("Address5");

                                aOrderInfoList.add(aBooking);
                            }

                            myReturnValues.bookingList.addAll(aOrderInfoList);
                        }

                    } catch (Exception e) {
                        myReturnValues.setResponseCode(RESPONSE_CODE_CONNECTION_FAILURE);
                        myReturnValues.setResponseMessage(ALERT_SOMETHING_WENT_WRONG);
                        e.printStackTrace();
                        Log.e(TAG, "BOOKING_HISTORY: " + e.toString());
                    }
                    callback.onSuccess(myReturnValues);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.onFailer(COMMON_RESPONSE_NOT_RECEIVE_MSG);
                    Log.e(TAG, "onErrorResponse BOOKING_HISTORY: " + error.toString());
                }
            });

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(stringRequest, TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bookingHistoryDetail(String aInputStr, final TPIWebServiceCallBack callback) {
        try {
            String aUrl = AddCart_URL + BOOKING_HISTORY_DETAIL + "P_Request_No=" + aInputStr;


            myReturnValues = new TPIReturnValues();

            Log.e(TAG, "BOOKING_HISTORY_DETAIL: " + aUrl);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, aUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.e(TAG, "Response: " + response);
                        JSONObject aBaseJsonObject = new JSONObject(response);

                        JSONObject aResponseJsonObject = aBaseJsonObject.getJSONObject(RESPONSE);
                        getResponseStatus(aResponseJsonObject);

                        ArrayList<Booking> aOrderInfoList = new ArrayList<>();


                        if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {

                            JSONArray aOrderInfoJSONArray = aBaseJsonObject.getJSONArray("RequestItemList");
                            Log.e("TAG", "" + aOrderInfoJSONArray.length());
                            for (int i = 0; i < aOrderInfoJSONArray.length(); i++) {
                                JSONObject aJsonObj = aOrderInfoJSONArray.getJSONObject(i);
                                Booking aBooking = new Booking();

                                aBooking.booking_sl_no = aJsonObj.getString("SLNo");
                                aBooking.booking_req_no = aJsonObj.getString("RequestNo");
                                aBooking.booking_date = aJsonObj.getString("CreatedDate");

                                aBooking.booking_model_name = aJsonObj.getString("ModelName");
                                aBooking.booking_model_desc = aJsonObj.getString("ModelDesc");
                                aBooking.booking_inventory_item_code = aJsonObj.getString("InventoryItemCode");
                                aBooking.booking_qty = aJsonObj.getString("ScheduleQuantity");

                                aOrderInfoList.add(aBooking);
                            }

                            myReturnValues.bookingList.addAll(aOrderInfoList);
                        }

                    } catch (Exception e) {
                        myReturnValues.setResponseCode(RESPONSE_CODE_CONNECTION_FAILURE);
                        myReturnValues.setResponseMessage(ALERT_SOMETHING_WENT_WRONG);
                        e.printStackTrace();
                        Log.e(TAG, "BOOKING_HISTORY_DETAIL: " + e.toString());
                    }
                    callback.onSuccess(myReturnValues);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.onFailer(COMMON_RESPONSE_NOT_RECEIVE_MSG);
                    Log.e(TAG, "onErrorResponse BOOKING_HISTORY_DETAIL: " + error.toString());
                }
            });

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(stringRequest, TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* public TIReturnValues sendVersionUpdate() {

        myReturnValues = new TIReturnValues();
        // Response Json Object
        JSONObject aResponseJsonObject;

        ArrayList<Category> aCategoryList = new ArrayList<Category>();

        Category aCategoryInfo = null;
        try {
            StringBuilder sb = new StringBuilder(VERSION_UPDATE_MAIN_URL);
            sb.append("&" + "app_id" + "=" + DEVICE_APP_ID);
            sb.append("&" + "version_code" + "=" + TIHelper.getAppVersion(myContext));
            sb.append("&" + "device_type" + "=" + DEVICE_TYPE);
            Log.e("VERSION_UPDATE", sb.toString());
            OkHttpClient aOkHttpClient = new OkHttpClient();
            Request aHttpRequest = new Request.Builder()
                    .url(sb.toString())
                    .build();
            Response aHttpResponse = aOkHttpClient.newCall(aHttpRequest).execute();
            String aJsonResponse = aHttpResponse.body().string();
            Log.e("VERSION_UPDATE", aJsonResponse);
            if (aJsonResponse != null)
                try {
                    // Get the response Json Object
                    aResponseJsonObject = new JSONObject(aJsonResponse);
                    // Get the response code and message
                    getResponseUpdateStatus(aResponseJsonObject);

                    if (myReturnValues.getResponseCode().equals(
                            RESPONSE_CODE_UPDATE_AVAILABLE)) {

                        if (myReturnValues.getResponseCode().equals(
                                RESPONSE_CODE_SUCCESS)) {

                            if (aResponseJsonObject.has("Track")) {

                                JSONObject aJSONSegObject = aResponseJsonObject.getJSONObject("Track");

                                Object aDataObject = aJSONSegObject.get("Data");

                                if (aDataObject instanceof JSONObject) {

                                    JSONObject aDataObjectJSON = (JSONObject) aDataObject;

                                    if (ValidJSON(aDataObjectJSON, "_x0032_")) {

                                        String aVersionSTR = aDataObjectJSON.getString("_x0032_");

                                        mySession.putVersionDetails(aVersionSTR);

                                        // Log.e("VERSION_CODE", aVersionSTR);

                                    } else {
                                        mySession.putVersionDetails("");
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    myReturnValues.setResponseCode(RESPONSE_CODE_FAILURE);
                    myReturnValues.setResponseMessage(RESPONSE_CODE_FAILURE_MESSAGE);
                    e.printStackTrace();
                }
        } catch (Exception e) {
            myReturnValues.setResponseCode(RESPONSE_CODE_FAILURE);
            myReturnValues.setResponseMessage(RESPONSE_CODE_FAILURE_MESSAGE);
            e.printStackTrace();
        }


        return myReturnValues;

    }*/


 /*   public TIReturnValues insertUpdateVersionUpdate() {

        myReturnValues = new TIReturnValues();
        // Response Json Object
        JSONObject aResponseJsonObject;

        ArrayList<Category> aCategoryList = new ArrayList<Category>();

        Category aCategoryInfo = null;
        try {
            StringBuilder sb = new StringBuilder(VERSION_UPDATE_MAIN_URL);
            sb.append("&" + "devicetype" + "=" + DEVICE_TYPE);
            sb.append("&" + "version_code" + "=" + TIHelper.getAppVersion(myContext));
            sb.append("&" + "Imei_number" + "=" + TIHelper.getIMEI(myContext));
            sb.append("&" + "gcm_token" + "=" + mySession.getFCMRegisterId());
            Log.e("VERSION_UPDATE", sb.toString());
            OkHttpClient aOkHttpClient = new OkHttpClient();
            Request aHttpRequest = new Request.Builder()
                    .url(sb.toString())
                    .build();
            Response aHttpResponse = aOkHttpClient.newCall(aHttpRequest).execute();
            String aJsonResponse = aHttpResponse.body().string();
            Log.e("VERSION_UPDATE", aJsonResponse);
            if (aJsonResponse != null)
                try {
                    // Get the response Json Object
                    aResponseJsonObject = new JSONObject(aJsonResponse);
                    // Get the response code and message
                    getResponseUpdateStatus(aResponseJsonObject);

                    if (myReturnValues.getResponseCode().equals(
                            RESPONSE_CODE_UPDATE_AVAILABLE)) {

                        if (myReturnValues.getResponseCode().equals(
                                RESPONSE_CODE_SUCCESS)) {


                        }

                    }
                } catch (Exception e) {
                    myReturnValues.setResponseCode(RESPONSE_CODE_FAILURE);
                    myReturnValues.setResponseMessage(RESPONSE_CODE_FAILURE_MESSAGE);
                    e.printStackTrace();
                }
        } catch (Exception e) {
            myReturnValues.setResponseCode(RESPONSE_CODE_FAILURE);
            myReturnValues.setResponseMessage(RESPONSE_CODE_FAILURE_MESSAGE);
            e.printStackTrace();
        }


        return myReturnValues;

    }*/


    // http://115.112.233.242:600/Catalogapp_Webservice/api/track?&devicetype=A&version_code=4&Imei_number=356905071576607&gcm_token=-
    // http://115.112.233.242:600/tpi_customer/api/track?&devicetype=A&version_code=4&Imei_number=356905071576607&gcm_token=-

    // http://115.112.233.242:600/tpi_customer/api/TPI_Customer/App_Version?P_APP_ID=2&P_DEVICE_TYPE=I&P_VERSION_CODE=1.0

}
