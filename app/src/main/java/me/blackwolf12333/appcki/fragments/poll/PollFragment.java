package me.blackwolf12333.appcki.fragments.poll;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.PollAPI;
import me.blackwolf12333.appcki.events.PollEvent;
import me.blackwolf12333.appcki.events.ShowProgressEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;
import me.blackwolf12333.appcki.generated.Poll;
import me.blackwolf12333.appcki.generated.PollOption;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PollFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PollFragment extends APIFragment {
    private PollAPI pollAPI = new PollAPI();
    private RadioGroup pollOptions;
    private TextView pollText;
    private ViewGroup view;

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

    private int getIDForRadioButtonIndex(int index) {
        //PollAPI.Poll poll = pollAPI.getActivePoll();
        //return poll.options[index].id;
        return 0;
    }

    private void populateWithActivePoll(Poll poll) {
        pollText.setText(poll.getPollItem().getTitle());
        for(PollOption option : poll.getOptions()) {
            RadioButton button = new RadioButton(pollOptions.getContext());
            button.setText(option.getName());
            if(poll.getMyVote() == option.getId()) {
                button.setChecked(true);
            }
            button.setEnabled(false); //TODO test this
            pollOptions.addView(button);
        }
    }

    public void onEventMainThread(PollEvent event) {
        EventBus.getDefault().post(new ShowProgressEvent(false));
        populateWithActivePoll(event.poll);
    }
}
