package com.angler.ti_customer.utils;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.angler.ti_customer.activities.TPI_Splash_Screen;
import com.angler.ti_customer.cachememory.TPIPreferences;
import com.angler.ti_customer.listener.IAuthenticateListener;

import javax.crypto.Cipher;


@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context myContext;
    private CancellationSignal myCancellationSignal;
    private TPIPreferences mySharedPreferences;
    private IAuthenticateListener myListener;


    public FingerprintHandler(Context context, TPIPreferences myPreference, IAuthenticateListener listener) {
        myContext = context;
        mySharedPreferences = myPreference;
        myListener = listener;
        myCancellationSignal = new CancellationSignal();
    }


    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        fingerprintManager.authenticate(cryptoObject, myCancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        if (errorCode != 5)
            Toast.makeText(myContext, errString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        Toast.makeText(myContext, helpString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

        Cipher cipher = result.getCryptoObject().getCipher();
        // String encoded = mySharedPreferences.getString(LoginActivity.KEY_PASSWORD, null);
        String encoded = mySharedPreferences.getEncryptPassword();
        String decoded = Utils.decryptString(encoded, cipher);
        myListener.onAuthenticate(decoded);
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(myContext, "onAuthenticationFailed", Toast.LENGTH_SHORT).show();
        myListener.onAuthenticate("0");
    }

    public void cancel() {
        if (myCancellationSignal != null) myCancellationSignal.cancel();
    }
}
