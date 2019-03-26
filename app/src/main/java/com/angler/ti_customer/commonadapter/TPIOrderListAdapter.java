package com.angler.ti_customer.commonadapter;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angler.ti_customer.R;
import com.angler.ti_customer.cachememory.TPIPreferences;
import com.angler.ti_customer.commonmodel.OrderScheduledInfo;
import com.angler.ti_customer.constants.TPICommonValues;
import com.angler.ti_customer.fragmentmanager.TPIFragmentManager;
import com.angler.ti_customer.helper.TPIHelper;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 06-07-2017
 * Created by mahalingam on 15-06-2017.
 * MGCropListAdapter
 */

public class TPIOrderListAdapter extends RecyclerView.Adapter<TPIOrderListAdapter.ItemViewHolders> implements TPICommonValues {

    public FragmentActivity myContext;
    private ArrayList<OrderScheduledInfo> myOrderInfoList;
    private TPIFragmentManager myFragmentManager;
    private TPIPreferences myPreference;
    private boolean[] myItemChecked;
    private ClickListener myListener;
    private String mySearchText;

    public TPIOrderListAdapter(FragmentActivity context, ArrayList<OrderScheduledInfo> aOrderInfoList) {
        try {
            myContext = context;
            myOrderInfoList = aOrderInfoList;
            myFragmentManager = new TPIFragmentManager(myContext);
            myPreference = new TPIPreferences(myContext);
            myItemChecked = new boolean[aOrderInfoList.size()];
            for (int i = 0; i < myItemChecked.length; i++) {
                myItemChecked[i] = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public ItemViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_inflate_order_list, parent, false);
        return new ItemViewHolders(itemView);
    }

    @Override
//    public void onBindViewHolder(ItemViewHolder holder, int position) {
    public void onBindViewHolder(ItemViewHolders holder, int position) {
        try {

            OrderScheduledInfo aOrderObject = myOrderInfoList.get(position);

            String aModelNamePosition = aOrderObject.getMODEL_NAME().toLowerCase();
            String aModelDescriptionPositon = aOrderObject.getMODEL_DESCRIPTION().toLowerCase();
            String aModelPartPosition = aOrderObject.getPART_NUMBER().toLowerCase();

            holder.myOrderModelName.setText(TPIHelper.capitalize(aOrderObject.getMODEL_NAME()));
            holder.myModelDesc.setText(TPIHelper.capitalize(aOrderObject.getMODEL_DESCRIPTION()));
            holder.myModelPart.setText(TPIHelper.capitalize(aOrderObject.getPART_NUMBER()));


            if (!mySearchText.isEmpty()) {
                int aModelNameIndex = aModelNamePosition.indexOf(mySearchText.toLowerCase());
                int aModelDescpritionIndex = aModelDescriptionPositon.indexOf(mySearchText.toLowerCase());
                int aModelPartIndex = aModelPartPosition.indexOf(mySearchText.toLowerCase());

                if (aModelNameIndex != -1 || aModelDescpritionIndex != -1 || aModelPartIndex != -1) {
                    Spannable spanText;

                    if (aModelNameIndex != -1) {
                        spanText = new SpannableString(holder.myOrderModelName.getText());
                        spanText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(myContext, R.color.confirm)), aModelNameIndex, aModelNameIndex + mySearchText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        holder.myOrderModelName.setText(spanText);
                    }

                    if (aModelDescpritionIndex != -1) {
                        spanText = new SpannableString(holder.myModelDesc.getText());
                        spanText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(myContext, R.color.confirm)), aModelDescpritionIndex, aModelDescpritionIndex + mySearchText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        holder.myModelDesc.setText(spanText);
                    }

                    if (aModelPartIndex != -1) {
                        spanText = new SpannableString(holder.myModelPart.getText());
                        spanText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(myContext, R.color.confirm)), aModelPartIndex, aModelPartIndex + mySearchText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        holder.myModelPart.setText(spanText);
                    }

                }
            } else {
//                holder.myOrderModelName.setText(TPIHelper.capitalize(aOrderObject.getMODEL_NAME()));
                holder.myModelDesc.setText(TPIHelper.capitalize(aOrderObject.getMODEL_DESCRIPTION()));
            }


            setListener(holder, aOrderObject, position);

            if (aOrderObject.isActive()) {
                holder.myOrdersAddBut.setVisibility(View.INVISIBLE);
                holder.myOrderschEdit.setText(aOrderObject.getOrder_Qty());
                holder.myPendingQty.setText(aOrderObject.getSCHEDULED_QUANTITY());
                holder.myOrderschEdit.setEnabled(false);
                holder.myCardview.setCardBackgroundColor(ContextCompat.getColor(myContext, R.color.light_grey));
            } else {
                holder.myOrdersAddBut.setVisibility(View.VISIBLE);
                holder.myOrderschEdit.setText(aOrderObject.getOrder_Qty());
                holder.myOrderschEdit.setEnabled(true);
                holder.myPendingQty.setText(aOrderObject.getSCHEDULED_QUANTITY());
                holder.myCardview.setCardBackgroundColor(ContextCompat.getColor(myContext, R.color.white));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListener(final ItemViewHolders holder, final OrderScheduledInfo aOrderObject, final int position) {
        holder.myOrderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    try {
                        Bundle aBundle = new Bundle();
                        Gson gson = new Gson();
                        String json = gson.toJson(aOrderObject);
                        aBundle.putString(GSON_ARRAYLIST, json);

                        // myPreference.putCropDetails(aCropObject);
                        //  myFragmentManager.updateContent(new TPI_ConfirmOrderFragment(), TPI_ConfirmOrderFragment.TAG, aBundle);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.myOrdersAddBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderBtnAction(holder, aOrderObject, position);
            }
        });
    }

    private void orderBtnAction(ItemViewHolders holder, OrderScheduledInfo aOrderObject, int position) {
        try {

            double aScheduleQty = Double.parseDouble(aOrderObject.getSCHEDULED_QUANTITY());

            String aMessageQuantity;

            if (holder.myOrderschEdit.getText().toString().trim().length() == 0) {
                aMessageQuantity = myContext.getResources().getString(R.string.alert_message_NoQuantity_Add);
                showAlertDialog(myContext, aMessageQuantity);
                holder.myOrderschEdit.setText("");
                TPIHelper.hideKeyBoard(myContext);
                return;
//                        Toast.makeText(myContext, "Please Fill Value", Toast.LENGTH_SHORT).show();
            } else if (Double.parseDouble(holder.myOrderschEdit.getText().toString().trim()) <= 0) {
                aMessageQuantity = myContext.getResources().getString(R.string.alert_message_ValidQuantity_Add);
                showAlertDialog(myContext, aMessageQuantity);
                holder.myOrderschEdit.setText("");
                TPIHelper.hideKeyBoard(myContext);
                return;
            } else {

                String selecteDate = myPreference.getSelectedDate();
                String lastDate = myPreference.getLastDayMonth();
                if (!selecteDate.isEmpty()) {
                    if (selecteDate.equals(lastDate)) {

                    } else {
                        if (TPIHelper.isAfter2pm() && TPIHelper.isValidDateForOrder(selecteDate)) {
                            String msg = String.format("Oops! Sorry you can't place the order for %s after 2PM. \nDo you want to refresh this page?", selecteDate);
                            showDateAlert(myContext, msg);
                            return;
                        }
                    }

                }

                myItemChecked[position] = true;
                String aString = holder.myOrderschEdit.getText().toString().trim();
                aOrderObject.setOrder_Qty(aString);

                double aorderQty = Double.parseDouble(aString.trim());

                if (aScheduleQty < aorderQty) {
                    showAlertDialog(myContext, myContext.getString(R.string.label_check_qty_item));
                    holder.myOrderschEdit.setText("");
                    TPIHelper.hideKeyBoard(myContext);
                } else {
                    myListener.getItem(aOrderObject, aorderQty);
                }
            }
            TPIHelper.hideKeyBoard(myContext);

        } catch (
                Exception e)

        {
            e.printStackTrace();
        }
    }


    public void showAlertDialog(final FragmentActivity aContext, String aMessage) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(aContext);
            dialog.setCancelable(false);
            dialog.setTitle(aContext.getString(R.string.app_name));
            dialog.setIcon(R.mipmap.ic_launcher);
            dialog.setMessage(aMessage);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            final AlertDialog alert = dialog.create();
            alert.show();

            //SET BUTTON_NEGATIVE and BUTTON_POSITIVE Color
            alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(aContext, R.color.black));
            alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(aContext, R.color.black));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showDateAlert(final FragmentActivity aContext, String aMessage) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(aContext);
            dialog.setCancelable(false);
            dialog.setTitle(aContext.getString(R.string.app_name));
            dialog.setIcon(R.mipmap.ic_launcher);
            dialog.setMessage(aMessage);
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    myListener.refresh();
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            dialog.setCancelable(false);

            final AlertDialog alert = dialog.create();
            alert.show();

            //SET BUTTON_NEGATIVE and BUTTON_POSITIVE Color
            alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(aContext, R.color.black));
            alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(aContext, R.color.black));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return myOrderInfoList.size();
    }

    public void updateSearchList(ArrayList<OrderScheduledInfo> aOrderinfo, String aSearchtext) {
        try {
            myOrderInfoList = aOrderinfo;
            notifyDataSetChanged();
            myItemChecked = new boolean[myOrderInfoList.size()];
            for (int i = 0; i < myItemChecked.length; i++) {
                myItemChecked[i] = false;
            }
            setFilter(myOrderInfoList, aSearchtext);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateList(ArrayList<OrderScheduledInfo> aOrderinfo) {
        try {
            myOrderInfoList = aOrderinfo;
            notifyDataSetChanged();
            myItemChecked = new boolean[myOrderInfoList.size()];
            for (int i = 0; i < myItemChecked.length; i++) {
                myItemChecked[i] = false;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFilter(ArrayList<OrderScheduledInfo> aModelNameList, String searchText) {
        myOrderInfoList = new ArrayList<>();
        if (aModelNameList.size() < 1) {
//            Toast.makeText(myContext,"No Records Found",Toast.LENGTH_LONG).show();
//            holder.myNoItemFoundLayout.setVisibility(View.VISIBLE);
//            return;
        }

        myOrderInfoList.addAll(aModelNameList);
        this.mySearchText = searchText;
        notifyDataSetChanged();
    }


    /*
     * ItemViewHolder
     * */
    public class ItemViewHolders extends RecyclerView.ViewHolder {

        @BindView(R.id.inflate_order_model_name)
        TextView myOrderModelName;

        @BindView(R.id.inflate_pending_qty)
        TextView myPendingQty;

        @BindView(R.id.inflate_order_model_desc)
        TextView myModelDesc;

        @BindView(R.id.inflate_order_model_part)
        TextView myModelPart;

        @BindView(R.id.inflate_order_card_linear)
        LinearLayout myOrderLayout;

        @BindView(R.id.inflate_sch_qty)
        EditText myOrderschEdit;

        @BindView(R.id.inflate_sch_add)
        TextView myOrdersAddBut;

        @BindView(R.id.inflate_root_cardview)
        CardView myCardview;

        @BindView(R.id.no_itemfound_include_layout)
        View myNoItemFoundLayout;

        @BindView(R.id.itemnotfoundtextview)
        TextView myItemNotFoundTextView;


        ItemViewHolders(View view) {
            super(view);
            ButterKnife.bind(this, view);
            setIsRecyclable(true);
        }

    }


    public interface ClickListener {
        void getItem(OrderScheduledInfo OrderSchedule, double aorderQty);

        void refresh();

    }


    public void getItem(ClickListener aListener) {
        myListener = aListener;
    }

}
