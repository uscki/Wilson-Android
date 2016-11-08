package nl.uscki.appcki.android.fragments.meeting;


import android.Manifest;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.vistrav.ask.Ask;

import nl.uscki.appcki.android.MainActivity;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.fragments.meeting.adapter.MeetingDetailAdapter;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeetingDetailTabsFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;

    MeetingItem item;

    public static final int PLANNER = 0;
    public static final int AANWEZIG = 1;
    public static final int AFWEZIG = 2;

    public MeetingDetailTabsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.currentScreen = MainActivity.Screen.MEETING_DETAIL;

        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_tabs, container, false);

        if (getArguments() != null) {
            Gson gson = new Gson();
            item = gson.fromJson(getArguments().getString("item"), MeetingItem.class);
        }

        tabLayout = (TabLayout) inflatedView.findViewById(R.id.tabLayout);
        if (item.getMeeting().getActualTime() != null) {
            tabLayout.addTab(tabLayout.newTab().setText("Overzicht"));
            tabLayout.addTab(tabLayout.newTab().setText("Aanwezig"));
            tabLayout.addTab(tabLayout.newTab().setText("Afwezig"));
            setHasOptionsMenu(true);
        } else {
            tabLayout.addTab(tabLayout.newTab().setText("Planner"));
            tabLayout.addTab(tabLayout.newTab().setText("Gereageerd"));
            tabLayout.addTab(tabLayout.newTab().setText("Niet gereageerd"));
        }

        viewPager = (ViewPager) inflatedView.findViewById(R.id.viewpager);

        viewPager.setAdapter(new MeetingDetailAdapter(getFragmentManager(), item));

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
        return inflatedView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.meeting_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_meeting_save) {
            //TODO save item in agenda
            Ask.on(this.getActivity())
                    .forPermissions(Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withRationales("Location permission need for map to work properly",
                            "In order to save file you will need to grant storage permission") //optional
                    .go();
            CalendarHelper.getInstance().addMeeting(this.item);
            return true; // consume event
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
       // EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
       // EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
