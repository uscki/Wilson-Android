package nl.uscki.appcki.android.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import org.joda.time.DateTime;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.error.Error;
import nl.uscki.appcki.android.events.AgendaItemSubscribedEvent;
import nl.uscki.appcki.android.events.ErrorEvent;
import nl.uscki.appcki.android.events.ServerErrorEvent;
import nl.uscki.appcki.android.fragments.agenda.AgendaDetailAdapter;
import nl.uscki.appcki.android.fragments.agenda.AgendaDetailFragment;
import nl.uscki.appcki.android.fragments.agenda.SubscribeDialogFragment;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.generated.agenda.AgendaParticipant;
import nl.uscki.appcki.android.generated.agenda.AgendaParticipantLists;
import nl.uscki.appcki.android.helpers.PermissionHelper;
import nl.uscki.appcki.android.helpers.UserHelper;
import nl.uscki.appcki.android.helpers.calendar.CalendarHelper;
import nl.uscki.appcki.android.services.OnetimeAlarmReceiver;
import retrofit2.Response;

public class AgendaActivity extends BasicActivity {
    public static final String PARAM_AGENDA_ID = "nl.uscki.appcki.android.activities.param.AGENDA_ID";

    AgendaItem item;
    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;

    boolean foundUser = false;
    Menu menu;

    private Callback<AgendaItem> agendaCallback = new Callback<AgendaItem>() {
        @Override
        public void onSucces(Response<AgendaItem> response) {

            if(response == null) {
                Log.e(this.getClass().toString(), "No response");
                return;
            }

            Log.e(this.getClass().toString(), response.toString());

            if(response.body() == null) {
                Log.e(this.getClass().toString(), "No response or response body");
                return;
            }

            Log.e(this.getClass().toString(), response.body().toString());

            item = response.body();
            viewPager.setAdapter(new AgendaDetailAdapter(getSupportFragmentManager(), item));

            for (AgendaParticipant part : item.getParticipants()) {
                if (part.getPerson() != null && UserHelper.getInstance().getPerson() != null) {
                    if (part.getPerson().getId().equals(UserHelper.getInstance().getPerson().getId())) {
                        foundUser = true;
                    }
                } else {
                    finish();
                }
            }
            
            setSubscribeButtons();
            setExportButtons();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if we're running on Android 5.0 or higher
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        } else {
            // Implement this feature without material design
        }*/

        if(!UserHelper.getInstance().isLoggedIn()) {
            Log.e("AgendaActivity", "Starting MainActivity");
            startActivity(new Intent(this, MainActivity.class));
        }

        setContentView(R.layout.activity_agenda);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MainActivity.currentScreen = MainActivity.Screen.AGENDA_DETAIL;

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Agenda"));
        tabLayout.addTab(tabLayout.newTab().setText("Deelnemers"));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.comments)));
        viewPager = findViewById(R.id.viewpager);

        if (getIntent().getBundleExtra("item") != null) {
            Gson gson = new Gson();
            item = gson.fromJson(getIntent().getBundleExtra("item").getString("item"), AgendaItem.class);
            Log.e(this.getClass().toString(), "Generating AgendaItem through bundle extra item");
            Services.getInstance().agendaService.get(item.getId()).enqueue(agendaCallback);
        } else if (getIntent().getStringExtra("item") != null) {
            Gson gson = new Gson();
            item = gson.fromJson(getIntent().getStringExtra("item"), AgendaItem.class);
            Log.e(this.getClass().toString(), "Generating AgendaItem through String extra item");
            Services.getInstance().agendaService.get(item.getId()).enqueue(agendaCallback);
        } else if (getIntent().getIntExtra(PARAM_AGENDA_ID, -1) >= 0) {
            Log.e(this.getClass().toString(), "Generating AgendaItem through id");
            Services.getInstance().agendaService.get(getIntent().getIntExtra(PARAM_AGENDA_ID, -1)).enqueue(agendaCallback);
        } else {
            // the item is no longer loaded so we can't open this activity, thus we'll close it
            Log.e(getClass().getSimpleName(), "Not loaded. Finish");
            finish();
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

        getMenuInflater().inflate(R.menu.agenda_menu, menu);
        this.menu = menu;

        this.menu.findItem(R.id.action_agenda_unsubscribe).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                subscribeToAgenda(false);
                return true;
            }
        });
        this.menu.findItem(R.id.action_agenda_subscribe).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                subscribeToAgenda(true);
                return true;
            }
        });
        this.menu.findItem(R.id.action_agenda_export).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                exportToCalendar();
                return true;
            }
        });
        this.menu.findItem(R.id.action_remove_from_calendar).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                removeFromCalendar();
                return true;
            }
        });

        // verander visibility pas als we in een detail view zitten
        setSubscribeButtons();
        setExportButtons();

        this.menu.findItem(R.id.action_agenda_archive).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResume() {

        super.onResume();

        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        setExportButtons();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void setExportButtons() {
        if(menu == null || item == null)
            return;

        int calendarEventItemId;

        try {
            calendarEventItemId = CalendarHelper.getInstance().getEventIdForItemIfExists(item);
        } catch(SecurityException e) {
            return;
        }

        MenuItem exportButton = menu.findItem(R.id.action_agenda_export);
        MenuItem removeCalendarButton = menu.findItem(R.id.action_remove_from_calendar);

        if(exportButton == null) {
            Log.e(getClass().getSimpleName(), "Export button is null. Whyyy");
            return;
        }
        if(removeCalendarButton == null) {
            Log.e(getClass().getSimpleName(), "Remove from calendar button is null. Whyyy");
            return;
        }

        if(calendarEventItemId > 0)
        {
            menu.findItem(R.id.action_agenda_export).setVisible(false);
            if(PermissionHelper.canDeleteCalendar()) {
                menu.findItem(R.id.action_remove_from_calendar).setVisible(true);

            }
        } else {
            menu.findItem(R.id.action_remove_from_calendar).setVisible(false);
            menu.findItem(R.id.action_agenda_export).setVisible(true);
        }
    }

    private void setSubscribeButtons() {
        if(menu == null) return;

        if(foundUser) {
            menu.findItem(R.id.action_agenda_subscribe).setVisible(false);
            menu.findItem(R.id.action_agenda_unsubscribe).setVisible(true);
        } else {
            menu.findItem(R.id.action_agenda_subscribe).setVisible(true);
            menu.findItem(R.id.action_agenda_unsubscribe).setVisible(false);
        }
    }

    /**
     * Sets an alarm for 30 minutes before the start time of this event.
     * @param item
     */
    private void setAlarmForEvent(AgendaItem item) {
        DateTime time = item.getStart().minusMinutes(30);
        time = new DateTime().plusMinutes(2); // FOR DEBUGGING PURPOSES
        Log.e("AgendaDetailTabs", "Setting alarm for id: " + item.getId() + " at time: " + time.toString());
        Gson gson = new Gson();

        Intent myIntent = new Intent(this, OnetimeAlarmReceiver.class);
        myIntent.putExtra("item", gson.toJson(item));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, item.getId(), myIntent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, time.getMillis(), pendingIntent);
    }

    private void unsetAlarmForEvent(AgendaItem item) {
        Log.e("AgendaDetailTabs", "Unsetting alarm for id: " + item.getId());
        Gson gson = new Gson();

        Intent myIntent = new Intent(this, OnetimeAlarmReceiver.class);
        myIntent.putExtra("item", gson.toJson(item));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, item.getId(), myIntent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void subscribeToAgenda(boolean subscribe) {
        if(subscribe) {
            DialogFragment newFragment = new SubscribeDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", item.getId());
            newFragment.setArguments(args);
            newFragment.show(getSupportFragmentManager(), "agenda_subscribe");
        } else {
            AgendaItem item = AgendaDetailFragment.item;

            if(item.getHasUnregisterDeadline()) {
                DateTime deadline = new DateTime(item.getUnregisterDeadline());

                if(!deadline.isAfterNow()) {
                    EventBus.getDefault().post(new ErrorEvent(new Error() {

                        @Override
                        public String getMessage() {
                            return "Kan niet unsubscriben door een unsubscribe deadline!";
                        }

                    }));

                    return; // don't still try to unsubscribe
                }
            }

            // no deadline for unsubscribing
            Log.d("MainActivity", "unsubscribing for:" + AgendaDetailFragment.item.getId());

            Services.getInstance().agendaService.unsubscribe(AgendaDetailFragment.item.getId())
                    .enqueue(new nl.uscki.appcki.android.api.Callback<AgendaParticipantLists>() {

                @Override
                public void onSucces(Response<AgendaParticipantLists> response) {
                    EventBus.getDefault()
                            .post(new AgendaItemSubscribedEvent(response.body(), true));
                    setExportButtons();
                }

            });
        }
    }

    private void exportToCalendar() {
        CalendarHelper.getInstance().addItemToCalendar(item);
        setExportButtons();
        Toast.makeText(this, R.string.agenda_toast_added_to_calendar ,Toast.LENGTH_SHORT)
                .show();
    }

    private void removeFromCalendar() {
        if(CalendarHelper.getInstance().removeItemFromCalendar(item))
            Toast.makeText(
                    this,
                    R.string.agenda_toast_removed_from_calendar,
                    Toast.LENGTH_SHORT
            ).show();

        setExportButtons();
    }

    // EVENT HANDLING

    public void onEventMainThread(ServerErrorEvent event) {
        switch (event.error.getStatus()) {
            case 412:
                //TODO translate error message
                Toast.makeText(this, event.error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onEventMainThread(AgendaItemSubscribedEvent event) {
        if(!event.showSubscribe) {
            setAlarmForEvent(item);
            menu.findItem(R.id.action_agenda_subscribe).setVisible(false);
            menu.findItem(R.id.action_agenda_unsubscribe).setVisible(true);

            int calendarEventItemId;

            try {
                calendarEventItemId = CalendarHelper.getInstance().getEventIdForItemIfExists(item);
            } catch(Exception e) {
                calendarEventItemId = -1;
            }

            if(PermissionHelper.canExportCalendarAuto() && calendarEventItemId <= 0) {
                exportToCalendar();
            }
        } else {
            unsetAlarmForEvent(item);
            menu.findItem(R.id.action_agenda_subscribe).setVisible(true);
            menu.findItem(R.id.action_agenda_unsubscribe).setVisible(false);
        }
        setExportButtons();
    }
}
