package com.angler.ti_customer.commonadapter;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.angler.ti_customer.R;
import com.angler.ti_customer.cachememory.TPIPreferences;
import com.angler.ti_customer.commonmodel.CustomerSiteList;
import com.angler.ti_customer.fragment.TPI_CartDetailFragment;
import com.angler.ti_customer.fragment.TPI_ConfirmOrderFragment;
import com.angler.ti_customer.fragmentmanager.TPIFragmentManager;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;


public class TPIAddCartItemAdapter extends BaseSwipeAdapter {

    FragmentActivity myContext;
    private SwipeLayout mySwipeLayout;
    ArrayList<CustomerSiteList> myCustomerSiteList;
    private TPIFragmentManager myFragmentManager;
    private TPIPreferences myPreferences;

    public ClickListener myListener;


    public TPIAddCartItemAdapter(FragmentActivity aContext, ArrayList<CustomerSiteList> aSiteList) {
        myContext = aContext;
        myCustomerSiteList = aSiteList;
        myFragmentManager = new TPIFragmentManager(myContext);
        myPreferences = new TPIPreferences(myContext);

    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.layout_inflate_user_mgmnt_swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View aView = LayoutInflater.from(myContext).inflate(R.layout.inflate_my_cart_order_item_list, null);


        return aView;
    }

    @Override
    public void fillValues(final int position, View aConvertView) {

        final CustomerSiteList aCustomerInfo = myCustomerSiteList.get(position);

        mySwipeLayout = (SwipeLayout) aConvertView.findViewById(getSwipeLayoutResourceId(position));

        final TextView aCustomerNameTXT = (TextView) aConvertView
                .findViewById(R.id.layout_inflate_my_cart_order_item_name_TXT);

        final TextView aCustomerNumberTXT = (TextView) aConvertView
                .findViewById(R.id.layout_inflate_my_cart_customer_number_TXT);

        TextView aLocationTXT = (TextView) aConvertView
                .findViewById(R.id.layout_inflate_my_cart_location_TXT);

        final RippleView aDeleteRV = (RippleView) aConvertView
                .findViewById(R.id.layout_inflate_my_cart_order_item_cancel_RV);

        LinearLayout aMainRL = (LinearLayout) aConvertView
                .findViewById(R.id.layout_inflate_my_cart_item_LAY);


        aCustomerNameTXT.setText(aCustomerInfo.getCUSTOMER_NAME());
        aCustomerNumberTXT.setText(aCustomerInfo.getSHIP_TO_LOCATION_ID());
        aLocationTXT.setText(aCustomerInfo.getSHIP_TO_LOCATION_CODE());


        aDeleteRV.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                showDeleteAlertDialog(myContext.getString(R.string.label_delete), myContext, aCustomerInfo,position);

            }
        });


        aMainRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle aBundle = new Bundle();
                aBundle.putString("Customer_Info", new Gson().toJson(aCustomerInfo));
                myPreferences.putCustomerInfo(aCustomerInfo);
                myFragmentManager.updateContent(new TPI_ConfirmOrderFragment(), TPI_ConfirmOrderFragment.TAG, aBundle);
//                myFragmentManager.updateContent(new TPI_CartDetailFragment(), TPI_CartDetailFragment.TAG, aBundle);
            }
        });
    }

    @Override
    public int getCount() {
        return myCustomerSiteList.size();
    }

    @Override
    public Object getItem(int position) {
        return myCustomerSiteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void setCount(ClickListener aListener) {

        myListener = aListener;

    }

    public void updateAdapter(ArrayList<CustomerSiteList> aCustomerSiteList) {
        myCustomerSiteList = aCustomerSiteList;
        notifyDataSetChanged();
    }

    public interface ClickListener {

        public void setCount(CustomerSiteList aCustomerInfo, SwipeLayout mySwipeLayout,int position);

        public void setCancel();
    }

    /**
     * @param aMessage      String
     * @param aCustomerInfo CustomerSiteList
     * @param position
     */
    private void showDeleteAlertDialog(String aMessage, final FragmentActivity aContext, final CustomerSiteList aCustomerInfo, final int position) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
            builder.setMessage(aMessage)
                    .setTitle(aContext.getString(R.string.app_name))
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            myListener.setCount(aCustomerInfo, mySwipeLayout,position);
                            dialog.dismiss();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //    myListener.setCancel();
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
            // Change the buttons color in dialog
            Button pButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            Button nButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            pButton.setTextColor(ContextCompat.getColor(aContext, R.color.black));
            nButton.setTextColor(ContextCompat.getColor(aContext, R.color.black));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}








