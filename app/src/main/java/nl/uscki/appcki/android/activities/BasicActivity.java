package nl.uscki.appcki.android.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.EventBusException;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.ErrorEvent;
import nl.uscki.appcki.android.events.LinkClickedEvent;
import nl.uscki.appcki.android.events.ServerErrorEvent;
import nl.uscki.appcki.android.events.UserLoggedInEvent;
import nl.uscki.appcki.android.generated.organisation.PersonSimpleName;
import nl.uscki.appcki.android.generated.organisation.PersonWithNote;
import nl.uscki.appcki.android.helpers.UserHelper;
import nl.uscki.appcki.android.services.NotificationReceiver;
import retrofit2.Response;

/**
 * Created by peter on 3/5/17.
 */

public abstract class BasicActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            Crashlytics.log("savedInstanceState != null");
            UserHelper.getInstance().load(savedInstanceState.getString("token"));
        } else {
            Crashlytics.log("savedInstanceState == null");
            UserHelper.getInstance().load();
        }

        NotificationReceiver.logToken();

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
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
        Crashlytics.log("onSaveInstanceState");
        if(MainActivity.currentScreen != null)
            outState.putInt("screen", MainActivity.currentScreen.ordinal());
        outState.putString("token", UserHelper.getInstance().getToken());
        super.onSaveInstanceState(outState);
    }

    public void openSmoboFor(final PersonSimpleName person) {
        Services.getInstance().permissionsService.hasPermission("useradmin", "admin").enqueue(new Callback<Boolean>() {
            @Override
            public void onSucces(Response<Boolean> response) {
                if(person.getDisplayonline() || response.body()) {
                    Intent smoboIntent = new Intent(BasicActivity.this, SmoboActivity.class);
                    smoboIntent.putExtra("id", person.getId());
                    smoboIntent.putExtra("name", person.getPostalname());
                    smoboIntent.putExtra("photo", person.getPhotomediaid());
                    startActivity(smoboIntent);
                }
            }
        });
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
        switch (event.error.getStatus()) {
            case 401: // Unauthorized
                toast = Toast.makeText(getApplicationContext(), getString(R.string.notauthorized), Toast.LENGTH_SHORT);
                toast.show();
                break;
            case 403: // Forbidden
                toast = Toast.makeText(getApplicationContext(), getString(R.string.notloggedin), Toast.LENGTH_SHORT);
                toast.show();
                EventBus.getDefault().post(new UserLoggedInEvent(false)); // initialise logged out ui when in main activity
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
                Crashlytics.logException(new Exception(gson.toJson(event.error))); // just log this server error to firebase
        }
    }

    public void onEventMainThread(LinkClickedEvent event) {
        Intent urlIntent = new Intent(Intent.ACTION_VIEW);
        urlIntent.setData(Uri.parse(event.url.replace('\"',' ').trim()));
        startActivity(urlIntent);
    }
}
