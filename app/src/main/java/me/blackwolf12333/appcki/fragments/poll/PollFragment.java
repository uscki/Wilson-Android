package me.blackwolf12333.appcki.fragments.poll;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

        this.recyclerView = (RecyclerView) view.findViewById(R.id.poll_options_list);

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
        pollOptions.removeAllViews(); // verwijder de polloptions van de vorige keer
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
                    if(poll.getMyVote() == null) {
                        PollOption option = poll.getOptions().get(checkedId);
                        // Maak een stemknop aan.
                        Button voteButton = (Button) view.findViewById(R.id.vote_button);
                        voteButton.setVisibility(View.VISIBLE);
                        voteButton.setOnTouchListener(new CustomListener(option));
                    }
                }
            });
        } else {
            showResults();
        }
    }

    private class CustomListener implements View.OnTouchListener {

        private PollOption option;

        public CustomListener(PollOption option) {
            this.option = option;
        }

        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                pollAPI.vote(option.getId());
                showResults();
                return true;
            }
            return false;
        }
    }

    private void showResults() { //TODO show results
        // Haalt keuzes weg
        pollOptions.removeAllViews();
        pollOptions.setVisibility(View.GONE);

        for(PollOption option : currentPoll.getOptions()) {
            TextView optionText = new TextView(pollOptions.getContext());
            optionText.setText(option.getName());

            pollOptions.addView(optionText);
        }
    }

    public void onEventMainThread(PollEvent event) {
        EventBus.getDefault().post(new ShowProgressEvent(false));
        currentPoll = event.poll;
        populateWithPoll(event.poll);
    }

    public void onEventMainThread(PollVoteEvent event) {
        currentPoll = event.poll;
        this.recyclerView.setAdapter(new PollOptionAdapter(currentPoll.getOptions()));
        this.recyclerView.setVisibility(View.VISIBLE);
        showResults();
    }
}
