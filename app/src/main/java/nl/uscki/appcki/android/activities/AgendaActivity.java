package nl.uscki.appcki.android.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import org.joda.time.DateTime;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.error.Error;
import nl.uscki.appcki.android.events.AgendaItemSubscribedEvent;
import nl.uscki.appcki.android.events.AgendaItemUpdatedEvent;
import nl.uscki.appcki.android.events.ErrorEvent;
import nl.uscki.appcki.android.events.ServerErrorEvent;
import nl.uscki.appcki.android.fragments.agenda.AgendaDetailAdapter;
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

    protected AgendaItem item;
    private int agendaId = -1;
    AgendaDetailAdapter fragmentAdapter;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.agenda_poster)
    SimpleDraweeView poster;

    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    boolean foundUser = false;
    Menu menu;

    private Callback<AgendaItem> agendaCallback = new Callback<AgendaItem>() {
        @Override
        public void onSucces(Response<AgendaItem> response) {
        if(response == null || response.body() == null) {
            Log.e(this.getClass().toString(), "No response or body");
            return;
        }

        item = response.body();

        setupItem();

        // This isn't nice, but the callback overrides tab selection, and its only called once
        // so with the current implementation, this is best
        // TODO move this to onIntentReceived
//        if(getIntent() != null && getIntent().getAction() != null &&
//                getIntent().getAction().equals(CommentsFragment.ACTION_VIEW_COMMENTS)) {
//            viewPager.setCurrentItem(2);
//            tabLayout.setScrollPosition(2, 0f, false);
//        }
        }
    };

    /**
     * Download the agenda item for the ID that is currently stored on this object
     */
    public void refreshAgendaItem() {
        if(agendaId >= 0) {
            Services.getInstance().agendaService.get(agendaId).enqueue(agendaCallback);
        } else {
            Log.e(getClass().getSimpleName(), "ID not yet present");
        }
    }

    public AgendaItem getAgendaItem() {
        return item;
    }

    /**
     * Once the item is loaded, populate the view with the agenda information
     */
    private void setupItem() {
        if(item == null) return;

        EventBus.getDefault().post(new AgendaItemUpdatedEvent(item));

        if(item.getPosterid() != null && item.getPosterid() >= 0) {
            // TODO: Can we scale to width, maintaining aspect ratio, and then readjusting the height
            // to fit the poster exactly?
            poster.setImageURI(MediaAPI.getMediaUri(item.getPosterid(), MediaAPI.MediaSize.NORMAL));
        }

        // TODO neither of this works?
        if(getSupportActionBar() != null) {
            Log.e(getClass().getSimpleName(), "Setting title in support action bar to " + item.getTitle());
            getSupportActionBar().setTitle(item.getTitle());
        } else if (toolbar != null) {
            Log.e(getClass().getSimpleName(), "Setting title the toolbar to " + item.getTitle());
            toolbar.setTitle(item.getTitle());
        }

        foundUser = false;
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

    /**
     * Create one adapter that can be used from now on
     */
    private void createAdapter() {
        fragmentAdapter = new AgendaDetailAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentAdapter);

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_agenda_details)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_agenda_participants)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.comments)));

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_agenda);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MainActivity.currentScreen = MainActivity.Screen.AGENDA_DETAIL;

        createAdapter();

        // TODO create handleIntent function, so onIntentReceived also works
        if (getIntent().getBundleExtra("item") != null) {
            Gson gson = new Gson();
            item = gson.fromJson(getIntent().getBundleExtra("item").getString("item"), AgendaItem.class);
        } else if (getIntent().getStringExtra("item") != null) {
            Gson gson = new Gson();
            item = gson.fromJson(getIntent().getStringExtra("item"), AgendaItem.class);
        } else if (getIntent().getIntExtra(PARAM_AGENDA_ID, -1) >= 0) {
            agendaId = getIntent().getIntExtra(PARAM_AGENDA_ID, -1);
        } else {
            // the item is no longer loaded so we can't open this activity, thus we'll close it
            Log.e(getClass().getSimpleName(), "Not loaded. Finish");
            finish();
        }

        if (UserHelper.getInstance().getPerson() == null) {
            finish();
        }

        if(agendaId < 0 && item != null) {
            // Artifact of passing objects rather than only the ID.
            // TODO: Refactor agenda intentions to only pass ID, never a (serialized) agenda item
            agendaId = item.getId();
        }

        // Perform an initial load of the data
        refreshAgendaItem();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        getMenuInflater().inflate(R.menu.agenda_menu, menu);
        this.menu = menu;

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

    /**
     * Set the visibility of export or delete buttons based on permissions and if the current
     * event is already in the users system calendar
     */
    private void setExportButtons() {
        if(menu == null || item == null)
            return;

        int calendarEventItemId;

        MenuItem exportButton = menu.findItem(R.id.action_agenda_export);
        MenuItem removeCalendarButton = menu.findItem(R.id.action_remove_from_calendar);

        if(exportButton == null) {
            return;
        }
        if(removeCalendarButton == null) {
            return;
        }

        try {
            calendarEventItemId = CalendarHelper.getInstance().getEventIdForItemIfExists(item);
        } catch(SecurityException e) {
            // No access to calendar. Always show export button
            calendarEventItemId = -1;
        }

        if(calendarEventItemId > 0)
        {
            menu.findItem(R.id.action_agenda_export).setVisible(false);
            if (PermissionHelper.canDeleteCalendar()) {
                menu.findItem(R.id.action_remove_from_calendar).setVisible(true);

            }
        } else {
            menu.findItem(R.id.action_remove_from_calendar).setVisible(false);
            menu.findItem(R.id.action_agenda_export).setVisible(true);
        }
    }

    /**
     * Show or hide subscribe and unsubscribe buttons based on the subscribed status of the current
     * user for this event, and the registration status of the event.
     */
    private void setSubscribeButtons() {
        if(menu == null) return;

        MenuItem subscribe = menu.findItem(R.id.action_agenda_subscribe);
        MenuItem unsubscribe = menu.findItem(R.id.action_agenda_unsubscribe);

        if(subscribe == null || unsubscribe == null) {
            Log.e(getClass().getSimpleName(), "Trying to set button behavior before menu is created");
            return;
        }

        if(foundUser) {
            subscribe.setVisible(false);
            unsubscribe.setVisible(true);
        } else {
            subscribe.setVisible(true);
            unsubscribe.setVisible(false);
        }

        if(item == null) {
            Log.e(getClass().getSimpleName(), "Trying to set button behavior before item is loaded");
            return;
        }

        if (this.item.getMaxregistrations() != null && this.item.getMaxregistrations() == 0) {
            prepareSubscribeButtonsForNoRegistration(subscribe, unsubscribe);
        } else {
            prepareSubscribeButtonsForRegistration(subscribe, unsubscribe);
        }
    }

    /**
     * Set the right subscribe/unsubscribe buttons based on the subscribed status of the current
     * user, in the case that registration is not closed until further notice
     * @param subscribeButton       Reference to the subscribe button object
     * @param unsubscribeButton     Reference to the unsubscribe button object
     */
    private void prepareSubscribeButtonsForRegistration(MenuItem subscribeButton, MenuItem unsubscribeButton) {
        subscribeButton
            .setIcon(R.drawable.plus)
            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    subscribeToAgenda(true);
                    return true;
                }
            });

        unsubscribeButton
            .setIcon(R.drawable.close)
            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    subscribeToAgenda(false);
                    return true;
                }
            });
    }

    /**
     * Set the right subscribe/unsubscribe buttons in the case that registration of the event
     * currently on display is closed until further notice
     * @param subscribeButton       Reference to the subscribe button object
     * @param unsubscribeButton     Reference to the unsubscribe button object
     */
    private void prepareSubscribeButtonsForNoRegistration(MenuItem subscribeButton, MenuItem unsubscribeButton) {
        subscribeButton
            .setIcon(R.drawable.plus_disabled)
            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Toast.makeText(
                            AgendaActivity.this,
                            R.string.agenda_prepublished_event_registration_closed,
                            Toast.LENGTH_LONG).show();

                    return true;
                }
            });
        unsubscribeButton
            .setIcon(R.drawable.close_disabled)
            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Toast.makeText(
                            AgendaActivity.this,
                            R.string.agenda_prepublished_event_registration_closed,
                            Toast.LENGTH_LONG).show();

                    return true;
                }
            });
    }

    /**
     * Sets an alarm for 30 minutes before the start time of this event.
     *
     * @param item Event to set the alarm for
     */
    private void setAlarmForEvent(AgendaItem item) {
        DateTime time = item.getStart().minusMinutes(30);
        Log.e("AgendaDetailTabs", "Setting alarm for id: " + item.getId() + " at time: " + time.toString());
        Gson gson = new Gson();

        Intent myIntent = new Intent(this, OnetimeAlarmReceiver.class);
        myIntent.putExtra("item", gson.toJson(item));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, item.getId(), myIntent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, time.getMillis(), pendingIntent);
    }

    /**
     * Remove a previously set alarm for an event
     * @param item  Event to remove a previous alarm for
     */
    private void unsetAlarmForEvent(AgendaItem item) {
        Log.e("AgendaDetailTabs", "Unsetting alarm for id: " + item.getId());
        Gson gson = new Gson();

        Intent myIntent = new Intent(this, OnetimeAlarmReceiver.class);
        myIntent.putExtra("item", gson.toJson(item));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, item.getId(), myIntent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    /**
     * Subscribe or unsubscribe the current user as a participant to the agenda item currently
     * on display
     * @param subscribe True for subscribe, false for unsubscribe
     */
    private void subscribeToAgenda(boolean subscribe) {
        if(item.getMaxregistrations() != null && item.getMaxregistrations() == 0) {
            // Don't allow subscribing if max registrations is 0
            return;
        }

        if (subscribe) {
            DialogFragment newFragment = new SubscribeDialogFragment();
            Bundle args = new Bundle();
            args.putSerializable("agenda_item", item);
            newFragment.setArguments(args);
            newFragment.show(getSupportFragmentManager(), "agenda_subscribe");
        } else {
            if (item.getHasUnregisterDeadline()) {
                DateTime deadline = new DateTime(item.getUnregisterDeadline());
                if (!deadline.isAfterNow()) {
                    EventBus.getDefault().post(new ErrorEvent(new Error() {

                        @Override
                        public String getMessage() {
                            return getString(R.string.agenda_unsubscribe_deadline_past);
                        }

                    }));

                    return; // don't still try to unsubscribe
                }
            }

            // no deadline for unsubscribing
            Log.d("MainActivity", "unsubscribing for:" + item.getId());

            Services.getInstance().agendaService.unsubscribe(item.getId())
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
        Toast.makeText(this, R.string.agenda_toast_added_to_calendar, Toast.LENGTH_SHORT).show();
    }

    private void removeFromCalendar() {
        if (CalendarHelper.getInstance().removeItemFromCalendar(item))
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

    // TODO never used, in theory replaced by newer stuff? Can this be made more decent
    public void onEventMainThread(AgendaItemSubscribedEvent event) {
        item.setParticipants(event.subscribed.getParticipants());
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
