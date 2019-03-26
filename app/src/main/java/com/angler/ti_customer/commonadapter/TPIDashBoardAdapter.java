package com.angler.ti_customer.commonadapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angler.ti_customer.R;
import com.angler.ti_customer.cachememory.TPIPreferences;
import com.angler.ti_customer.commonmodel.Dashboard_info;
import com.angler.ti_customer.constants.TPICommonValues;
import com.angler.ti_customer.fragment.TPI_OrderPageFreagment;
import com.angler.ti_customer.fragmentmanager.TPIFragmentManager;
import com.angler.ti_customer.helper.TPIHelper;

import java.util.ArrayList;

/**
 * 06-07-2017
 * Created by mahalingam on 15-06-2017.
 * MGCropListAdapter
 */

public class TPIDashBoardAdapter extends RecyclerView.Adapter<TPIDashBoardAdapter.ItemViewHolders> implements TPICommonValues {

    public FragmentActivity myContext;
    private ArrayList<Dashboard_info> myCropInfoList;
    private TPIFragmentManager myFragmentManager;
    private TPIPreferences myPreference;
    ItemViewHolders myHolderObj;

    public TPIDashBoardAdapter(FragmentActivity context, ArrayList<Dashboard_info> cropInfoList) {
        try {
            myContext = context;
            myCropInfoList = cropInfoList;
            myFragmentManager = new TPIFragmentManager(myContext);
            myPreference = new TPIPreferences(myContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public ItemViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_inflate_dashboard_list, parent, false);
        return new ItemViewHolders(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolders holder, int position) {
        try {
            final Dashboard_info aDashboardObject = myCropInfoList.get(position);

            // holder.myUnitName.setText(TPIHelper.capitalize(aDashboardObject.getSHIP_TO_SITE_NAME()) + " - " + aDashboardObject.getSHIP_TO_SITE_ID());
            // holder.myUnitName.setText(TPIHelper.capitalize(aDashboardObject.getSHIP_TO_SITE_NAME()));
            holder.myUnitName.setText(aDashboardObject.getSHIP_TO_SITE_NAME());
            holder.myUnitAddress.setText(String.format("%s %s %s %s", aDashboardObject.getADDRESS1(), aDashboardObject.getADDRESS2(), aDashboardObject.getADDRESS3(), aDashboardObject.getADDRESS5()));

            setListener(holder, aDashboardObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListener(ItemViewHolders holder, final Dashboard_info aDashboardObject) {
        holder.myUnitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    try {
                      
                        TPI_OrderPageFreagment aOrderPageObj = new TPI_OrderPageFreagment();

                        myPreference.putDashboardInfo(aDashboardObject);

                        String aSiteId = aDashboardObject.getSHIP_TO_SITE_ID();
                        String aSiteName = aDashboardObject.getSHIP_TO_SITE_NAME();

                        Bundle aPassDataBundle = new Bundle();
                        aPassDataBundle.putString("SITENAME", aSiteName);
                        aPassDataBundle.putString("SITEID", aSiteId);

                        myFragmentManager.updateContent(new TPI_OrderPageFreagment(), TPI_OrderPageFreagment.TAG, aPassDataBundle);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return myCropInfoList.size();
    }

    public void updateList(ArrayList<Dashboard_info> aDashBoardinfo) {
        myCropInfoList = aDashBoardinfo;
        notifyDataSetChanged();
    }

    public void setDisabled(ItemViewHolders aHolder) {

        aHolder.myUnitName.setText("");
    }


    /*
 * ItemViewHolder
 * */
    public class ItemViewHolders extends RecyclerView.ViewHolder {

        //  @BindView(R.id.inflate_unit_name)
        TextView myUnitName;

        // @BindView(R.id.inflate_unit_address)
        TextView myUnitAddress;

        // @BindView(R.id.inflate_card_linear)
        LinearLayout myUnitLayout;


        ItemViewHolders(View view) {
            super(view);
            myUnitName = (TextView) view.findViewById(R.id.inflate_unit_name);
            myUnitAddress = (TextView) view.findViewById(R.id.inflate_order_model_name);
            myUnitLayout = (LinearLayout) view.findViewById(R.id.inflate_card_linear);

            //  ButterKnife.bind(myContext, view);
        }

        public void holderfunction() {
            myUnitName.setText("");
        }


    }
}
