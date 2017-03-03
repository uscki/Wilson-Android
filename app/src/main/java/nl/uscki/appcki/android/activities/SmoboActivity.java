package nl.uscki.appcki.android.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.generated.organisation.Person;
import retrofit2.Response;

public class SmoboActivity extends AppCompatActivity {
    ImageView profile;
    TextView name;
    TextView address;
    TextView phone;
    TextView birthday;

    private Callback<Person> smoboCallback = new Callback<Person>() {
        @Override
        public void onSucces(Response<Person> response) {
            Person p = response.body();

            name.setText(p.getName());
            address.setText(p.getAddress2());
            phone.setText("p.getPhonenumber");
            birthday.setText("p.getBirthday");

            Services.getInstance().picasso.load(MediaAPI.getMediaUrl(p.getPhotomediaid())).into(profile);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smobo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getIntExtra("id", 0) != 0) {
            Log.e("Smobo", "id found?");
            Integer id = getIntent().getIntExtra("id", 0);
            Services.getInstance().peopleService.get(id).enqueue(smoboCallback);
        }

        profile = (ImageView) findViewById(R.id.smobo_profile);
        name = (TextView) findViewById(R.id.smobo_name);
        address = (TextView) findViewById(R.id.smobo_address);
        phone = (TextView) findViewById(R.id.smobo_phone);
        birthday = (TextView) findViewById(R.id.smobo_birthday);
    }
}
