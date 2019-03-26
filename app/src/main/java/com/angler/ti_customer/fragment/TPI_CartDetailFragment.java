package com.angler.ti_customer.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.angler.ti_customer.R;
import com.angler.ti_customer.cachememory.TPIPreferences;
import com.angler.ti_customer.commonadapter.TPICartDetailItemAdapter;
import com.angler.ti_customer.commonmodel.CustomerSelectedItem;
import com.angler.ti_customer.commonmodel.CustomerSiteList;
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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahalingam on 30-01-2018.
 * TPI_Add_To_CartFragment
 */

public class TPI_CartDetailFragment extends Fragment implements TPICartDetailItemAdapter.ClickListener, TPICommonValues, TPIWebCommonValues {

    public static String TAG = TPI_CartDetailFragment.class.getSimpleName();

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

    @BindView(R.id.fragment_footer)
    RelativeLayout myFooterLayout;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout myCoordinateLAyout;

    @BindView(R.id.fragment_confirm_But)
    Button myConfirmBUT;

    @BindView(R.id.fragment_detail_SRL)
    SwipeRefreshLayout myRefreshLayout;

    private TPICartDetailItemAdapter mAdapter;
    private ArrayList<CustomerSelectedItem> myCustomerSelectedList = new ArrayList<>();
    private CustomerSiteList myCustomerSiteObj;
    String myCustomerId = "", myCustomerUserId = "", myLocationId = "", myLocationName = "";

    private boolean showProgress = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_cart_detail, container, false);
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


        getBundleValues();

        setToolbar();
        clickListener();
        setAdapter();
        getCustomerSelectedList();
    }

    private void getBundleValues() {

        try {
            Bundle aBundle = getArguments();
            if (aBundle != null) {
                String aDetailStr = aBundle.getString("Customer_Info");

                myCustomerSiteObj = new Gson().fromJson(
                        aDetailStr, new TypeToken<CustomerSiteList>() {
                        }.getType());

                myCustomerId = myCustomerSiteObj.getCUSTOMER_ID();
                myCustomerUserId = myCustomerSiteObj.getCUSTOMER_USER_ID();
                myLocationId = myCustomerSiteObj.getSHIP_TO_LOCATION_ID();
                myLocationName = myCustomerSiteObj.getSHIP_TO_LOCATION_CODE();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setToolbar() {
        try {
            AppCompatActivity actionBar = (AppCompatActivity) getActivity();
            actionBar.setSupportActionBar(myToolbar);
            myToolbarTitle.setText(getString(R.string.label_add_cart__detail_page));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void clickListener() {
        myBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFragmentManager.backpress();
            }
        });


        myConfirmBUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle aBundle = new Bundle();
                aBundle.putParcelableArrayList("confirmList", myCustomerSelectedList);
                aBundle.putString("Location_name", myLocationName);
                aBundle.putString("Customer_Info", new Gson().toJson(myCustomerSiteObj));
                myFragmentManager.updateContent(new TPI_ConfirmOrderFragment(), TPI_ConfirmOrderFragment.TAG, aBundle);
            }
        });

        myRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isShowProgress()) {
                    showProgress = true;
                    myRefreshLayout.setRefreshing(true);
                }
                getCustomerSelectedList();

            }
        });
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    private void setAdapter() {
        try {
            mAdapter = new TPICartDetailItemAdapter(myContext, myCustomerSelectedList);
            mAdapter.setCount(this);
            myCartListView.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean checkInternet() {
        return TPIHelper.checkInternet(myContext);
    }


    private void getCustomerSelectedList() {

        if (isShowProgress()) {
            showProgress = false;
            myRefreshLayout.setRefreshing(false);
        }
        myCustomerSelectedList.clear();
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
                                myCustomerSelectedList = myReturnValues.getMyCustomerSelectedList();

                                //UpdateAdapter();
                                if (myCustomerSelectedList.size() > 0 && myCustomerSelectedList != null) {
                                    setAdapter();
                                    myIncludeLayout.setVisibility(View.GONE);
                                    myFooterLayout.setVisibility(View.VISIBLE);
                                } else {
                                    myIncludeLayout.setVisibility(View.VISIBLE);
                                    myFooterLayout.setVisibility(View.GONE);
                                }

                            } else {
                                setAdapter();
                                myIncludeLayout.setVisibility(View.VISIBLE);
                                myFooterLayout.setVisibility(View.GONE);
                                //  TPIHelper.showAlertDialog(myReturnValues.getResponseMessage(), myContext);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailer(Object object) {
                        myProgressDialog.dismiss();
                        if (isShowProgress()) {
                            showProgress = false;
                            myRefreshLayout.setRefreshing(false);
                        }
                        setAdapter();
                        TPIHelper.showAlertDialog(COMMON_RESPONSE_NOT_RECEIVE_MSG, myContext);
                    }
                });

            } else {
                Toast.makeText(myContext, getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            if (isShowProgress()) {
                showProgress = false;
                myRefreshLayout.setRefreshing(false);
            }
            e.printStackTrace();
        }
    }

    private void UpdateAdapter() {
        if (myCustomerSelectedList.size() > 0 && myCustomerSelectedList != null) {
            mAdapter.updateAdapter(myCustomerSelectedList);
            mAdapter.notifyDataSetChanged();
            myIncludeLayout.setVisibility(View.GONE);
        } else {
            myIncludeLayout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void setCount(CustomerSelectedItem aCustomerSelectInfo, SwipeLayout mySwipeLayout) {

        DeleteCart(aCustomerSelectInfo);
    }

    @Override
    public void setCancel() {
        if (myCustomerSelectedList.size() > 0 && myCustomerSelectedList != null) {
            setAdapter();
            myIncludeLayout.setVisibility(View.GONE);
        } else {
            myIncludeLayout.setVisibility(View.VISIBLE);
        }
    }

    private void DeleteCart(CustomerSelectedItem aCustomerSelectInfo) {
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

                                showAlertDialog(myReturnValues.getReturn_Description(), myContext);
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
                Toast.makeText(myContext, getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param aMessage String
     * @param aContext FragmentActivity
     */
    public void showAlertDialog(String aMessage, final FragmentActivity aContext) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
            builder.setMessage("Item remove successfully")
                    .setTitle(aContext.getString(R.string.app_name))
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getCustomerSelectedList();
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
}
