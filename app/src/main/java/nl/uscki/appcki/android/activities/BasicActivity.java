package nl.uscki.appcki.android.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.google.gson.Gson;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.EventBusException;
import nl.uscki.appcki.android.NotificationUtil;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.ErrorEvent;
import nl.uscki.appcki.android.events.LinkClickedEvent;
import nl.uscki.appcki.android.events.ServerErrorEvent;
import nl.uscki.appcki.android.events.UserLoggedInEvent;
import nl.uscki.appcki.android.fragments.PrivacyPolicyModalFragment;
import nl.uscki.appcki.android.generated.organisation.PersonSimpleName;
import nl.uscki.appcki.android.generated.organisation.PersonWithNote;
import nl.uscki.appcki.android.helpers.PermissionHelper;
import nl.uscki.appcki.android.helpers.UserHelper;
import nl.uscki.appcki.android.services.NotificationReceiver;
import retrofit2.Response;

/**
 * Created by peter on 3/5/17.
 */

public abstract class BasicActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            UserHelper.getInstance().load(savedInstanceState.getString("token"));
        } else {
            UserHelper.getInstance().load();
        }

        // Force enable FCM if user has agreed to terms
        NotificationUtil.setFirebaseEnabled(PermissionHelper.hasAgreedToNotificationPolicy(this));

        NotificationReceiver.logToken(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {

        if(!PermissionHelper.hasAgreedToBasicPolicy(this)) {
            // User needs to agree with privacy policy

            PrivacyPolicyModalFragment privacyPolicyModalFragment = new PrivacyPolicyModalFragment();
            privacyPolicyModalFragment.show(getFragmentManager(), "privacyPolicyDialog");
        }

        if (!UserHelper.getInstance().isLoggedIn() || UserHelper.getInstance().getPerson() == null) {
            UserHelper.getInstance().load();
            UserHelper.getInstance().loadCurrentUser();
        }

        try {
            EventBus.getDefault().register(this);
        } catch (EventBusException e) {
            e.printStackTrace(); // just ignore eventbus exceptions, they are not very relevant
        }

        super.onStart();
    }

    @Override
    protected void onPause() {
        UserHelper.getInstance().save();
        UserHelper.getInstance().saveCurrentUser();
        super.onPause();
    }

    @Override
    public void onStop() {
        UserHelper.getInstance().save();
        try {
            EventBus.getDefault().unregister(this);
        } catch (EventBusException e) {
            e.printStackTrace(); // just ignore eventbus exceptions, they are not very relevant
        }
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(MainActivity.currentScreen != null)
            outState.putInt("screen", MainActivity.currentScreen.ordinal());
        outState.putString("token", UserHelper.getInstance().getToken());
        super.onSaveInstanceState(outState);
    }

    /**
     * Show a person's smobo page, if the current user has sufficient permission to view it, or
     * show an error otherwise.
     *
     * @param person    Person object for the person for whom to show the smobo page
     */
    public void openSmoboFor(final PersonSimpleName person) {
        if(person.getDisplayonline() || person.getId().equals(UserHelper.getInstance().getPerson().getId())) {
            forceOpenSmobo(person.getId(), person.getPostalname(), person.getPhotomediaid());
        } else {
            Services.getInstance().permissionsService.hasPermission("useradmin", "admin").enqueue(new Callback<Boolean>() {
                @Override
                public void onSucces(Response<Boolean> response) {
                    if (response.body()) {
                        forceOpenSmobo(person.getId(), person.getPostalname(), person.getPhotomediaid());
                    } else {
                        Toast.makeText(
                                BasicActivity.this,
                                getString(R.string.person_not_display_online_error),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
            });
        }
    }

    /**
     * Private helper function to open a person's smobo page after all checks have passed.
     * This function is private so it cannot accidentally be called from another class. Before
     * this function is called, a permission check needs to have been performed
     *
     * @param personId      ID of person to show
     * @param postalName    Postal name of person to show
     * @param photoMediaId  ID of photo media for the profile of the person to show
     */
    private void forceOpenSmobo(int personId, String postalName, Integer photoMediaId) {
        Intent smoboIntent = new Intent(BasicActivity.this, SmoboActivity.class);
        smoboIntent.putExtra("id", personId);
        smoboIntent.putExtra("name", postalName);
        smoboIntent.putExtra("photo", photoMediaId);
        startActivity(smoboIntent);
    }

    public void openSmoboFor(PersonWithNote person) {
        openSmoboFor(person.getPerson());
    }

    public void onEventMainThread(ErrorEvent event) {
        Toast toast = Toast.makeText(getApplicationContext(), event.error.getMessage(), Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onEventMainThread(ServerErrorEvent event) {
        Toast toast;

        if(event == null || event.error == null) {
            toast = Toast.makeText(getApplicationContext(), getString(R.string.content_loading_error), Toast.LENGTH_SHORT);
            toast.show();
            Gson gson = new Gson();
        } else {
            switch (event.error.getStatus()) {
                case 401: // Unauthorized
                    toast = Toast.makeText(getApplicationContext(), getString(R.string.notloggedin), Toast.LENGTH_SHORT);
                    toast.show();
                    EventBus.getDefault().post(new UserLoggedInEvent(false)); // initialise logged out ui when in main activity
                    break;
                case 403: // Forbidden
                    toast = Toast.makeText(getApplicationContext(), getString(R.string.noaccess), Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                case 404: // Not found
                    toast = Toast.makeText(getApplicationContext(), getString(R.string.content_loading_error), Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                case 405:
                    break;
                case 500: // Internal error
                    toast = Toast.makeText(getApplicationContext(), getString(R.string.content_loading_error), Toast.LENGTH_SHORT);
                    toast.show();
                    Gson gson = new Gson();
            }
        }
    }

    public void onEventMainThread(LinkClickedEvent event) {
        Intent urlIntent = new Intent(Intent.ACTION_VIEW);
        urlIntent.setData(Uri.parse(event.url.replace('\"',' ').trim()));
        startActivity(urlIntent);
    }
}
