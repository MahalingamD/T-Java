package com.angler.ti_customer.fragmentmanager;

import android.support.v4.app.Fragment;

public abstract class TPIFragment extends Fragment {

  public abstract void onBackPressed( );

  public abstract boolean onResumeFragment( );

}
