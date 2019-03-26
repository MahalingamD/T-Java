package com.angler.ti_customer.commonadapter;

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
import com.angler.ti_customer.commonmodel.Unit_info;
import com.angler.ti_customer.constants.TPICommonValues;
import com.angler.ti_customer.fragment.TPI_OrderPageFreagment;
import com.angler.ti_customer.fragmentmanager.TPIFragmentManager;
import com.angler.ti_customer.helper.TPIHelper;
import com.google.gson.Gson;


import java.util.ArrayList;

/**
 * 06-07-2017
 * Created by mahalingam on 15-06-2017.
 * MGCropListAdapter
 */

public class TPIUnitListAdapter extends RecyclerView.Adapter<TPIUnitListAdapter.ItemViewHolders> implements TPICommonValues {

    public FragmentActivity myContext;
    private ArrayList<Unit_info> myCropInfoList;
    private TPIFragmentManager myFragmentManager;
    private TPIPreferences myPreference;

    public TPIUnitListAdapter(FragmentActivity context, ArrayList<Unit_info> cropInfoList) {
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
            final Unit_info aCropObject = myCropInfoList.get(position);

            holder.myUnitName.setText(TPIHelper.capitalize(aCropObject.getUnit_name()));
            holder.myUnitAddress.setText(TPIHelper.capitalize(aCropObject.getUnit_address()));

            setListener(holder, aCropObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListener(ItemViewHolders holder, final Unit_info aCropObject) {
        holder.myUnitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    try {
                        Bundle aBundle = new Bundle();
                        Gson gson = new Gson();
                        String json = gson.toJson(aCropObject);
                        aBundle.putString(GSON_ARRAYLIST, json);

                        // myPreference.putCropDetails(aCropObject);
                        myFragmentManager.updateContent(new TPI_OrderPageFreagment(), TPI_OrderPageFreagment.TAG, aBundle);
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
    }
}
