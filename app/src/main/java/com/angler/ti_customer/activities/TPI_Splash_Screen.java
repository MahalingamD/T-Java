package com.angler.ti_customer.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.angler.ti_customer.BuildConfig;
import com.angler.ti_customer.R;
import com.angler.ti_customer.cachememory.TPIPreferences;
import com.angler.ti_customer.commonmodel.Version;
import com.angler.ti_customer.constants.TPICommonValues;
import com.angler.ti_customer.constants.TPIWebCommonValues;
import com.angler.ti_customer.helper.TPIHelper;
import com.angler.ti_customer.listener.IAuthenticateListener;
import com.angler.ti_customer.listener.TPIWebServiceCallBack;
import com.angler.ti_customer.utils.APPUpdater;
import com.angler.ti_customer.utils.FingerprintHandler;
import com.angler.ti_customer.utils.Utils;
import com.angler.ti_customer.webservices.TPIReturnValues;
import com.angler.ti_customer.webservices.TPIWebService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class TPI_Splash_Screen extends AppCompatActivity implements TPIWebCommonValues, TPICommonValues, IAuthenticateListener {

    private AppCompatActivity myAppContext;
    int DELAY_TIME_FOR_SPLASH_SCREEN = 3000;
    private TPIPreferences myPreference;

    private String myDBFileName;
    private boolean isSplashScreenLoadedTimeOut = false;
    private DBInsert myDBLoadTask = null;
    private boolean isDBPresent = true;


    private TPIReturnValues myReturnValue;
    TPIWebService myWebservice;

    public APPUpdater myUpdateAPP;
    private static boolean mycheck = false;


    public AlertDialog.Builder myOptionalDialog;
    public AlertDialog myOptionalAlert;

    FingerprintHandler myFingerprintHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        myAppContext = TPI_Splash_Screen.this;
        myPreference = new TPIPreferences(myAppContext);
        myUpdateAPP = new APPUpdater(myAppContext);
        myReturnValue = new TPIReturnValues();


      /*  if (!isDBExist(myDBFileName)) {

            Log.d("DB", "NOT PRESENT & Started async task");

            isDBPresent = false;
            myDBLoadTask = new DBInsert();
            myDBLoadTask.execute();

        } else {
            Log.d("DB", "DB PRESENT");
        }*/

       /*Handler aHoldScreen = new Handler();
        aHoldScreen.postDelayed(new Runnable() {
            public void run() {
                checkAppStatus();
            }
        }, DELAY_TIME_FOR_SPLASH_SCREEN);*/

        // checkAppStatus();
        //appVersionInsert();

        // splashTimer();

        //  requestPermission();
        // checkAppStatus();
    }


    private void requestPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        } else {
            checkAppStatus();
        }

    }


    private void callMainScreen() {
        if (isLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), TPI_HomeActivity.class));
           // startActivity(new Intent(getApplicationContext(), TPI_FingerPrintActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), TPI_Login_screen.class));
        }
        this.finish();
    }

    private boolean isLoggedIn() {
        return myPreference.getLoginStatus();
    }

    @Override
    public void onAuthenticate(String decryptPassword) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }

    class DBInsert extends AsyncTask<Void, Void, Long> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Long doInBackground(Void... params) {

            constructNewFileFromResources(myDBFileName);
            return null;
        }

        protected void onPostExecute(Long result) {
            super.onPostExecute(result);

            Log.d("SPLASH", "Completed" + result);

            if (isSplashScreenLoadedTimeOut) {
                isSplashScreenLoadedTimeOut = true;
                Log.d("DB", "DB LOADED AFTER SPLASH SCREEN TIME OUT");
                callMainScreen();

            } else {
                Log.d("DB", "DB LOADED BEFORE SPLASH SCREEN TIME OUT");
                // callMainScreen();
            }

        }

    }

    private boolean isDBExist(String aDBFileName) {
        boolean aStatus = false;
        SQLiteDatabase aCheck = null;
        try {
            File file = new File(aDBFileName);
            if (file.exists() && !file.isDirectory()) {
                aCheck = SQLiteDatabase.openDatabase(aDBFileName, null,
                        SQLiteDatabase.OPEN_READONLY
                                | SQLiteDatabase.NO_LOCALIZED_COLLATORS);

                aStatus = (aCheck != null) ? true : false;

                if (aStatus)
                    aCheck.close();
            } else {
                aStatus = false;
            }

        } catch (SQLiteException e) {
            e.printStackTrace();

        }
        return aStatus;
    }

    public void constructNewFileFromResources(String DBFile) {
        try {
            String packageName = getApplicationContext().getPackageName();
            String appDatabaseDirectory = String.format(
                    "/data/data/%s/databases", packageName);
            (new File(appDatabaseDirectory)).mkdir();
            OutputStream dos = new FileOutputStream(appDatabaseDirectory + "/" + DATABASE_NAME);
            InputStream dis = getResources().openRawResource(DB_RAW_RESOURCES_ID);
            byte[] buffer = new byte[1028];
            while ((dis.read(buffer)) > 0) {
                dos.write(buffer);
            }
            dos.flush();
            dos.close();
            dis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean checkInternet() {
        return TPIHelper.checkInternet(myAppContext);
    }


    public void checkAppStatus() {
        if (checkInternet()) {
            try {
                myWebservice = new TPIWebService(myAppContext);

                String aInputStr = "P_APP_ID" + "=" + BuildConfig.APP_Id +
                        "&" + "P_DEVICE_TYPE" + "=" + BuildConfig.Device_Type +
                        "&" + "P_VERSION_CODE" + "=" + BuildConfig.VERSION_NAME;
                // "&" + "P_VERSION_CODE" + "=" + "1.1";

                if (aInputStr.contains(" ")) {
                    aInputStr = aInputStr.replaceAll(" ", "%20");
                }

                myWebservice.checkVersionUpdate(aInputStr, new TPIWebServiceCallBack() {
                    @Override
                    public void onSuccess(Object object) {
                        //myProgressDialog.dismiss();
                        myReturnValue = (TPIReturnValues) object;

                        if (myReturnValue.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {

                            if (myReturnValue.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {
                                callLocalServerFunction(myReturnValue);
                                myUpdateAPP.setOnLaterButtonListener(new APPUpdater.OnLaterButtonListener() {
                                    @Override
                                    public void onLaterEvent(boolean bool) {
                                        if (bool) {
                                            callMainScreen();
                                        }
                                    }
                                });
                            } else {
                                TPIHelper.showAlertDialog(myReturnValue.getResponseMessage(), myAppContext);
                            }


                        } else {
                            TPIHelper.showAlertDialog(myReturnValue.getResponseMessage(), myAppContext);
                        }
                    }

                    @Override
                    public void onFailer(Object object) {
                        Handler aHoldScreen = new Handler();
                        aHoldScreen.postDelayed(new Runnable() {

                            public void run() {
                                if (isDBPresent) {
                                    callMainScreen();
                                } else {
                                    checkAsynStatus();
                                }
                            }
                        }, DELAY_TIME_FOR_SPLASH_SCREEN);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            callMainScreen();
        }

    }


    public void checkAsynStatus() {
        try {
            if (myDBLoadTask.getStatus() == AsyncTask.Status.RUNNING) {
                isSplashScreenLoadedTimeOut = true;
            } else {
                Log.d("TSG", "DB LOADING FINISHED WHILE SPLASH SCREEN HANDLER CALL");
                callMainScreen();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TIDCReturnValues myReturnValues
    public void callLocalServerFunction(TPIReturnValues myReturnValues) {

        try {
            if (myReturnValues != null) {
                //Get the version Array items from webservice
                Version myVersionCheck = myReturnValues.getMyVersionResponse();

                if (myVersionCheck.getMyUpdateType().equalsIgnoreCase(getString(R.string.app_update_local_type))) {
                    //Condition to check Update not mandatory
                    if (myVersionCheck.getMyCurrentVersion().equals(BuildConfig.VERSION_NAME)) {
                        // if (myVersionCheck.getMyCurrentVersion().equals("1.1")) {
                        callMainScreen();  //BuildConfig.VERSION_NAME
                    } else {
                        APPUpdater.Type aType;
                        if (myVersionCheck.getMyStatus().equals("2")) {
                            aType = APPUpdater.Type.MANDATORY_UPDATE;
                        } else if (myVersionCheck.getMyStatus().equals("1")) {
                            aType = APPUpdater.Type.NON_MANDATORY_UPDATE;
                        } else {
                            aType = APPUpdater.Type.NO_UPDATE;
                        }
                        myUpdateAPP.updateAPPMethod(myAppContext, myVersionCheck.getMyMessage(), myVersionCheck.getMyUpdateURL(), aType, getString(R.string.app_name), R.mipmap.ic_launcher);
                    }
                } else if (myVersionCheck.getMyUpdateType().equalsIgnoreCase(getString(R.string.app_update_store_type))) {
                    if (myVersionCheck.getMyCurrentVersion().equals(BuildConfig.VERSION_NAME)) {
                        callMainScreen();
                    } else {
                        showUpdateDialog(getString(R.string.app_update_text));
                    }
                } else {
                    callMainScreen();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showUpdateDialog(String aAlertContent) {
        String aButtonName;
        try {
            myOptionalDialog = new AlertDialog.Builder(myAppContext);
            myOptionalDialog.setCancelable(true);
            myOptionalDialog.setTitle(getString(R.string.app_name));
            myOptionalDialog.setIcon(R.mipmap.ic_launcher);
            myOptionalDialog.setMessage(aAlertContent);
            myOptionalDialog.setCancelable(false);
            aButtonName = myAppContext.getString(R.string.msg_app_update);
            myOptionalDialog.setPositiveButton(aButtonName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            myOptionalDialog.setNegativeButton(R.string.msg_app_later, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            myOptionalAlert = myOptionalDialog.create();
            myOptionalAlert.show();

            myOptionalAlert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(myAppContext, R.color.blue_end));
            myOptionalAlert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(myAppContext, R.color.blue_end));

            myOptionalAlert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        myUpdateAPP.launchMarket(BuildConfig.APPLICATION_ID);
                        myOptionalAlert.dismiss();
                        myAppContext.finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            myOptionalAlert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        callMainScreen();
                        myOptionalAlert.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            //checkAppStatus();

            //if (mycheck)
            requestPermission();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              //  initSensor();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSensor() {
        if (Utils.checkSensorState(this)) {
            FingerprintManager.CryptoObject cryptoObject = Utils.getCryptoObject();
            if (cryptoObject != null) {
               // Toast.makeText(this, "Use fingerprint to login", Toast.LENGTH_LONG).show();
                FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    myFingerprintHandler = new FingerprintHandler(this, myPreference, this);
                    myFingerprintHandler.startAuth(fingerprintManager, cryptoObject);
                }

            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    checkAppStatus();

                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE);

                    if (!showRationale) {
                        // showMaterialAlertDialog();
                    } else {
                        showMaterialAlertDialog();
                    }

                    //showAlertForDeny("If you are deny you cant access this app");
                } else {

                }
                break;
        }
    }


    public void showMaterialAlertDialog() {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(myAppContext);
            dialog.setCancelable(true);
            dialog.setTitle(myAppContext.getString(R.string.app_name));
            dialog.setIcon(R.mipmap.ic_launcher);
            dialog.setMessage("Kindly give permission to continues use");
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    mycheck = true;
                    TPIHelper.goToSettings(myAppContext);
                }
            });
            final AlertDialog alert = dialog.create();
            alert.show();

            //SET BUTTON_NEGATIVE and BUTTON_POSITIVE Color
            alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(myAppContext, R.color.blue_end));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
