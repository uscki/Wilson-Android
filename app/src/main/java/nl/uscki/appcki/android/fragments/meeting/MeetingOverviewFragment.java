package nl.uscki.appcki.android.fragments.meeting;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.adapters.MeetingItemAdapter;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;
import nl.uscki.appcki.android.generated.meeting.MeetingOverview;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeetingOverviewFragment extends PageableFragment<MeetingOverview> {
    public MeetingOverviewFragment() {
        // Required empty public constructor
    }

    private final int MEETING_PAGE_SIZE = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        setAdapter(new MeetingItemAdapter(new ArrayList<MeetingItem>()));
        Services.getInstance().meetingService.getMeetingCollection(page, MEETING_PAGE_SIZE).enqueue(callback);
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
    protected int getPageSize() {
        return MEETING_PAGE_SIZE;
    }

    @Override
    public void onScrollRefresh() {
        Services.getInstance().meetingService.getMeetingCollection(page, MEETING_PAGE_SIZE).enqueue(callback);
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().meetingService.getMeetingCollection(page, MEETING_PAGE_SIZE).enqueue(callback);
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.meeting_no_new_meetings);
    }
}
