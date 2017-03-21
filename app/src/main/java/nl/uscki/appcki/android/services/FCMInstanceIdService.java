package nl.uscki.appcki.android.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

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
        //TODO add this in the api, this token needs to be registered in the database for the server
        // to be able to send messages to this app instance

        super.onTokenRefresh();
    }
}
