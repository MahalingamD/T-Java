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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.angler.ti_customer.R;
import com.angler.ti_customer.cachememory.TPIPreferences;
import com.angler.ti_customer.commonadapter.TPIAddCartItemAdapter;
import com.angler.ti_customer.commonmodel.CustomerSiteList;
import com.angler.ti_customer.commonmodel.SalesInfo;
import com.angler.ti_customer.commonmodel.User_info;
import com.angler.ti_customer.constants.TPICommonValues;
import com.angler.ti_customer.constants.TPIWebCommonValues;
import com.angler.ti_customer.fragmentmanager.TPIFragmentManager;
import com.angler.ti_customer.helper.TPIHelper;
import com.angler.ti_customer.listener.TPIWebServiceCallBack;
import com.angler.ti_customer.utils.MGProgressDialog;
import com.angler.ti_customer.webservices.TPIReturnValues;
import com.angler.ti_customer.webservices.TPIWebService;
import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahalingam on 30-01-2018.
 * TPI_Add_To_CartFragment
 */

public class TPI_Add_To_CartFragment extends Fragment implements TPIAddCartItemAdapter.ClickListener, TPICommonValues, TPIWebCommonValues {

    public static String TAG = TPI_Add_To_CartFragment.class.getSimpleName();

    private FragmentActivity myContext;
    private MGProgressDialog myProgressDialog;
    private TPIWebService myWebService;

    private TPIPreferences myPreferences;
    private TPIFragmentManager myFragmentManager;
    private TPIReturnValues myReturnValues;


    @BindView(R.id.fragment_cart_toolbar)
    Toolbar myToolbar;

    @BindView(R.id.cart_toolbar_title)
    TextView myToolbarTitle;

    @BindView(R.id.cart_back_image)
    ImageView myBackImage;

    @BindView(R.id.screen_my_cart_LV)
    ListView myCartListView;

    @BindView(R.id.include_layout)
    View myIncludeLayout;

    @BindView(R.id.fragment_root_layout)
    CoordinatorLayout myRootLayout;

    @BindView(R.id.fragment_add_cart_SRL)
    SwipeRefreshLayout mySwifeRefreshLayout;

    private Snackbar mySnackBar;

    private boolean showProgress = false;

    private TPIAddCartItemAdapter mAdapter;
    private ArrayList<CustomerSiteList> myCustomerSiteList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_to_cart, container, false);
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

        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        actionBar.setSupportActionBar(myToolbar);
        myToolbarTitle.setText(getString(R.string.label_add_cart_page));

        clickListener();
        setAdapter();
        getValuesFormAPI();

    }

    private void getValuesFormAPI() {

        if (checkInternet()) {
            getCustomerList();
        } else {
            myCustomerSiteList.clear();
            setAdapter();
            showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
        }

    }

    private void clickListener() {
        myBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFragmentManager.backpress();
            }
        });

        mySwifeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isShowProgress()) {
                    showProgress = true;
                    mySwifeRefreshLayout.setRefreshing(true);
                }
                if (checkInternet()) {
                    getCustomerList();
                    if (mySnackBar != null)
                        mySnackBar.dismiss();
                } else {
                    if (isShowProgress()) {
                        showProgress = false;
                        mySwifeRefreshLayout.setRefreshing(false);
                    }
                    myCustomerSiteList.clear();
                    setAdapter();
                    showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
                }

            }
        });
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    private void setAdapter() {
        mAdapter = new TPIAddCartItemAdapter(myContext, myCustomerSiteList);
        mAdapter.setCount(this);
        myCartListView.setAdapter(mAdapter);
    }


    private boolean checkInternet() {
        return TPIHelper.checkInternet(myContext);
    }


    public void getCustomerList() {

        if (isShowProgress()) {
            showProgress = false;
            mySwifeRefreshLayout.setRefreshing(false);
        }
        myCustomerSiteList.clear();
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
                                if (myCustomerSiteList.size() > 0 && myCustomerSiteList != null) {
                                    setAdapter();
                                    myIncludeLayout.setVisibility(View.GONE);
                                } else {
                                    myIncludeLayout.setVisibility(View.VISIBLE);
                                }
                            } else {
                                setAdapter();
                                myIncludeLayout.setVisibility(View.VISIBLE);
                                // TPIHelper.showAlertDialog(myReturnValues.getResponseMessage(), myContext);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailer(Object object) {

                        if (isShowProgress()) {
                            showProgress = false;
                            mySwifeRefreshLayout.setRefreshing(false);
                        }

                        myProgressDialog.dismiss();
                        setAdapter();
                        myIncludeLayout.setVisibility(View.VISIBLE);
                        TPIHelper.showAlertDialog(COMMON_RESPONSE_NOT_RECEIVE_MSG, myContext);
                    }
                });

            } else {
                showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
            }
        } catch (Exception e) {
            if (isShowProgress()) {
                showProgress = false;
                mySwifeRefreshLayout.setRefreshing(false);
            }
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getValuesFormAPI();
    }

    private void UpdateAdapter() {
        if (myCustomerSiteList.size() > 0 && myCustomerSiteList != null) {
            mAdapter.updateAdapter(myCustomerSiteList);
            mAdapter.notifyDataSetChanged();
            myIncludeLayout.setVisibility(View.GONE);
        } else {
            myIncludeLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setCount(CustomerSiteList aCustomerInfo, SwipeLayout mySwipeLayout, int position) {
        DeleteCart(aCustomerInfo, position);
    }

    @Override
    public void setCancel() {
        setAdapter();
    }

    private void DeleteCart(CustomerSiteList aCustomerInfo, final int position) {
        try {
            if (checkInternet()) {
                final MGProgressDialog myProgressDialog = new MGProgressDialog(myContext);

                myReturnValues = new TPIReturnValues();

                myProgressDialog.setMessage(getString(R.string.label_loading));
                myProgressDialog.setCancelable(false);
                myProgressDialog.show();

                String myCustomerId = aCustomerInfo.getCUSTOMER_ID();
                String myCustomerUserId = aCustomerInfo.getCUSTOMER_USER_ID();
                String myLocationId = aCustomerInfo.getSHIP_TO_LOCATION_ID();

                String aInputStr = "P_Cust_User_Id=" + myCustomerUserId + "&" + "P_Customer_Id="
                        + myCustomerId + "&" + "Ship_To_Location_Id=" + myLocationId + "&" +
                        "Inventory_Item_Id=" + 0;


                if (aInputStr.contains(" ")) {
                    aInputStr = aInputStr.replaceAll(" ", "%20");
                }

                myWebService.DeleteCart(aInputStr, new TPIWebServiceCallBack() {

                    @Override
                    public void onSuccess(Object object) {
                        try {
                            myProgressDialog.dismiss();
                            myReturnValues = (TPIReturnValues) object;

                            if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {
                                myCustomerSiteList.remove(position);
                                setAdapter();
                                showAlertDialog(myReturnValues.getReturn_Description(), myContext);
                            } else {
                                setAdapter();
                                TPIHelper.showAlertDialog(ALERT_SOMETHING_WENT_WRONG, myContext);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailer(Object object) {
                        myProgressDialog.dismiss();
                        setAdapter();
                        TPIHelper.showAlertDialog(ALERT_SOMETHING_WENT_WRONG, myContext);
                    }
                });

            } else {
                showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param aMessage
     */
    public void showAlertDialog(String aMessage, final FragmentActivity aContext) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
            builder.setMessage(getString(R.string.label_item_remove))
                    .setTitle(aContext.getString(R.string.app_name))
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getCustomerList();
                            mAdapter.notifyDataSetChanged();
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
                        getValuesFormAPI();
                        // Refresh Home Fragment data onResume
                        if (getCurrentFragment() instanceof TPI_Add_To_CartFragment) {
                            ((TPI_Add_To_CartFragment) getCurrentFragment()).onResume();
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
