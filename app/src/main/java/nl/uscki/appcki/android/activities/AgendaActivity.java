package nl.uscki.appcki.android.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.net.ConnectException;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.error.ConnectionError;
import nl.uscki.appcki.android.error.Error;
import nl.uscki.appcki.android.events.AgendaItemSubscribedEvent;
import nl.uscki.appcki.android.events.ErrorEvent;
import nl.uscki.appcki.android.fragments.agenda.AgendaDetailAdapter;
import nl.uscki.appcki.android.fragments.agenda.AgendaDetailFragment;
import nl.uscki.appcki.android.fragments.agenda.SubscribeDialogFragment;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.generated.agenda.AgendaParticipant;
import nl.uscki.appcki.android.generated.agenda.AgendaParticipantLists;
import nl.uscki.appcki.android.helpers.UserHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgendaActivity extends BasicActivity {
    AgendaItem item;
    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;

    boolean foundUser = false;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if we're running on Android 5.0 or higher
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        } else {
            // Implement this feature without material design
        }*/

        setContentView(R.layout.activity_agenda);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MainActivity.currentScreen = MainActivity.Screen.AGENDA_DETAIL;

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Agenda"));
        tabLayout.addTab(tabLayout.newTab().setText("Deelnemers"));
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        if (getIntent().getBundleExtra("item") != null) {
            Gson gson = new Gson();
            item = gson.fromJson(getIntent().getBundleExtra("item").getString("item"), AgendaItem.class);
            if (item == null) {
                finish();
            }

            viewPager.setAdapter(new AgendaDetailAdapter(getSupportFragmentManager(), item));

            for (AgendaParticipant part : item.getParticipants()) {
                if (part.getPerson().getId() != null && UserHelper.getInstance().getPerson().getId() != null) {
                    if (part.getPerson().getId().equals(UserHelper.getInstance().getPerson().getId())) {
                        foundUser = true;
                    }
                }
            }
        } else {
            // the item is no longer loaded so we can't open this activity, thus we'll close it
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

        // verander visibility pas als we in een detail view zitten
        if(foundUser) {
            menu.findItem(R.id.action_agenda_subscribe).setVisible(false);
            menu.findItem(R.id.action_agenda_unsubscribe).setVisible(true);
        } else {
            menu.findItem(R.id.action_agenda_subscribe).setVisible(true);
            menu.findItem(R.id.action_agenda_unsubscribe).setVisible(false);
        }

        menu.findItem(R.id.action_agenda_unsubscribe).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                subscribeToAgenda(false);
                return true;
            }
        });
        menu.findItem(R.id.action_agenda_subscribe).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                subscribeToAgenda(true);
                return true;
            }
        });

        this.menu = menu;

        return super.onCreateOptionsMenu(menu);
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
            Services.getInstance().agendaService.unsubscribe(AgendaDetailFragment.item.getId()).enqueue(new Callback<AgendaParticipantLists>() {
                @Override
                public void onResponse(Call<AgendaParticipantLists> call, Response<AgendaParticipantLists> response) {
                    EventBus.getDefault().post(new AgendaItemSubscribedEvent(response.body(), true));
                }

                @Override
                public void onFailure(Call<AgendaParticipantLists> call, Throwable t) {
                    if (t instanceof ConnectException) {
                        new ConnectionError(t); // handle connection error in MainActivity
                    } else {
                        throw new RuntimeException(t);
                    }
                }
            });
        }
    }

    // EVENT HANDLING

    public void onEventMainThread(AgendaItemSubscribedEvent event) {
        if(!event.showSubscribe) {
            menu.findItem(R.id.action_agenda_subscribe).setVisible(false);
            menu.findItem(R.id.action_agenda_unsubscribe).setVisible(true);
        } else {
            menu.findItem(R.id.action_agenda_subscribe).setVisible(true);
            menu.findItem(R.id.action_agenda_unsubscribe).setVisible(false);
        }
    }
}
