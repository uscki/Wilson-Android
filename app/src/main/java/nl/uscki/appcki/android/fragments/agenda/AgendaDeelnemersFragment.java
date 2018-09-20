package nl.uscki.appcki.android.fragments.agenda;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.AgendaActivity;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.AgendaItemSubscribedEvent;
import nl.uscki.appcki.android.events.AgendaSubscribersEvent;
import nl.uscki.appcki.android.fragments.RefreshableFragment;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.generated.agenda.AgendaParticipant;
import retrofit2.Response;

/**
 * A fragment representing a list of AgendaParticipants.
 */
public class AgendaDeelnemersFragment extends RefreshableFragment {
    private int id;

    @BindView(R.id.empty_text)
    TextView emptyText;

    @BindView(R.id.recyclerView)
    RecyclerView participantList;

    private Callback<AgendaItem> refreshCallback = new Callback<AgendaItem>() {
        @Override
        public void onSucces(Response<AgendaItem> response) {
            Log.e(getClass().getSimpleName(), "Succesful callback on participants!");
            swipeContainer.setRefreshing(false);
            if (getAdapter() instanceof AgendaDeelnemersAdapter) {
                getAdapter().update(response.body().getParticipants());
            }
            if(emptyText != null && participantList != null) {
                if (response.body().getMaxregistrations() != null && response.body().getMaxregistrations() == 0) {
                    emptyText.setText(getString(R.string.agenda_prepublished_event_registration_closed));
                    emptyText.setVisibility(View.VISIBLE);
                    participantList.setVisibility(View.GONE);
                } else if (response.body().getParticipants().isEmpty()) {
                    emptyText.setText(getString(R.string.agenda_participants_list_empty));
                    emptyText.setVisibility(View.VISIBLE);
                    participantList.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.GONE);
                    participantList.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            id = getArguments().getInt("id");
            Services.getInstance().agendaService.get(getArguments().getInt("id")).enqueue(refreshCallback);
        }

        return view;
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().agendaService.get(id).enqueue(refreshCallback);
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof AgendaActivity) {
            AgendaDeelnemersAdapter adapter = new AgendaDeelnemersAdapter(new ArrayList<AgendaParticipant>());
            adapter.activity = (AgendaActivity) context;
            setAdapter(adapter);
        }
        super.onAttach(context);
    }

    // EVENT HANDLING

    public void onEventMainThread(AgendaSubscribersEvent event) {
        swipeContainer.setRefreshing(false);
        if (getAdapter() instanceof AgendaDeelnemersAdapter) {
            getAdapter().update(event.subscribers.getContent());
        }
    }

    public void onEventMainThread(AgendaItemSubscribedEvent event) {
        swipeContainer.setRefreshing(false);
        if (getAdapter() instanceof AgendaDeelnemersAdapter) {
            if(event.subscribed != null) { //TODO because of dirty hackin MainActivity
                getAdapter().update(event.subscribed.getParticipants());
            }
        }
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
