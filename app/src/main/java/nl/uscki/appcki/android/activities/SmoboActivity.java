package nl.uscki.appcki.android.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
/*import nl.uscki.appcki.android.fragments.adapters.SmoboCommissieAdapter;
import nl.uscki.appcki.android.fragments.adapters.SmoboMediaAdapter;*/
import nl.uscki.appcki.android.fragments.adapters.SmoboViewPagerAdapter;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.organisation.Committee;
import nl.uscki.appcki.android.generated.smobo.SmoboItem;
import nl.uscki.appcki.android.views.SmoboInfoWidget;
import retrofit2.Response;

public class SmoboActivity extends BasicActivity implements AppBarLayout.OnOffsetChangedListener, SmoboInfoWidget.OnContextButtonClickListener {

    public static final int PERSON = 0;
    public static final int WICKI = 1;

    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.smobo_profile)
    SimpleDraweeView profile;

    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.smobo_viewpager)
    ViewPager viewPager;

    boolean collapsed = false;

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
            final Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mainText});

            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        }
    }
}
