package me.blackwolf12333.appcki.fragments.meetings;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.VolleyMeeting;
import me.blackwolf12333.appcki.events.MeetingEvent;
import me.blackwolf12333.appcki.events.MeetingOverviewEvent;
import me.blackwolf12333.appcki.events.ShowProgressEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class MeetingFragment extends APIFragment {
    VolleyMeeting meetingAPI = new VolleyMeeting();
    private View view;

    public MeetingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        meetingAPI.getMeetingOverview();
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_meeting_list, container, false);
        return view;
    }

    @Override
    public void refresh() {
        meetingAPI.getMeetingOverview();
    }

    public void onEventMainThread(MeetingOverviewEvent event) {
        EventBus.getDefault().post(new ShowProgressEvent(false));
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setAdapter(new MeetingRecyclerViewAdapter(event.overview.getContent()));
        }
    }

    public void onEventMainThread(MeetingEvent event) {
        EventBus.getDefault().post(new ShowProgressEvent(false));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
