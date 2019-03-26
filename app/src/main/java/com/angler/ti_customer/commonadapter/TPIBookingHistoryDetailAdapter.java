package com.angler.ti_customer.commonadapter;

import android.annotation.SuppressLint;
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
import com.angler.ti_customer.commonmodel.Booking;
import com.angler.ti_customer.constants.TPICommonValues;
import com.angler.ti_customer.fragment.TPI_BookingHistoryDetail_Fragment;
import com.angler.ti_customer.fragmentmanager.TPIFragmentManager;
import com.angler.ti_customer.helper.TPIHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mathan on 03-09-2018.
 * TPI_BookingHistory_Fragment
 */

public class TPIBookingHistoryDetailAdapter extends RecyclerView.Adapter<TPIBookingHistoryDetailAdapter.ItemViewHolder> implements TPICommonValues {

    public FragmentActivity myContext;
    private ArrayList<Booking> myOrderInfoList;
    private TPIFragmentManager myFragmentManager;
    private TPIPreferences myPreference;

    public TPIBookingHistoryDetailAdapter(FragmentActivity context, ArrayList<Booking> aOrderInfoList) {
        try {
            myContext = context;
            myOrderInfoList = aOrderInfoList;
            myFragmentManager = new TPIFragmentManager(myContext);
            myPreference = new TPIPreferences(myContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_booking_history_detail_list, parent, false);
        return new ItemViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        try {
            final Booking aBookingItem = myOrderInfoList.get(position);

            holder.mySno.setText(String.format("%s", position + 1));
            holder.myModelName.setText(aBookingItem.booking_model_name +"\n" +aBookingItem.booking_model_desc);
            holder.myQty.setText(aBookingItem.booking_qty);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return myOrderInfoList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.inflate_sno_txt)
        TextView mySno;

        @BindView(R.id.inflate_model_txt)
        TextView myModelName;

        @BindView(R.id.inflate_qty_txt)
        TextView myQty;


        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
