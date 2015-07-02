package com.whcs_ucf.whcs_android;

import android.util.Log;

/**
 * Created by Jimmy on 7/2/2015.
 */
public class WHCSActivityWithCleanup extends WHCSActivity{

    @Override protected void onDestroy() {
        super.onDestroy();
        if(issuerAndListenerInitialized) {
            saveBaseStationDeviceForStop();
            Log.d("WHCS-UCF", "Destroying issuer and listener.");
            destroyIssuerAndListener();
        }
    }
}
