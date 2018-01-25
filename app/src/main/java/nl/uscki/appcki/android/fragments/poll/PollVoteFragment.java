package nl.uscki.appcki.android.fragments.poll;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.OpenFragmentEvent;
import nl.uscki.appcki.android.generated.poll.PollItem;
import nl.uscki.appcki.android.generated.poll.PollOption;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PollVoteFragment extends Fragment {
    @BindView(R.id.poll_vote_question)
    TextView question;
    @BindView(R.id.poll_vote_options)
    RadioGroup options;
    @BindView(R.id.poll_vote_vote)
    Button vote;

    PollItem item;

    public PollVoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_poll_vote, container, false);
        ButterKnife.bind(this, view);

        MainActivity.currentScreen = MainActivity.Screen.POLL_VOTE;

        if (getArguments() != null) {
            item = new Gson().fromJson(getArguments().getString("item"), PollItem.class);
            setupViews();
        }

        return view;
    }

    private void setupViews() {
        question.setText(item.getPoll().getTitle());

        for (PollOption option : item.getOptions()) {
            addOption(option.getName(), option.getId());
        }

        vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = options.getCheckedRadioButtonId();
                Services.getInstance().pollService.vote(id).enqueue(new Callback<PollItem>() {
                    @Override
                    public void onSucces(Response<PollItem> response) {
                        Bundle bundle = new Bundle();
                        bundle.putString("item", new Gson().toJson(response.body()));
                        EventBus.getDefault().post(new OpenFragmentEvent(new PollResultFragment(), bundle));
                    }
                });
            }
        });
    }

    private void addOption(String text, int id) {
        RadioButton option = new RadioButton(this.getContext());
        option.setText(text);
        option.setId(id);

        options.addView(option);
    }
}
