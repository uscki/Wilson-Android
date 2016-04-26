package me.blackwolf12333.appcki.fragments.poll2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.VolleyPoll;
import me.blackwolf12333.appcki.events.PollEvent;
import me.blackwolf12333.appcki.events.PollVotedEvent;
import me.blackwolf12333.appcki.events.ShowProgressEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;
import me.blackwolf12333.appcki.generated.poll.Poll;
import me.blackwolf12333.appcki.generated.poll.PollOption;

/**
 * A simple {@link Fragment} subclass.
 */
public class PollFragment extends APIFragment {
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView recyclerView;
    protected PollOptionAdapter optionAdapter = new PollOptionAdapter(new ArrayList<PollOption>());
    protected PollResultAdapter resultAdapter = new PollResultAdapter(new ArrayList<PollOption>());
    private VolleyPoll pollAPI = new VolleyPoll();
    private TextView question;
    private Poll currentPoll;
    private boolean showResults = false;

    public PollFragment() {
        pollAPI.getActivePoll();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_poll, container, false);
        question = (TextView) view.findViewById(R.id.question);
        setupSwipeContainer(view);
        setupRecyclerView(view);
        return view;
    }

    private void setupRecyclerView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(optionAdapter);
    }

    private void setupSwipeContainer(View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.refreshContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setRefreshing(true);
    }

    public void updateContent(List<PollOption> content) {
        if (showResults) {
            resultAdapter.clear();
            resultAdapter.addAll(content);
        } else {
            optionAdapter.clear();
            optionAdapter.addAll(content);
        }
        swipeContainer.setRefreshing(false);
    }

    public void onEventMainThread(PollEvent event) {
        EventBus.getDefault().post(new ShowProgressEvent(false));
        question.setText(event.poll.getPollItem().getTitle());
        if(event.poll.getMyVote() == null) {
            showResults = false;
        } else {
            showResults = true;
            recyclerView.setAdapter(resultAdapter);
        }
        currentPoll = event.poll;
        updateContent(currentPoll.getOptions());
    }

    public void onEventMainThread(PollVotedEvent event) {
        resultAdapter.clear();
        resultAdapter.addAll(currentPoll.getOptions());
        recyclerView.setAdapter(resultAdapter);
    }

    @Override
    public void refresh() {
        pollAPI.getActivePoll();
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
