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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.angler.ti_customer.R;
import com.angler.ti_customer.cachememory.TPIPreferences;
import com.angler.ti_customer.commonadapter.TPIAddCartItemAdapter;
import com.angler.ti_customer.commonadapter.TPIConfirmOrderListAdapter;
import com.angler.ti_customer.commonmodel.CustomerSelectedItem;
import com.angler.ti_customer.commonmodel.CustomerSiteList;
import com.angler.ti_customer.commonmodel.Dashboard_info;
import com.angler.ti_customer.commonmodel.OrderScheduledInfo;
import com.angler.ti_customer.commonmodel.Order_info;
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
import com.daimajia.swipe.SwipeLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mahalingam on 06-02-2018.
 * TPI_ConfirmOrderFragment
 */

public class TPI_ConfirmOrderFragment extends Fragment implements TPICommonValues, TPIWebCommonValues, TPIConfirmOrderListAdapter.ClickListener {

    public static String TAG = TPI_ConfirmOrderFragment.class.getSimpleName();

    private FragmentActivity myContext;
    private MGProgressDialog myProgressDialog;
    private TPIWebService myWebService;

    private TPIPreferences myPreferences;
    private TPIFragmentManager myFragmentManager;
    private TPIReturnValues myReturnValues;

    @BindView(R.id.fragment_order_toolbar)
    Toolbar myToolbar;

    @BindView(R.id.order_toolbar_title)
    TextView myToolbarTitle;

    @BindView(R.id.fragment_ship_to_address_TXT)
    TextView myShipToAddress;

    @BindView(R.id.fragment_ship_from_address_TXT)
    TextView mySupplyAddress;

    @BindView(R.id.fragment_total_model_TXT)
    TextView myTotaiModel;

    @BindView(R.id.fragment_total_qty_TXT)
    TextView myTotalQty;

    @BindView(R.id.fragment_date_TXT)
    TextView myOrderDate;

    @BindView(R.id.order_back_image)
    ImageView myBackImage;

    @BindView(R.id.fragment_summary_add_new_IMG)
    ImageView myNewOrderImage;

    @BindView(R.id.fragment_summary_confirm_BTN)
    Button myConfirmButton;

    @BindView(R.id.fragment_order_recycler)
    RecyclerView myOrderRecyclerView;

    @BindView(R.id.show_hide_layout)
    CardView myShowHideCardView;

    @BindView(R.id.layout_address_header_card)
    CardView myLayoutAddressHeaderCardView;

    @BindView(R.id.upArrow)
    ImageView myUpArrowImageView;

    @BindView(R.id.downArrow)
    ImageView myDownArrowImageView;

    @BindView(R.id.tpitextView)
    TextView myTPITextView;

    @BindView(R.id.fragment_summary_add_new_RPLV)
    RippleView myAddNewRippleView;

    @BindView(R.id.fragment_summary_confirm_IMG_RPLV)
    RippleView myConfirmIMGRippleView;

    @BindView(R.id.fragment_root_layout)
    CoordinatorLayout myRootLayout;


    @BindView(R.id.orderpage_content)
    RelativeLayout myOrderContentRelativieLayout;

    private Snackbar mySnackBar;

    ArrayList<CustomerSelectedItem> myConfirmItemList = new ArrayList<>();

    TPIConfirmOrderListAdapter myadapter;

    private CustomerSiteList myCustomerSiteObj;
    CustomerSiteList myCustomerSiteList;
    String myCustomerId = "", myCustomerUserId = "", myLocationId = "", myLocationName = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tpi_order_summary, container, false);
        ButterKnife.bind(this, rootView);

        myTPITextView.setText(getString(R.string.label_CardView_show));
        myDownArrowImageView.setVisibility(View.VISIBLE);

        ClassAndWidgetInitialise();

        return rootView;
    }

    private void ClassAndWidgetInitialise() {
        myContext = getActivity();

        myProgressDialog = new MGProgressDialog(myContext);
        myWebService = new TPIWebService(myContext);
        myPreferences = new TPIPreferences(myContext);
        myFragmentManager = new TPIFragmentManager(myContext);

        myToolbarTitle.setText(getString(R.string.label_order_summary));
        getBundleValues();
        clickListener();
        setOrderValues();
        getValueFromAPI();
    }

    private void getValueFromAPI() {
        if (checkInternet()) {
            getCustomerSelectedList();
        } else {
            try {
                myOrderContentRelativieLayout.setVisibility(View.GONE);
                showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void getBundleValues() {
        try {
            Bundle aBundle = getArguments();
            if (aBundle != null) {

                String aDetailStr = aBundle.getString("Customer_Info");

                myCustomerSiteObj = new Gson().fromJson(aDetailStr, new TypeToken<CustomerSiteList>() {
                }.getType());

                myCustomerId = myCustomerSiteObj.getCUSTOMER_ID();
                myCustomerUserId = myCustomerSiteObj.getCUSTOMER_USER_ID();
                myLocationId = myCustomerSiteObj.getSHIP_TO_LOCATION_ID();
                myLocationName = myCustomerSiteObj.getSHIP_TO_LOCATION_CODE();

                Dashboard_info aDashboard_info = myPreferences.getDashboardInfo();

                myCustomerSiteList = myPreferences.getCustomerInfo();

            } else {
                Dashboard_info aDashboard_info = myPreferences.getDashboardInfo();
                myLocationId = aDashboard_info.getSHIP_TO_SITE_ID();
                myLocationName = aDashboard_info.getSHIP_TO_SITE_NAME();
                User_info aUser_info = myPreferences.getUserInfo();
                myCustomerId = aUser_info.getCUSTOMER_ID();
                myCustomerUserId = aUser_info.getUSER_ID();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clickListener() {
        myBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFragmentManager.backpress();
                // myFragmentManager.clearAllFragments();
                // myFragmentManager.updateContent(new TPI_DashboardFragment(), TPI_DashboardFragment.TAG, null);
            }
        });

        myShowHideCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myUpArrowImageView.getVisibility() == View.VISIBLE) {
                    myLayoutAddressHeaderCardView.setVisibility(View.GONE);
                    myTPITextView.setText(myContext.getString(R.string.label_CardView_show));
                    myUpArrowImageView.setVisibility(View.GONE);
                    myDownArrowImageView.setVisibility(View.VISIBLE);

                } else {
                    myLayoutAddressHeaderCardView.setVisibility(View.VISIBLE);
                    myDownArrowImageView.setVisibility(View.GONE);
                    myUpArrowImageView.setVisibility(View.VISIBLE);
                    myTPITextView.setText(myContext.getString(R.string.label_CardView_hide));
                }
            }
        });

        myAddNewRippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                showNewOrderAlertDialog(getString(R.string.label_add_item), myContext);
            }
        });

        myConfirmIMGRippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                showConfirmRippleViewAlertDialog(myContext);

            }
        });
    }

    private boolean checkInternet() {
        return TPIHelper.checkInternet(myContext);
    }

    private void confirmOrder() {

        try {
            if (checkInternet()) {
                final MGProgressDialog myProgressDialog = new MGProgressDialog(myContext);

                myReturnValues = new TPIReturnValues();

                myProgressDialog.setMessage(getString(R.string.label_loading));
                myProgressDialog.setCancelable(false);
                myProgressDialog.show();


                String aInputStr = "P_Cust_User_Id=" + myCustomerUserId + "&" + "P_Customer_Id="
                        + myCustomerId + "&" + "Ship_To_Location_Id=" + myLocationId;

                if (aInputStr.contains(" ")) {
                    aInputStr = aInputStr.replaceAll(" ", "%20");
                }

                myWebService.confirmOrder(aInputStr, new TPIWebServiceCallBack() {

                    @Override
                    public void onSuccess(Object object) {
                        try {
                            myProgressDialog.dismiss();
                            myReturnValues = (TPIReturnValues) object;

                            if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {
                                showAlertDialog(myReturnValues.getRequestNumber(), myContext);
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
                // Toast.makeText(myContext, getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlertDialog(String aMessage, FragmentActivity aContext) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
            builder.setMessage(getString(R.string.label_item_confirm) + aMessage)
                    .setTitle(aContext.getString(R.string.app_name))
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            callDashBoard();
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

    private void showConfirmRippleViewAlertDialog(FragmentActivity aContext) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
            builder.setMessage(getString(R.string.alert_message_confirm))
                    .setTitle(aContext.getString(R.string.app_name))
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            confirmOrder();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void updateAlertDialog(String aMessage, FragmentActivity aContext) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
            builder.setMessage(getString(R.string.label_item_update))
                    .setTitle(aContext.getString(R.string.app_name))
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getCustomerSelectedList();
                            dialog.dismiss();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
            // Change the buttons color in dialog
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            pbutton.setTextColor(ContextCompat.getColor(aContext, R.color.black));
            nbutton.setTextColor(ContextCompat.getColor(aContext, R.color.black));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOrderValues() {
        try {
            myOrderRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(myContext);
            myOrderRecyclerView.setLayoutManager(mLayoutManager);

            myadapter = new TPIConfirmOrderListAdapter(myContext, myConfirmItemList);
            myadapter.setCount(this);
            myOrderRecyclerView.setAdapter(myadapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCount(CustomerSelectedItem aCustomerSelectInfo, int position) {
        DeleteCart(aCustomerSelectInfo, position);
    }

    @Override
    public void setUpdate(CustomerSelectedItem acustomerItem, double aQty) {
        addTocart(acustomerItem, aQty);
    }

    private void setSelectedDate(ArrayList<CustomerSelectedItem> myConfirmItemList) {
        try {
            CustomerSelectedItem aSelectedItem = myConfirmItemList.get(0);
            String aDateString = TPIHelper.dateConvertion(aSelectedItem.getSCHEDULE_DATE());
            myOrderDate.setText(aDateString);
            mySupplyAddress.setText(aSelectedItem.getSHIP_TO_LOCATION_CODE());
            String aAddress = aSelectedItem.getADDRESS1() + ","
                    + aSelectedItem.getADDRESS2() + ","
                    + aSelectedItem.getADDRESS3() + ","
                    + aSelectedItem.getAddress4() + ","
                    + aSelectedItem.getADDRESS5();
            myShipToAddress.setText(aAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCustomerSelectedList() {

        String aInputStr;
        try {
            if (checkInternet()) {
                final MGProgressDialog myProgressDialog = new MGProgressDialog(myContext);

                myReturnValues = new TPIReturnValues();

                myProgressDialog.setMessage(getString(R.string.label_loading));
                myProgressDialog.setCancelable(false);
                myProgressDialog.show();

                aInputStr = "P_Cust_User_Id=" + myCustomerUserId + "&" + "P_Customer_Id="
                        + myCustomerId + "&" + "P_Ship_To_Location_Id=" + myLocationId;

                if (aInputStr.contains(" ")) {
                    aInputStr = aInputStr.replaceAll(" ", "%20");
                }

                myWebService.getCustomerItemList(aInputStr, "1", new TPIWebServiceCallBack() {

                    @Override
                    public void onSuccess(Object object) {
                        try {
                            myProgressDialog.dismiss();
                            myReturnValues = (TPIReturnValues) object;

                            if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {
                                // myConfirmItemList.clear();
                                myConfirmItemList = myReturnValues.getMyCustomerSelectedList();

                                //UpdateAdapter();
                                if (myConfirmItemList.size() > 0 && myConfirmItemList != null) {
                                    myadapter.updateAdapter(myConfirmItemList);
                                    myadapter.notifyDataSetChanged();
                                    setItemQty();
                                    setSelectedDate(myConfirmItemList);
                                } else {
                                    myadapter.updateAdapter(myConfirmItemList);
                                    myadapter.notifyDataSetChanged();
                                }

                            } else {
                                myadapter.updateAdapter(myConfirmItemList);
                                myadapter.notifyDataSetChanged();
                                callDashBoard();
                                //   showNavigateAlertDialog(myReturnValues.getResponseMessage(), myContext);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailer(Object object) {
                        myProgressDialog.dismiss();
                        TPIHelper.showAlertDialog(ALERT_SOMETHING_WENT_WRONG, myContext);
                    }
                });

            } else {
                //Toast.makeText(myContext, getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setItemQty() {
        try {
            double aQty = 0;
            if (myConfirmItemList != null && myConfirmItemList.size() > 0) {
                for (int i = 0; i < myConfirmItemList.size(); i++) {
                    CustomerSelectedItem aCustomerSelectedItem = myConfirmItemList.get(i);
                    if (!aCustomerSelectedItem.getSCHEDULE_QUANTITY().equals("")) {
                        aQty = aQty + Double.parseDouble(aCustomerSelectedItem.getSCHEDULE_QUANTITY());
                    }
                }
                myTotaiModel.setText("" + myConfirmItemList.size());

                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.CEILING);

                myTotalQty.setText("" + df.format(aQty));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void DeleteCart(CustomerSelectedItem aCustomerSelectInfo, final int position) {
        try {
            if (checkInternet()) {
                final MGProgressDialog myProgressDialog = new MGProgressDialog(myContext);

                myReturnValues = new TPIReturnValues();

                myProgressDialog.setMessage(getString(R.string.label_loading));
                myProgressDialog.setCancelable(false);
                myProgressDialog.show();

                String Inventory_Item_Id = aCustomerSelectInfo.getINVENTORY_ITEM_ID();

                String aInputStr = "P_Cust_User_Id=" + myCustomerUserId + "&" + "P_Customer_Id="
                        + myCustomerId + "&" + "Ship_To_Location_Id=" + myLocationId + "&" +
                        "Inventory_Item_Id=" + Inventory_Item_Id;

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
                                myConfirmItemList.remove(position);
                                myadapter.notifyDataSetChanged();
                                showDeleteAlertDialog(myReturnValues.getReturn_Description(), myContext);
                            } else {
                                // UpdateAdapter();
                                TPIHelper.showAlertDialog(ALERT_SOMETHING_WENT_WRONG, myContext);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailer(Object object) {
                        myProgressDialog.dismiss();
                        TPIHelper.showAlertDialog(ALERT_SOMETHING_WENT_WRONG, myContext);
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

    private void addTocart(final CustomerSelectedItem orderSchedule, double aorderQty) {
        String aInputStr;
        try {
            if (checkInternet()) {
                final MGProgressDialog myProgressDialog = new MGProgressDialog(myContext);

                myReturnValues = new TPIReturnValues();

                myProgressDialog.setMessage(getString(R.string.label_loading));
                myProgressDialog.setCancelable(false);
                myProgressDialog.show();

                Double score = (Double) aorderQty;
                String aOrderQty = String.valueOf(score);

                User_info aUser_info = myPreferences.getUserInfo();
                Dashboard_info aDashboardObject = myPreferences.getDashboardInfo();

                aInputStr = "P_Cust_User_Id=" + aUser_info.getUSER_ID() +
                        "&" + "P_Customer_Id=" + aUser_info.getCUSTOMER_ID() +
                        "&" + "P_Customer_Number=" + aDashboardObject.getCUSTOMER_NUMBER().trim() +
                        "&" + "P_Customer_Name=" + aDashboardObject.getCUSTOMER_NAME().trim() +
                        "&" + "P_Inventory_Item_Code=" + orderSchedule.getINVENTORY_ITEM_CODE() +
                        "&" + "P_Inventory_Item_Desc=" + orderSchedule.getINVENTORY_ITEM_DESC() +
                        "&" + "P_Primary_Uom_Code=" + orderSchedule.getPRIMARY_UOM_CODE().trim() +
                        "&" + "P_Inventory_Item_Id=" + orderSchedule.getINVENTORY_ITEM_ID().trim() +
                        "&" + "P_Model_Name=" + orderSchedule.getMODEL_NAME().trim() +
                        "&" + "P_Model_Description=" + orderSchedule.getMODEL_DESCRIPTION().trim() +
                        "&" + "P_Part_Number=" + orderSchedule.getPART_NUMBER().trim() +
                        "&" + "P_Ship_To_Location_Code=" + aDashboardObject.getSHIP_TO_SITE_NAME().trim() +
                        "&" + "P_Ship_To_Location_Id=" + myLocationId.trim() +
                        "&" + "P_Address1=" + aDashboardObject.getADDRESS1().trim() +
                        "&" + "P_Address2=" + aDashboardObject.getADDRESS2().trim() +
                        "&" + "P_Address3=" + aDashboardObject.getADDRESS3().trim() +
                        "&" + "P_Address4=" + aDashboardObject.getADDRESS4().trim() +
                        "&" + "P_Address5=" + aDashboardObject.getADDRESS5().trim() +
                        "&" + "P_Schedule_Quantity=" + aOrderQty.trim() +
                        "&" + "P_Schedule_Date=" + myOrderDate.getText().toString().trim();

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
                                myadapter.notifyDataSetChanged();
                                updateAlertDialog(myReturnValues.getReturn_Description(), myContext);
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
                //  Toast.makeText(myContext, getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDeleteAlertDialog(String aMessage, final FragmentActivity aContext) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
            builder.setMessage(getString(R.string.label_item_remove))
                    .setTitle(aContext.getString(R.string.app_name))
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getCustomerSelectedList();
                            myadapter.notifyDataSetChanged();
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

    public void showNewOrderAlertDialog(String aMessage, final FragmentActivity aContext) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
            builder.setMessage(aMessage)
                    .setTitle(aContext.getString(R.string.app_name))
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            callOrderPage();
                            dialog.dismiss();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showNavigateAlertDialog(String aMessage, final FragmentActivity aContext) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
            builder.setMessage(aMessage)
                    .setTitle(aContext.getString(R.string.app_name))
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            callDashBoard();
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

    private void callOrderPage() {
        try {
            Bundle aBundle = new Bundle();
            aBundle.putString("Location_id", myLocationId);
            aBundle.putString("Location_name", myLocationName);
            myFragmentManager.clearAllFragments();
            myFragmentManager.updateContent(new TPI_OrderPageFreagment(), TPI_OrderPageFreagment.TAG, aBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callDashBoard() {
        try {
            myFragmentManager.clearAllFragments();
            myFragmentManager.updateContent(new TPI_DashboardFragment(), TPI_DashboardFragment.TAG, null);
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
                        if (getCurrentFragment() instanceof TPI_ConfirmOrderFragment) {
                            ((TPI_ConfirmOrderFragment) getCurrentFragment()).onResume();
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
