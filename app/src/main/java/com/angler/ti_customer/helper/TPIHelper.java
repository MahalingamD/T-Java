package com.angler.ti_customer.helper;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.angler.ti_customer.BuildConfig;
import com.angler.ti_customer.R;
import com.angler.ti_customer.constants.TPICommonValues;
import com.angler.ti_customer.listener.TPIDenyAlert;
import com.angler.ti_customer.utils.TIDCNetworkManager;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.angler.ti_customer.utils.NoInternetConnection.TAG;


/**
 * Created by mahalingam on 26-10-2017.
 * TIDCHelper
 */


public class TPIHelper implements TPICommonValues {


    public static boolean checkInternet(Context aContext) {
        return TIDCNetworkManager.isInternetOnCheck(aContext);
    }


    public static void showPermissionErrorLayout(final AppCompatActivity aContext, String aPermissionMsg, int aImageId,
                                                 boolean aStatus) {
        try {
            if (aContext == null)
                return;

            LinearLayout aNoPermissionLay = (LinearLayout) aContext
                    .findViewById(R.id.activity_common_LAY_inflate_no_permission);

            if (aStatus) {
                aNoPermissionLay.setVisibility(View.VISIBLE);
            } else {
                aNoPermissionLay.setVisibility(View.GONE);
                return;
            }

            TextView aContentTXT = (TextView) aNoPermissionLay
                    .findViewById(R.id.layout_inflate_no_internet_TXT_give_access);


            ImageView aNoDataIMG = (ImageView) aNoPermissionLay
                    .findViewById(R.id.layout_inflate_server_no_data_IMG);

            ImageView aPermissionIMG = (ImageView) aNoPermissionLay
                    .findViewById(R.id.layout_inflate_server_no_permission_IMG);

            final Animation animation = new AlphaAnimation(1, 0);
            animation.setDuration(1000);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            aNoDataIMG.startAnimation(animation);

            aContentTXT.setText(aPermissionMsg);

            aPermissionIMG.setImageResource(aImageId);

            TextView aTapToTryTXT = (TextView) aNoPermissionLay
                    .findViewById(R.id.layout_inflate_no_permission_TXT_content);

            aTapToTryTXT.setText(Html.fromHtml(aContext.getResources().getString(R.string.label_tap_to_retry_permission)));

            aTapToTryTXT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        startInstalledAppDetailsActivity(aContext);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            aTapToTryTXT.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * load Image View using Volley
     *
     * @param aContext
     * @param aImageView
     * @param aImageUrlStr
     * @param aDefaultImage
     * @param aErrorImage
     */
    public static void loadImageView(FragmentActivity aContext,
                                     final ImageView aImageView, String aImageUrlStr, int aDefaultImage,
                                     int aErrorImage) {

        try {
            Log.i("IMAGE URL", aImageUrlStr);
            if (aImageUrlStr.length() > 0) {
                Picasso.with(aContext)
                        .load(aImageUrlStr.replace(" ", "%20"))
                        .error(aContext.getResources().getDrawable(aErrorImage))
                        .placeholder(aContext.getResources().getDrawable(aDefaultImage))
                        .into(aImageView);
            } else {
                aImageView.setImageResource(aErrorImage);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadImageView(final FragmentActivity aContext, String aUrl, ImageView aImageView) {
        Glide.with(aContext)
                .load(aUrl).placeholder(R.mipmap.ic_launcher)
                .centerCrop()
                .crossFade()
                .into(aImageView);
        //.placeholder(R.drawable.loading_spinner);
    }


    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }


    public static void showDenyPermissionAlert(final Activity aContext, String aContent, final TPIDenyAlert aTRIDenyAlert) {
        new MaterialDialog.Builder(aContext)
                .content(aContent)
                .title(aContext.getResources().getString(R.string.app_name))
                .backgroundColorRes(R.color.white)
                .positiveColorRes(R.color.dark_oriange)
                .negativeColorRes(R.color.dark_oriange)
                .contentColorRes(R.color.dark_grey)
                .positiveText("Yes")
                .cancelable(true)
                .negativeText("No")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        try {
                            try {
                                aTRIDenyAlert.onClick();
                                dialog.dismiss();
                            } catch (ActivityNotFoundException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        try {
                            try {
                                TPIHelper.showPermissionErrorLayout((AppCompatActivity) aContext,
                                        aContext.getResources().getString(R.string.message_permission_denied),
                                        R.drawable.ic_permission, true);
                            } catch (ActivityNotFoundException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
    }


    /**
     * @param aMessage
     */
    public static void showAlertDialog(final String aMessage, final FragmentActivity aContext) {
        try {


            AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
            builder.setMessage(aMessage)
                    .setTitle(aContext.getString(R.string.app_name))
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (aMessage.equals(aContext.getString(R.string.alert_message_Invalid_crenditals))) {
                                EditText aEditext = (EditText) aContext.findViewById(R.id.activity_login_EDT_password);
                                aEditext.setText("");
                                dialog.dismiss();
                            } else
                                dialog.dismiss();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
            // Change the buttons color in dialog
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(aContext, R.color.black));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean isInternetAvailable() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * Turn drawable resource into byte array.
     *
     * @param context parent context
     * @param id      drawable resource id
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    /**
     * Turn drawable into byte array.
     *
     * @param drawable data
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    /**
     * Show the Dialog with Dismiss  Button
     *
     * @param aContext FragmentActivity
     * @param aMessage String
     */
    public static void showMaterialAlertDialog(FragmentActivity aContext, String aMessage) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(aContext);
            dialog.setCancelable(true);
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
            alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(aContext, R.color.blue_end));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * To check tablet screen or mobile screen
     *
     * @return
     */
    public static String isTabletDevice(FragmentActivity aContext) {
        boolean aTabletSize = false;
        try {
            aTabletSize = aContext.getResources().getBoolean(R.bool.isTablet);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, e.getCause().toString());
            aTabletSize = false;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getCause().toString());
            aTabletSize = false;
        }

        if (aTabletSize)
            return "Tablet";
        else
            return "Mobile";

    }


    public static boolean isValidEmail(String target) {
        if (target.equals("")) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


   /* public static void getDetails(){
        String s = "Debug-infos:";
        s += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
        s += "\n OS API Level: " + android.os.Build.VERSION.SDK_INT;
        s += "\n Device: " + android.os.Build.DEVICE;
        s += "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")";
        s += "\n manufacturer = " + Build.MANUFACTURER;
        s += "\n model = " + Build.MODEL;
        s += "\n Screen height = " + getDeviceHeight();
        s += "\n Screen width = " + getDeviceWidth();
        s += "\n IMEI NO = " + telephonyManager.getDeviceId();
        s += "\n Device type = " + isTabletDevice(myContext);
    }*/


    public static String getCurrentTime() {
        String aTime = "";
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, -3);
            aTime = dateFormat.format(cal.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aTime;
    }

    public static void hideKeyBoard(FragmentActivity aContext) {
        // Check if no view has focus:

        try {
            View view = aContext.getCurrentFocus();

            if (view != null) {

                InputMethodManager imm = (InputMethodManager) aContext.getSystemService(Context.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = null;
        try {
            capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
            while (capMatcher.find()) {
                capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
            }
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
        }
        return capMatcher.appendTail(capBuffer).toString();
    }


    public static void setColorScheme(SwipeRefreshLayout aSwipeRefreshLayout) {
        aSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark,
                R.color.colorAccent);
    }

    public static String dateConvertion(String aDate) {
        String aConvertDateStr = "";

        //  4/2/2018 12:00:00 AM
        SimpleDateFormat format = new SimpleDateFormat("M/d/yyyy HH:mm:ss a", Locale.US);
        SimpleDateFormat dateFormat = new SimpleDateFormat(myServerFormat);
        try {
            Date date = format.parse(aDate);
            String dateTime = dateFormat.format(date);
            System.out.println("Current Date Time : " + dateTime);
            aConvertDateStr = dateTime;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return aConvertDateStr;
    }


    public static String getAppVersion(Context aContext) {
        PackageInfo aPackageInfo = null;

        // Get the App Version Code
        String aAppVersionCode = String.valueOf(BuildConfig.VERSION_CODE);

        try {
            aPackageInfo = aContext.getPackageManager().getPackageInfo(aContext.getPackageName(), 0);
            aAppVersionCode = "" + aPackageInfo.versionCode;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return aAppVersionCode;


    }

    public static String getIMEI(Context activity) {

        String aIdentifier = null;
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null)
            aIdentifier = tm.getDeviceId();
        if (aIdentifier == null || aIdentifier.length() == 0)
            aIdentifier = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);

        return aIdentifier;
    }


    /**
     * Go to App setting Page
     *
     * @param aContext
     */
    public static void goToSettings(FragmentActivity aContext) {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + aContext.getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        aContext.startActivity(myAppSettings);
    }

    public static String getCurrentDate(String dateFormat) {
        Calendar cal = Calendar.getInstance();
        return new SimpleDateFormat(dateFormat, Locale.ENGLISH).format(cal.getTime());
    }

    public static String getCustomDate(String dateFormat, int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, days);
        return new SimpleDateFormat(dateFormat, Locale.ENGLISH).format(cal.getTime());
    }


    public static boolean isValideDates(String from, String to, String format) {
        SimpleDateFormat sdf_date = new SimpleDateFormat(format, Locale.ENGLISH);
        int days = 0;
        try {
            Date fdo = sdf_date.parse(from);
            Date tdo = sdf_date.parse(to);
            long diff = tdo.getTime() - fdo.getTime();
            if (tdo.after(fdo)) {
                days = (int) (diff / (24 * 60 * 60 * 1000));
                return true;
            } else if (tdo.equals(fdo)) {
                days = (int) (diff / (24 * 60 * 60 * 1000));
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void showFromDateDialog(Context context, String SelectedDate, DatePickerDialog.OnDateSetListener from_datePicker) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String date[] = SelectedDate.split("-");

        DatePickerDialog dialog = new DatePickerDialog(context, from_datePicker, year, month, dayOfMonth);
      //  dialog.getDatePicker().setMaxDate(new Date().getTime());

        Calendar calendar1 = Calendar.getInstance(); // this would default to now
        calendar1.add(Calendar.DAY_OF_MONTH, +8);
        //dialog.getDatePicker().setMaxDate(new Date().getTime());
        dialog.getDatePicker().setMaxDate(calendar1.getTime().getTime());

        // Selected Date
        dialog.getDatePicker().init(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]), null);

        dialog.setTitle("");
        dialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "SELECT", dialog);
        dialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "CANCEL", dialog);
        dialog.show();
    }


    public static void showToDateDialog(Context context, String format, String fromDate, String SelectedDate, DatePickerDialog.OnDateSetListener from_datePicker) {
        SimpleDateFormat sdf_date = new SimpleDateFormat(format, Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String Selected[] = SelectedDate.split("-");
        try {
            DatePickerDialog dialog = new DatePickerDialog(context, from_datePicker, year, month, dayOfMonth);

            // Selected Date
            dialog.getDatePicker().init(Integer.parseInt(Selected[2]), Integer.parseInt(Selected[1]) - 1, Integer.parseInt(Selected[0]), null);

            dialog.getDatePicker().setMinDate(sdf_date.parse(fromDate).getTime());

            Calendar calendar1 = Calendar.getInstance(); // this would default to now
            calendar1.add(Calendar.DAY_OF_MONTH, +8);
            //dialog.getDatePicker().setMaxDate(new Date().getTime());
            dialog.getDatePicker().setMaxDate(calendar1.getTime().getTime());

            dialog.setTitle("");
            dialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "SELECT", dialog);
            dialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "CANCEL", dialog);
            dialog.show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public static Date addDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    /**
     * convertIntoDateFormat
     *
     * @param aSelectedDate
     * @param aConversionFormat
     * @param aCurrentFormat
     * @return
     */
    public static String convertIntoDateFormat(String aSelectedDate, String aConversionFormat, String aCurrentFormat) {
        String aDate = "";
        if (!aSelectedDate.equals("")) {
            if (aSelectedDate.length() == 0)
                return "";
            DateFormat curFormater = new SimpleDateFormat(aCurrentFormat);
            Date dateObj = null;
            try {
                dateObj = curFormater.parse(aSelectedDate);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }

            DateFormat postFormater = new SimpleDateFormat(aConversionFormat);
            try {
                aDate = postFormater.format(dateObj);
            } catch (android.net.ParseException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
        }
        return aDate;
    }

    public static boolean isAfter2pm() {
        SimpleDateFormat aTime = new SimpleDateFormat(myTimeFormat, Locale.ENGLISH);
        int time = Integer.parseInt(aTime.format(Calendar.getInstance().getTime()));
        return time >= TIME2PM;
    }


    public static boolean isValidDateForOrder(String selectedDate) {
        String pastDate = getCustomDate(myServerFormat, +1);
        return selectedDate.equals(pastDate);
    }
}
