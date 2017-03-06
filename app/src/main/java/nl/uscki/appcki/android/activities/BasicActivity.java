package nl.uscki.appcki.android.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import nl.uscki.appcki.android.generated.organisation.PersonSimpleName;
import nl.uscki.appcki.android.generated.organisation.PersonWithNote;

/**
 * Created by peter on 3/5/17.
 */

public abstract class BasicActivity extends AppCompatActivity {
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
