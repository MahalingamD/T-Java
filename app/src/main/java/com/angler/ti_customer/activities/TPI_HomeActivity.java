package com.angler.ti_customer.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.angler.ti_customer.R;
import com.angler.ti_customer.cachememory.TPIPreferences;
import com.angler.ti_customer.fragment.TPI_Add_To_CartFragment;
import com.angler.ti_customer.fragment.TPI_DashboardFragment;
import com.angler.ti_customer.fragment.TPI_OrderPageFreagment;
import com.angler.ti_customer.fragmentmanager.TPIFragmentManager;

import butterknife.BindView;

public class TPI_HomeActivity extends AppCompatActivity {

    private static long myBackPressed;
    private AppCompatActivity myContext;
    TPIPreferences myPreferences;
    TPIFragmentManager myFragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tpi_home);

        initializeClassAndWidgets();
    }

    private void initializeClassAndWidgets() {
        myContext = this;
        myPreferences = new TPIPreferences(myContext);
        myFragmentManager = new TPIFragmentManager(myContext);


        defaultScreen();

    }

    private void defaultScreen() {
        try {
            myFragmentManager.updateContent(new TPI_DashboardFragment(), TPI_DashboardFragment.TAG, null);
            //  myFragmentManager.updateContent(new TPI_Add_To_CartFragment(), TPI_Add_To_CartFragment.TAG, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (myFragmentManager.getBackstackCount() > 1) {
                myFragmentManager.backpress();

            } else {
                if (getCurrentFragment() instanceof TPI_OrderPageFreagment) {
                    myFragmentManager.clearAllFragments();
                    myFragmentManager.updateContent(new TPI_DashboardFragment(), TPI_DashboardFragment.TAG, null);
                } else {
                    exitApp();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Method to exit the App
     */
    private void exitApp() {
        try {
            if (myBackPressed + 2000 > System.currentTimeMillis()) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.app_exit_toast), Toast.LENGTH_SHORT).show();
                myBackPressed = System.currentTimeMillis();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the current fragment from Backstack
     *
     * @return Fragment
     */
    private Fragment getCurrentFragment() {
        FragmentManager aFragmentManager = myContext.getSupportFragmentManager();
        String aFragmentTag = aFragmentManager.getBackStackEntryAt(aFragmentManager.getBackStackEntryCount() - 1).getName();
        Fragment aCurrentFragment = myContext.getSupportFragmentManager().findFragmentByTag(aFragmentTag);
        return aCurrentFragment;
    }
}
