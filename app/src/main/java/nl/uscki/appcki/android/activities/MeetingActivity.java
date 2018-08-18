package nl.uscki.appcki.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.meeting.adapter.MeetingDetailAdapter;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;
import nl.uscki.appcki.android.helpers.UserHelper;
import nl.uscki.appcki.android.helpers.calendar.CalendarHelper;
import retrofit2.Response;

public class MeetingActivity extends BasicActivity {
    MeetingItem item;
    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;
    private Menu menu;

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

        if(!UserHelper.getInstance().isLoggedIn()) {
            Log.e("AgendaActivity", "Starting MainActivity");
            startActivity(new Intent(this, MainActivity.class));
        }

        setContentView(R.layout.activity_agenda); // this is actually correct cause it uses the same layout
        MainActivity.currentScreen = MainActivity.Screen.MEETING_DETAIL;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        if (getIntent().getBundleExtra("item") != null) {
            // this happens when this activity is launched from the overview
            Gson gson = new Gson();
            item = gson.fromJson(getIntent().getBundleExtra("item").getString("item"), MeetingItem.class);
            Services.getInstance().meetingService.get(item.getMeeting().getId()).enqueue(meetingCallback);
        } else if (getIntent().getIntExtra("id", -1) != -1) {
            // this happens on receiving a notification from the server
            Services.getInstance().meetingService.get(getIntent().getIntExtra("id", -1)).enqueue(meetingCallback);
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

        menu.findItem(R.id.action_meeting_export).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                exportMeeting();
                return true;
            }
        });

        menu.findItem(R.id.action_remove_meeting_from_calendar).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                removeMeetingFromSystemCalendar();
                return true;
            }
        });

        this.menu = menu;
        setExportButtons();
        return super.onCreateOptionsMenu(menu);
    }


    private void exportMeeting() {
        if(item.getMeeting().getActual_slot() == null) return;
        CalendarHelper.getInstance().addItemToCalendar(item);
        setExportButtons();
    }

    private void removeMeetingFromSystemCalendar() {
        if(item.getMeeting().getActual_slot() == null) return;
        CalendarHelper.getInstance().removeItemFromCalendar(item);
        setExportButtons();
    }

    @Override
    public void onResume(){
        super.onResume();
        setExportButtons();
    }

    private void setExportButtons() {
        if(item == null || item.getMeeting().getActual_slot() == null || this.menu == null) return;

        int calendarEventItemId;

        try {
            calendarEventItemId = CalendarHelper.getInstance().getEventIdForItemIfExists(item);
        } catch(SecurityException e) {
            return;
        }

        if(calendarEventItemId > 0) {
            menu.findItem(R.id.action_meeting_export).setVisible(false);
            menu.findItem(R.id.action_remove_meeting_from_calendar).setVisible(true);
        } else {
            menu.findItem(R.id.action_meeting_export).setVisible(true);
            menu.findItem(R.id.action_remove_meeting_from_calendar).setVisible(false);
        }
    }
}
