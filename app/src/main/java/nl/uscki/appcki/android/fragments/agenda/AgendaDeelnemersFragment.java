package nl.uscki.appcki.android.fragments.agenda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.net.ConnectException;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.error.ConnectionError;
import nl.uscki.appcki.android.events.AgendaItemSubscribedEvent;
import nl.uscki.appcki.android.events.AgendaSubscribersEvent;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of AgendaParticipants.
 */
public class AgendaDeelnemersFragment extends PageableFragment {
    private AgendaItem item;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Gson gson = new Gson();
            item = gson.fromJson(getArguments().getString("item"), AgendaItem.class);
            setAdapter(new AgendaDeelnemersAdapter(item.getParticipants()));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        swipeContainer.setRefreshing(false);
        return view;
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().agendaService.get(item.getId()).enqueue(new Callback<AgendaItem>() {
            @Override
            public void onResponse(Call<AgendaItem> call, Response<AgendaItem> response) {
                swipeContainer.setRefreshing(false);
                if (getAdapter() instanceof AgendaDeelnemersAdapter) {
                    getAdapter().update(response.body().getParticipants());
                }
            }

            @Override
            public void onFailure(Call<AgendaItem> call, Throwable t) {
                if (t instanceof ConnectException) {
                    new ConnectionError(t); // handle connection error in MainActivity
                } else {
                    throw new RuntimeException(t);
                }
            }
        });
    }

    @Override
    protected int getPageSize() {
        return 0;
    }

    @Override
    public void onScrollRefresh() {}

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
