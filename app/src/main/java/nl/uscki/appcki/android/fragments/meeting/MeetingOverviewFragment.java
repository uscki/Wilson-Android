package nl.uscki.appcki.android.fragments.meeting;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.ConnectException;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.MainActivity;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.error.ConnectionError;
import nl.uscki.appcki.android.events.MeetingOverviewEvent;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.adapters.MeetingItemAdapter;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;
import nl.uscki.appcki.android.generated.meeting.MeetingOverview;
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

    private final int MEETING_PAGE_SIZE = 4;

    private Callback<MeetingOverview> overviewCallback = new Callback<MeetingOverview>() {
        @Override
        public void onResponse(Call<MeetingOverview> call, Response<MeetingOverview> response) {
            swipeContainer.setRefreshing(false);
            if (loading) {
                loading = false;
                if (getAdapter() instanceof MeetingItemAdapter) {
                    if (response.body() != null) {
                        getAdapter().addItems(response.body().getContent());
                    }
                }
            } else {
                if (getAdapter() instanceof MeetingItemAdapter) {
                    if (response.body() != null) {
                        getAdapter().update(response.body().getContent());
                    }
                }
            }
        }

        @Override
        public void onFailure(Call<MeetingOverview> call, Throwable t) {
            if (t instanceof ConnectException) {
                new ConnectionError(t); // handle connection error in MainActivity
            } else {
                throw new RuntimeException(t);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        MainActivity.currentScreen = MainActivity.Screen.MEETING_OVERVIEW;

        setAdapter(new MeetingItemAdapter(new ArrayList<MeetingItem>()));
        Services.getInstance().meetingService.overview(page, MEETING_PAGE_SIZE).enqueue(overviewCallback);
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
        Services.getInstance().meetingService.overview(page, MEETING_PAGE_SIZE).enqueue(overviewCallback);
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().meetingService.overview(page, MEETING_PAGE_SIZE).enqueue(overviewCallback);
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
