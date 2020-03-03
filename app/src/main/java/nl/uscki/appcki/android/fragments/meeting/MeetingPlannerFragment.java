package nl.uscki.appcki.android.fragments.meeting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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

    RecyclerView recyclerView;
    CardView notesCard;
    TextView notesText;

    private MeetingItem item;

    public MeetingPlannerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.currentScreen = MainActivity.Screen.MEETING_PLANNER;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_meeting_planner, container, false);
        this.recyclerView = view.findViewById(R.id.planner_list);
        this.notesCard = view.findViewById(R.id.meeting_planner_notes_card);
        this.notesText = view.findViewById(R.id.meeting_planner_notes);

        MeetingActivity activity = (MeetingActivity) getActivity();
        if(activity != null && activity.getMeetingItem() != null) {
            this.item = activity.getMeetingItem();
            populate();
        }

        return view;
    }

    private void populate() {
        if(this.item == null || recyclerView == null) return;

        if(item.getMeeting().getPlannotes() != null && !item.getMeeting().getPlannotes().isEmpty()) {
            this.notesCard.setVisibility(View.VISIBLE);
            this.notesText.setText(item.getMeeting().getPlannotes().trim());
        } else {
            this.notesCard.setVisibility(View.GONE);
        }

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
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
