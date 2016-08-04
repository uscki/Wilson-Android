package me.blackwolf12333.appcki.fragments.meeting;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import me.blackwolf12333.appcki.MainActivity;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.fragments.meeting.adapter.MeetingDetailAdapter;
import me.blackwolf12333.appcki.generated.meeting.MeetingItem;

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
