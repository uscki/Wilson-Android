package nl.uscki.appcki.android.fragments.meeting;


import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.meeting.adapter.MeetingDetailAdapter;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeetingDetailTabsFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;

    MeetingItem item;

    public static final int ITEM = 0;
    public static final int AANWEZIG = 1;
    public static final int AFWEZIG = 2;

    private Callback<MeetingItem> meetingCallback = new Callback<MeetingItem>() {
        @Override
        public void onSucces(Response<MeetingItem> response) {
            if(response.body() != null) {
                item = response.body();

                if (item.getMeeting().getStartdate() != null) {
                    tabLayout.addTab(tabLayout.newTab().setText("Overzicht"));
                    tabLayout.addTab(tabLayout.newTab().setText("Aanwezig"));
                    tabLayout.addTab(tabLayout.newTab().setText("Afwezig"));
                    setHasOptionsMenu(true);
                } else {
                    tabLayout.addTab(tabLayout.newTab().setText("Planner"));
                    tabLayout.addTab(tabLayout.newTab().setText("Gereageerd"));
                    tabLayout.addTab(tabLayout.newTab().setText("Niet gereageerd"));
                }

                viewPager.setAdapter(new MeetingDetailAdapter(getFragmentManager(), item));
            } else {
                //// TODO: 11/24/16 error handling
            }
        }
    };

    public MeetingDetailTabsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.currentScreen = MainActivity.Screen.MEETING_DETAIL;

        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_tabs, container, false);

        int id = -1;
        if (getArguments() != null) {
            id = getArguments().getInt("id");
        }

        Services.getInstance().meetingService.get(id).enqueue(meetingCallback);

        tabLayout = (TabLayout) inflatedView.findViewById(R.id.tabLayout);

        viewPager = (ViewPager) inflatedView.findViewById(R.id.viewpager);

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
