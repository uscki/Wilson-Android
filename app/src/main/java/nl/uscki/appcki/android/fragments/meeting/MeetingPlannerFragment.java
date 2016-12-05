package nl.uscki.appcki.android.fragments.meeting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import nl.uscki.appcki.android.MainActivity;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.fragments.meeting.adapter.DaySlots;
import nl.uscki.appcki.android.fragments.meeting.adapter.MeetingPreferenceDayAdapter;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;

/**
 * Created by peter on 7/16/16.
 */
public class MeetingPlannerFragment extends Fragment {
    RecyclerView recyclerView;
    MeetingItem item;
    Integer meetingItemId;

    public MeetingPlannerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Gson gson = new Gson();
        item = gson.fromJson(getArguments().getString("item"), MeetingItem.class);
        meetingItemId = item.getMeeting().getId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MainActivity.currentScreen = MainActivity.Screen.MEETING_PLANNER;
        View view = inflater.inflate(R.layout.fragment_meeting_planner, container, false);
        if (view instanceof RecyclerView) {
            recyclerView = (RecyclerView) view;
            //recyclerView.setAdapter(new MeetingPreferenceDayAdapter(new ArrayList<DaySlots>()));
            recyclerView.setAdapter(new MeetingPreferenceDayAdapter(DaySlots.fromSlots(item.getSlots())));
           /* Services.getInstance().meetingService.get(meetingItemId).enqueue(new Callback<MeetingItem>() {
                @Override
                public void onResponse(Call<MeetingItem> call, Response<MeetingItem> response) {
                    recyclerView.setAdapter(new MeetingPreferenceDayAdapter(DaySlots.fromSlots(response.body().getSlots())));
                }

                @Override
                public void onFailure(Call<MeetingItem> call, Throwable t) {
                    Log.d("MeetingPlannerFragment", "Failed to get meeting item!");
                }
            });*/

        }
        return view;
    }
}
