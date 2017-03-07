package nl.uscki.appcki.android.activities;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.generated.smobo.SmoboItem;
import retrofit2.Response;

public class SmoboActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    @BindView(R.id.smobo_swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;
    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.smobo_profile)
    SimpleDraweeView profile;
    @BindView(R.id.smobo_address)
    TextView address;
    @BindView(R.id.smobo_phone)
    TextView phone;
    @BindView(R.id.smobo_birthday)
    TextView birthday;
    @BindView(R.id.smobo_zipcode)
    TextView zipcode;
    @BindView(R.id.smobo_city)
    TextView city;
    boolean collapsed = false;

    private Callback<SmoboItem> smoboCallback = new Callback<SmoboItem>() {
        @Override
        public void onSucces(Response<SmoboItem> response) {
            SmoboItem p = response.body();
            swipeRefreshLayout.setRefreshing(false);
            scrollView.setVisibility(View.VISIBLE);

            address.setText(p.getPerson().getAddress1());
            zipcode.setText(p.getPerson().getZipcode() + ", ");
            city.setText(p.getPerson().getCity());

            Log.e("SmoboActivity", p.getPerson().getMobilenumber());
            if(p.getPerson().getPhonenumber() != null && !p.getPerson().getPhonenumber().isEmpty()) {
                phone.setText(p.getPerson().getPhonenumber());
            } else if(p.getPerson().getMobilenumber() != null && !p.getPerson().getMobilenumber().isEmpty()) {
                phone.setText(p.getPerson().getMobilenumber());
            } else {
                phone.setVisibility(View.GONE);
            }

            DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-yyyy");
            String birthdayStr = new DateTime(p.getPerson().getBirthdate()).toString(fmt);
            birthday.setText(birthdayStr);

            if (p.getPerson().getPhotomediaid() != null) {
                profile.setImageURI(MediaAPI.getMediaUri(p.getPerson().getPhotomediaid()));
            } else {
                appBarLayout.setExpanded(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if we're running on Android 5.0 or higher
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Call some material design APIs here
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        } else {
            // Implement this feature without material design
        }*/

        setContentView(R.layout.activity_smobo);
        ButterKnife.bind(this);

        if(getIntent().getStringExtra("name") != null) {
            collapsingToolbarLayout.setTitle(getIntent().getStringExtra("name"));
        }
        toolbar.setTitle(" ");

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getIntExtra("id", 0) != 0) {
            final Integer id = getIntent().getIntExtra("id", 0);
            swipeRefreshLayout.setRefreshing(true);
            scrollView.setVisibility(View.INVISIBLE);
            Services.getInstance().smoboService.get(id).enqueue(smoboCallback);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Services.getInstance().smoboService.get(id).enqueue(smoboCallback);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if(collapsed) {
                    finish(); // don't do transition animations if the profile isn't visible
                } else {
                    supportFinishAfterTransition();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        if (offset == 0) {
            collapsed = true;
        }
        else {
            collapsed = false;
        }
    }
}
