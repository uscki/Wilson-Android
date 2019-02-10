package nl.uscki.appcki.android.services;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.io.IOException;

import nl.uscki.appcki.android.BuildConfig;
import nl.uscki.appcki.android.NotificationUtil;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.helpers.PermissionHelper;
import nl.uscki.appcki.android.helpers.notification.AbstractNotification;
import retrofit2.Response;

/**
 * Created by peter on 3/21/17.
 */

public class NotificationReceiver extends FirebaseMessagingService {
    private final String TAG = getClass().getSimpleName();

    // Groups should be appended with the ID of the item in that group
    static final String GROUP_KEY_AGENDA = "nl.uscki.appcki.android.groupkey.AGENDA.";
    static final String GROUP_KEY_MEETING = "nl.uscki.appcki.android.groupkey.MEETING.";
    static final String GROUP_KEY_FORUM = "nl.uscki.appcki.android.groupkey.FORUM.";

    Context context;

    public NotificationReceiver() {
        // Zero argument constructor required by the system to create services the way they are intended
        this.context = this;
    }

    public NotificationReceiver(Context context) {
        this.context = context;
    }

    @Override
    public void onNewToken(String refreshedToken) {
        if(BuildConfig.DEBUG) {
            Log.d(TAG, "Refreshed firebase Token: " + refreshedToken);
        }

        if(PermissionHelper.hasAgreedToNotificationPolicy(this)) {
            // If you want to send messages to this application instance or
            // manage this apps subscriptions on the server side, send the
            // Instance ID token to your app server.
            //sendRegistrationToServer(refreshedToken);
            Services.getInstance().userService.registerDeviceId(refreshedToken)
                    .enqueue(new Callback<ActionResponse<Boolean>>() {
                @Override
                public void onSucces(Response<ActionResponse<Boolean>> response) {
                    if(response.body().payload) {
                        Log.d(NotificationReceiver.class.getSimpleName(),
                                "New firebase token succesfully communicated with server");
                    } else {
                        Log.e(NotificationReceiver.class.getSimpleName(),
                                "New firebase token rejected by server");
                    }
                }
            });
        } else {
            // A horrible feature of firebase was just activated. Unless you know about this, you
            // won't know to avoid it. Incredibly extensive error message to alert developers and
            // catch their attention
            Log.e(getClass().getSimpleName(),
                    "\n===================================================================================\n" +
                    "===================================================================================\n" +
                    "= 																				  =\n" +
                    "= 							!!!	DANGER !!!!										  =\n" +
                    "= 																				  =\n" +
                    "===================================================================================\n" +
                    "===================================================================================\n" +
                    "DANGER! User did not consent with sharing an FCM token with firebase, but a token\n" +
                    "refresh was still registered.\n" +
                    "Make sure nowhere in the code an instance of Firebase, FirebaseMessaging or\n" +
                    "FirebaseInstanceID is requested, unless the user has SPECIFICALLY agreed to sharing\n" +
                    "this token with Firebase. Requesting any of these instances automatically initiates\n" +
                    "Firebase, and triggers the sharing of such a token.\n" +
                    "===================================================================================");
        }
    }

    /**
     * Log the current firebase token
     */
    public static void logToken(Context context) {
        if(BuildConfig.DEBUG && PermissionHelper.hasAgreedToNotificationPolicy(context)) {

            // WARNING! Requesting a firebase, firebase instance ID or firebase messaging instance
            // automatically generates a firebase messaging token and communicates
            // this token with the Firebase servers. If user has not granted permission to do so,
            // this should not be done. DO NOT REMOVE THIS IF STATEMENT

            Task<InstanceIdResult> instanceIdResultTask = FirebaseInstanceId.getInstance().getInstanceId();
            instanceIdResultTask.addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String token = instanceIdResult.getToken();
                    Log.d(NotificationReceiver.class.getSimpleName(), "Firebase Token: " + token);
                }
            });
        } else {
            Log.d(NotificationReceiver.class.getSimpleName(),
                    "User has not consented with sharing an identifier in favour of receiving " +
                            "notifications. No firebase token exists");
        }
    }

    /**
     * Invalidate the current Firebase token, so a new one is generated
     *
     * @param allowNew  If true, firebase messaging is not prevented from generating a new token
     */
    public static void invalidateFirebaseInstanceId(boolean allowNew) {
        Log.e(NotificationReceiver.class.getSimpleName(), "Invalidating firebase instance ID");
        NotificationUtil.setFirebaseEnabled(allowNew);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                    Log.d(NotificationReceiver.class.getSimpleName(), "Firebase instance ID invalidated");
                } catch (IOException e) {
                    Log.d(NotificationReceiver.class.getSimpleName(), e.getMessage());
                }
            }
        }).start();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom() + "\n");

        AbstractNotification notification = AbstractNotification.fromFirebaseMessage(this, remoteMessage);
        notification.show();
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
