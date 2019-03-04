package nl.uscki.appcki.android.fragments.meeting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.activities.MeetingActivity;
import nl.uscki.appcki.android.events.DetailItemUpdatedEvent;
import nl.uscki.appcki.android.fragments.meeting.adapter.DaySlots;
import nl.uscki.appcki.android.fragments.meeting.adapter.MeetingPreferenceDayAdapter;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;

/**
 * Created by peter on 7/16/16.
 */
public class MeetingPlannerFragment extends Fragment {

    @BindView(R.id.planner_list)
    RecyclerView recyclerView;

    MeetingItem item;

    public MeetingPlannerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.currentScreen = MainActivity.Screen.MEETING_PLANNER;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_meeting_planner, container, false);
        ButterKnife.bind(this, view);

        MeetingActivity activity = (MeetingActivity) getActivity();
        if(activity != null && activity.getMeetingItem() != null) {
            this.item = activity.getMeetingItem();
            populate();
        }

        return view;
    }

    private void populate() {
        if(this.item == null || recyclerView == null) return;

        this.recyclerView.setAdapter(
                new MeetingPreferenceDayAdapter(
                        (AppCompatActivity) getActivity(),
                        DaySlots.fromSlots(this.item.getSlots())
                )
        );
    }

    public void onEventMainThread(DetailItemUpdatedEvent<MeetingItem> event) {
        this.item = event.getUpdatedItem();
        populate();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
}
