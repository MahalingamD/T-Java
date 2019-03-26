package com.angler.ti_customer.app;


import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;


public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    private static AppController myInstance;
    private RequestQueue myRequestQueue;
    private ImageLoader myImageLoader;

    public static synchronized AppController getInstance() {
        return myInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myInstance = this;
        //setFont();
    }

    public RequestQueue getRequestQueue() {

        try {
            if (myRequestQueue == null)
                myRequestQueue = Volley.newRequestQueue(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return myRequestQueue;
    }

    public ImageLoader getImageLoader() {
        try {
            getRequestQueue();
            if (myImageLoader == null)
                myImageLoader = new ImageLoader(this.myRequestQueue, new LruBitmapCache());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.myImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        try {
            // set the default tag if tag is empty
            req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
            getRequestQueue().add(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void addToRequestQueue(Request<T> req) {
        try {
            req.setTag(TAG);
            getRequestQueue().add(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelPendingRequests(Object tag) {
        try {
            if (myRequestQueue != null) {
                myRequestQueue.cancelAll(tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 /*   private void setFont() {
        TIDCFontsOverride.setDefaultFont(getApplicationContext(), "DEFAULT", "fonts/OpenSans_Light.ttf");
        TIDCFontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/OpenSans_Light.ttf");
        TIDCFontsOverride.setDefaultFont(this, "SERIF", "fonts/OpenSans_Light.ttf");
        TIDCFontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/OpenSans_Light.ttf");

    }*/


   /* public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }*/
}