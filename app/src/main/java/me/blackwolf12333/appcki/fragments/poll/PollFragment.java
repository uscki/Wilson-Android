package me.blackwolf12333.appcki.fragments.poll;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.VolleyPoll;
import me.blackwolf12333.appcki.events.PollEvent;
import me.blackwolf12333.appcki.events.PollVotedEvent;
import me.blackwolf12333.appcki.events.ShowProgressEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;
import me.blackwolf12333.appcki.generated.poll.Poll;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * interface.
 */
public class PollFragment extends APIFragment {
    private RecyclerView recyclerView;
    private TextView question;
    private Poll currentPoll;
    VolleyPoll pollAPI = new VolleyPoll();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PollFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pollAPI.getActivePoll();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pollitem_list, container, false);
        question = (TextView) view.findViewById(R.id.question);
        if(view.findViewById(R.id.list_poll) instanceof RecyclerView) {
            this.recyclerView = (RecyclerView) view.findViewById(R.id.list_poll);
        }

        return view;
    }

    public void onEventMainThread(PollEvent event) {
        EventBus.getDefault().post(new ShowProgressEvent(false));
        question.setText(event.poll.getPollItem().getTitle());
        if(event.poll.getMyVote() == null) {
            recyclerView.setAdapter(new PollOptionAdapter(event.poll.getOptions()));
        } else {
            recyclerView.setAdapter(new PollResultAdapter(event.poll.getOptions()));
        }
        currentPoll = event.poll;
    }

    public void onEventMainThread(PollVotedEvent event) {
        recyclerView.setAdapter(new PollResultAdapter(currentPoll.getOptions()));
    }

    @Override
    public void refresh() {

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
