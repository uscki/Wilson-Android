package nl.uscki.appcki.android.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.events.SwitchTabEvent;
import nl.uscki.appcki.android.fragments.adapters.HomeViewPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_tabs, container, false);

        tabLayout = (TabLayout) inflatedView.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Nieuws"));
        tabLayout.addTab(tabLayout.newTab().setText("Agenda"));
        tabLayout.addTab(tabLayout.newTab().setText("Roephoek"));
        viewPager = (ViewPager) inflatedView.findViewById(R.id.viewpager);

        viewPager.setAdapter(new HomeViewPagerAdapter(getFragmentManager()));
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

        if (getArguments() != null) {
            int index = getArguments().getInt("index");
            viewPager.setCurrentItem(index);
            tabLayout.setScrollPosition(index, 0f, false);
        }

        return inflatedView;
    }

    // EVENT HANDLING

    public void onEventMainThread(SwitchTabEvent event) {
        viewPager.setCurrentItem(event.index);
        tabLayout.setScrollPosition(event.index, 0f, false);
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
