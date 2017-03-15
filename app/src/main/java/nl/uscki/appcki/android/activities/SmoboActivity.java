package nl.uscki.appcki.android.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.fragments.adapters.SmoboCommissieAdapter;
import nl.uscki.appcki.android.fragments.adapters.SmoboMediaAdapter;
import nl.uscki.appcki.android.generated.organisation.Committee;
import nl.uscki.appcki.android.generated.smobo.SmoboItem;
import nl.uscki.appcki.android.views.SmoboInfoWidget;
import retrofit2.Response;

public class SmoboActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, SmoboInfoWidget.OnContextButtonClickListener {

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

    @BindView(R.id.smobo_media_gridview)
    HorizontalGridView mediaGrid;

    @BindView(R.id.smobo_address_info)
    FrameLayout addressInfo;
    @BindView(R.id.smobo_email_info)
    FrameLayout emailInfo;
    @BindView(R.id.smobo_phone_info)
    FrameLayout phoneInfo;
    @BindView(R.id.smobo_mobile_info)
    FrameLayout mobileInfo;

    @BindView(R.id.smobo_groups)
    RecyclerView smoboGroups;

    boolean collapsed = false;

    private Callback<SmoboItem> smoboCallback = new Callback<SmoboItem>() {
        @Override
        public void onSucces(Response<SmoboItem> response) {
            SmoboItem p = response.body();
            swipeRefreshLayout.setRefreshing(false);
            scrollView.setVisibility(View.VISIBLE);

            if (p.getNumOfPhotos() > 0) {
                ((BaseItemAdapter) mediaGrid.getAdapter()).update(p.getPhotos());
            }

            createAddressInfoWidget(p);
            createEmailInfoWidget(p);
            createPhoneInfoWidget(p);
            createMobileInfoWidget(p);

            ((BaseItemAdapter) smoboGroups.getAdapter()).update(p.getGroups());

            if (p.getPerson().getPhotomediaid() != null) {
                profile.setImageURI(MediaAPI.getMediaUri(p.getPerson().getPhotomediaid()));
            } else {
                appBarLayout.setExpanded(false);
            }
        }
    };

    private void createAddressInfoWidget(SmoboItem p) {
        Bundle bundle = new Bundle();
        bundle.putString("maintext", p.getPerson().getAddress1() + "\n" + p.getPerson().getZipcode() + ", " + p.getPerson().getCity());
        bundle.putString("subtext", "Home");
        bundle.putInt("infotype", SmoboInfoWidget.InfoType.ADRESS.ordinal());

        SmoboInfoWidget widget = new SmoboInfoWidget();
        widget.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.smobo_address_info, widget)
                .commit();
    }

    private void createEmailInfoWidget(SmoboItem p) {
        Bundle bundle = new Bundle();
        bundle.putString("maintext", p.getPerson().getEmailaddress());
        bundle.putString("subtext", "Home");
        bundle.putInt("infotype", SmoboInfoWidget.InfoType.EMAIL.ordinal());

        SmoboInfoWidget widget = new SmoboInfoWidget();
        widget.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.smobo_email_info, widget)
                .commit();
    }

    private void createPhoneInfoWidget(SmoboItem p) {
        if(p.getPerson().getPhonenumber() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("maintext", p.getPerson().getPhonenumber());
            bundle.putString("subtext", "Home");
            bundle.putInt("infotype", SmoboInfoWidget.InfoType.PHONE.ordinal());

            SmoboInfoWidget widget = new SmoboInfoWidget();
            widget.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.smobo_phone_info, widget)
                    .commit();
        } else {
            phoneInfo.setPadding(0,0,0,0);
        }
    }

    private void createMobileInfoWidget(SmoboItem p) {
        if (p.getPerson().getMobilenumber() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("maintext", p.getPerson().getMobilenumber());
            bundle.putString("subtext", "Mobile");
            bundle.putInt("infotype", SmoboInfoWidget.InfoType.PHONE.ordinal());

            SmoboInfoWidget widget = new SmoboInfoWidget();
            widget.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.smobo_mobile_info, widget)
                    .commit();
        } else {
            mobileInfo.setPadding(0,0,0,0);
        }
    }

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
            collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.AppTheme_CollapsingToolbarTitle);
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

        mediaGrid.setAdapter(new SmoboMediaAdapter(new ArrayList<Integer>()));
        smoboGroups.setAdapter(new SmoboCommissieAdapter(new ArrayList<Committee>()));
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

    @Override
    public void onClick(String mainText, SmoboInfoWidget.InfoType type) {
        if (type == SmoboInfoWidget.InfoType.ADRESS) {
            String address = mainText.replaceAll("\\s", "+");
            Uri gmmIntentUri = Uri.parse(String.format("geo:0,0?q=%s", address));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        } else if (type == SmoboInfoWidget.InfoType.PHONE) {
            Intent messagingIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + mainText));

            startActivity(Intent.createChooser(messagingIntent, "Send message..."));
        } else if (type == SmoboInfoWidget.InfoType.EMAIL) {
            final Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mainText});

            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        }
    }
}
