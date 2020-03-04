package nl.uscki.appcki.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.DetailItemUpdatedEvent;
import nl.uscki.appcki.android.fragments.meeting.adapter.MeetingDetailAdapter;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;
import nl.uscki.appcki.android.helpers.PermissionHelper;
import nl.uscki.appcki.android.helpers.UserHelper;
import nl.uscki.appcki.android.helpers.calendar.CalendarHelper;
import retrofit2.Response;

public class MeetingActivity extends BasicActivity {
    public static final String PARAM_MEETING_ID = "nl.uscki.appcki.android.activities.param.MEETING_ID";

    private int itemId;
    private MeetingItem item;
    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;
    ProgressBar loadingIndicator;
    private Menu menu;
    private PlannerView currentView = PlannerView.UNINSTANTIATED;


    private Callback<MeetingItem> meetingCallback = new Callback<MeetingItem>() {
        @Override
        public void onSucces(Response<MeetingItem> response) {
            loadingIndicator.setVisibility(View.GONE);
            tabLayout.setVisibility(View.VISIBLE);

            if(response.body() != null) {
                setMeetingItem(response.body());
                populateTabLayout();
                setExportButtons();
            } else {
                //// TODO: 11/24/16 error handling
            }
        }
    };

    private void populateTabLayout() {
        if(this.item == null) return;

        boolean noResetRequired = this.getMeetingItem().getMeeting().getStartdate() != null &&
                this.currentView == PlannerView.PLANNED;
        noResetRequired |= (this.getMeetingItem().getMeeting().getStartdate() == null &&
                this.currentView.equals(PlannerView.UNPLANNED));

        if(noResetRequired) {
            // Correct tabs are already loaded for this view. Resetting adapter would also
            // unnecessarily reset current tab
            return;
        }

        tabLayout.removeAllTabs();
        if (this.item.getMeeting().getStartdate() != null) {
            this.currentView = PlannerView.PLANNED;
            tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.meeting_header_overview)));
            tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.meeting_header_present)));
            tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.meeting_header_absent)));
        } else {
            this.currentView = PlannerView.UNPLANNED;
            tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.meeting_header_planner_view)));
            tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.meeting_header_responded)));
            tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.meeting_header_no_response)));
        }
        viewPager.setAdapter(new MeetingDetailAdapter(getSupportFragmentManager(), this.currentView));
    }

    public MeetingItem getMeetingItem() {
        return this.item;
    }

    public void setMeetingItem(MeetingItem item) {
        this.item = item;
        this.itemId = item.getId();
        EventBus.getDefault().post(new DetailItemUpdatedEvent<>(item));
    }

    public void refreshMeetingItem() {
        int id = this.item == null ? this.itemId : this.item.getId();
        if(id >= 0) {
            Services.getInstance().meetingService.get(id).enqueue(meetingCallback);
        } else {
            Log.e(getClass().getSimpleName(), "Requested meeting item refresh, but ID not present");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!UserHelper.getInstance().isLoggedIn()) {
            Log.e("AgendaActivity", "Starting MainActivity");
            startActivity(new Intent(this, MainActivity.class));
        }

        setContentView(R.layout.acitivity_meeting);
        MainActivity.currentScreen = MainActivity.Screen.MEETING_DETAIL;

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setVisibility(View.INVISIBLE);
        viewPager = findViewById(R.id.viewpager);

        loadingIndicator = findViewById(R.id.meeting_loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);
        loadingIndicator.setIndeterminate(true);

        if (getIntent().getIntExtra(PARAM_MEETING_ID, -1) != -1) {
            // this happens on receiving a notification from the server or when opening this activity
            this.itemId = getIntent().getIntExtra(PARAM_MEETING_ID, -1);
            refreshMeetingItem();
        }

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
            calendarEventItemId = -1;
        }

        if(calendarEventItemId > 0) {
            menu.findItem(R.id.action_meeting_export).setVisible(false);
            if(PermissionHelper.canDeleteCalendar()) {
                menu.findItem(R.id.action_remove_meeting_from_calendar).setVisible(true);
            }
        } else {
            menu.findItem(R.id.action_meeting_export).setVisible(true);
            menu.findItem(R.id.action_remove_meeting_from_calendar).setVisible(false);
        }
    }

    public enum PlannerView {
        UNPLANNED,
        PLANNED,
        UNINSTANTIATED
    }
}
