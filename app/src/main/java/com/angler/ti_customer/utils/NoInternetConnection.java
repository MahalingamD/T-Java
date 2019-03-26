package com.angler.ti_customer.utils;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.angler.ti_customer.R;
import com.angler.ti_customer.listener.TPIRefreshListener;


public class NoInternetConnection implements OnClickListener {

    public static String TAG = NoInternetConnection.class.getSimpleName();
    private LinearLayout myLinearLayout;
    TPIRefreshListener myListener;

    public NoInternetConnection(Activity aContext) {
        try {
            myLinearLayout = (LinearLayout) aContext
                    .findViewById(R.id.layout_inflate_no_internrt_main_LAY);
            myLinearLayout.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View aView) {
        switch (aView.getId()) {
            case R.id.layout_inflate_no_internrt_main_LAY:
                try {
                    myListener.onRefreshLayoutClick();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }

    }

    public void setonTapListener(TPIRefreshListener aListener) {
        myListener = aListener;

    }

}
