package me.blackwolf12333.appcki.fragments.agenda;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.net.ConnectException;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.MainActivity;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.Services;
import me.blackwolf12333.appcki.error.ConnectionError;
import me.blackwolf12333.appcki.events.AgendaItemSubscribedEvent;
import me.blackwolf12333.appcki.fragments.HomeSubFragments;
import me.blackwolf12333.appcki.generated.agenda.AgendaItem;
import me.blackwolf12333.appcki.generated.agenda.AgendaParticipant;
import me.blackwolf12333.appcki.generated.agenda.Subscribers;
import me.blackwolf12333.appcki.helpers.UserHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgendaDetailTabsFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;

    AgendaItem item;

    public static final int AGENDA = 0;
    public static final int DEELNEMERS = 1;

    public AgendaDetailTabsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.currentScreen = MainActivity.Screen.AGENDA_DETAIL;

        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_agenda_detail_tabs, container, false);

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

        // setup menu listeners here, before determining what menu item to show, because once either one is used
        // it's replaced with the other.
        HomeSubFragments.m.findItem(R.id.action_agenda_unsubscribe).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                subscribeToAgenda(false);
                return true;
            }
        });

        HomeSubFragments.m.findItem(R.id.action_agenda_subscribe).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                subscribeToAgenda(true);
                return true;
            }
        });

        boolean foundUser = false;
        for (AgendaParticipant part : item.getParticipants()) {
            if (part.getPerson().getName().equals(UserHelper.getInstance().getPerson().getName())) {
                foundUser = true;
                HomeSubFragments.m.findItem(R.id.action_agenda_unsubscribe).setVisible(true);
            }
        }
        if (!foundUser) {
            HomeSubFragments.m.findItem(R.id.action_agenda_subscribe).setVisible(true);
        }

        return inflatedView;
    }

    private void subscribeToAgenda(boolean subscribe) {
        if(subscribe) {
            DialogFragment newFragment = new SubscribeDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", item.getId());
            newFragment.setArguments(args);
            newFragment.show(getFragmentManager(), "agenda_subscribe");
        } else {
            Log.d("MainActivity", "unsubscribing for:" + AgendaDetailFragment.item.getId());
            Services.getInstance().agendaService.unsubscribe(AgendaDetailFragment.item.getId()).enqueue(new Callback<Subscribers>() {
                @Override
                public void onResponse(Call<Subscribers> call, Response<Subscribers> response) {
                    //TODO
                    EventBus.getDefault().post(new AgendaItemSubscribedEvent(null)); // TODO: 6/29/16 dirty hack to get the right action in the menu in AgendaDetailTabsFragment
                }

                @Override
                public void onFailure(Call<Subscribers> call, Throwable t) {
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
        if(event.subscribed != null) {
            HomeSubFragments.m.findItem(R.id.action_agenda_subscribe).setVisible(false);
            HomeSubFragments.m.findItem(R.id.action_agenda_unsubscribe).setVisible(true);
        } else {
            HomeSubFragments.m.findItem(R.id.action_agenda_subscribe).setVisible(true);
            HomeSubFragments.m.findItem(R.id.action_agenda_unsubscribe).setVisible(false);
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
