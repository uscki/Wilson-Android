package nl.uscki.appcki.android.activities;

import android.app.SharedElementCallback;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Map;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.fragments.adapters.SmoboViewPagerAdapter;
import nl.uscki.appcki.android.helpers.ISharedElementViewContainer;
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
    ViewPager viewPager;

    boolean collapsed = false;

    private ISharedElementViewContainer viewContainer;

    public boolean registerSharedElementCallback(ISharedElementViewContainer viewContainer) {
        boolean status = this.viewContainer == null;
        this.viewContainer = viewContainer;
        return status;
    }

    public boolean deregisterSharedElementCallback(ISharedElementViewContainer viewContainer) {
        boolean status = this.viewContainer != null && this.viewContainer.equals(viewContainer);
        this.viewContainer = null;
        return status;
    }

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
            viewPager.setAdapter(new SmoboViewPagerAdapter(getSupportFragmentManager(), id));

            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    //setCurrentScreen(tab.getPosition());
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
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

        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if(!names.isEmpty() && names.get(0).equals("smobo_profile_photo")) {
                    sharedElements.put(names.get(0), profile);
                } else if(viewContainer != null) {
                    Log.e("SmoboActivity", "Propegating onMapSharedElements to " + viewContainer.getClass());
                    viewContainer.onMapSharedElements(names, sharedElements);
                } else {
                    Log.e("SmoboActivity", "No viewcontainer. Not propegating onMapSharedElements");
                }
            }
        });
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        if(viewContainer != null) {
            Log.e("SmoboActivity", "Propegating onActivityReenter to " + this.viewContainer.getClass());
            resultCode = viewContainer.activityReentering(resultCode, data);
        } else {
            Log.e("SmoboActivity", "No viewcontainer. Not propegating onActivityReenter");
        }
        super.onActivityReenter(resultCode, data);
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
            final Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mainText});

            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        }
    }
}
