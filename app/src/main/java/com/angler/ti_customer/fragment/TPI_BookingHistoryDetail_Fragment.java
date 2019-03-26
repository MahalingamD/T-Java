package com.angler.ti_customer.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.angler.ti_customer.R;
import com.angler.ti_customer.cachememory.TPIPreferences;
import com.angler.ti_customer.commonadapter.TPIBookingHistoryDetailAdapter;
import com.angler.ti_customer.commonmodel.Booking;
import com.angler.ti_customer.constants.TPICommonValues;
import com.angler.ti_customer.constants.TPIWebCommonValues;
import com.angler.ti_customer.fragmentmanager.TPIFragmentManager;
import com.angler.ti_customer.helper.TPIHelper;
import com.angler.ti_customer.listener.TPIWebServiceCallBack;
import com.angler.ti_customer.utils.MGProgressDialog;
import com.angler.ti_customer.webservices.TPIReturnValues;
import com.angler.ti_customer.webservices.TPIWebService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mathan on 03-09-2018.
 * TPI_BookingHistory_Fragment
 */

public class TPI_BookingHistoryDetail_Fragment extends Fragment implements TPICommonValues, TPIWebCommonValues {

    public static String TAG = TPI_BookingHistoryDetail_Fragment.class.getSimpleName();

    private FragmentActivity myContext;
    private MGProgressDialog myProgressDialog;
    private TPIWebService myWebService;

    private TPIPreferences myPreferences;
    private TPIFragmentManager myFragmentManager;
    private TPIReturnValues myReturnValues;
    private Booking myBookingInfo;
    private ArrayList<Booking> myOrderInfoList;
    private TPIBookingHistoryDetailAdapter myAdapter;


    @BindView(R.id.fragment_cart_toolbar)
    Toolbar myToolbar;

    @BindView(R.id.cart_toolbar_title)
    TextView myToolbarTitle;

    @BindView(R.id.cart_back_image)
    ImageView myBackImage;

    @BindView(R.id.inflate_req_no_txt)
    TextView myReqNo;

    @BindView(R.id.inflate_date_txt)
    TextView myDate;

    @BindView(R.id.inflate_ship_address_txt)
    TextView myShipAddress;

    @BindView(R.id.inflate_ship_to_txt)
    TextView myShipTo;

    @BindView(R.id.inflate_total_qty_txt)
    TextView myTotalQty;

    @BindView(R.id.fragment_booking_recycler)
    RecyclerView myBookingDetailRecycler;

    @BindView(R.id.show_hide_layout)
    CardView myShowHideCard;

    @BindView(R.id.fragment_booking_detail_lay)
    CardView myBookingDetailCard;

    @BindView(R.id.booking_arrow)
    ImageView myBookingArrow;

    @BindView(R.id.tpitextView)
    TextView myShowHideTitle;

    @BindView(R.id.fragment_nodata_found)
    TextView myNodata_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_booking_history_detail, container, false);
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
        myToolbarTitle.setText(getString(R.string.label_booking_history));

        getBundle();

        myOrderInfoList = new ArrayList<>();

        myBookingDetailRecycler.setLayoutManager(new LinearLayoutManager(myContext));
        myBookingDetailRecycler.hasFixedSize();
        myAdapter = new TPIBookingHistoryDetailAdapter(myContext, myOrderInfoList);
        myBookingDetailRecycler.setAdapter(myAdapter);

        clickListener();

        getValueFromAPI();

    }

    private void getBundle() {
        Bundle aBundle = getArguments();
        if (aBundle != null) {
            myBookingInfo = (Booking) aBundle.getSerializable("values");
            if (myBookingInfo != null) {

                String aShipAddress = myBookingInfo.booking_address1 + myBookingInfo.booking_address2 +
                        myBookingInfo.booking_address3 + myBookingInfo.booking_address4 + myBookingInfo.booking_address5;

                myReqNo.setText(String.format(":    %s", myBookingInfo.booking_req_no));
                myDate.setText(String.format(":    %s", myBookingInfo.booking_date));
                myShipAddress.setText(aShipAddress);
                myShipTo.setText(String.format(":    %s", myBookingInfo.booking_location_code));
                myTotalQty.setText(String.format(":    %s", myBookingInfo.booking_total_qty));


            }
        }
    }

    private void clickListener() {
        myBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFragmentManager.backpress();
            }
        });

        myShowHideCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myBookingArrow.getTag().equals("down")) {
                    myBookingDetailCard.setVisibility(View.VISIBLE);
                    myShowHideTitle.setText(getString(R.string.hide_booking_details));
                    myBookingArrow.setImageDrawable(myContext.getResources().getDrawable(R.drawable.ic_up));
                    myBookingArrow.setTag("up");
                } else {
                    myBookingDetailCard.setVisibility(View.GONE);
                    myShowHideTitle.setText(getString(R.string.show_booking_details));
                    myBookingArrow.setImageDrawable(myContext.getResources().getDrawable(R.drawable.ic_down));
                    myBookingArrow.setTag("down");
                }
            }
        });
    }

    private boolean checkInternet() {
        return TPIHelper.checkInternet(myContext);
    }

    private void getValueFromAPI() {

        try {
            if (checkInternet()) {
                String aInputStr = myBookingInfo.booking_req_no;

                if (aInputStr.contains(" ")) {
                    aInputStr = aInputStr.replaceAll(" ", "%20");
                }

                myWebService.bookingHistoryDetail(aInputStr, new TPIWebServiceCallBack() {

                    @Override
                    public void onSuccess(Object object) {
                        try {
                            myReturnValues = (TPIReturnValues) object;

                            if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {
                                myOrderInfoList.clear();
                                myOrderInfoList.addAll(myReturnValues.bookingList);
                                myAdapter.notifyDataSetChanged();

                                if (myOrderInfoList.size() == 0)
                                    myNodata_layout.setVisibility(View.VISIBLE);
                                else myNodata_layout.setVisibility(View.GONE);

                            } else {
                                myNodata_layout.setVisibility(View.VISIBLE);
                                TPIHelper.showAlertDialog(myReturnValues.getResponseMessage(), myContext);
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
                showNetworkSnackBarAlert(myBookingArrow, NO_INTERNET_MESSAGE);
            }
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
            Snackbar mySnackBar = Snackbar.make(aCoordinatorLAY, aMessage, Snackbar.LENGTH_INDEFINITE).setAction(RETRY, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        getValueFromAPI();
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

    @Override
    public void onResume() {
        super.onResume();
    }
}
