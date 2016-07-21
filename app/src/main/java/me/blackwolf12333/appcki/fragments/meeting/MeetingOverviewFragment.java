package me.blackwolf12333.appcki.fragments.meeting;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.MainActivity;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.Services;
import me.blackwolf12333.appcki.events.MeetingOverviewEvent;
import me.blackwolf12333.appcki.fragments.PageableFragment;
import me.blackwolf12333.appcki.fragments.adapters.MeetingItemAdapter;
import me.blackwolf12333.appcki.generated.meeting.MeetingItem;
import me.blackwolf12333.appcki.generated.meeting.MeetingOverview;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        setHasOptionsMenu(true);

        MainActivity.currentScreen = MainActivity.Screen.MEETING_OVERVIEW;

        setAdapter(new MeetingItemAdapter(new ArrayList<MeetingItem>()));
        Services.getInstance().meetingService.overview().enqueue(new Callback<MeetingOverview>() {
            @Override
            public void onResponse(Call<MeetingOverview> call, Response<MeetingOverview> response) {
                swipeContainer.setRefreshing(false);
                if (getAdapter() instanceof MeetingItemAdapter) {
                    getAdapter().update(response.body().getContent());
                }
            }

            @Override
            public void onFailure(Call<MeetingOverview> call, Throwable t) {
                Log.d("MeetingOverviewFragment", "Failed to get the meeting overview");
                Log.d("MeetingOverviewFragment", t.getMessage());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onScrollRefresh() {
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().meetingService.overview().enqueue(new Callback<MeetingOverview>() {
            @Override
            public void onResponse(Call<MeetingOverview> call, Response<MeetingOverview> response) {
                swipeContainer.setRefreshing(false);
                if (getAdapter() instanceof MeetingItemAdapter) {
                    getAdapter().update(response.body().getContent());
                }
            }

            @Override
            public void onFailure(Call<MeetingOverview> call, Throwable t) {

            }
        });
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
