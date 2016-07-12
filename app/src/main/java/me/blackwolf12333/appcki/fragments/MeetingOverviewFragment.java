package me.blackwolf12333.appcki.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.api.VolleyMeeting;
import me.blackwolf12333.appcki.events.MeetingOverviewEvent;
import me.blackwolf12333.appcki.fragments.adapters.MeetingItemAdapter;
import me.blackwolf12333.appcki.generated.meeting.MeetingItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeetingOverviewFragment extends PageableFragment {


    public MeetingOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAdapter(new MeetingItemAdapter(new ArrayList<MeetingItem>()));
        VolleyMeeting.getInstance().getMeetingOverview();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onScrollRefresh() {
        //VolleyMeeting.getInstance().getMeetingOverview();
    }

    @Override
    public void onSwipeRefresh() {
        VolleyMeeting.getInstance().getMeetingOverview();
    }

    // EVENT HANDLING
    public void onEventMainThread(MeetingOverviewEvent event) {
        swipeContainer.setRefreshing(false);
        if (getAdapter() instanceof MeetingItemAdapter) {
            Log.d("MeetingOverviewFragment", event.overview.getContent().toString());
            getAdapter().update(event.overview.getContent());
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
