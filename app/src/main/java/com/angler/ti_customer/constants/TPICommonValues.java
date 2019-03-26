package com.angler.ti_customer.constants;

/**
 * Created by mahalingam on 28-10-2017.
 * TIDCCommonValues
 */

public interface TPICommonValues {


    String myAPKVersion = "1";


    //String VERSION_UPDATE_MAIN_URL = "http://115.112.233.242:600/Catalogapp_Webservice/api/track?";

    //String VERSION_UPDATE_MAIN_URL = "http://115.112.233.242:600/tpi_customer/api/TPI_Customer/App_Version?";

    // http://115.112.233.242:600/tpi_customer/api/TPI_Customer/
    // http://115.112.233.242:600/tpi_customer/api/TPI_Customer/App_Version?P_APP_ID=1&P_DEVICE_TYPE=A&P_VERSION_CODE=1

    String DEVICE_TYPE = "A";

    String DEVICE_APP_ID = "1";

    String DATABASE_NAME = "tpi_db";

    // int DB_RAW_RESOURCES_ID = R.raw.tidc_db;

    int DB_RAW_RESOURCES_ID = 10;

    int TIMEOUT = 120000;


    int RESULT_IMAGE_OK = -1;

    int RESULT_CANCELED = 0;

    int GALLERY_SELECT_IMAGE_REQUEST_CODE = 100;

    int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 200;


    String myDayDateMonthFormat = "EEE dd MMM";
    String myDateMonthFormat = "dd MMM";
    String myDayFormat = "EEEE";
    String myServerFormat = "dd-MMM-yyyy";
    String mydateFormat = "dd-MM-yyyy";
    String myTimeFormat = "HH";
    String myDateTimeFormat = "mm/dd/yyyy hh:mm:ss a";


    String JAN_26 = "26 Jan";
    String MAY_1 = "01 May";
    String AUG_15 = "15 Aug";
    String OCT_2 = "02 Oct";
    String SUNDAY = "Sunday";

    int TIME2PM = 14;

    String GSON_ARRAYLIST = "GSON_ARRAYLIST";

    String NO_INTERNET_MESSAGE = "No Internet Connection";
    String RETRY = "RETRY";


    String USER_INFO_TABLE = "User_info";
    String USER_CONTET_TABLE = "user_content";
    String LANGUAGE_LIST_TABLE = "language_list";

    //Store detail MECHANIC values
    String LUBRICANT_TABLE = "lubricant_list";
    String MOST_SERVICE_VEHICLE_LIST = "most_service_vehicle_list";
    String NO_OF_VEHICLE_SERVICE_LIST = "no_of_vehicle_service_list";
    String MECHANIC_CUSTOMER_VOICE_LIST = "mechanic_customer_voice_list";

    //RETAILER
    String CHAINS_DEALT_LIST = "chain_dealt_list";
    String MOST_SOLD_VEHICLE_LIST = "most_sold_vehicle_list";
    String RETAILER_OTHER_PRODUCT = "retailer_other_product_list";
    String RETAILER_CUSTOMER_VOICE_LIST = "retail_customer_voice_list";

    //Dealer
    String ESTABLISH_DETAIL = "establish_detail";
    String ESTABLISH_ADDRESS_DETAIL = "establish_address_detail";
    String DEALER_OTHER_PRODUCT = "dealer_other_product_list";
    String DEALER_AREA_SERVICED = "dealer_area_serviced_list";
    String RETAILER_ATTACHED_LIST = "dealer_retailer_attach_list";
    String AVG_KIT_SALES_LIST = "dealer_avg_kit_sales_list";

    String GIF_NO_INTERNET = "drawable-hdpi/gif_no_internet.gif";
    String GIF_NOT_REACHABLE = "drawable-hdpi/gif_not_reachable.gif";
    String NO_INTERNET = "No internet connection found. Check your connection or try again.";


}
