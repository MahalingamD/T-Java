package com.angler.ti_customer.commonadapter;

import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angler.ti_customer.R;
import com.angler.ti_customer.cachememory.TPIPreferences;
import com.angler.ti_customer.commonmodel.SalesDate;
import com.angler.ti_customer.constants.TPICommonValues;
import com.angler.ti_customer.fragmentmanager.TPIFragmentManager;
import com.angler.ti_customer.helper.TPIHelper;

import java.util.ArrayList;

/**
 * Created by Mahalingam on 23-01-2018.
 */

public class TPIDateListAdapter extends RecyclerView.Adapter<TPIDateListAdapter.ItemViewHolders> implements TPICommonValues {

    public FragmentActivity myContext;
    private ArrayList<SalesDate> myDateInfoList;
    private TPIFragmentManager myFragmentManager;
    private TPIPreferences myPreference;
    private boolean[] myItemChecked;
    private AppCallback myCallback;


    public TPIDateListAdapter(FragmentActivity context, ArrayList<SalesDate> aDateInfoList, AppCallback myCallback) {
        try {
            myContext = context;
            this.myCallback = myCallback;
            myDateInfoList = aDateInfoList;
            myFragmentManager = new TPIFragmentManager(myContext);
            myPreference = new TPIPreferences(myContext);
            myItemChecked = new boolean[aDateInfoList.size()];
            for (int i = 0; i < myItemChecked.length; i++) {
                myItemChecked[i] = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ItemViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_inflate_date_list, parent, false);
        return new TPIDateListAdapter.ItemViewHolders(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolders holder, int position) {
        try {
            final SalesDate aCropObject = myDateInfoList.get(position);

            String aDisplayString = aCropObject.getDisplayDate();

            String array1[] = aDisplayString.split(" ");

            holder.myDay.setText(TPIHelper.capitalize(array1[0]));
            holder.myDate.setText(array1[1]);
            holder.myMonth.setText(TPIHelper.capitalize(array1[2]));

            clickListener(holder, position);
            if (aCropObject.isActive()) {
                holder.myDateLinear.setBackgroundColor(ContextCompat.getColor(myContext, R.color.dark_oriange));
            } else {
                holder.myDateLinear.setBackgroundColor(ContextCompat.getColor(myContext, R.color.blue_end));
            }

           /* if (myItemChecked[position]) {
                holder.myDateLinear.setBackgroundColor(ContextCompat.getColor(myContext, R.color.dark_oriange));
            } else {
                holder.myDateLinear.setBackgroundColor(ContextCompat.getColor(myContext, R.color.blue_end));
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clickListener(final ItemViewHolders holder, final int position) {
        holder.myDateLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < myItemChecked.length; i++) {
                    myItemChecked[i] = false;
                }
                myItemChecked[position] = true;
                myCallback.onClick(position);
                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return myDateInfoList.size();
    }

    public void update(ArrayList<SalesDate> aDateArraylist) {
        myDateInfoList = aDateArraylist;
        notifyDataSetChanged();
    }

    public interface AppCallback {
        void onClick(int position);
    }


    public class ItemViewHolders extends RecyclerView.ViewHolder {

        //  @BindView(R.id.inflate_unit_name)
        TextView myDate;

        TextView myDay;

        // @BindView(R.id.inflate_unit_address)
        TextView myMonth;

        // @BindView(R.id.inflate_card_linear)
        LinearLayout myDateLinear;


        ItemViewHolders(View view) {
            super(view);
            //myOrderModelName = (TextView) view.findViewById(R.id.inflate_unit_name);
            myDate = (TextView) view.findViewById(R.id.inflate_date);
            myDay = (TextView) view.findViewById(R.id.inflate_day);
            myMonth = (TextView) view.findViewById(R.id.inflate_month);
            myDateLinear = (LinearLayout) view.findViewById(R.id.inflate_card_linear);

            //  ButterKnife.bind(myContext, view);
        }
    }
}
