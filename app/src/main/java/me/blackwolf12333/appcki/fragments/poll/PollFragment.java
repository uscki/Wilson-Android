package me.blackwolf12333.appcki.fragments.poll;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.events.PollEvent;
import me.blackwolf12333.appcki.events.PollVoteEvent;
import me.blackwolf12333.appcki.events.ShowProgressEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;
import me.blackwolf12333.appcki.generated.Poll;
import me.blackwolf12333.appcki.generated.PollOption;
import me.blackwolf12333.appcki.api.VolleyPoll;

/**
 * A simple {@link Fragment} subclass.
 */
public class PollFragment extends APIFragment {
    private VolleyPoll pollAPI = new VolleyPoll();
    private RadioGroup pollOptions;
    private TextView pollText;
    private ViewGroup view;
    private Poll currentPoll;
    private RecyclerView recyclerView;

    public PollFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pollAPI.getActivePoll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_poll, container, false);
        this.pollOptions = (RadioGroup) view.findViewById(R.id.poll_options);
        this.pollText = (TextView) view.findViewById(R.id.poll_text);

        return view;
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

    private void populateWithPoll(final Poll poll) {
        pollText.setText(poll.getPollItem().getTitle());
        pollOptions.removeAllViews(); // verwijder de polloptions van de vorige keer laden
        boolean voted = false;
        for(PollOption option : poll.getOptions()) {
            RadioButton button = new RadioButton(pollOptions.getContext());
            button.setText(option.getName());
            if(option.getId().equals(poll.getMyVote())) {
                voted = true;
                button.setChecked(true);
            }
            pollOptions.addView(button);
        }

        if(!voted) {
            this.pollOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if(poll.getMyVote() < 0) {
                        PollOption option = poll.getOptions().get(checkedId);
                        pollAPI.vote(option.getId());
                    }
                }
            });
        } else {
            showResults();
        }
    }

    private void showResults() { //TODO show results
        //pollOptions.removeAllViews();
        //pollOptions.setVisibility(View.GONE);
        //recyclerView.setVisibility(View.VISIBLE);

        pollText.setText(currentPoll.getPollItem().getTitle());


        //recyclerView.setAdapter(new PollOptionAdapter(currentPoll.getOptions()));
    }

    public void onEventMainThread(PollEvent event) {
        EventBus.getDefault().post(new ShowProgressEvent(false));
        currentPoll = event.poll;
        populateWithPoll(event.poll);
    }

    public void onEventMainThread(PollVoteEvent event) {
        showResults();
    }
}
