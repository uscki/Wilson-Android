package nl.uscki.appcki.android.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import retrofit2.Response;

/**
 * Created by peter on 3/21/17.
 */

public class FCMInstanceIdService extends FirebaseInstanceIdService {
    private final String TAG = getClass().getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
        Services.getInstance().userService.registerDeviceId(refreshedToken).enqueue(new Callback<Boolean>() {
            @Override
            public void onSucces(Response<Boolean> response) {
                // what do?
            }
        });

        super.onTokenRefresh();
    }
}
