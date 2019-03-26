package com.angler.ti_customer.utils;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class TPIHideSoftKeypad {

  /**
   * Hide Soft Keyboard onTouchEvent
   */
  public static void setupKeyboardDismiss(View view, final FragmentActivity aContext ) {

    // Set up touch listener for non-text box views to hide keyboard.
    if( !( view instanceof EditText) ) {

      view.setOnTouchListener( new OnTouchListener() {

        public boolean onTouch(View v, MotionEvent event ) {
          if( isTouchInsideView( event, v ) ) {
            hideSoftKeyboard( aContext );
          }
          return false;
        }

      } );
    }

    // If retailer_radio_select layout container, iterate over children and seed recursion.
    if( view instanceof ViewGroup) {

      for(int i = 0; i < ( (ViewGroup)view ).getChildCount(); i++ ) {

        View innerView = ( (ViewGroup)view ).getChildAt( i );

        setupKeyboardDismiss( innerView, aContext );
      }
    }
  }

  public static void hideSoftKeyboard( FragmentActivity aContext ) {
    InputMethodManager inputMethodManager = (InputMethodManager)aContext
            .getSystemService( FragmentActivity.INPUT_METHOD_SERVICE );

    View aView = aContext.getCurrentFocus();
    if( aView == null )
      return;
    if( aView.getWindowToken() != null ) {
      inputMethodManager.hideSoftInputFromWindow( aContext
              .getCurrentFocus().getWindowToken(), 0 );
    }
  }

  /**
   * Disable soft keys when touch out from EDIT TEXT
   */
  public static boolean isTouchInsideView(MotionEvent ev, View currentFocus ) {
    final int[] loc = new int[ 2 ];
    currentFocus.getLocationOnScreen( loc );
    return ev.getRawX() > loc[ 0 ] && ev.getRawY() > loc[ 1 ]
            && ev.getRawX() < ( loc[ 0 ] + currentFocus.getWidth() )
            && ev.getRawY() < ( loc[ 1 ] + currentFocus.getHeight() );
  }


  public static void setupUI(View aView, final Activity aActivity ) {

    // Set up touch listener for non-text box views to hide keyboard
    if( !( aView instanceof EditText) ) {

      aView.setOnTouchListener( new OnTouchListener() {

        public boolean onTouch(View v, MotionEvent event ) {

          // Hide soft keyboard
          hideSoftKeyboard( aActivity );
          return false;
        }

      } );
    }

    // If retailer_radio_select layout container, iterate over children and seed recursion
    if( aView instanceof ViewGroup) {

      for(int aCount = 0; aCount < ( (ViewGroup)aView ).getChildCount(); aCount++ ) {

        View aInnerView = ( (ViewGroup)aView ).getChildAt( aCount );

        setupUI( aInnerView, aActivity );
      }
    }
  }


  /**
   * Function to hide soft key board
   *
   * @param activity Current Activity
   */
  public static void hideSoftKeyboard( Activity activity ) {

    try {
      if( activity.getCurrentFocus() != null ) {
        InputMethodManager inputMethodManager = (InputMethodManager)activity
                .getSystemService( Activity.INPUT_METHOD_SERVICE );
        inputMethodManager.hideSoftInputFromWindow( activity
                .getCurrentFocus().getWindowToken(), 0 );
      }
    } catch( Exception e ) {
      e.printStackTrace();
    }
  }


  //Requestfoucs
  public static void requestFocus(final FragmentActivity aContext ) {
    aContext.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE );
  }


}
