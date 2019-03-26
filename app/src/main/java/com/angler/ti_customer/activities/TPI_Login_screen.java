package com.angler.ti_customer.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.angler.ti_customer.BuildConfig;
import com.angler.ti_customer.R;
import com.angler.ti_customer.cachememory.TPIPreferences;
import com.angler.ti_customer.commonmodel.ServerResponse;
import com.angler.ti_customer.commonmodel.User_info;
import com.angler.ti_customer.constants.TPICommonValues;
import com.angler.ti_customer.constants.TPIWebCommonValues;
import com.angler.ti_customer.fragment.TPI_DashboardFragment;
import com.angler.ti_customer.helper.TPIHelper;
import com.angler.ti_customer.listener.IAuthenticateListener;
import com.angler.ti_customer.listener.TPIDenyAlert;
import com.angler.ti_customer.listener.TPIWebServiceCallBack;
import com.angler.ti_customer.utils.MGProgressDialog;
import com.angler.ti_customer.utils.TPIHideSoftKeypad;
import com.angler.ti_customer.webservices.TPIReturnValues;
import com.angler.ti_customer.webservices.TPIWebService;

import butterknife.BindView;
import butterknife.ButterKnife;
import permission.auron.com.marshmallowpermissionhelper.ActivityManagePermission;
import permission.auron.com.marshmallowpermissionhelper.PermissionResult;
import permission.auron.com.marshmallowpermissionhelper.PermissionUtils;

public class TPI_Login_screen extends ActivityManagePermission implements PermissionResult, TPICommonValues, TPIWebCommonValues, IAuthenticateListener {

    private AppCompatActivity myContext;
    private static long myBackPressed;

    @BindView(R.id.activity_login_EDT_email)
    EditText myLoginUserEDT;

    @BindView(R.id.activity_login_EDT_password)
    public EditText myLoginPasswordEDT;

    @BindView(R.id.activity_register_RPLV_submit)
    RippleView mySubmitRPV;

    @BindView(R.id.rememberMe_XML_CheckBox)
    CheckBox myRememberMeCheckBox;

    @BindView(R.id.activity_root_layout)
    CoordinatorLayout myRootLayout;

    @BindView(R.id.layout_tel_header_TXT_version_title)
    TextView myVersionTXT;


    private Snackbar mySnackBar;

    // TIDCDBHepler myDBHepler;
    TPIPreferences myPreferences;
    TPIWebService myWebService;
    TPIReturnValues myReturnValues;
    private MGProgressDialog myProgressDialog;
    private boolean myButtonSelection = false;
    private String myIMEI = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tpi_login_screen);
        ButterKnife.bind(this);
        initializeClassAndWidgets();
    }

    private void initializeClassAndWidgets() {

        try {
            myContext = TPI_Login_screen.this;
            myWebService = new TPIWebService(myContext);
            myPreferences = new TPIPreferences(myContext);
            myProgressDialog = new MGProgressDialog(myContext);


            myVersionTXT.setText(String.format("V_%s", BuildConfig.VERSION_NAME));


            if (myPreferences.getRememberStatus()) {
                myRememberMeCheckBox.setChecked(true);
                myLoginUserEDT.setText(myPreferences.getUserName());
                myLoginPasswordEDT.setText(myPreferences.getUserPassword());
            }

            if (!checkInternet()) {
                showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
            }


            askPermission();
            setClickListener();
            hideKeyboard();

            CheckFingerPrint();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void CheckFingerPrint() {

        FingerprintManager fingerprintManager = (FingerprintManager) myContext.getSystemService(Context.FINGERPRINT_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!fingerprintManager.isHardwareDetected()) {
                // Device doesn't support fingerprint authentication
                Log.e("TPI_Login_screen", "device have NO Finger Print ");
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                // User hasn't enrolled any fingerprints to authenticate with
                Log.e("TPI_Login_screen", "Device have Finger Print no access");
            } else {
                // Everything is ready for fingerprint authentication
                Log.e("TPI_Login_screen", "Everything is ready");
            }
        }
    }


    private void hideKeyboard() {
        View aView = (View) findViewById(R.id.activity_register_main_LAY);
        TPIHideSoftKeypad.setupUI(aView, TPI_Login_screen.this);
    }

    private void setClickListener() {

        myLoginPasswordEDT.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        TPIHelper.hideKeyBoard(myContext);
                        callLogin();
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        mySubmitRPV.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                callLogin();
            }
        });


        myRememberMeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (myRememberMeCheckBox.isChecked()) {
                    if (validateLogin()) {
                        myPreferences.putRememberStatus(true);
                        myRememberMeCheckBox.setChecked(true);
                    } else {
                        myPreferences.putRememberStatus(false);
                        myRememberMeCheckBox.setChecked(false);
                    }
                } else {
                    myPreferences.putRememberStatus(false);
                }
            }
        });

    }


    private void callLogin() {
        if (validateLogin()) {
            if (checkInternet()) {
                callLoginPMethod();
            } else {
                showNetworkSnackBarAlert(myRootLayout, NO_INTERNET_MESSAGE);
            }

        }
    }


    private boolean validateLogin() {
        try {
            if (getEditTextValue(myLoginUserEDT).trim().length() == 0) {
                TPIHelper.showMaterialAlertDialog(myContext, getString(R.string.alert_message_empty_user_name));
                return false;
            } else if (getEditTextValue(myLoginPasswordEDT).trim().length() == 0) {
                TPIHelper.showMaterialAlertDialog(myContext, getString(R.string.alert_message_empty_password));
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private String getEditTextValue(EditText aEditText) {
        return aEditText.getText().toString().trim();
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }


    private void exitApp() {
        try {
            if (myBackPressed + 2000 > System.currentTimeMillis()) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                this.finish();
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.app_exit_toast), Toast.LENGTH_SHORT).show();
                myBackPressed = System.currentTimeMillis();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkPermissionGranted();
        myIMEI = TPIHelper.getIMEI(myContext);
        if (!myIMEI.equals("")) {
            appVersionInsert();
        }
    }


    private boolean checkInternet() {
        return TPIHelper.checkInternet(myContext);
    }


    private void callLoginPMethod() {
        try {
            if (checkInternet()) {
                if (validateLogin()) {

                    myReturnValues = new TPIReturnValues();

                    myProgressDialog.setMessage(getString(R.string.label_loading));
                    myProgressDialog.setCancelable(false);
                    myProgressDialog.show();

                    String aInputStr = "User_name=" + getEditTextValue(myLoginUserEDT) +
                            "&" + "Password=" + getEditTextValue(myLoginPasswordEDT);

                    if (aInputStr.contains(" ")) {
                        aInputStr = aInputStr.replaceAll(" ", "%20");
                    }

                    myWebService.getLogin(aInputStr, new TPIWebServiceCallBack() {
                        @Override
                        public void onSuccess(Object object) {
                            myProgressDialog.dismiss();
                            myReturnValues = (TPIReturnValues) object;

                            if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {
                                myPreferences.putLastUpdateTime(TPIHelper.getCurrentTime());
                                ServerResponse myServer = myReturnValues.getMyCommonServerResponse();
                                User_info aUSerinfo = myServer.getUser();
                                myPreferences.putUserInfo(aUSerinfo);
                                myPreferences.putCheckBool("1");

                                if (myRememberMeCheckBox.isChecked()) {
                                    myPreferences.putUserName(getEditTextValue(myLoginUserEDT));
                                    myPreferences.putUserPassword(getEditTextValue(myLoginPasswordEDT));

                                    myPreferences.putEncryptUserName(getEditTextValue(myLoginUserEDT));
                                    myPreferences.putEncryptPassword(getEditTextValue(myLoginPasswordEDT));

                                }
                                callHomeScreen();

                                Log.e("Login", "Success");
                            } else {
                                TPIHelper.showAlertDialog(myReturnValues.getResponseMessage(), myContext);
                            }
                        }

                        @Override
                        public void onFailer(Object object) {
                            myProgressDialog.dismiss();
                            TPIHelper.showAlertDialog(COMMON_RESPONSE_NOT_RECEIVE_MSG, myContext);
                        }
                    });
                }
            } else {
                Toast.makeText(myContext, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void appVersionInsert() {
        if (checkInternet()) {
            try {
                myReturnValues = new TPIReturnValues();

                String aInputStr = "app_id" + "=" + BuildConfig.APP_Id +
                        "&" + "device_type" + "=" + BuildConfig.Device_Type +
                        "&" + "device_token" + "=" + "-" +
                        "&" + "device_IMEI" + "=" + myIMEI +
                        "&" + "version_code" + "=" + BuildConfig.VERSION_NAME;

                if (aInputStr.contains(" ")) {
                    aInputStr = aInputStr.replaceAll(" ", "%20");
                }

                myWebService.insertUpdateVersionUpdate(aInputStr, new TPIWebServiceCallBack() {

                    @Override
                    public void onSuccess(Object object) {
                        myReturnValues = (TPIReturnValues) object;

                        if (myReturnValues.getResponseCode().equals(RESPONSE_CODE_SUCCESS)) {

                        } else {

                        }

                    }

                    @Override
                    public void onFailer(Object object) {

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(myContext, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void callHomeScreen() {
        myPreferences.putLoginStatus(true);
        Intent aIntent = new Intent(myContext, TPI_HomeActivity.class);
        startActivity(aIntent);
        this.finish();
    }


    private void askPermission() {
        try {
            askCompactPermissions(new String[]{
                    PermissionUtils.Manifest_READ_PHONE_STATE,
                    PermissionUtils.Manifest_READ_EXTERNAL_STORAGE,
                    PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE
            }, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check for permission in onResume
     */
    public boolean checkPermissionGranted() {
        boolean isGranted = false;
        try {
            isGranted = isPermissionsGranted(myContext, new String[]{
                    PermissionUtils.Manifest_READ_PHONE_STATE,
                    PermissionUtils.Manifest_READ_EXTERNAL_STORAGE,
                    PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (isGranted)
                TPIHelper.showPermissionErrorLayout(myContext,
                        myContext.getResources().getString(R.string.message_permission_denied),
                        R.drawable.ic_permission, false);
        }
        return isGranted;
    }

    @Override
    public void permissionGranted() {

    }

    @Override
    public void permissionDenied() {
        try {
            TPIHelper.showDenyPermissionAlert(myContext,
                    myContext.getResources().getString(R.string.message_permission_denied),
                    new TPIDenyAlert() {
                        @Override
                        public void onClick() {
                            myButtonSelection = false;
                            askPermission();
                        }
                    });
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void permissionForeverDenied() {
        try {
            TPIHelper.showPermissionErrorLayout(myContext,
                    myContext.getResources().getString(R.string.message_permission_denied),
                    R.drawable.ic_permission, true);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the material snackbar alert message
     *
     * @param aCoordinatorLAY View
     * @param aMessage        String
     */
    public void showNetworkSnackBarAlert(View aCoordinatorLAY, String aMessage) {

        try {
            mySnackBar = Snackbar.make(aCoordinatorLAY, aMessage, Snackbar.LENGTH_INDEFINITE).setAction(RETRY, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        callLogin();
                        // Refresh Home Fragment data onResume
                        onResume();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mySnackBar.setActionTextColor(ContextCompat.getColor(myContext, R.color.snack_bar_retry_color));
            View aSnackBarView = mySnackBar.getView();
            aSnackBarView.setBackgroundColor(ContextCompat.getColor(myContext, R.color.colorPrimary));
            android.widget.TextView textView = (android.widget.TextView) aSnackBarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            mySnackBar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAuthenticate(String decryptPassword) {

    }
}
