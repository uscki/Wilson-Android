package me.blackwolf12333.appcki.fragments.agenda;


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
import me.blackwolf12333.appcki.generated.agenda.AgendaItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgendaDetailTabsFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;

    public static final int AGENDA = 0;
    public static final int DEELNEMERS = 1;

    public AgendaDetailTabsFragment() {
        // Required empty public constructor
        MainActivity.currentScreen = MainActivity.Screen.AGENDA_DETAIL;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_agenda_detail_tabs, container, false);

        tabLayout = (TabLayout) inflatedView.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Agenda"));
        tabLayout.addTab(tabLayout.newTab().setText("Deelnemers"));
        viewPager = (ViewPager) inflatedView.findViewById(R.id.viewpager);

        if (getArguments() != null) {
            Gson gson = new Gson();
            AgendaItem item = gson.fromJson(getArguments().getString("item"), AgendaItem.class);
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

        return inflatedView;
    }

}
