package nl.uscki.appcki.android.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.google.gson.Gson;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.meeting.adapter.MeetingDetailAdapter;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;
import retrofit2.Response;

public class MeetingActivity extends AppCompatActivity {
    MeetingItem item;
    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;

    private Callback<MeetingItem> meetingCallback = new Callback<MeetingItem>() {
        @Override
        public void onSucces(Response<MeetingItem> response) {
            if(response.body() != null) {
                item = response.body();

                if (item.getMeeting().getStartdate() != null) {
                    tabLayout.addTab(tabLayout.newTab().setText("Overzicht"));
                    tabLayout.addTab(tabLayout.newTab().setText("Aanwezig"));
                    tabLayout.addTab(tabLayout.newTab().setText("Afwezig"));
                } else {
                    tabLayout.addTab(tabLayout.newTab().setText("Planner"));
                    tabLayout.addTab(tabLayout.newTab().setText("Gereageerd"));
                    tabLayout.addTab(tabLayout.newTab().setText("Niet gereageerd"));
                }

                viewPager.setAdapter(new MeetingDetailAdapter(getSupportFragmentManager(), item));
            } else {
                //// TODO: 11/24/16 error handling
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda); // this is actually correct cause it uses the same layout
        MainActivity.currentScreen = MainActivity.Screen.MEETING_DETAIL;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        if (getIntent().getBundleExtra("item") != null) {
            Gson gson = new Gson();
            item = gson.fromJson(getIntent().getBundleExtra("item").getString("item"), MeetingItem.class);
            Services.getInstance().meetingService.get(item.getMeeting().getId()).enqueue(meetingCallback);
        }

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        getMenuInflater().inflate(R.menu.meeting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
