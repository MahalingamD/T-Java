package com.angler.ti_customer.fragmentmanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.angler.ti_customer.R;


/**
 * Created by kiran on 19-11-2015.
 * TPIFragmentManager
 */
public class TPIFragmentManager {


    private FragmentActivity myContext;

    /**
     * Last fragment tag
     */
    public static String myLastTag = "";

    /**
     * Constructor to Initiate fragment manager
     *
     * @param aContext
     */
    public TPIFragmentManager(FragmentActivity aContext) {
        myContext = aContext;
    }

    /**
     * Update the Current Fragment by passing the below parameters
     *
     * @param aFragment
     * @param tag
     * @param aBundle
     */
    public void updateContent(Fragment aFragment, String tag, Bundle aBundle) {
        try {

            Log.e("TAG Screen name", tag);

            // Initialise Fragment Manager
            final FragmentManager aFragmentManager = myContext.getSupportFragmentManager();
            // Initialise Fragment Transaction
            final FragmentTransaction aTransaction = aFragmentManager.beginTransaction();
            if (aBundle != null) {
                aFragment.setArguments(aBundle);
            }
           // aTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            // Add the selected fragment
            aTransaction.add(R.id.activity_main_frame_layout, aFragment, tag);
            // add the tag to the backstack
            aTransaction.addToBackStack(tag);
            // Commit the Fragment transaction
            aTransaction.commit();
            myLastTag = tag;
            Log.i("LastTag", myLastTag);

        } catch (StackOverflowError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateAnimateContent(Fragment aFragment, String tag, Bundle aBundle) {
        try {

            Log.e("TAG Screen name", tag);

            // Initialise Fragment Manager
            final FragmentManager aFragmentManager = myContext.getSupportFragmentManager();
            // Initialise Fragment Transaction
            final FragmentTransaction aTransaction = aFragmentManager.beginTransaction();
            if (aBundle != null) {
                aFragment.setArguments(aBundle);
            }
            // aTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
            //TranslateAnimation anim = new TranslateAnimation(0, 0, 0, 500);

            // aTransaction.setTransition(R.anim.slide_up);
            // Add the selected fragment
            aTransaction.add(R.id.activity_main_frame_layout, aFragment, tag);
            // add the tag to the backstack
            aTransaction.addToBackStack(tag);
            // Commit the Fragment transaction
            aTransaction.commit();
            myLastTag = tag;
            Log.i("LastTag", myLastTag);

        } catch (StackOverflowError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Get the last fragment tag
     *
     * @return
     */
    public String getlastfragmentTag() {

        Log.v("TAG recieved", myLastTag);

        return myLastTag;
    }


    /**
     * Clear All Fragments
     */
    public void clearAllFragments() {

        try {
            FragmentManager aFragmentManager = myContext
                    .getSupportFragmentManager();

            aFragmentManager.popBackStack(null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (StackOverflowError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void removeFragment(int aCount) {

        FragmentManager aFragmentManager = myContext
                .getSupportFragmentManager();

        for (int i = 0; i < aCount; i++) {

            aFragmentManager.popBackStack();
        }

    }


    public void backpress() {

        try {
            FragmentManager aFragmentManager = myContext
                    .getSupportFragmentManager();

            if (aFragmentManager.getBackStackEntryCount() >= 1) {
                aFragmentManager.popBackStack();
                aFragmentManager.executePendingTransactions();

                Log.d("TAG",
                        "CURRENT FRAGMENT BACK STACK CLASS "
                                + aFragmentManager
                                .getBackStackEntryAt(
                                        aFragmentManager
                                                .getBackStackEntryCount() - 1)
                                .getName());


                Fragment aFragment = aFragmentManager.getFragments().get(aFragmentManager.getBackStackEntryCount() - 1);
                aFragment.onResume();

                if (aFragment instanceof TPIFragment) {
                    ((TPIFragment) aFragment).onResumeFragment();
                }
                String aFragmentName = aFragmentManager.getBackStackEntryAt(
                        aFragmentManager.getBackStackEntryCount() - 1).getName();
            }
        } catch (StackOverflowError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getBackstackCount() {

        FragmentManager aFragmentManager = myContext.getSupportFragmentManager();

        return aFragmentManager.getBackStackEntryCount();
    }

    public FragmentManager.OnBackStackChangedListener getListener() {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                FragmentManager manager = myContext.getSupportFragmentManager();
                if (manager != null) {
                    int backStackEntryCount = manager.getBackStackEntryCount();
                    if (backStackEntryCount == 0) {

                    }
                    Fragment fragment = manager.getFragments()
                            .get(backStackEntryCount - 1);
                    fragment.onResume();
                }
            }
        };
        return result;
    }

//Get the Current TAG

    public String getActiveFragmentTAG() {

        if (myContext.getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return null;
        }
        String aCurrentTAG = myContext.getSupportFragmentManager().getBackStackEntryAt(myContext.getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        Log.d("TPIFragmentManager", "getActiveFragmentTAG: "+aCurrentTAG);
        return aCurrentTAG;
    }


}
