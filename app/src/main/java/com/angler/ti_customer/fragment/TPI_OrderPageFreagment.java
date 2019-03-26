package com.angler.ti_customer.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angler.ti_customer.R;
import com.angler.ti_customer.cachememory.TPIPreferences;
import com.angler.ti_customer.commonadapter.TPIDateListAdapter;
import com.angler.ti_customer.commonadapter.TPIOrderListAdapter;
import com.angler.ti_customer.commonmodel.CustomerSelectedItem;
import com.angler.ti_customer.commonmodel.Dashboard_info;
import com.angler.ti_customer.commonmodel.OrderScheduledInfo;
import com.angler.ti_customer.commonmodel.SalesDate;
import com.angler.ti_customer.commonmodel.User_info;
import com.angler.ti_customer.constants.TPICommonValues;
import com.angler.ti_customer.constants.TPIWebCommonValues;
import com.angler.ti_customer.fragmentmanager.TPIFragmentManager;
import com.angler.ti_customer.helper.TPIHelper;
import com.angler.ti_customer.listener.TPIWebServiceCallBack;
import com.angler.ti_customer.utils.MGProgressDialog;
import com.angler.ti_customer.webservices.TPIReturnValues;
import com.angler.ti_customer.webservices.TPIWebService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahalingam on 22-01-2018.
 * TPI_OrderPageFreagment
 */

public class TPI_OrderPageFreagment extends Fragment implements TPICommonValues, TPIWebCommonValues, TPIOrderListAdapter.ClickListener, TPIDateListAdapter.AppCallback {
    public static String TAG = TPI_OrderPageFreagment.class.getSimpleName();

    private FragmentActivity myContext;
    private MGProgressDialog myProgressDialog;
    private TPIWebService myWebService;

    private TPIPreferences myPreferences;
    private TPIFragmentManager myFragmentManager;
    private TPIReturnValues myReturnValues;

    @BindView(R.id.fragment_recycler)
    RecyclerView myDateRecyclerView;

    @BindView(R.id.fragment_order_recycler)
    RecyclerView myOrderRecyclerView;

    @BindView(R.id.fragment_order_toolbar)
    Toolbar myToolbar;

    @BindView(R.id.dash_toolbar_title)
    TextView myToolbarTitle;

    @BindView(R.id.order_back_image)
    ImageView myBackImage;

    @BindView(R.id.order_CheckList_icon)
    ImageView myOrderCheckListIcon;

    @BindView(R.id.addcart_badge_count)
    TextView myBadgeCount;

    @BindView(R.id.fragment_order_search_AutoTXT)
    EditText mySearchEDTXT;

    @BindView(R.id.fragment_order_SRLAY)
    SwipeRefreshLayout mySwipeRefresh;

    @BindView(R.id.siteNameAndId)
    TextView mySiteNameAndSiteIdTextView;

    @BindView(R.id.fragment_root_layout)
    CoordinatorLayout myRootLayout;

    @BindView(R.id.no_item_layout)
    LinearLayout NoDataLayout;

    Dashboard_info aDashboardObject;

    String myShipId;


    private TPIOrderListAdapter myadapter;
    private TPIDateListAdapter myDateadapter;

    private ArrayList<SalesDate> myDateArraylist = new ArrayList<>();
    private ArrayList<OrderScheduledInfo> myOrder_infoArrayList = new ArrayList<>();
    private ArrayList<CustomerSelectedItem> myCustomerSelectedList = new ArrayList<>();
    private String mySelectedDate;
    private boolean showProgress = false;
    private boolean isDateSelected = false;

    int myPositonOfDate;
    private String myLoacationName;

    private Snackbar mySnackBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tpi_order_screen, container, false);
        ButterKnife.bind(this, rootView);
        ClassAndWidgetInitialise();


        return rootView;
    }

    private void ClassAndWidgetInitialise() {
        myContext = getActivity();
        myProgressDialog = new MGProgressDialog(myContext);
        myWebService = new TPIWebService(myContext);
        myPreferences = new TPIPreferences(myContext);
        myFragmentManager = new TPIFragmentManager(myContext);
        myReturnValues = new TPIReturnValues();

        setToolbar();

        myBadgeCount.setVisibility(View.GONE);

        getBundleValues();

        clickListener();

        setDateValues();
        setOrderValues();

//        getValueFromAPI();


        TPIHelper.hideKeyBoard(myContext);
    }

    private void getValueFromAPI() {
        if (checkInternet()) {
            callSelectedOrderValueMethod();
            callOrderValueMethod();
        } else {
            showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
        }
    }

    private void setToolbar() {
        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        actionBar.setSupportActionBar(myToolbar);
        myToolbarTitle.setText(getString(R.string.label_order_page));
    }

    private void clickListener() {
        myBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFragmentManager.clearAllFragments();
                myFragmentManager.updateContent(new TPI_DashboardFragment(), TPI_DashboardFragment.TAG, null);
            }
        });

        myOrderCheckListIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myCustomerSelectedList.size() > 0)
                    myFragmentManager.updateContent(new TPI_ConfirmOrderFragment(), TPI_ConfirmOrderFragment.TAG, null);
                else
                    TPIHelper.showMaterialAlertDialog(myContext, getString(R.string.alertMessage_no_records_summary));
            }
        });


        mySwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    mySearchEDTXT.setText("");
                    if (!isShowProgress()) {
                        showProgress = true;
                        mySwipeRefresh.setRefreshing(true);
                    }

                    NoDataLayout.setVisibility(View.GONE);
                    myOrderRecyclerView.setVisibility(View.VISIBLE);

                    if (checkInternet()) {
                        callSelectedOrderValueMethod();
                        callOrderValueMethod();

                        if (mySnackBar.isShown())
                            mySnackBar.dismiss();

                    } else {
                        if (mySwipeRefresh != null)
                            if (mySwipeRefresh.isRefreshing())
                                mySwipeRefresh.setRefreshing(false);

                        mySearchEDTXT.setVisibility(View.GONE);
                        myOrderRecyclerView.setVisibility(View.GONE);
                        showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
                    }
                } catch (Exception e) {
//                    Log.e("Exception", e.toString());
                }

            }
        });

        mySearchEDTXT.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        TPIHelper.hideKeyBoard(myContext);
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        mySearchEDTXT.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
//                    if (myOrder_infoArrayList.size() > 0 && myOrder_infoArrayList != null)
//                    {
                    if (s.length() > 0)
                        searchMethod(s.toString());
                    else
                        // updateSearchValues(myOrder_infoArrayList, s.toString());
                        updateValues(myOrder_infoArrayList);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void searchMethod(String s) {
        try {
            ArrayList<OrderScheduledInfo> myOrderSearchArrayList = new ArrayList<>();

            for (OrderScheduledInfo orderList : myOrder_infoArrayList) {
                if (orderList.getMODEL_NAME().toLowerCase().contains(s.toLowerCase()) ||
                        orderList.getMODEL_DESCRIPTION().toLowerCase().contains(s.toLowerCase()) ||
                        orderList.getPART_NUMBER().toLowerCase().contains(s.toLowerCase())) {

                    myOrderSearchArrayList.add(orderList);
                }
               /* if (orderList.getMODEL_DESCRIPTION().toLowerCase().contains(s.toLowerCase())) {

                    myOrderSearchArrayList.add(orderList);
                }
                if (orderList.getPART_NUMBER().toLowerCase().contains(s.toLowerCase())) {

                    myOrderSearchArrayList.add(orderList);
                }*/

            }
            updateSearchValues(myOrderSearchArrayList, s);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getBundleValues() {
        try {
            Bundle aBundle = getArguments();

            /*if (aBundle != null) {
                myShipId = aBundle.getString("Location_id");
            }*/
            if (aBundle.getString("Location_id") != null) {
                myShipId = aBundle.getString("Location_id");
                myLoacationName = aBundle.getString("Location_name");

            } else {
                aDashboardObject = myPreferences.getDashboardInfo();
                myShipId = aDashboardObject.getSHIP_TO_SITE_ID();
                myLoacationName = aDashboardObject.getSHIP_TO_SITE_NAME();
            }
            // mySiteNameAndSiteIdTextView.setText(myLoacationName + "-" + myShipId);
            mySiteNameAndSiteIdTextView.setText(myLoacationName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    private void setDateValues() {
        myDateArraylist.clear();

        SimpleDateFormat serverFormat = new SimpleDateFormat(myServerFormat);
        SimpleDateFormat sdf = new SimpleDateFormat(myDayDateMonthFormat);
        SimpleDateFormat aDMF = new SimpleDateFormat(myDateMonthFormat);
        SimpleDateFormat aSunF = new SimpleDateFormat(myDayFormat);

        Calendar aLastDayCalendar = Calendar.getInstance();
        int lastDate = aLastDayCalendar.getActualMaximum(Calendar.DATE);
        aLastDayCalendar.set(Calendar.DATE, lastDate);

        Calendar aLastDayPreviousCalendar = Calendar.getInstance();

        aLastDayPreviousCalendar.set(Calendar.DATE, (lastDate - 1));

        String aLastDate = serverFormat.format(aLastDayCalendar.getTime());

        myPreferences.setLastDayMonth(aLastDate);


        for (int i = 0; myDateArraylist.size() < 7; i++) {
            SalesDate aSaleDate = new SalesDate();
            Calendar calendar = new GregorianCalendar();


            if (aDMF.format(aLastDayPreviousCalendar.getTime()).equals(aDMF.format(calendar.getTime()))) {
                calendar.add(Calendar.DATE, +1);  //current + 1day
            } else if (aDMF.format(aLastDayCalendar.getTime()).equals(aDMF.format(calendar.getTime()))) {

                if (myDateArraylist.size() == 0) {
                    SalesDate aSaleDate1 = new SalesDate();
                    aSaleDate1.setDisplayDate(sdf.format(aLastDayCalendar.getTime()));
                    aSaleDate1.setServerDate(serverFormat.format(aLastDayCalendar.getTime()));
                    myDateArraylist.add(aSaleDate1);
                }

                 check2PM(calendar);

            } else {

                check2PM(calendar);
            }


            calendar.add(Calendar.DATE, i);
            String day = sdf.format(calendar.getTime());
            String aSeverDate = serverFormat.format(calendar.getTime());


            //Avoid Govt holiday
            if (aDMF.format(calendar.getTime()).trim().equals(JAN_26) ||
                    aDMF.format(calendar.getTime()).trim().equals(MAY_1) ||
                    aDMF.format(calendar.getTime()).trim().equals(AUG_15) ||
                    aDMF.format(calendar.getTime()).trim().equals(OCT_2)) {
                //Last date add
            } else if (aDMF.format(aLastDayCalendar.getTime()).equals(aDMF.format(calendar.getTime()))) {

                aSaleDate.setDisplayDate(sdf.format(aLastDayCalendar.getTime()));
                aSaleDate.setServerDate(serverFormat.format(aLastDayCalendar.getTime()));
                myDateArraylist.add(aSaleDate);

            } else {
                //avoid sunday
//                if (!aSunF.format(calendar.getTime()).equals(SUNDAY)) {
                aSaleDate.setDisplayDate(day);
                aSaleDate.setServerDate(aSeverDate);
                myDateArraylist.add(aSaleDate);
//                }
            }
        }

        myDateadapter = new TPIDateListAdapter(myContext, myDateArraylist, this);
        myDateRecyclerView.setAdapter(myDateadapter);


        myDateRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(myContext, LinearLayoutManager.HORIZONTAL, false);
        myDateRecyclerView.setLayoutManager(mLayoutManager);

    }

    private void check2PM(Calendar calendar) {
        try {
            if (TPIHelper.isAfter2pm())
                calendar.add(Calendar.DATE, +2);  //current + 2day
            else
                calendar.add(Calendar.DATE, +1);   //current + 1day
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefalutDate() {
        try {
            for (int i = 0; i < myDateArraylist.size(); i++) {
                SalesDate aDate = myDateArraylist.get(i);
                aDate.setActive(false);
            }
            myDateadapter.update(myDateArraylist);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        if (checkInternet()) {
            getValueFromAPI();
            setDateValues();
        } else {
            showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myPreferences.putSelectedDate("");
    }

    private void setOrderValues() {
        try {
            myOrderRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(myContext);
            myOrderRecyclerView.setLayoutManager(mLayoutManager);
            myadapter = new TPIOrderListAdapter(myContext, myOrder_infoArrayList);
            myOrderRecyclerView.setAdapter(myadapter);
            myadapter.getItem(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean checkInternet() {
        return TPIHelper.checkInternet(myContext);
    }


    private void updateSearchValues(ArrayList<OrderScheduledInfo> aOrderScheduleinfo, String asearchtext) {
        try {
            if (myCustomerSelectedList.size() > 0 && aOrderScheduleinfo.size() > 0) {
                for (int i = 0; i < aOrderScheduleinfo.size(); i++) {
                    OrderScheduledInfo aScheduledInfo = aOrderScheduleinfo.get(i);
//                    String aUModel = aScheduledInfo.getMODEL_NAME().trim().toLowerCase();
                    String aUModel = aScheduledInfo.getMODEL_DESCRIPTION().trim().toLowerCase();

                    for (int j = 0; j < myCustomerSelectedList.size(); j++) {
                        CustomerSelectedItem aSelectedItem = myCustomerSelectedList.get(j);
//                        if (aUModel.equals(aSelectedItem.getMODEL_NAME().trim().toLowerCase())) {
                        if (aUModel.equals(aSelectedItem.getMODEL_DESCRIPTION().trim().toLowerCase())) {
                            aScheduledInfo.setActive(true);
                            aScheduledInfo.setOrder_Qty(aSelectedItem.getSCHEDULE_QUANTITY());
                        }
                    }
                }
                myadapter.updateSearchList(aOrderScheduleinfo, asearchtext);
                myadapter.notifyDataSetChanged();
            } else {
                if (aOrderScheduleinfo.size() > 0) {
                    myadapter.updateSearchList(aOrderScheduleinfo, asearchtext);
                    NoDataLayout.setVisibility(View.GONE);
                    myOrderRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    myadapter.updateSearchList(aOrderScheduleinfo, asearchtext);
                    NoDataLayout.setVisibility(View.VISIBLE);
                    myOrderRecyclerView.setVisibility(View.GONE);
                }

//                myadapter.notifyDataSetChanged();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //XXX
    private void updateValues(ArrayList<OrderScheduledInfo> aOrderScheduleinfo) {
        try {
            if (myCustomerSelectedList.size() > 0 && aOrderScheduleinfo.size() > 0) {

                for (int j = 0; j < myDateArraylist.size(); j++) {
                    String aDateString = TPIHelper.dateConvertion(myCustomerSelectedList.get(0).getSCHEDULE_DATE());
                    SalesDate aDate = myDateArraylist.get(j);
                    aDate.setActive(false);
                    if (aDateString.toLowerCase().equals(aDate.getServerDate().toLowerCase())) {
                        myDateRecyclerView.smoothScrollToPosition(j);
                        aDate.setActive(true);
                        mySelectedDate = aDate.getServerDate();
                    } else {
                        aDate.setActive(false);
                    }
                }

                for (int i = 0; i < aOrderScheduleinfo.size(); i++) {
                    OrderScheduledInfo aScheduledInfo = aOrderScheduleinfo.get(i);
                    aScheduledInfo.setActive(false);
                    String aUModel = aScheduledInfo.getMODEL_DESCRIPTION().trim().toLowerCase();
                    for (int j = 0; j < myCustomerSelectedList.size(); j++) {
                        CustomerSelectedItem aSelectedItem = myCustomerSelectedList.get(j);
                        String aSelectedDate = TPIHelper.dateConvertion(myCustomerSelectedList.get(0).getSCHEDULE_DATE());
                        if (aUModel.equals(aSelectedItem.getMODEL_DESCRIPTION().trim().toLowerCase()) && !mySelectedDate.isEmpty() &&
                                aSelectedDate.equals(mySelectedDate)) {
                            aScheduledInfo.setActive(true);
                            aScheduledInfo.setOrder_Qty(aSelectedItem.getSCHEDULE_QUANTITY());
                        }
                    }
                }

                NoDataLayout.setVisibility(View.GONE);
                myOrderRecyclerView.setVisibility(View.VISIBLE);
                myadapter.updateSearchList(aOrderScheduleinfo, "");
                myadapter.notifyDataSetChanged();
            } else {
                myadapter.updateSearchList(aOrderScheduleinfo, "");
                myadapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setbadgeCount() {
        try {
            if (myCustomerSelectedList.size() > 0 && myCustomerSelectedList != null) {
                myBadgeCount.setVisibility(View.VISIBLE);
                myBadgeCount.setText("" + myCustomerSelectedList.size());
            } else
                myBadgeCount.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void getItem(OrderScheduledInfo OrderSchedule, double aorderQty) {
        try {
            if (!isDateSelected) {
                if (!mySelectedDate.equals("") && mySelectedDate != null)
                    addTocart(OrderSchedule, aorderQty);
                else
                    TPIHelper.showMaterialAlertDialog(myContext, getString(R.string.label_select_date));
            } else addTocart(OrderSchedule, aorderQty);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() {
        onResume();
    }

    private void callOrderValueMethod() {

        myOrder_infoArrayList = new ArrayList<>();
        String aInputStr;
        try {
            if (checkInternet()) {
                final MGProgressDialog myProgressDialog = new MGProgressDialog(myContext);

                myReturnValues = new TPIReturnValues();

                myProgressDialog.setMessage(getString(R.string.label_loading));
                myProgressDialog.setCancelable(false);
                myProgressDialog.show();

                aInputStr = "Ship_To_Site_Id=" + myShipId;

                if (aInputStr.contains(" ")) {
                    aInputStr = aInputStr.replaceAll(" ", "%20");
                }

                myWebService.getOrderDetail(aInputStr, new TPIWebServiceCallBack() {
                    @Override
                    public void onSuccess(Object object) {
                        try {
                            myProgressDialog.dismiss();
                            myReturnValues = (TPIReturnValues) object;

                            if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {
                                myPreferences.putLastUpdateTime(TPIHelper.getCurrentTime());
                                myOrder_infoArrayList = myReturnValues.getMyOrderScheduleList();
                                updateValues(myOrder_infoArrayList);
                            } else {
                                TPIHelper.showAlertDialog(myReturnValues.getResponseMessage(), myContext);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailer(Object object) {
                        myProgressDialog.dismiss();
                        TPIHelper.showAlertDialog(COMMON_RESPONSE_NOT_RECEIVE_MSG, myContext);
                    }

                });

            } else {
                //  Toast.makeText(myContext, getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void callSelectedOrderValueMethod() {

        if (isShowProgress()) {
            showProgress = false;
            mySwipeRefresh.setRefreshing(false);
        }

        myCustomerSelectedList = new ArrayList<>();
        String aInputStr;
        try {
            if (checkInternet()) {
                final MGProgressDialog myProgressDialog = new MGProgressDialog(myContext);

                myReturnValues = new TPIReturnValues();

                myProgressDialog.setMessage(getString(R.string.label_loading));
                myProgressDialog.setCancelable(false);
                myProgressDialog.show();

                User_info aUser_info = myPreferences.getUserInfo();
                aInputStr = "P_Cust_User_Id=" + aUser_info.getUSER_ID() + "&" + "P_Customer_Id="
                        + aUser_info.getCUSTOMER_ID() + "&" + "P_Ship_To_Location_Id=" + myShipId;

                if (aInputStr.contains(" ")) {
                    aInputStr = aInputStr.replaceAll(" ", "%20");
                }

                myWebService.getCustomerItemList(aInputStr, "0", new TPIWebServiceCallBack() {

                    @Override
                    public void onSuccess(Object object) {
                        try {
                            myProgressDialog.dismiss();
                            myReturnValues = (TPIReturnValues) object;

                            if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {
                                myCustomerSelectedList = myReturnValues.getMyCustomerSelectedList();
                                setSelectedDate(myCustomerSelectedList);
                                setbadgeCount();
                            } else {
                                setbadgeCount();
                                setDefalutDate();
                                mySelectedDate = "";

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailer(Object object) {
                        myProgressDialog.dismiss();
                        setbadgeCount();
                    }
                });

            } else {
                // Toast.makeText(myContext, getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //xxx
    private void setSelectedDate(ArrayList<CustomerSelectedItem> myCustomerSelectedList) {
        if (myCustomerSelectedList.size() > 0) {
            CustomerSelectedItem aSelectedItem = myCustomerSelectedList.get(0);
            String aDateString = TPIHelper.dateConvertion(aSelectedItem.getSCHEDULE_DATE());
            for (int j = 0; j < myDateArraylist.size(); j++) {
                SalesDate aDate = myDateArraylist.get(j);
                aDate.setActive(false);
                if (aDateString.toLowerCase().equals(aDate.getServerDate().toLowerCase())) {
                    myDateRecyclerView.smoothScrollToPosition(j);
                    aDate.setActive(true);
                    mySelectedDate = aDate.getServerDate();
                    myPreferences.putSelectedDate(mySelectedDate);
                } else {
                    mySelectedDate = "";
                    aDate.setActive(false);
                }
            }
            myDateadapter.update(myDateArraylist);
        } else {
            mySelectedDate = "";
        }
    }

    private void addTocart(final OrderScheduledInfo orderSchedule, double aorderQty) {

        if (isShowProgress()) {
            showProgress = false;
            mySwipeRefresh.setRefreshing(false);
        }

        myCustomerSelectedList = new ArrayList<>();
        String aInputStr;
        try {
            if (checkInternet()) {
                final MGProgressDialog myProgressDialog = new MGProgressDialog(myContext);

                myReturnValues = new TPIReturnValues();

                myProgressDialog.setMessage(getString(R.string.label_loading));
                myProgressDialog.setCancelable(false);
                myProgressDialog.show();

                Double score = aorderQty;
                String aOrderQty = String.valueOf(score);

                User_info aUser_info = myPreferences.getUserInfo();

                aDashboardObject = myPreferences.getDashboardInfo();

                aInputStr = "P_Cust_User_Id=" + aUser_info.getUSER_ID() +
                        "&" + "P_Customer_Id=" + aUser_info.getCUSTOMER_ID() +
                        "&" + "P_Customer_Number=" + aDashboardObject.getCUSTOMER_NUMBER().trim() +
                        "&" + "P_Customer_Name=" + aDashboardObject.getCUSTOMER_NAME().trim() +
                        "&" + "P_Inventory_Item_Code=" + orderSchedule.getITEM_CODE().trim() +
                        "&" + "P_Inventory_Item_Desc=" + orderSchedule.getITEM_DESC().trim() +
                        "&" + "P_Primary_Uom_Code=" + orderSchedule.getPRIMARY_UOM_CODE().trim() +
                        "&" + "P_Inventory_Item_Id=" + orderSchedule.getINVENTORY_ITEM_ID().trim() +
                        "&" + "P_Model_Name=" + orderSchedule.getMODEL_NAME().trim() +
                        "&" + "P_Model_Description=" + orderSchedule.getMODEL_DESCRIPTION().trim() +
                        "&" + "P_Part_Number=" + orderSchedule.getPART_NUMBER().trim() +
                        "&" + "P_Ship_To_Location_Code=" + aDashboardObject.getSHIP_TO_SITE_NAME().trim() +
                        "&" + "P_Ship_To_Location_Id=" + myShipId.trim() +
                        "&" + "P_Address1=" + aDashboardObject.getADDRESS1().trim() +
                        "&" + "P_Address2=" + aDashboardObject.getADDRESS2().trim() +
                        "&" + "P_Address3=" + aDashboardObject.getADDRESS3().trim() +
                        "&" + "P_Address4=" + aDashboardObject.getADDRESS4().trim() +
                        "&" + "P_Address5=" + aDashboardObject.getADDRESS5().trim() +
                        "&" + "P_Schedule_Quantity=" + aOrderQty.trim() +
                        "&" + "P_Schedule_Date=" + mySelectedDate.trim();

                if (aInputStr.contains(" ")) {
                    aInputStr = aInputStr.replaceAll(" ", "%20");
                }


                myWebService.addTocartInsert(aInputStr, new TPIWebServiceCallBack() {

                    @Override
                    public void onSuccess(Object object) {
                        try {
                            myProgressDialog.dismiss();
                            myReturnValues = (TPIReturnValues) object;

                            if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {
                                orderSchedule.setActive(true);
                                mySearchEDTXT.setText("");
                                myadapter.notifyDataSetChanged();
                                showAlertDialog(myReturnValues.getReturn_Description(), myContext, "1");
                            } else {
                                TPIHelper.showAlertDialog(myReturnValues.getReturn_Description(), myContext);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailer(Object object) {
                        myProgressDialog.dismiss();
                        TPIHelper.showAlertDialog(COMMON_RESPONSE_NOT_RECEIVE_MSG, myContext);
                    }
                });

            } else {
                showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
                //  Toast.makeText(myContext, getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param aMessage String
     * @param aContext FragmentActivity
     * @param aType    String
     */
    public void showAlertDialog(String aMessage, final FragmentActivity aContext, final String aType) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
            builder.setMessage(aMessage)
                    .setTitle(aContext.getString(R.string.app_name))
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (aType.equals("1")) {
                                callSelectedOrderValueMethod();
                            } else if (aType.equals("0")) {
                                setSelectedDate(myCustomerSelectedList);
                            }
                            dialog.dismiss();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
            // Change the buttons color in dialog
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(aContext, R.color.black));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the material snackbar alert message
     *
     * @param aCoordinatorLAY View
     * @param aMessage        String
     */
    public void showNetworkSnackBarAlert(View aCoordinatorLAY, String aMessage) {

        try {
            mySnackBar = Snackbar.make(aCoordinatorLAY, aMessage, Snackbar.LENGTH_INDEFINITE).setAction(RETRY, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        getValueFromAPI();
                        // Refresh Home Fragment data onResume
                        if (getCurrentFragment() instanceof TPI_OrderPageFreagment) {
                            getCurrentFragment().onResume();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mySnackBar.setActionTextColor(ContextCompat.getColor(myContext, R.color.snack_bar_retry_color));
            View aSnackBarView = mySnackBar.getView();
            aSnackBarView.setBackgroundColor(ContextCompat.getColor(myContext, R.color.colorPrimary));
            android.widget.TextView textView = aSnackBarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            mySnackBar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the current fragment from Backstack
     *
     * @return Fragment
     */
    private Fragment getCurrentFragment() {
        FragmentManager aFragmentManager = myContext.getSupportFragmentManager();
        String aFragmentTag = aFragmentManager.getBackStackEntryAt(aFragmentManager.getBackStackEntryCount() - 1).getName();
        Fragment aCurrentFragment = myContext.getSupportFragmentManager().findFragmentByTag(aFragmentTag);
        return aCurrentFragment;
    }

    @Override
    public void onClick(int position) {
        SalesDate aSalesDate = myDateArraylist.get(position);
        // Log.e("SELECTED SERVER DATE", aSalesDate.getServerDate());

        if (!checkInternet()) {
            aSalesDate.setActive(false);
            return;
        }

        if (myCustomerSelectedList.size() < 1)
            setDefalutDate();

        if (myCustomerSelectedList.size() > 0) {
            String aDateString = myCustomerSelectedList.get(0).getSCHEDULE_DATE();
            String aString = TPIHelper.dateConvertion(aDateString).toLowerCase();

            if (aSalesDate.getServerDate().toLowerCase().equals(aString)) {
                aSalesDate.setActive(true);
            } else {
                showAlertDialog("Please Confirm your old order", myContext, "0");
            }
        } else if (myCustomerSelectedList.size() == 0) {
            mySelectedDate = aSalesDate.getServerDate();
            myPreferences.putSelectedDate(mySelectedDate);
            isDateSelected = true;
            aSalesDate.setActive(true);
            myDateadapter.update(myDateArraylist);
        }
    }
}
