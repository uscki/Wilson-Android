package nl.uscki.appcki.android.fragments.agenda;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.net.ConnectException;

import de.greenrobot.event.EventBus;

import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.fragments.dialogs.ConfirmationDialog;
import nl.uscki.appcki.android.services.OnetimeAlarmReceiver;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.error.ConnectionError;
import nl.uscki.appcki.android.error.Error;
import nl.uscki.appcki.android.events.AgendaItemSubscribedEvent;
import nl.uscki.appcki.android.events.ErrorEvent;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.generated.agenda.AgendaParticipant;
import nl.uscki.appcki.android.generated.agenda.AgendaParticipantLists;
import nl.uscki.appcki.android.helpers.UserHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgendaDetailTabsFragment extends Fragment implements ConfirmationDialog.ConfirmationDialogListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private AgendaItem item;
    private Menu menu;

    private boolean foundUser = false;

    public static final int AGENDA = 0;
    public static final int DEELNEMERS = 1;

    public AgendaDetailTabsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.currentScreen = MainActivity.Screen.AGENDA_DETAIL;
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_tabs, container, false);

        tabLayout = (TabLayout) inflatedView.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Agenda"));
        tabLayout.addTab(tabLayout.newTab().setText("Deelnemers"));
        viewPager = (ViewPager) inflatedView.findViewById(R.id.viewpager);

        if (getArguments() != null) {
            Gson gson = new Gson();
            item = gson.fromJson(getArguments().getString("item"), AgendaItem.class);
            viewPager.setAdapter(new AgendaDetailAdapter(getFragmentManager(), item));
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

        for (AgendaParticipant part : item.getParticipants()) {
            if (part.getPerson().getId().equals(UserHelper.getInstance().getPerson().getId())) {
                foundUser = true;
            }
        }

        return inflatedView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.agenda_menu, menu);

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
                ConfirmationDialog dialog = new ConfirmationDialog();
                Bundle args = new Bundle();
                args.putInt("messageId", R.string.agenda_confirm_unsubscribe_message);
                args.putInt("positiveId", R.string.agenda_confirm_unsubscribe_positive);
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "");
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

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void subscribeToAgenda(boolean subscribe) {
        if(subscribe) {
            DialogFragment newFragment = new SubscribeDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", item.getId());
            newFragment.setArguments(args);
            newFragment.show(getFragmentManager(), "agenda_subscribe");
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
            Log.d("AgendaDetailTabs", "unsubscribing for:" + AgendaDetailFragment.item.getId());
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

    @Override
    public void onPositive() {
        subscribeToAgenda(false);
    }

    /**
     * Sets an alarm for 30 minutes before the start time of this event.
     * @param item
     */
    private void setAlarmForEvent(AgendaItem item) {
        DateTime time = item.getStart().minusMinutes(30);
        //time = new DateTime().plusMinutes(2); // FOR DEBUGGING PURPOSES
        Log.e("AgendaDetailTabs", "Setting alarm for id: " + item.getId() + " at time: " + time.toString());
        Gson gson = new Gson();

        Intent myIntent = new Intent(this.getActivity(), OnetimeAlarmReceiver.class);
        myIntent.putExtra("item", gson.toJson(item));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getActivity(), item.getId(), myIntent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager)this.getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, time.getMillis(), pendingIntent);
    }

    private void unsetAlarmForEvent(AgendaItem item) {
        Log.e("AgendaDetailTabs", "Unsetting alarm for id: " + item.getId());
        Gson gson = new Gson();

        Intent myIntent = new Intent(this.getActivity(), OnetimeAlarmReceiver.class);
        myIntent.putExtra("item", gson.toJson(item));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getActivity(), item.getId(), myIntent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager)this.getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    // EVENT HANDLING

    public void onEventMainThread(AgendaItemSubscribedEvent event) {
        if(event.showSubscribe) {
            unsetAlarmForEvent(item);
            menu.findItem(R.id.action_agenda_subscribe).setVisible(true);
            menu.findItem(R.id.action_agenda_unsubscribe).setVisible(false);
        } else{
            setAlarmForEvent(item);
            menu.findItem(R.id.action_agenda_subscribe).setVisible(false);
            menu.findItem(R.id.action_agenda_unsubscribe).setVisible(true);
        }
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
