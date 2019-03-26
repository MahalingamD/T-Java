package com.angler.ti_customer.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.angler.ti_customer.BuildConfig;
import com.angler.ti_customer.R;
import com.angler.ti_customer.activities.TPI_Login_screen;
import com.angler.ti_customer.cachememory.TPIPreferences;
import com.angler.ti_customer.commonadapter.TPIDashBoardAdapter;
import com.angler.ti_customer.commonmodel.CustomerSiteList;
import com.angler.ti_customer.commonmodel.Dashboard_info;
import com.angler.ti_customer.commonmodel.User_info;
import com.angler.ti_customer.constants.TPICommonValues;
import com.angler.ti_customer.constants.TPIWebCommonValues;
import com.angler.ti_customer.fragmentmanager.TPIFragmentManager;
import com.angler.ti_customer.helper.TPIHelper;
import com.angler.ti_customer.listener.TPIWebServiceCallBack;
import com.angler.ti_customer.utils.MGProgressDialog;
import com.angler.ti_customer.webservices.TPIReturnValues;
import com.angler.ti_customer.webservices.TPIWebService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TPI_DashboardFragment extends Fragment implements TPICommonValues, TPIWebCommonValues {

    public static String TAG = TPI_DashboardFragment.class.getSimpleName();

    private FragmentActivity myContext;
    private MGProgressDialog myProgressDialog;
    private TPIWebService myWebService;
    public TPI_Add_To_CartFragment myAddCartObj;

    private TPIPreferences myPreferences;
    private TPIFragmentManager myFragmentManager;

    private TPIReturnValues myReturnValues;

    TPIDashBoardAdapter myadapter;


    @BindView(R.id.fragment_dashboard_toolbar)
    Toolbar myToolbar;

    @BindView(R.id.dash_toolbar_title)
    TextView myToolbarTitle;

    @BindView(R.id.layout_tel_header_TXT_version_title)
    TextView myVersionCode;

    @BindView(R.id.layout_tel_user_TXT_name_title)
    TextView myUserNameTXT;

    @BindView(R.id.addcart_badge_count)
    TextView myAddCardBadgeCount;

    @BindView(R.id.fragment_dashboard_RV)
    RecyclerView myRecyclerView;

    @BindView(R.id.fragment_dash_swipeRefreshLayout)
    SwipeRefreshLayout mySwipeRefresh;

    @BindView(R.id.dash_logout_img)
    ImageView myLogout;

    @BindView(R.id.dash_settins_img)
    ImageView mySettings;

    @BindView(R.id.order_addcart_img)
    ImageView myOrderAddCardImage;

    @BindView(R.id.dash_history_img)
    ImageView myBookingHistoryIMG;

    @BindView(R.id.fragment_root_layout)
    CoordinatorLayout myRootLayout;

    TPIDashBoardAdapter.ItemViewHolders myItemHolder;

    public Snackbar mySnackBar;

    View myIncludeLayout;

    private ArrayList<CustomerSiteList> myCustomerSiteList = new ArrayList<>();

    ArrayList<Dashboard_info> aUnit_infoArrayList = new ArrayList<>();
    Calendar calendar;

    int myCountBadge;

    EditText myUserName;
    TextView myTextView;


    RecyclerView.LayoutManager mLayoutManager;
    private static int firstVisibleInListview;

    String myUser_id = "";
    private boolean showProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tpi_home, container, false);
        myIncludeLayout = rootView.findViewById(R.id.include_layout);


        ButterKnife.bind(this, rootView);

        ClassAndWidgetInitialise();


        if (myPreferences.getCheckBool().equals("1"))
            showSnackBarAlert(myRootLayout, "Login successful");
        return rootView;
    }

    private void ClassAndWidgetInitialise() {
        myContext = getActivity();

        myWebService = new TPIWebService(myContext);
        myPreferences = new TPIPreferences(myContext);
        myFragmentManager = new TPIFragmentManager(myContext);


        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        actionBar.setSupportActionBar(myToolbar);

        aUnit_infoArrayList = new ArrayList<>();

        myVersionCode.setText(String.format("V_%s", BuildConfig.VERSION_NAME));
        myToolbarTitle.setText(getString(R.string.label_dashboard));
        myAddCardBadgeCount.setVisibility(View.GONE);

        TPIHelper.setColorScheme(mySwipeRefresh);

        clickListener();

        setValues();
        setUserInfo();


    }

    private void getValuesFormAPI() {
        if (checkInternet()) {
            getCustomerList();
            callDashBoardMethod();
            myRecyclerView.setVisibility(View.VISIBLE);
        } else {
            myRecyclerView.setVisibility(View.GONE);
            showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
        }
    }

    public void getCustomerList() {
        try {
            if (checkInternet()) {
                myCustomerSiteList.clear();
                String aInputStr;
                User_info aUser_info = myPreferences.getUserInfo();
                //  if(aUser_info!=null)
                aInputStr = "P_Cust_User_Id=" + aUser_info.getUSER_ID() + "&" + "P_Customer_Id="
                        + aUser_info.getCUSTOMER_ID();

                if (aInputStr.contains(" ")) {
                    aInputStr = aInputStr.replaceAll(" ", "%20");
                }

                myWebService.getCustomerSiteList(aInputStr, new TPIWebServiceCallBack() {
                    @Override
                    public void onSuccess(Object object) {
                        try {
                            myProgressDialog.dismiss();
                            myReturnValues = (TPIReturnValues) object;

                            if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {
                                myCustomerSiteList = myReturnValues.getMyCustomerSiteList();
                                myRecyclerView.setVisibility(View.VISIBLE);
                                myAddCardBadgeCount.setVisibility(View.VISIBLE);
                                myAddCardBadgeCount.setText("" + myCustomerSiteList.size());
                            } else {
                                myAddCardBadgeCount.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailer(Object object) {
                        TPIHelper.showAlertDialog(COMMON_RESPONSE_NOT_RECEIVE_MSG, myContext);
                    }
                });
            } else {
                showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUserInfo() {
        try {
            User_info aUserInfo = myPreferences.getUserInfo();
            if (aUserInfo != null) {
                myUserNameTXT.setText(aUserInfo.getUSER_NAME());
                myUser_id = aUserInfo.getUSER_ID();
                getValuesFormAPI();
            } else {
                Toast.makeText(myContext, "Kindly Logout and Login", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean isShowProgress() {
        return showProgress;
    }

    private void clickListener() {
        mySwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    if (!isShowProgress()) mySwipeRefresh.setRefreshing(true);

                    if (checkInternet()) {
                        getCustomerList();
                        callDashBoardMethod();
                        myRecyclerView.setVisibility(View.VISIBLE);
                        if (mySnackBar.isShown())
                            mySnackBar.dismiss();
                    } else {
                        if (mySwipeRefresh != null)
                            if (mySwipeRefresh.isRefreshing())
                                mySwipeRefresh.setRefreshing(false);

                        myAddCardBadgeCount.setVisibility(View.GONE);
                        myRecyclerView.setVisibility(View.GONE);
                        showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        myLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMaterialAlertDialog(myContext, getString(R.string.alert_message_logout));
            }
        });

        mySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showoptionDialog();

            }
        });

        myOrderAddCardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_FAIL))
                        TPIHelper.showMaterialAlertDialog(myContext, getString(R.string.alertMessage_no_records));
                    else {
                        if (myCustomerSiteList.size() > 0)
                            myFragmentManager.updateContent(new TPI_Add_To_CartFragment(), TPI_Add_To_CartFragment.TAG, null);
                        else {
                            TPIHelper.showMaterialAlertDialog(myContext, getString(R.string.alertMessage_no_records));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        myBookingHistoryIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bundle aBundle = new Bundle();

                    myFragmentManager.updateContent(new TPI_BookingHistory_Fragment(), TPI_BookingHistory_Fragment.TAG, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }


    @SuppressLint("NewApi")
    private void setValues() {
        myRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(myContext);
        myRecyclerView.setLayoutManager(mLayoutManager);
        myadapter = new TPIDashBoardAdapter(myContext, aUnit_infoArrayList);
        myRecyclerView.setAdapter(myadapter);
    }


    private boolean checkInternet() {
        return TPIHelper.checkInternet(myContext);
    }


    private void callDashBoardMethod() {

        if (mySwipeRefresh != null)
            if (mySwipeRefresh.isRefreshing())
                mySwipeRefresh.setRefreshing(false);

        try {
            if (checkInternet()) {
                myProgressDialog = new MGProgressDialog(myContext);

                myReturnValues = new TPIReturnValues();

                myProgressDialog.setMessage(getString(R.string.label_loading));
                myProgressDialog.setCancelable(false);
                myProgressDialog.show();

                String aInputStr = "User_Id=" + myUser_id;

                if (aInputStr.contains(" ")) {
                    aInputStr = aInputStr.replaceAll(" ", "%20");
                }

                myWebService.getDashboardDetail(aInputStr, new TPIWebServiceCallBack() {
                    @Override
                    public void onSuccess(Object object) {
                        myProgressDialog.dismiss();
                        myReturnValues = (TPIReturnValues) object;

                        if (isShowProgress()) mySwipeRefresh.setRefreshing(false);

                        if (mySwipeRefresh != null) if (mySwipeRefresh.isRefreshing())
                            mySwipeRefresh.setRefreshing(false);

                        if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {
                            myPreferences.putLastUpdateTime(TPIHelper.getCurrentTime());
                            ArrayList<Dashboard_info> aDashBoardinfo = myReturnValues.getMyDashboardList();
                            LoadValues(aDashBoardinfo);
                            Log.e("Login", "Success");
                        } else {
                            TPIHelper.showAlertDialog(myReturnValues.getResponseMessage(), myContext);
                        }
                    }

                    @Override
                    public void onFailer(Object object) {
                        myProgressDialog.dismiss();
                        if (isShowProgress()) mySwipeRefresh.setRefreshing(false);

                        if (mySwipeRefresh != null) if (mySwipeRefresh.isRefreshing())
                            mySwipeRefresh.setRefreshing(false);

                        TPIHelper.showAlertDialog(COMMON_RESPONSE_NOT_RECEIVE_MSG, myContext);
                    }


                });

            } else {
                // Toast.makeText(myContext, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
            }
        } catch (Exception e) {
            if (isShowProgress()) mySwipeRefresh.setRefreshing(false);
            e.printStackTrace();
        }


    }

    private void LoadValues(ArrayList<Dashboard_info> aDashBoardinfo) {
        if (myadapter != null) {
            myadapter.updateList(aDashBoardinfo);
            Log.e(TAG, "" + aUnit_infoArrayList.size());
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (checkInternet())
            getCustomerList();
        else
            showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
    }


    private void showoptionDialog() {
       /* Switch sw = new Switch(getActivity());
        sw.setTextOn("start");
        sw.setTextOff("close");
        sw.setTextColor(ContextCompat.getColor(myContext, R.color.black));


        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.addView(sw);*/


        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        myDialog.setTitle(myContext.getString(R.string.app_name));
        myDialog.setIcon(R.mipmap.ic_launcher);
        myDialog.setMessage(R.string.label_enable_disable);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.layout_setting_alert, null);
        myDialog.setView(v);


        myDialog.show();
    }

    /**
     * Show the Dialog with Dismiss  Button
     *
     * @param aContext FragmentActivity
     * @param aMessage String
     */
    public void showMaterialAlertDialog(final FragmentActivity aContext, String aMessage) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(aContext);
            dialog.setCancelable(true);
            dialog.setTitle(aContext.getString(R.string.app_name));
            dialog.setIcon(R.mipmap.ic_launcher);
            dialog.setMessage(aMessage);
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    myPreferences.putLoginStatus(false);
                    String aUserName = myPreferences.getUserName();
                    String aPassword = myPreferences.getUserPassword();
                    boolean aStatus = myPreferences.getRememberStatus();

                    myPreferences.clear();

                    myPreferences.putRememberStatus(aStatus);
                    myPreferences.putUserName(aUserName);
                    myPreferences.putUserPassword(aPassword);

                    Intent aIntent = new Intent(myContext, TPI_Login_screen.class);
                    aContext.startActivity(aIntent);
                    getActivity().finish();

                    dialog.dismiss();
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            final AlertDialog alert = dialog.create();
            alert.show();

            //SET BUTTON_NEGATIVE and BUTTON_POSITIVE Color
            alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(aContext, R.color.black));
            alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(aContext, R.color.black));

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
    public void showSnackBarAlert(View aCoordinatorLAY, String aMessage) {
        try {
            myPreferences.putCheckBool("0");
            Snackbar snackbar = Snackbar
                    .make(aCoordinatorLAY, aMessage, Snackbar.LENGTH_LONG);

            snackbar.setActionTextColor(ContextCompat.getColor(myContext, R.color.white));
            View aSnackBarView = snackbar.getView();
            aSnackBarView.setBackgroundColor(ContextCompat.getColor(myContext, R.color.colorPrimary));
            android.widget.TextView textView = (android.widget.TextView) aSnackBarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
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
                        getValuesFormAPI();
                        // Refresh Home Fragment data onResume
                        if (getCurrentFragment() instanceof TPI_DashboardFragment) {
                            ((TPI_DashboardFragment) getCurrentFragment()).onResume();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mySnackBar.setActionTextColor(ContextCompat.getColor(myContext, R.color.snack_bar_retry_color));
            View aSnackBarView = mySnackBar.getView();
            aSnackBarView.setBackgroundColor(ContextCompat.getColor(myContext, R.color.colorPrimary));
            android.widget.TextView textView = (android.widget.TextView) aSnackBarView.findViewById(android.support.design.R.id.snackbar_text);
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

}
