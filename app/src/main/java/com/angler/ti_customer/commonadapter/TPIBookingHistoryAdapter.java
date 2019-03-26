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

public class TPIBookingHistoryAdapter extends RecyclerView.Adapter<TPIBookingHistoryAdapter.ItemViewHolder> implements TPICommonValues {

    public FragmentActivity myContext;
    private ArrayList<Booking> myOrderInfoList;
    private TPIFragmentManager myFragmentManager;
    private TPIPreferences myPreference;

    public TPIBookingHistoryAdapter(FragmentActivity context, ArrayList<Booking> aOrderInfoList) {
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_booking_history_list, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        try {
            final Booking aBookingItem = myOrderInfoList.get(position);

            holder.mySno.setText(aBookingItem.booking_sl_no);
            holder.myRequestNo.setText(aBookingItem.booking_req_no);
            holder.myShippingTo.setText(aBookingItem.booking_location_code);

            String aDate = TPIHelper.convertIntoDateFormat(aBookingItem.booking_date, mydateFormat, "MM/dd/yyyy hh:mm:ss a");
            holder.myDate.setText(aDate);
          //  holder.myDate.setText(TPIHelper.convertIntoDateFormat(aBookingItem.booking_date, mydateFormat, myDateTimeFormat));
            holder.myTotalQty.setText(aBookingItem.booking_total_qty);

            holder.myBookingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("values", aBookingItem);
                    myFragmentManager.updateContent(new TPI_BookingHistoryDetail_Fragment(), TPI_BookingHistoryDetail_Fragment.TAG, bundle);
                }
            });

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

        @BindView(R.id.inflate_req_no_txt)
        TextView myRequestNo;

        @BindView(R.id.inflate_ship_to_txt)
        TextView myShippingTo;

        @BindView(R.id.inflate_date_txt)
        TextView myDate;

        @BindView(R.id.inflate_total_qty_txt)
        TextView myTotalQty;

        @BindView(R.id.booking_LAY)
        LinearLayout myBookingView;


        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
