package nl.uscki.appcki.android.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.util.Locale;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.error.Error;
import nl.uscki.appcki.android.events.AgendaItemSubscribedEvent;
import nl.uscki.appcki.android.events.DetailItemUpdatedEvent;
import nl.uscki.appcki.android.events.ErrorEvent;
import nl.uscki.appcki.android.events.ServerErrorEvent;
import nl.uscki.appcki.android.fragments.agenda.AgendaDetailAdapter;
import nl.uscki.appcki.android.fragments.agenda.AgendaDetailTabsFragment;
import nl.uscki.appcki.android.fragments.agenda.SubscribeDialogFragment;
import nl.uscki.appcki.android.fragments.comments.CommentsFragment;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.generated.agenda.AgendaParticipantLists;
import nl.uscki.appcki.android.generated.agenda.AgendaUserParticipation;
import nl.uscki.appcki.android.helpers.AgendaSubscribedHelper;
import nl.uscki.appcki.android.helpers.PermissionHelper;
import nl.uscki.appcki.android.helpers.UserHelper;
import nl.uscki.appcki.android.helpers.calendar.CalendarHelper;
import nl.uscki.appcki.android.services.OnetimeAlarmReceiver;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class AgendaActivity extends BasicActivity {
    public static final String PARAM_AGENDA_ID = "nl.uscki.appcki.android.activities.param.AGENDA_ID";

    public static final String ACTION_AGENDA_MAIN = "nl.uscki.appcki.android.activities.agenda.action.MAIN";
    public static final String ACTION_AGENDA_PARTICIPANTS = "nl.uscki.appcki.android.activities.agenda.action.PARTICIPANT_LIST";

    protected AgendaItem item;
    private int agendaId = -1;
    AgendaDetailAdapter fragmentAdapter;

    CollapsingToolbarLayout toolbarLayout;
    TabLayout tabLayout;
    ViewPager viewPager;
    ImageView poster;
    AppBarLayout appBarLayout;
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

    private void updateTabTitleCounts() {
        if(this.item != null) {
            updateTabTitleCount(AgendaDetailTabsFragment.DEELNEMERS, R.string.tab_agenda_participants, this.item.getParticipants().size());
            updateTabTitleCount(AgendaDetailTabsFragment.COMMENTS, R.string.comments, this.item.getTotalComments());
        }
    }

    private void updateTabTitleCount(int tabIndex, int stringResource, int count) {
        TabLayout.Tab t = tabLayout.getTabAt(tabIndex);
        if(t != null) {
            String title = String.format(
                    Locale.getDefault(),
                    "%d %s",
                    count,
                    getString(stringResource)
            );
            t.setText(title);
        }
    }

    /**
     * Once the item is loaded, populate the view with the agenda information
     */
    private void setupItem() {
        if(item == null) return;

        EventBus.getDefault().post(new DetailItemUpdatedEvent<>(item));

        if(item.getPosterid() != null && item.getPosterid() >= 0) {
            Services.getInstance().mediaService.file(item.getPosterid(), "normal").enqueue(posterLoadedCallback);
        } else {
            this.appBarLayout.setExpanded(false);
        }

        if(toolbarLayout != null) {
            this.toolbarLayout.setTitle(this.item.getTitle());
        }

        foundUser = item.getUserParticipation() != null &&
                (item.getUserParticipation().isAttends() || item.getUserParticipation().isBackuplist());

        setSubscribeButtons();
        setExportButtons();
        updateTabTitleCounts();
    }

    Callback<ResponseBody> posterLoadedCallback = new Callback<ResponseBody>() {

        @Override
        public void onSucces(Response<ResponseBody> response) {
            if(response == null) {
                appBarLayout.setExpanded(false);
            } else {
                Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());

                float scale = toolbarLayout.getWidth() / (float) bitmap.getWidth();

                int bitmapWidth = Math.round(scale * bitmap.getWidth());
                int bitmapHeight = Math.round(scale * bitmap.getHeight());
                int preferredToolbarHeight = toolbarLayout.getHeight();

                if(bitmapHeight > preferredToolbarHeight) {
                    // Don't scale to height, will take too much of vbox
                    poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else {
                    poster.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    toolbarLayout.getLayoutParams().height = bitmapHeight;
                }

                BitmapDrawable drawable = new BitmapDrawable(AgendaActivity.this.getResources(), bitmap);
                drawable.setBounds(0, 0, bitmapWidth, bitmapHeight);
                poster.setImageDrawable(drawable);
            }
        }

        @Override
        public void onError(Response<ResponseBody> response) {
            appBarLayout.setExpanded(false);
        }
    };

    /**
     * Create one adapter that can be used from now on
     */
    private void createAdapter() {
        fragmentAdapter = new AgendaDetailAdapter(getSupportFragmentManager(), this.agendaId);
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

        setTabByIntent();
    }

    private void setTabByIntent() {
        Intent intent = getIntent();

        if(intent != null && intent.getAction() != null) {
            // Tab defaults to details
            int tab;
            switch (intent.getAction()) {
                case ACTION_AGENDA_MAIN:
                    // Intentional carry-over
                default:
                    tab = AgendaDetailAdapter.AGENDA_DETAILS_TAB_POSITION;
                    break;
                case ACTION_AGENDA_PARTICIPANTS:
                    tab = AgendaDetailAdapter.AGENDA_PARTICIPANTS_TAB_POSITION;
                    break;
                case CommentsFragment.ACTION_VIEW_COMMENTS:
                    tab = AgendaDetailAdapter.AGENDA_COMMENTS_TAB_POSITION;
                    break;
            }

            viewPager.setCurrentItem(tab);
            tabLayout.setScrollPosition(tab, 0f, false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_agenda);

        toolbarLayout = findViewById(R.id.agenda_collapsing_toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewpager);
        poster = findViewById(R.id.agenda_poster);
        appBarLayout = findViewById(R.id.appbar);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MainActivity.currentScreen = MainActivity.Screen.AGENDA_DETAIL;

        if (getIntent().getBundleExtra("item") != null) {
            Gson gson = new Gson();
            item = gson.fromJson(getIntent().getBundleExtra("item").getString("item"), AgendaItem.class);
            Log.e(getClass().getSimpleName(), "WARNIGN! Used Bundle JSON to pass agenda item! Use ID instead!");
        } else if (getIntent().getStringExtra("item") != null) {
            Gson gson = new Gson();
            item = gson.fromJson(getIntent().getStringExtra("item"), AgendaItem.class);
            Log.e(getClass().getSimpleName(), "WARNIGN! Used StringExtra JSON to pass agenda item! Use ID instead!");
        } else if (getIntent().getIntExtra(PARAM_AGENDA_ID, -1) >= 0) {
            agendaId = getIntent().getIntExtra(PARAM_AGENDA_ID, -1);
        } else {
            // the item is no longer loaded so we can't open this activity, thus we'll close it
            Log.e(getClass().getSimpleName(), "Not loaded. Finish");
            finish();
        }

        if (UserHelper.getInstance().getCurrentUser() == null) {
            finish();
        }

        if(agendaId < 0 && item != null) {
            // Artifact of passing objects rather than only the ID.
            // TODO: Refactor agenda intentions to only pass ID, never a (serialized) agenda item
            agendaId = item.getId();
        }

        createAdapter();

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
     *
     * // TODO obnoxiously long method
     */
    private void setSubscribeButtons() {
        if(menu == null) return;

        MenuItem subscribe = menu.findItem(R.id.action_agenda_subscribe);
        MenuItem unsubscribe = menu.findItem(R.id.action_agenda_unsubscribe);

        // Make sure not to break app
        if(subscribe == null || unsubscribe == null) {
            Log.e(getClass().getSimpleName(), "Trying to set button behavior before menu is created");
            return;
        }

        if(item == null) {
            Log.e(getClass().getSimpleName(), "Trying to set button behavior before item is loaded");
            return;
        }

        // Initial checks
        boolean registered = this.item.getUserParticipation() != null &&
                (this.item.getUserParticipation().isAttends() || this.item.getUserParticipation().isBackuplist());
        boolean started = this.item.getStart().isBeforeNow();
        boolean registrationPassed = this.item.getHasDeadline() && this.item.getDeadline().isBeforeNow();
        boolean unregisterPassed = item.getHasUnregisterDeadline() && this.item.getUnregisterDeadline().isBeforeNow();
        boolean subscribeEnabled = true, unsubscribeEnabled = true;

        // Aggregation variables
        int onclickText = -1, registerIcon = R.drawable.plus, unregisterIcon = R.drawable.close;

        // Check if unregistration should be disabled
        if (unregisterPassed || (started && !this.item.getHasUnregisterDeadline())) {
            unregisterIcon = R.drawable.close_disabled;
            unsubscribeEnabled = false;
            onclickText = unregisterPassed ?
                    R.string.agenda_unsubscribe_deadline_passed : R.string.agenda_event_started;
        }

        // Check if registration should be disabled
        if (registrationPassed || (started && !this.item.getHasDeadline())) {
            registerIcon = R.drawable.plus_disabled;
            subscribeEnabled = false;
            onclickText = registrationPassed ?
                    R.string.agenda_subscribe_deadline_passed : R.string.agenda_event_started;
        }

        // Check if registration icon should be edit icon
        if (registered && (registrationPassed || (started && !this.item.getHasDeadline()))
        ) {
            registerIcon = R.drawable.ic_outline_edit_disabled_24px;
            onclickText = registrationPassed ?
                    R.string.agenda_subscribe_deadline_passed : R.string.agenda_event_started;
        } else if (registered) {
            registerIcon = R.drawable.ic_outline_edit_24px;
        }

        if(this.item.getMaxregistrations() != null && this.item.getMaxregistrations() == 0) {
            registerIcon = registered ? R.drawable.ic_outline_edit_disabled_24px : R.drawable.plus_disabled;
            onclickText = R.string.agenda_prepublished_event_registration_opens_later;
            subscribeEnabled = false;
        }

        // Mark icons based on calculated configuration
        subscribe.setIcon(registerIcon).setVisible(true);
        unsubscribe.setIcon(unregisterIcon).setVisible(registered);

        // Add proper listeners to menu buttons
        addSubscribeListener(subscribe, true, subscribeEnabled, onclickText);
        addSubscribeListener(unsubscribe, false, unsubscribeEnabled, onclickText);
    }

    private void addSubscribeListener(MenuItem menuItem, final boolean subscribe, boolean enabled, final int text) {
        if(!enabled && text >= 0) {
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Toast.makeText(
                            AgendaActivity.this,
                            text,
                            Toast.LENGTH_LONG).show();

                    return true;
                }
            });
        } else if (enabled) {
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    subscribeToAgenda(subscribe);
                    return true;
                }
            });
        }
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
            newFragment.show(getSupportFragmentManager(), "agenda_subscribe");
        } else {
            if (item.getHasUnregisterDeadline()) {
                DateTime deadline = new DateTime(item.getUnregisterDeadline());
                if (!deadline.isAfterNow()) {
                    EventBus.getDefault().post(new ErrorEvent(new Error() {

                        @Override
                        public String getMessage() {
                            return getString(R.string.agenda_unsubscribe_deadline_passed);
                        }

                    }));

                    return; // don't still try to unsubscribe
                }
            }

            if(item.getHasDeadline() && item.getDeadline().isBeforeNow()) {
                requestConfirm(getResources().getString(R.string.agenda_confirm_unsubscribe_deadline));
            } else if (item.getMaxregistrations() != null && item.getMaxregistrations() > 0 && item.getParticipants().size() >= item.getMaxregistrations() - 5) {
                // Arbitrary border but still nice
                requestConfirm(getResources().getString(
                        R.string.agenda_confirm_unsubscribe_backuplist,
                        this.item.getParticipants().size(),
                        this.item.getMaxregistrations()));
            } else {
                queUnregister();
            }

            // no deadline for unsubscribing
            Log.d("MainActivity", "unsubscribing for:" + item.getId());
        }
    }

    /**
     * Unregister current user from active event through API
     * Sends a AgendaItemSubscribedEvent on EventBus
     */
    private void queUnregister() {
        Services.getInstance().agendaService.unsubscribe(item.getId())
            .enqueue(new nl.uscki.appcki.android.api.Callback<ActionResponse<AgendaParticipantLists>>() {

                @Override
                public void onSucces(Response<ActionResponse<AgendaParticipantLists>> response) {
                    EventBus.getDefault()
                            .post(new AgendaItemSubscribedEvent(response.body().payload, true));
                }

                @Override
                public void onError(Response<ActionResponse<AgendaParticipantLists>> response) {
                    super.onError(response);
                    Toast.makeText(AgendaActivity.this, R.string.agenda_unsubscribe_failed, Toast.LENGTH_SHORT).show();
                }
            });
    }

    /**
     * Show a confirmation dialog that unsubscribes the user from active event when confirmed
     * @param message   Message to show in dialog
     */
    private void requestConfirm(String message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.agenda_confirm_unsubscribe_header)
                .setMessage(message)
                .setPositiveButton(R.string.agenda_confirm_unsubscribe_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        queUnregister();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
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

    /**
     * Triggered by unsubscribing in this activity, and by subscribing in the SubscribeDialogFragment
     * @param event
     */
    public void onEventMainThread(AgendaItemSubscribedEvent event) {
        this.item.setParticipants(event.subscribed.getParticipants());
        this.item.setBackupList(event.subscribed.getBackupList());
        if(!event.showSubscribe) {
            setAlarmForEvent(item);

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
            Toast.makeText(AgendaActivity.this, R.string.agenda_unsubscribe_confirmed, Toast.LENGTH_SHORT).show();
            unsetAlarmForEvent(item);

            // TODO UserParticipation should be updated by API; not like this
            this.item.getUserParticipation().setAnswer(null);
            this.item.getUserParticipation().setNote(null);
            this.item.getUserParticipation().setSubscribed(null);
        }
        updateSubscribedStatus(event.subscribed);
        setExportButtons();
        setSubscribeButtons();
        showSubscribeConfirmation(event.subscribed);
        EventBus.getDefault().post(new DetailItemUpdatedEvent<>(item));
        updateTabTitleCounts();
    }

    /**
     * Update subscribed status of user on current Agenda item, until refresh
     * @param nowSubscribedLists    List passed by (un)subscribe API call
     */
    // TODO this is really ugly, and should just be returned by API
    private void updateSubscribedStatus(AgendaParticipantLists nowSubscribedLists) {
        if(UserHelper.getInstance().getCurrentUser() != null) {
            AgendaUserParticipation participation = this.item.getUserParticipation();
            int status = AgendaSubscribedHelper.isSubscribed(nowSubscribedLists);

            if(status == AgendaSubscribedHelper.AGENDA_NOT_SUBSCRIBED) {
                participation.setBackuplist(false);
                participation.setAttends(false);
            } else if (status == AgendaSubscribedHelper.AGENDA_ON_BACKUP_LIST) {
                participation.setBackuplist(true);
                participation.setAttends(false);
            } else {
                participation.setAttends(true);
                participation.setBackuplist(false);
            }
        }
    }

    private void showSubscribeConfirmation(AgendaParticipantLists nowSubscribedLists) {
        int messageResourceId = -1;

        if(UserHelper.getInstance().getCurrentUser() != null) {

            // TODO userParticipation is not a response payload to (un)subscribing, so we still need this
            int status = AgendaSubscribedHelper.isSubscribed(nowSubscribedLists);
            int previousStatus = AgendaSubscribedHelper.isSubscribed(item);

            if(status == previousStatus) {
                messageResourceId = R.string.agenda_subscribe_changed;
            } else if (status == AgendaSubscribedHelper.AGENDA_SUBSCRIBED) {

                messageResourceId = R.string.agenda_subscribe_confirmed;
            } else if (status == AgendaSubscribedHelper.AGENDA_ON_BACKUP_LIST) {
                messageResourceId = R.string.agenda_subscribe_backuplist;
            }

            if (status > AgendaSubscribedHelper.AGENDA_NOT_SUBSCRIBED) {
                Toast.makeText(this, messageResourceId, Toast.LENGTH_SHORT).show();
            } else {
                Log.e(getClass().getSimpleName(), "User not found on either list. No message shown");
            }
        }
    }
}
