package nl.uscki.appcki.android.fragments.home;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.events.SwitchTabEvent;
import nl.uscki.appcki.android.fragments.adapters.HomeViewPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    public static final int NEWS = 0;
    public static final int AGENDA = 1;
    public static final int ROEPHOEK = 2;

    ViewPager viewPager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_tabs, container, false);
        viewPager = inflatedView.findViewById(R.id.viewpager);

        viewPager.setAdapter(new HomeViewPagerAdapter(getContext(), getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));

        TabLayout tabLayout = inflatedView.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setCurrentScreen(position);
                EventBus.getDefault().post(new SwitchTabEvent(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        int index = -1;
        if(getArguments() != null) index = getArguments().getInt("index");
        else if (savedInstanceState != null) index = savedInstanceState.getInt("index");

        if (index > -1) {
            setCurrentScreen(index);
            viewPager.setCurrentItem(index);
        }

        return inflatedView;
    }

    private void setCurrentScreen(int index) {
        switch (index) {
            case NEWS:
                MainActivity.currentScreen = MainActivity.Screen.NEWS;
                break;
            case AGENDA:
                MainActivity.currentScreen = MainActivity.Screen.AGENDA;
                break;
            case ROEPHOEK:
                MainActivity.currentScreen = MainActivity.Screen.ROEPHOEK;
                break;
        }
    }

    // EVENT HANDLING

    public void onEventMainThread(SwitchTabEvent event) {
        setCurrentScreen(event.index);
        viewPager.setCurrentItem(event.index);
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        MainActivity.setHomeScreenExists();
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        MainActivity.setHomescreenDestroyed();
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("index", this.viewPager.getCurrentItem());
    }
}
