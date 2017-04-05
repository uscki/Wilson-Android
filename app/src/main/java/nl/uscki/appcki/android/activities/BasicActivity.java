package nl.uscki.appcki.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.EventBusException;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.ErrorEvent;
import nl.uscki.appcki.android.generated.organisation.PersonSimpleName;
import nl.uscki.appcki.android.generated.organisation.PersonWithNote;
import nl.uscki.appcki.android.helpers.UserHelper;
import retrofit2.Response;

/**
 * Created by peter on 3/5/17.
 */

public abstract class BasicActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            FirebaseCrash.log("savedInstanceState != null");
            UserHelper.getInstance().load(savedInstanceState.getString("token"));
        } else {
            FirebaseCrash.log("savedInstanceState == null");
            UserHelper.getInstance().load();
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        Log.e("Main", "Loading onStart");
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
        FirebaseCrash.log("onSaveInstanceState");
        if(MainActivity.currentScreen != null)
            outState.putInt("screen", MainActivity.currentScreen.ordinal());
        outState.putString("token", UserHelper.getInstance().TOKEN);
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
}
