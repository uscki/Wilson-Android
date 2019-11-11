package nl.uscki.appcki.android.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.fragments.adapters.SmoboViewPagerAdapter;
import nl.uscki.appcki.android.views.SmoboInfoWidget;

public class SmoboActivity extends BasicActivity implements AppBarLayout.OnOffsetChangedListener, SmoboInfoWidget.OnContextButtonClickListener {

    public static final int PERSON = 0;
    public static final int WICKI = 1;

    AppBarLayout appBarLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;

    SimpleDraweeView profile;

    TabLayout tabLayout;
    ViewPager viewPager;

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

        toolbar.setTitle(" ");

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getIntExtra("id", 0) != 0) {
            if(getIntent().getStringExtra("name") != null) {
                collapsingToolbarLayout.setTitle(" ");
                tabLayout.addTab(tabLayout.newTab().setText(getIntent().getStringExtra("name")), PERSON);
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
            profile.setImageURI(MediaAPI.getMediaUri(getIntent().getIntExtra("photo", 0)));
        } else {
            appBarLayout.setExpanded(false);
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
