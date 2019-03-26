package com.angler.ti_customer.activities;

import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.angler.ti_customer.R;
import com.angler.ti_customer.cachememory.TPIPreferences;
import com.angler.ti_customer.listener.IAuthenticateListener;
import com.angler.ti_customer.utils.FingerprintHandler;
import com.angler.ti_customer.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahalingam on 16-07-2018.
 * TPI_FingerPrintActivity
 */

public class TPI_FingerPrintActivity extends AppCompatActivity implements IAuthenticateListener {

    AppCompatActivity myContext;
    FingerprintHandler myFingerprintHandler;
    private TPIPreferences myPreference;

    @BindView(R.id.activity_fingerprint_IMG)
    ImageView myFingerprintImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print);
        ButterKnife.bind(this);
        initializeClassAndWidgets();
    }

    private void initializeClassAndWidgets() {
        myContext = this;
        myPreference = new TPIPreferences(myContext);


    }

    @Override
    public void onAuthenticate(String decryptPassword) {
        // myFingerprintImg.tint
        // myFingerprintImg.setColorFilter(ContextCompat.getColor(myContext, R.color.green), android.graphics.PorterDuff.Mode.MULTIPLY);
        // callMainScreen();

        if (decryptPassword.equals("0")) {
            myFingerprintImg.setColorFilter(ContextCompat.getColor(myContext, R.color.red), android.graphics.PorterDuff.Mode.MULTIPLY);
            //callMainScreen();
        } else {
            myFingerprintImg.setColorFilter(ContextCompat.getColor(myContext, R.color.green), android.graphics.PorterDuff.Mode.MULTIPLY);
            callMainScreen();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initSensor();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (myFingerprintHandler != null) {
                myFingerprintHandler.cancel();
            }
        }
    }

    private void initSensor() {
        if (Utils.checkSensorState(this)) {
            FingerprintManager.CryptoObject cryptoObject = Utils.getCryptoObject();
            if (cryptoObject != null) {
                //   Toast.makeText(this, "Use fingerprint to login", Toast.LENGTH_LONG).show();
                FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    myFingerprintHandler = new FingerprintHandler(this, myPreference, this);
                    myFingerprintHandler.startAuth(fingerprintManager, cryptoObject);
                }

            }
        }
    }


    private void callMainScreen() {
        if (isLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), TPI_HomeActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), TPI_Login_screen.class));
        }
        this.finish();
    }

    private boolean isLoggedIn() {
        return myPreference.getLoginStatus();
    }
}
