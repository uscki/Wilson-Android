package me.blackwolf12333.appcki.fragments.poll;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.api.PollAPI;
import me.blackwolf12333.appcki.events.NewPollEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PollFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PollFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PollFragment extends APIFragment {

    private static final String ARG_USER = "user";

    private User user;
    private PollAPI pollAPI;
    private OnFragmentInteractionListener mListener;
    private RadioGroup pollOptions;
    private TextView pollText;
    private ViewGroup view;
    private ProgressBar progressBar;
    private ViewGroup pollGroup;

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
    public static PollFragment newInstance(User user) {
        PollFragment fragment = new PollFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().get(ARG_USER);
            pollAPI = new PollAPI(user);
            pollAPI.getActivePoll();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_poll, container, false);
        this.content = (ViewGroup) view.findViewById(R.id.poll_group);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        this.pollOptions = (RadioGroup) view.findViewById(R.id.poll_options);
        this.pollText = (TextView) view.findViewById(R.id.poll_text);

        showProgress(true);

        //pollOptions.check(getIndexForID(pollAPI.getActivePoll().myvote));

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

    private void populateWithActivePoll(PollAPI.Poll poll) {
        pollText.setText(poll.pollItem.title);
        for(PollAPI.PollOption option : poll.options) {
            RadioButton button = new RadioButton(pollOptions.getContext());
            button.setText(option.name);
            pollOptions.addView(button);
        }

        if(poll.myvote > 0) {
            for(int i = 0; i < poll.options.length; i++) {
                if(poll.options[i].id == poll.myvote) {
                    ((RadioButton)pollOptions.getChildAt(i)).setChecked(true);
                }
            }
        }
    }

    public void onEventMainThread(NewPollEvent event) {
        showProgress(false);
        populateWithActivePoll(event.poll);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
