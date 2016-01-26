package me.blackwolf12333.appcki.fragments.poll;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.MainActivity;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.api.PollAPI;
import me.blackwolf12333.appcki.events.NewPollEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;
import me.blackwolf12333.appcki.fragments.ProgressActivity;
import me.blackwolf12333.appcki.generated.Poll;
import me.blackwolf12333.appcki.generated.PollOption;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PollFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PollFragment extends APIFragment {

    private static final String ARG_USER = "user";

    private User user;
    private PollAPI pollAPI;
    private RadioGroup pollOptions;
    private TextView pollText;
    private ViewGroup view;

    private ProgressActivity activity;

    public PollFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user User
     * @return A new instance of fragment PollFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PollFragment newInstance() {
        PollFragment fragment = new PollFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pollAPI = new PollAPI(MainActivity.user);
        pollAPI.getActivePoll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_poll, container, false);
        this.pollOptions = (RadioGroup) view.findViewById(R.id.poll_options);
        this.pollText = (TextView) view.findViewById(R.id.poll_text);

        this.pollOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(!pollAPI.hasVoted()) {
                    pollAPI.vote(getIDForRadioButtonIndex(checkedId));
                } else {
                    //group.check(getIndexForID(pollAPI.getActivePoll().myvote));
                }
            }
        });

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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ProgressActivity)) {
            throw new IllegalArgumentException("BaseLoginFragment's acitivity must implement interface LoginActivity");
        }
        this.activity = (ProgressActivity) activity;
    }

    private int getIDForRadioButtonIndex(int index) {
        //PollAPI.Poll poll = pollAPI.getActivePoll();
        //return poll.options[index].id;
        return 0;
    }

    private int getIndexForID(int id) {
        //PollAPI.Poll poll = pollAPI.getActivePoll();
        //for(int i = 0; i < poll.options.length; i++) {
        //    if(poll.options[i].id == id) {
        //        return i;
        //    }
        //}
        return 0;
    }

    private void populateWithActivePoll(Poll poll) {
        pollText.setText(poll.getPollItem().getTitle());
        for(PollOption option : poll.getOptions()) {
            RadioButton button = new RadioButton(pollOptions.getContext());
            button.setText(option.getName());
            pollOptions.addView(button);
        }

        if(poll.getMyVote() > 0) {
            for(int i = 0; i < poll.getOptions().size(); i++) {
                if(poll.getOptions().get(i).getId() == poll.getMyVote()) {
                    ((RadioButton)pollOptions.getChildAt(i)).setChecked(true);
                }
            }
        }
    }

    public void onEventMainThread(NewPollEvent event) {
        activity.showProgress(false);
        populateWithActivePoll(event.poll);
    }

    @Override
    public void setUser(User user) {
        this.user = user;
        pollAPI = new PollAPI(user);
        pollAPI.getActivePoll();
    }
}
