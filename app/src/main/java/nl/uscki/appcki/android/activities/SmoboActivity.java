package nl.uscki.appcki.android.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;
import java.util.Map;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.events.DetailItemUpdatedEvent;
import nl.uscki.appcki.android.fragments.adapters.SmoboViewPagerAdapter;
import nl.uscki.appcki.android.generated.smobo.SmoboItem;
import nl.uscki.appcki.android.views.SmoboInfoWidget;

public class SmoboActivity extends BasicActivity implements AppBarLayout.OnOffsetChangedListener, SmoboInfoWidget.OnContextButtonClickListener {

    public static final int PERSON = 0;
    public static final int WICKI = 1;

    AppBarLayout appBarLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;

    ImageView profile;
    private String personName;

    TabLayout tabLayout;
    ViewPager2 viewPager;
    SmoboViewPagerAdapter adapter;

    boolean collapsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_smobo);
        appBarLayout = findViewById(R.id.appbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        toolbar = findViewById(R.id.toolbar);
        profile = findViewById(R.id.smobo_profile);
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.smobo_viewpager);

        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getIntExtra("id", 0) != 0) {
            if(getIntent().getStringExtra("name") != null) {
                this.personName = getIntent().getStringExtra("name");
                collapsingToolbarLayout.setTitle(" ");
                tabLayout.addTab(tabLayout.newTab().setText(this.personName), PERSON);
                collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.AppTheme_CollapsingToolbarTitle);
            }

            int id = getIntent().getIntExtra("id", 0);

            tabLayout.addTab(tabLayout.newTab().setText("WiCKI"), WICKI);
            this.adapter = new SmoboViewPagerAdapter(getSupportFragmentManager(), getLifecycle(), id);
            viewPager.setAdapter(this.adapter);

            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                String tabTitle;
                switch (position) {
                    default:
                    case PERSON:
                        tabTitle = this.personName;
                        break;
                    case WICKI:
                        tabTitle = getApplicationContext().getString(R.string.smobo_tab_wicki);
                }
                tab.setText(tabTitle);
            }).attach();
        }

        if (getIntent().getIntExtra("photo", 0) != 0) {
            Glide.with(this)
                    .load(MediaAPI.getMediaUri(getIntent().getIntExtra("photo", 0), MediaAPI.MediaSize.LARGE))
                    .into(profile);
            profile.setTransitionName("smobo_profile_photo");
            profile.setOnClickListener(v -> {
                Intent intent = new FullScreenMediaActivity
                        .SingleImageIntentBuilder(personName, "smobo_profile_photo")
                        .media(getIntent().getIntExtra("photo", 0))
                        .build(SmoboActivity.this);

                SmoboActivity.this.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(
                        SmoboActivity.this, profile, profile.getTransitionName()).toBundle());
            });
        } else {
            appBarLayout.setExpanded(false);
        }
    }

    @Override
    protected void propegateMapSharedElements(List<String> names, Map<String, View> sharedElements) {
        if(!names.isEmpty() && names.get(0).equals("smobo_profile_photo")) {
            sharedElements.put(names.get(0), profile);
        } else {
            super.propegateMapSharedElements(names, sharedElements);
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

    @Override
    public void onClick(String mainText, SmoboInfoWidget.InfoType type) {
        if (type == SmoboInfoWidget.InfoType.ADRESS) {
            String address = mainText.replaceAll("\\s", "+");
            Uri gmmIntentUri = Uri.parse(String.format("https://www.google.com/maps/dir/?api=1&destination=%s", address));
            Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            startActivity(intent);
        } else if (type == SmoboInfoWidget.InfoType.PHONE) {
            Intent messagingIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + mainText));
            startActivity(Intent.createChooser(messagingIntent, "Send message..."));
        } else if (type == SmoboInfoWidget.InfoType.EMAIL) {
            final Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mainText});

            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        }
    }

    public void onEventMainThread(DetailItemUpdatedEvent<SmoboItem> item) {
        if(this.adapter != null) {
            this.adapter.setHasWicki(item.getUpdatedItem().getWickiPage() != null);
        }
    }
}
