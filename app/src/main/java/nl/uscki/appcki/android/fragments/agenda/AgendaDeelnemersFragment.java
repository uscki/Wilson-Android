package nl.uscki.appcki.android.fragments.agenda;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
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
    private AgendaItem item;

    private Callback<AgendaItem> refreshCallback = new Callback<AgendaItem>() {
        @Override
        public void onSucces(Response<AgendaItem> response) {
            swipeContainer.setRefreshing(false);
            if (getAdapter() instanceof AgendaDeelnemersAdapter) {
                getAdapter().update(response.body().getParticipants());
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Services.getInstance().agendaService.get(getArguments().getInt("id")).enqueue(refreshCallback);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().agendaService.get(item.getId()).enqueue(refreshCallback);
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
