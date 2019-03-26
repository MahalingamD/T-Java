package com.angler.ti_customer.commonadapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.angler.ti_customer.R;
import com.angler.ti_customer.cachememory.TPIPreferences;
import com.angler.ti_customer.commonmodel.CustomerSelectedItem;
import com.angler.ti_customer.commonmodel.CustomerSiteList;
import com.angler.ti_customer.commonmodel.OrderScheduledInfo;
import com.angler.ti_customer.constants.TPICommonValues;
import com.angler.ti_customer.fragmentmanager.TPIFragmentManager;
import com.angler.ti_customer.helper.TPIHelper;
import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 06-07-2017
 * Created by mahalingam on 15-06-2017.
 * MGCropListAdapter
 */

public class TPIConfirmOrderListAdapter extends RecyclerView.Adapter<TPIConfirmOrderListAdapter.ItemViewHolders> implements TPICommonValues {

    public FragmentActivity myContext;
    private ArrayList<CustomerSelectedItem> myOrderInfoList;
    private TPIFragmentManager myFragmentManager;
    private TPIPreferences myPreference;
    public ClickListener myListener;

    public TPIConfirmOrderListAdapter(FragmentActivity context, ArrayList<CustomerSelectedItem> aOrderInfoList) {
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
    public ItemViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_inflate_confirm_order, parent, false);
        return new ItemViewHolders(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolders holder, int position) {
        try {
            final CustomerSelectedItem aCustomerSelectedItem = myOrderInfoList.get(position);

            holder.mySno.setText(String.format("%d", position + 1));
            holder.myModelName.setText(TPIHelper.capitalize(aCustomerSelectedItem.getMODEL_NAME()));
            holder.myModelDescriptionTextView.setText(TPIHelper.capitalize(aCustomerSelectedItem.getMODEL_DESCRIPTION()));
            holder.myModelPartNumberTextView.setText(TPIHelper.capitalize(aCustomerSelectedItem.getPART_NUMBER()));

            holder.myOrderQtyEdit.setText(TPIHelper.capitalize(aCustomerSelectedItem.getSCHEDULE_QUANTITY()));

            setListener(holder, aCustomerSelectedItem, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListener(ItemViewHolders holder, final CustomerSelectedItem aCustomerSelectedItem, final int position) {
        holder.myDeleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showDeleteAlertDialog(myContext.getString(R.string.label_delete), myContext, aCustomerSelectedItem, position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.myOrderQtyEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAlertDialog(aCustomerSelectedItem);
            }
        });
    }

    private void updateAlertDialog(final CustomerSelectedItem schedule_quantity) {

        LayoutInflater inflater = LayoutInflater.from(myContext);
        final View view = inflater.inflate(R.layout.layout_confirm_alert, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(myContext).create();
        alertDialog.setTitle(R.string.app_name);
        alertDialog.setIcon(R.mipmap.ic_launcher_round);
        alertDialog.setCancelable(false);
       /* alertDialog.setMessage(myContext.getString(R.string.label_pending_quantity) + schedule_quantity.getPending_Qty()
                + System.getProperty("line.separator") + myContext.getString(R.string.label_enter_qty));*/


        final EditText etQty = (EditText) view.findViewById(R.id.inflate_Qty_Edt);
        etQty.setText(schedule_quantity.getSCHEDULE_QUANTITY());
        int position = etQty.length();
        etQty.setSelection(position);

        final TextView aPendingQty = (TextView) view.findViewById(R.id.alert_peding_qty);
        if (schedule_quantity.getPending_Qty() != null && !schedule_quantity.getPending_Qty().equals(""))
            aPendingQty.setText(schedule_quantity.getPending_Qty());


        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if (etQty.getText().toString().trim().length() == 0) {
                        alertDialog.dismiss();
                        notifyDataSetChanged();
                        showAlertDialog(myContext, myContext.getString(R.string.alert_message_ValidQuantity_Add));
                    } else if (Double.parseDouble(etQty.getText().toString().trim()) <= 0) {
                        alertDialog.dismiss();
                        notifyDataSetChanged();
                        showAlertDialog(myContext, myContext.getString(R.string.alert_message_ValidQuantity_Add));
                    } else if (Double.parseDouble(etQty.getText().toString().trim()) > 0) {
                        try {
                            double aOrderQty = Double.parseDouble(etQty.getText().toString().trim());
                            double aPendingQty = Double.parseDouble(schedule_quantity.getPending_Qty());

                            if (aPendingQty < aOrderQty) {
                                alertDialog.dismiss();
                                TPIHelper.hideKeyBoard(myContext);
                                showAlertDialog(myContext, myContext.getString(R.string.label_check_qty_item));
                                etQty.setText("");
                            } else {
                                double aQty = Double.parseDouble(etQty.getText().toString().trim());
                                myListener.setUpdate(schedule_quantity, aQty);
                                alertDialog.dismiss();
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });


        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(view);
        alertDialog.show();
    }


    public void showAlertDialog(final FragmentActivity aContext, String aMessage) {
        try {
            android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(aContext);
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

            final android.support.v7.app.AlertDialog alert = dialog.create();
            alert.show();

            //SET BUTTON_NEGATIVE and BUTTON_POSITIVE Color
            alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(aContext, R.color.black));
            alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(aContext, R.color.black));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param aMessage            String
     * @param aCustomerSelectInfo CustomerSelectedItem
     * @param position
     */
    private void showDeleteAlertDialog(String aMessage, final FragmentActivity aContext, final CustomerSelectedItem aCustomerSelectInfo, final int position) {
        try {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(aContext);
            builder.setMessage(aMessage)
                    .setTitle(aContext.getString(R.string.app_name))
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            myListener.setCount(aCustomerSelectInfo, position);
                            dialog.dismiss();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            android.support.v7.app.AlertDialog alert = builder.create();
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

    @Override
    public int getItemCount() {
        return myOrderInfoList.size();
    }


    public void setCount(ClickListener aListener) {
        myListener = aListener;
    }

    public void updateAdapter(ArrayList<CustomerSelectedItem> aConfirmItemList) {
        myOrderInfoList = aConfirmItemList;
        notifyDataSetChanged();
    }

    public interface ClickListener {

        public void setCount(CustomerSelectedItem aCustomerInfo, int position);

        public void setUpdate(CustomerSelectedItem acustomerItem, double aQty);
    }

    /*
 * ItemViewHolder
 * */
    public class ItemViewHolders extends RecyclerView.ViewHolder {

        @BindView(R.id.inflate_sno_txt)
        TextView mySno;

        @BindView(R.id.inflate_model_name_txt)
        TextView myModelName;

        @BindView(R.id.model_Description_textView)
        TextView myModelDescriptionTextView;

        @BindView(R.id.model_partNumber_textView)
        TextView myModelPartNumberTextView;

        @BindView(R.id.inflate_Qty_Edt)
        TextView myOrderQtyEdit;

        @BindView(R.id.inflate_delete_img)
        ImageView myDeleteImg;


        ItemViewHolders(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
