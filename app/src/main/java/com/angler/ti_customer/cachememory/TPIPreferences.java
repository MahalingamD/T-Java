package com.angler.ti_customer.cachememory;

import android.content.Context;
import android.content.SharedPreferences;

import com.angler.ti_customer.commonmodel.CustomerSiteList;
import com.angler.ti_customer.commonmodel.Dashboard_info;
import com.angler.ti_customer.commonmodel.User_info;
import com.angler.ti_customer.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


public class TPIPreferences {

   private static final String myPREF_NAME = "TIDC";
   private static String LOGIN_STATUS = "LoginStatus";
   private static String USER_DETAILS = "User_info";
   private static String DASH_BOARD = "DashBoard";
   private static String CUSTOMER_INFO = "Customer_info";
   private static String Sales_DETAILS = "Sales_info";
   private static String LANG = "lang";
   private static String LANGUAGE = "select_lang";
   private static String LOGIN_DETAILS = "save_login_details";

   private static String UPDATE_APK_PATH = "Apk_path";
   private static String VERSION_CODE = "version";

   private SharedPreferences mySharedPreference;
   private SharedPreferences.Editor mySharedEditor;


   public TPIPreferences( Context aContext ) {
      mySharedPreference = aContext.getApplicationContext().getSharedPreferences( myPREF_NAME, Context.MODE_PRIVATE );
      mySharedEditor = mySharedPreference.edit();
   }

   public void clear() {
      mySharedEditor.clear();
      mySharedEditor.commit();
   }

   /**
    * @param aStatus Boolean
    */
   public void putLoginStatus( Boolean aStatus ) {
      mySharedEditor.putBoolean( LOGIN_STATUS, aStatus );
      mySharedEditor.commit();
   }

   /**
    * @return Boolean
    */
   public Boolean getLoginStatus() {
      return mySharedPreference.getBoolean( LOGIN_STATUS, false );
   }


   /**
    * Put User login details
    *
    * @param aUserInfo UserInfo
    */
   public void putUserInfo( User_info aUserInfo ) {

      String aUserDetailJson = null;
      Gson aGson = new Gson();
      aUserDetailJson = aGson.toJson( aUserInfo );
      mySharedEditor.putString( USER_DETAILS, aUserDetailJson );
      mySharedEditor.commit();

   }


   /**
    * Get the User login details
    *
    * @return UserInfo
    */
   public User_info getUserInfo() {

      User_info aUserInfo = null;
      try {
         aUserInfo = new User_info();
         String aUserInfoJSON = mySharedPreference.getString( USER_DETAILS, null );

         if( aUserInfoJSON != null ) {
            Gson aGson = new Gson();
            aUserInfo = aGson.fromJson( aUserInfoJSON, User_info.class );
         }
      } catch( JsonSyntaxException e ) {
         e.printStackTrace();
      }
      return aUserInfo;
   }


   public void putDashboardInfo( Dashboard_info aUserInfo ) {

      String aUserDetailJson = null;
      Gson aGson = new Gson();
      aUserDetailJson = aGson.toJson( aUserInfo );
      mySharedEditor.putString( DASH_BOARD, aUserDetailJson );
      mySharedEditor.commit();

   }

   public Dashboard_info getDashboardInfo() {

      Dashboard_info aDashboardInfo = null;
      try {
         aDashboardInfo = new Dashboard_info();
         String aDashBoardInfoJSON = mySharedPreference.getString( DASH_BOARD, null );

         if( aDashBoardInfoJSON != null ) {
            Gson aGson = new Gson();
            aDashboardInfo = aGson.fromJson( aDashBoardInfoJSON, Dashboard_info.class );
         }
      } catch( JsonSyntaxException e ) {
         e.printStackTrace();
      }
      return aDashboardInfo;
   }


   public void putCustomerInfo( CustomerSiteList aUserInfo ) {

      String aUserDetailJson = null;
      Gson aGson = new Gson();
      aUserDetailJson = aGson.toJson( aUserInfo );
      mySharedEditor.putString( CUSTOMER_INFO, aUserDetailJson );
      mySharedEditor.commit();

   }

   public CustomerSiteList getCustomerInfo() {

      CustomerSiteList aCustomerInfo = null;
      try {
         aCustomerInfo = new CustomerSiteList();
         String aDashBoardInfoJSON = mySharedPreference.getString( CUSTOMER_INFO, null );

         if( aDashBoardInfoJSON != null ) {
            Gson aGson = new Gson();
            aCustomerInfo = aGson.fromJson( aDashBoardInfoJSON, CustomerSiteList.class );
         }
      } catch( JsonSyntaxException e ) {
         e.printStackTrace();
      }
      return aCustomerInfo;
   }


   /**
    * Save the login auth key for the ic_user to remember
    */
   public void putAuthKey( String aValue ) {
      mySharedEditor.putString( "Authenticate_User_Key", aValue );
      mySharedEditor.commit();

   }

   public String getAuthKey() {
      String aValue = "";
      return mySharedPreference.getString( "Authenticate_User_Key", aValue );

   }

   public void removeUserName() {
      mySharedEditor.remove( "LoginUserName" );
      mySharedEditor.commit();
   }

   public void removePassword() {
      mySharedEditor.remove( "LoginPassword" );
      mySharedEditor.commit();
   }

   /**
    * Save the login username/email
    */
   public void putUserName( String aValue ) {
      mySharedEditor.putString( "LoginUserName", aValue );
      mySharedEditor.commit();

   }

   public String getUserName() {
      String aValue = "";
      return mySharedPreference.getString( "LoginUserName", aValue );

   }

   public void putEncryptUserName( String userName ) {
      String encoded = Utils.encryptString( userName );
      mySharedEditor.putString( "EncryptUserName", encoded );
      mySharedEditor.commit();
   }

   public String getEncryptUserName() {
      String aValue = "";
      return mySharedPreference.getString( "EncryptUserName", aValue );

   }

   public void putUserPassword( String aValue ) {
      mySharedEditor.putString( "LoginPassword", aValue );
      mySharedEditor.commit();

   }

   public String getUserPassword() {
      String aValue = "";
      return mySharedPreference.getString( "LoginPassword", aValue );

   }

   public void putEncryptPassword( String password ) {
      String encoded = Utils.encryptString( password );
      mySharedEditor.putString( "EncryptPassword", encoded );
      mySharedEditor.commit();
   }

   public String getEncryptPassword() {
      String aValue = "";
      return mySharedPreference.getString( "EncryptPassword", aValue );
   }


   /**
    * Put Downloaded Apk file path
    *
    * @param aFilePath string
    */
   public void putAPKDwonloadPath( String aFilePath ) {
      mySharedEditor.putString( UPDATE_APK_PATH, aFilePath );
      mySharedEditor.commit();
   }


   /**
    * Get Downloaded Apk file path
    *
    * @return String
    */
   public String getAPKDwonloadPath() {
      return mySharedPreference.getString( UPDATE_APK_PATH, "" );
   }


   public void putDefaultLocale( String aLocale ) {
      mySharedEditor.putString( LANG, aLocale );
      mySharedEditor.commit();
   }


   public String getDefaultLocale() {
      return mySharedPreference.getString( LANG, "" );
   }


   public void putDefaultLanguage( String aLocale ) {
      mySharedEditor.putString( LANGUAGE, aLocale );
      mySharedEditor.commit();
   }


   public String getDefaultLanguage() {
      return mySharedPreference.getString( LANGUAGE, "" );
   }


   public void putLastUpdateTime( String aDateTime ) {

      mySharedEditor.putString( "LastUpdateTime", aDateTime );
      mySharedEditor.commit();
   }

   public String gettLastUpdateTime() {
      return mySharedPreference.getString( "LastUpdateTime", "" );
   }

   public void putRememberStatus( boolean aStatus ) {
      mySharedEditor.putBoolean( "Remember", aStatus );
      mySharedEditor.commit();
   }

   public boolean getRememberStatus() {
      return mySharedPreference.getBoolean( "Remember", false );
   }

   public void putCheckBool( String aStr ) {
      mySharedEditor.putString( "Check", aStr );
      mySharedEditor.commit();
   }

   public String getCheckBool() {
      return mySharedPreference.getString( "Check", "" );
   }

   public void putVersionDetails( String aCategoryItemSTR ) {

      // ---Put string---
      mySharedEditor.putString( "Version", aCategoryItemSTR );
      mySharedEditor.commit();
   }

   public String getVersionDetails() {

      // ---Initializing String---
      String aCategoryItemSTR = "";

      // ---Assign value---
      aCategoryItemSTR = mySharedPreference.getString( "Version", "" );

      // ---return value---
      return aCategoryItemSTR;

   }


   public String getSelectedDate() {
      return mySharedPreference.getString( "SelectedDate", "" );
   }

   public void putSelectedDate( String aDate ) {
      mySharedEditor.putString( "SelectedDate", aDate );
      mySharedEditor.commit();
   }

   public void setLastDayMonth( String aDate ) {
      mySharedEditor.putString( "SetLastDate", aDate );
      mySharedEditor.commit();
   }

   public String getLastDayMonth() {
      return mySharedPreference.getString( "SetLastDate", "" );

   }
}
