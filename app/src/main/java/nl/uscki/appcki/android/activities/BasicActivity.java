package nl.uscki.appcki.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.generated.organisation.PersonSimpleName;
import nl.uscki.appcki.android.generated.organisation.PersonWithNote;
import nl.uscki.appcki.android.helpers.UserHelper;

/**
 * Created by peter on 3/5/17.
 */

public abstract class BasicActivity extends AppCompatActivity {
    @Override
    protected void onStart() {
        Log.e("Main", "Loading onStart");
        UserHelper.getInstance().setPreferences(getPreferences(MODE_PRIVATE));
        UserHelper.getInstance().load();
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onPause() {
        UserHelper.getInstance().save();
        super.onPause();
    }

    @Override
    public void onStop() {
        UserHelper.getInstance().save();
        EventBus.getDefault().unregister(this);
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

    public void openSmoboFor(PersonSimpleName person) {
        if(person.getDisplayonline()) {
            Intent smoboIntent = new Intent(this, SmoboActivity.class);
            smoboIntent.putExtra("id", person.getId());
            smoboIntent.putExtra("name", person.getPostalname());
            startActivity(smoboIntent);
        }
    }

    public void openSmoboFor(PersonWithNote person) {
        if (person.getPerson().getDisplayonline()) {
            Intent smoboIntent = new Intent(this, SmoboActivity.class);
            smoboIntent.putExtra("id", person.getPerson().getId());
            smoboIntent.putExtra("name", person.getPerson().getPostalname());
            startActivity(smoboIntent);
        }
    }
}
