package com.angler.ti_customer.fragment;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.angler.ti_customer.R;
import com.angler.ti_customer.cachememory.TPIPreferences;
import com.angler.ti_customer.commonadapter.TPIBookingHistoryAdapter;
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

public class TPI_BookingHistory_Fragment extends Fragment implements TPICommonValues, TPIWebCommonValues {

    public static String TAG = TPI_BookingHistory_Fragment.class.getSimpleName();

    private FragmentActivity myContext;
    private MGProgressDialog myProgressDialog;
    private TPIWebService myWebService;

    private TPIPreferences myPreferences;
    private TPIFragmentManager myFragmentManager;
    private TPIReturnValues myReturnValues;
    private ArrayList<Booking> myOrderInfoList;
    private TPIBookingHistoryAdapter myBookingAdapter;


    @BindView(R.id.fragment_cart_toolbar)
    Toolbar myToolbar;

    @BindView(R.id.cart_toolbar_title)
    TextView myToolbarTitle;

    @BindView(R.id.cart_back_image)
    ImageView myBackImage;

    @BindView(R.id.fragment_booking_recycler)
    RecyclerView myBookingRecycler;

    @BindView(R.id.booking_swipe_ref)
    SwipeRefreshLayout myBookingRef;

    @BindView(R.id.fragment_from_date_txt)
    TextView myFromDate;

    @BindView(R.id.fragment_to_date_txt)
    TextView myToDate;

    @BindView(R.id.activity_register_RPLV_submit)
    RippleView mySearchBtn;

    @BindView(R.id.fragment_nodata_found)
    TextView myNodata_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_booking_history, container, false);
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

        myBookingRef.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        myFromDate.setText(TPIHelper.getCustomDate(mydateFormat, -7));
        myToDate.setText(TPIHelper.getCurrentDate(mydateFormat));

        myOrderInfoList = new ArrayList<>();
        myBookingRef.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getValueFromAPI();
            }
        });

        setupRecycler();
        clickListener();
        getValueFromAPI();
    }

    private void setupRecycler() {
        myBookingRecycler.setLayoutManager(new LinearLayoutManager(myContext));
        myBookingRecycler.hasFixedSize();
        myBookingAdapter = new TPIBookingHistoryAdapter(myContext, myOrderInfoList);
        myBookingRecycler.setAdapter(myBookingAdapter);
    }

    private DatePickerDialog.OnDateSetListener from_datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String date = TPIHelper.convertIntoDateFormat(String.format("%s-%s-%s", dayOfMonth, (month + 1), year), mydateFormat, mydateFormat);
            myFromDate.setText(date);
        }
    };

    private DatePickerDialog.OnDateSetListener to_datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String date = TPIHelper.convertIntoDateFormat(String.format("%s-%s-%s", dayOfMonth, (month + 1), year), mydateFormat, mydateFormat);
            myToDate.setText(date);
        }


    };

    private void clickListener() {
        myBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFragmentManager.backpress();
            }
        });

        myFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TPIHelper.showFromDateDialog(myContext, myFromDate.getText().toString(), from_datePicker);
            }
        });

        myToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TPIHelper.showToDateDialog(myContext, mydateFormat, myFromDate.getText().toString(), myToDate.getText().toString(), to_datePicker);
            }
        });

        mySearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TPIHelper.isValideDates(myFromDate.getText().toString(), myToDate.getText().toString(), mydateFormat)) {
                    if (checkInternet())
                        getValueFromAPI();
                    else
                        TPIHelper.showMaterialAlertDialog(myContext, getString(R.string.check_internet));
                } else
                    TPIHelper.showMaterialAlertDialog(myContext, getString(R.string.invalid_todate));
            }
        });
    }

    private boolean checkInternet() {
        return TPIHelper.checkInternet(myContext);
    }

    private void getValueFromAPI() {

        try {
            if (checkInternet()) {
                myBookingRef.setRefreshing(true);
                String aInputStr = String.format("P_Cust_User_Id=%s&P_Fm_Creation_Date=%s&P_To_Creation_Date=%s",
                        myPreferences.getUserInfo().getUSER_ID(),
                        TPIHelper.convertIntoDateFormat(myFromDate.getText().toString(), myServerFormat, mydateFormat)
                        , TPIHelper.convertIntoDateFormat(myToDate.getText().toString(), myServerFormat, mydateFormat));

//                String aInputStr = String.format("P_Cust_User_Id=%s&P_Fm_Creation_Date=%s&P_To_Creation_Date=%s",
//                        "209", "01-Jun-2018", "30-Jun-2018");

                if (aInputStr.contains(" ")) {
                    aInputStr = aInputStr.replaceAll(" ", "%20");
                }

                myWebService.bookingHistory(aInputStr, new TPIWebServiceCallBack() {

                    @Override
                    public void onSuccess(Object object) {
                        try {
                            myBookingRef.setRefreshing(false);
                            myReturnValues = (TPIReturnValues) object;
                            myOrderInfoList.clear();
                            if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {
                                myOrderInfoList.addAll(myReturnValues.bookingList);
                                myBookingAdapter.notifyDataSetChanged();

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
                        myBookingRef.setRefreshing(false);
                        TPIHelper.showAlertDialog(COMMON_RESPONSE_NOT_RECEIVE_MSG, myContext);
                    }
                });

            } else {
                showNetworkSnackBarAlert(myFromDate, NO_INTERNET_MESSAGE);
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
//                            callDashBoard();
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
            Snackbar mySnackBar = Snackbar.make(aCoordinatorLAY, aMessage, Snackbar.LENGTH_INDEFINITE).setAction(RETRY, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        getValueFromAPI();
                        // Refresh Home Fragment data onResume
//                        if (getCurrentFragment() instanceof TPI_ConfirmOrderFragment) {
//                            ((TPI_ConfirmOrderFragment) getCurrentFragment()).onResume();
//                        }
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

    @Override
    public void onResume() {
        super.onResume();
    }
}
