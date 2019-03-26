package com.angler.ti_customer.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Toast;


import com.angler.ti_customer.R;
import com.angler.ti_customer.cachememory.TPIPreferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * update
 * Created by gowthamraj on 18-03-2017.
 */

public class APPUpdater extends Activity {

    public static final String TAG = APPUpdater.class.getSimpleName();

    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    public static boolean CALL_HOME = false;

    public AppCompatActivity myContext;
    private TPIPreferences myAppPreferences;
    private UpdateAsyncTask myUpdateAsyncTask;
    private Type myUpdateType;
    private String myAlertContent, myApkServerPath, myAppName;
    private Drawable myAppIcon;
    public AlertDialog.Builder myMandatoryDialog;
    public AlertDialog.Builder myOptionalDialog;
    public AlertDialog myOptionalAlert;
    public AlertDialog myMandatoryAlert;


    public enum Type {
        NO_UPDATE,
        NON_MANDATORY_UPDATE,
        MANDATORY_UPDATE
    }


    public OnLaterButtonListener myLaterListener;


    public void setOnLaterButtonListener(OnLaterButtonListener aLaterListener) {
        myLaterListener = aLaterListener;
    }


    /**
     * Getter and setter for this class
     */
    public Type getMyUpdateType() {
        return myUpdateType;
    }

    public void setMyUpdateType(Type myUpdateType) {
        this.myUpdateType = myUpdateType;
    }

    /**
     * A Constructor
     * class from instantiating.
     *
     * @param aContext AppCompatActivity
     */
    public APPUpdater(AppCompatActivity aContext) {
        try {
            myContext = aContext;
            myUpdateAsyncTask = new UpdateAsyncTask();
            myAppPreferences = new TPIPreferences(myContext);

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Update app Call method from Login function
     * Check this url is valid or not
     *
     * @param aAlertContent  String
     * @param aApkServerPath String
     * @param aUpdateType    Enum
     */
    public boolean updateAPPMethod(AppCompatActivity myAppContext, String aAlertContent, String aApkServerPath, Type aUpdateType, String aAppName, int aAppIcon) {
        try {

            setMyUpdateType(aUpdateType);

            // if (URLUtil.isValidUrl(aApkServerPath)) {
            myAlertContent = aAlertContent;
            myAppName = aAppName;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                myAppIcon = ContextCompat.getDrawable(myAppContext, aAppIcon);
            else
                myAppIcon = myAppContext.getResources().getDrawable(aAppIcon);

            if (aApkServerPath.contains(" "))
                myApkServerPath = aApkServerPath.replaceAll(" ", "%20");
            else
                myApkServerPath = aApkServerPath;


           /* } else {
                myAlertContent = aAlertContent;
                myAppName = aAppName;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    myAppIcon = ContextCompat.getDrawable(myAppContext, aAppIcon);
                else
                    myAppIcon = myAppContext.getResources().getDrawable(aAppIcon);
                Log.e(TAG, "Invalid URL. Please check your URL");
            }*/

            try {
                if (TIDCNetworkManager.isInternetOnCheck(myAppContext))
                    checkAndroidVersion();
                else
                    Toast.makeText(myAppContext, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CALL_HOME;
    }


    /**
     * Give Runtime Permission for Greater than Lollipop version
     */
    private void checkAndroidVersion() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkAndRequestPermissions())
                    checkPermission();
            } else
                callAlertDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check Individual External Storage
     */
    private void checkPermission() {
        try {
            int result = ContextCompat.checkSelfPermission(myContext, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (result == PackageManager.PERMISSION_GRANTED) {
                callAlertDialog();
            } else {
                checkAndroidVersion();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Call Alert Dialog
     */
    private void callAlertDialog() {
        try {
            if (getMyUpdateType().equals(Type.MANDATORY_UPDATE)) {
                showMandatoryUpdateDialog(myAlertContent, myApkServerPath);
            } else if (getMyUpdateType().equals(Type.NON_MANDATORY_UPDATE)) {
                showUpdateDialog(myAlertContent, myApkServerPath);
            } else if (getMyUpdateType().equals(Type.NO_UPDATE))
                CALL_HOME = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Call myUpdateAPP Async Task or install app method call
     * Check file EXISTS or not not
     *
     * @param aApkServerPath String
     */
    private void updateAsyncMethod(String aApkServerPath) {
        try {

            if (myAppPreferences.getAPKDwonloadPath().length() == 0)
                try {
                    myUpdateAsyncTask.execute(aApkServerPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            else {
                try {
                    File file = new File(myAppPreferences.getAPKDwonloadPath());
                    myUpdateAsyncTask.execute(aApkServerPath);
                  /*  if (file.exists()) {
                     //   Toast.makeText(myContext, "FILE EXISTS", Toast.LENGTH_SHORT).show();
                        autoInstallMethod(myAppPreferences.getAPKDwonloadPath());
                    } else {
                        myUpdateAsyncTask.execute(aApkServerPath);
                      //  Toast.makeText(myContext, "FILE NOT EXISTS", Toast.LENGTH_SHORT).show();
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Async Update Apk installation
     */
    private class UpdateAsyncTask extends AsyncTask<String, Integer, String> {

        ProgressDialog progressDialog;

        UpdateAsyncTask() {
            try {
                progressDialog = new ProgressDialog(myContext, R.style.AppCompatAlertDialogStyle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                progressDialog.setMax(100);
                progressDialog.setTitle(myAppName);
                progressDialog.setMessage(myContext.getString(R.string.msg_download_title));
                progressDialog.setIcon(myAppIcon);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... aURL) {

            String aApkFileName = "TPI_CUSTOMER.apk";
            File aOutputFile = null;

            try {
                URL aUrl = new URL(aURL[0]);
                HttpURLConnection aUrlConnection = (HttpURLConnection) aUrl.openConnection();
                aUrlConnection.setRequestMethod("GET");
                // aUrlConnection.setDoOutput(true);
                aUrlConnection.connect();
                int aLenghtOfFile = aUrlConnection.getContentLength();


                String aDownloadPath = Environment.getExternalStorageDirectory() + "/download/";
                File aDownloadFile = new File(aDownloadPath);
                aDownloadFile.mkdirs();
                aOutputFile = new File(aDownloadFile, aApkFileName);
                myAppPreferences.putAPKDwonloadPath(aOutputFile.getAbsolutePath());
                FileOutputStream aFileOutputStream = new FileOutputStream(aOutputFile);

                InputStream aInputStream = aUrlConnection.getInputStream();

                byte[] aBuffer = new byte[1024];
                long aTotal = 0;
                int aLen;
                while ((aLen = aInputStream.read(aBuffer)) != -1) {
                    aTotal += aLen;
                    publishProgress((int) ((aTotal * 100) / aLenghtOfFile));
                    aFileOutputStream.write(aBuffer, 0, aLen);
                }

                aFileOutputStream.flush();
                aFileOutputStream.close();
                aInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                //Toast.makeText(myContext, "Update error!", Toast.LENGTH_LONG).show();
            }
            return aOutputFile.getAbsolutePath();
        }

        /**
         * Updating progress bar
         *
         * @param progress Integer
         */
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            progressDialog.setProgress(progress[0]);
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * Auto Install Code
         *
         * @param outputFileName String
         **/
        @Override
        protected void onPostExecute(String outputFileName) {
            try {
                progressDialog.dismiss();
                autoInstallMethod(outputFileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Auto Install application after download
     * Close application and install new application in intent
     *
     * @param outputFileName string
     */
    private void autoInstallMethod(String outputFileName) {
        try {

            //Close app
            Intent aCloseIntent = new Intent(Intent.ACTION_MAIN);
            aCloseIntent.addCategory(Intent.CATEGORY_HOME);
            aCloseIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            myContext.finish();
            myContext.startActivity(aCloseIntent);

            //Install app
            Intent aUpdateIntent = new Intent(Intent.ACTION_VIEW);
            aUpdateIntent.setDataAndType(Uri.fromFile(new File(outputFileName)), "application/vnd.android.package-archive");
            aUpdateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            myContext.startActivity(aUpdateIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update function showMandatoryUpdateDialog
     *
     * @param aAlertContent  String
     * @param aApkServerPath url
     */
    private void showMandatoryUpdateDialog(String aAlertContent, final String aApkServerPath) {
        String aButtonName;
        try {
            myMandatoryDialog = new AlertDialog.Builder(myContext);
            myMandatoryDialog.setCancelable(true);
            myMandatoryDialog.setTitle(myAppName);
            myMandatoryDialog.setIcon(myAppIcon);
            myMandatoryDialog.setMessage(aAlertContent);
            myMandatoryDialog.setCancelable(false);

            if (myAppPreferences.getAPKDwonloadPath().length() == 0)
                aButtonName = myContext.getString(R.string.msg_app_update);
            else
                aButtonName = myContext.getString(R.string.msg_app_install);

            myMandatoryDialog.setNegativeButton(aButtonName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            myMandatoryAlert = myMandatoryDialog.create();
            if (!myMandatoryAlert.isShowing()) {
                myMandatoryAlert.show();
            } else

                // SET BUTTON_NEGATIVE and BUTTON_POSITIVE Color

                myMandatoryAlert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(myContext, android.R.color.black));


            myMandatoryAlert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int result = ContextCompat.checkSelfPermission(myContext, Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (result == PackageManager.PERMISSION_GRANTED)
                            updateAsyncMethod(aApkServerPath);
                        else
                            checkAndroidVersion();

                        myMandatoryAlert.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (
                Exception e)

        {
            e.printStackTrace();
        }
    }

    /**
     * Update function showUpdateDialog
     *
     * @param aAlertContent  String
     * @param aApkServerPath url
     */
    private void showUpdateDialog(String aAlertContent, final String aApkServerPath) {
        String aButtonName;
        try {
            myOptionalDialog = new AlertDialog.Builder(myContext);
            myOptionalDialog.setCancelable(true);
            myOptionalDialog.setTitle("TPI CUSTOMER APP");
            myOptionalDialog.setIcon(myAppIcon);
            myOptionalDialog.setMessage(aAlertContent);
            myOptionalDialog.setCancelable(false);

            if (myAppPreferences.getAPKDwonloadPath().length() == 0)
                aButtonName = myContext.getString(R.string.msg_app_update);
            else
                aButtonName = myContext.getString(R.string.msg_app_install);


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
            if (!myOptionalAlert.isShowing()) {
                myOptionalAlert.show();
            }

            myOptionalAlert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(myContext, android.R.color.black));
            myOptionalAlert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(myContext, android.R.color.black));

            myOptionalAlert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int result = ContextCompat.checkSelfPermission(myContext, Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (result == PackageManager.PERMISSION_GRANTED)
                            updateAsyncMethod(aApkServerPath);
                        else
                            checkAndroidVersion();

                        myOptionalAlert.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            myOptionalAlert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        CALL_HOME = true;
                        myLaterListener.onLaterEvent(CALL_HOME);

                        myOptionalAlert.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // myContext.startActivity(new Intent(myContext, myContext.getClass()));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Check Request Permission
     */
    private boolean checkAndRequestPermissions() {
        try {
            int aStoragePermission = ContextCompat.checkSelfPermission(myContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE);

            int aPhoneState = ContextCompat.checkSelfPermission(myContext,
                    Manifest.permission.READ_PHONE_STATE);

            List<String> aPermissionsNeededList = new ArrayList<>();
            if (aStoragePermission != PackageManager.PERMISSION_GRANTED) {
                aPermissionsNeededList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (aPhoneState != PackageManager.PERMISSION_GRANTED) {
                aPermissionsNeededList.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (!aPermissionsNeededList.isEmpty()) {
                ActivityCompat.requestPermissions(myContext,
                        aPermissionsNeededList.toArray(new String[aPermissionsNeededList.size()]),
                        PERMISSIONS_MULTIPLE_REQUEST);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                try {
                    if (grantResults.length > 0 && (grantResults[0] + grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    public interface OnLaterButtonListener {
        void onLaterEvent(boolean bool);

    }


    /**
     * Launch the Play store Market
     */
    public void launchMarket(String aApp_Id) {

        try {
            Uri uriData = Uri.parse("market://details?id=" + aApp_Id);
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uriData);
            try {
                myContext.startActivity(goToMarket);
                finish();
            } catch (ActivityNotFoundException e) {
                Toast.makeText(myContext, "Couldn't launch the market",
                        Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
