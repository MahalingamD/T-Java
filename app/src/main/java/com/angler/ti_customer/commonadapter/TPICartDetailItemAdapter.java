package com.angler.ti_customer.commonadapter;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.angler.ti_customer.R;
import com.angler.ti_customer.commonmodel.CustomerSelectedItem;
import com.angler.ti_customer.commonmodel.CustomerSiteList;
import com.angler.ti_customer.fragmentmanager.TPIFragmentManager;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;


public class TPICartDetailItemAdapter extends BaseSwipeAdapter {

    FragmentActivity myContext;
    private SwipeLayout mySwipeLayout;
    ArrayList<CustomerSelectedItem> myCustomerSeletedList;
    public ClickListener myListener;
    private TPIFragmentManager myFragmentManager;


    public TPICartDetailItemAdapter(FragmentActivity aContext, ArrayList<CustomerSelectedItem> aSiteList) {
        myContext = aContext;
        myCustomerSeletedList = aSiteList;
        myFragmentManager = new TPIFragmentManager(myContext);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.layout_inflate_user_mgmnt_swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View aView = LayoutInflater.from(myContext).inflate(R.layout.inflate_my_cart_detail_list, null);
        mySwipeLayout = (SwipeLayout) aView.findViewById(getSwipeLayoutResourceId(position));

        return aView;
    }

    @Override
    public void fillValues(int position, View aConvertView) {

        final CustomerSelectedItem aCustomerSelectInfo = myCustomerSeletedList.get(position);

        final TextView aInvenNoTXT = (TextView) aConvertView
                .findViewById(R.id.layout_inflate_my_inven_id_TXT);

        final TextView aShipNameTXT = (TextView) aConvertView
                .findViewById(R.id.layout_inflate_my_cart_order_item_shipName_TXT);

        TextView aProductCountTXT = (TextView) aConvertView.findViewById(R.id.layout_inflate_my_cart_order_item_count_TXT);

        TextView aModelNameTXT = (TextView) aConvertView.findViewById(R.id.layout_inflate_my_cart_order_model_name_TXT);
        TextView aModelDescription = (TextView) aConvertView.findViewById(R.id.layout_inflate_my_cart_order_model_descrtion_TXT);

        ImageView aProductImage = (ImageView) aConvertView
                .findViewById(R.id.layout_inflate_my_cart_order_item_next_IM);

        RippleView aDeleteRV = (RippleView) aConvertView
                .findViewById(R.id.layout_inflate_my_cart_order_item_cancel_RV);

        LinearLayout aMainRL = (LinearLayout) aConvertView
                .findViewById(R.id.layout_inflate_my_cart_item_LAY);

        aProductImage.setVisibility(View.GONE);

        aModelNameTXT.setText(aCustomerSelectInfo.getMODEL_NAME());
        aShipNameTXT.setText(aCustomerSelectInfo.getSHIP_TO_LOCATION_CODE());
        aInvenNoTXT.setText(aCustomerSelectInfo.getINVENTORY_ITEM_ID());
        aModelDescription.setText(aCustomerSelectInfo.getMODEL_DESCRIPTION());
        aProductCountTXT.setText(aCustomerSelectInfo.getSCHEDULE_QUANTITY());


        aDeleteRV.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                showDeleteAlertDialog(myContext.getString(R.string.label_delete), myContext, aCustomerSelectInfo);
            }
        });


        aMainRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* Bundle aBundle = new Bundle();

                aBundle.putString(BUNDLE_CART_ITEM, new Gson().toJson(aSalesInfo));

                aBundle.putString(BUNDLE_ITEM, "SHOW");


                myFragmentManager.updateContent(new TPISearchConfirmFragment(), TPISearchConfirmFragment.TAG, aBundle);
*/
            }
        });
    }

    @Override
    public int getCount() {
        return myCustomerSeletedList.size();
    }

    @Override
    public Object getItem(int position) {
        return myCustomerSeletedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void setCount(ClickListener aListener) {

        myListener = aListener;

    }

    public void updateAdapter(ArrayList<CustomerSelectedItem> aCustomerSiteList) {
        myCustomerSeletedList = aCustomerSiteList;
        notifyDataSetChanged();
    }

    public interface ClickListener {

        public void setCount(CustomerSelectedItem aCustomerSelectInfo, SwipeLayout mySwipeLayout);

        public void setCancel();
    }


    /**
     * @param aMessage            String
     * @param aCustomerSelectInfo CustomerSelectedItem
     */
    private void showDeleteAlertDialog(String aMessage, final FragmentActivity aContext, final CustomerSelectedItem aCustomerSelectInfo) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
            builder.setMessage(aMessage)
                    .setTitle(aContext.getString(R.string.app_name))
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            myListener.setCount(aCustomerSelectInfo, mySwipeLayout);
                            dialog.dismiss();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //myListener.setCancel();
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





