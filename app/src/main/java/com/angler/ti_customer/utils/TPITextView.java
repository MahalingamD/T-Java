package com.angler.ti_customer.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.angler.ti_customer.R;


/**
 * custom text view class to incorporate custom styles in screens
 */
@SuppressLint("NewApi")
public class TPITextView extends AppCompatTextView {


  public TPITextView(Context context ) {
    super( context );
    init( context, null, 0, 0 );
  }

  public TPITextView(Context context, AttributeSet attrs ) {
    super( context, attrs );
    init( context, attrs, 0, 0 );
  }

  public TPITextView(Context context, AttributeSet attrs, int defStyleAttr ) {
    super( context, attrs, defStyleAttr );
    init( context, attrs, defStyleAttr, 0 );
  }

  public TPITextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes ) {
    super (context);

    init( context, attrs, defStyleAttr, defStyleRes );
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes ) {

    try {
      if( attrs == null )
        return;

      TypedArray aTypedArray = context.obtainStyledAttributes( attrs, R.styleable.TPITextView );
     String aFontName = aTypedArray.getString( R.styleable.TPITextView_fonts);

      if( aFontName == null )
        return;


      if( aFontName.length() > 0 ) {
        Typeface myTypeface = Typeface.createFromAsset( getContext().getAssets(), "fonts/" + aFontName );


        setTypeface( myTypeface );
      }

      aTypedArray.recycle();

    } catch( Exception e ) {
      e.printStackTrace();

    }
  }


}
