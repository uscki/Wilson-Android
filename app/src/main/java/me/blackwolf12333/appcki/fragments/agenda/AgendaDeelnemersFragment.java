package me.blackwolf12333.appcki.fragments.agenda;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.google.gson.Gson;

import java.net.ConnectException;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.MediaAPI;
import me.blackwolf12333.appcki.api.Services;
import me.blackwolf12333.appcki.error.ConnectionError;
import me.blackwolf12333.appcki.events.AgendaItemSubscribedEvent;
import me.blackwolf12333.appcki.events.AgendaSubscribersEvent;
import me.blackwolf12333.appcki.events.ImageZoomEvent;
import me.blackwolf12333.appcki.fragments.PageableFragment;
import me.blackwolf12333.appcki.generated.agenda.AgendaItem;
import me.blackwolf12333.appcki.views.NetworkImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of AgendaParticipants.
 */
public class AgendaDeelnemersFragment extends PageableFragment {
    AgendaItem item;

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
        //VolleyAgenda.getInstance().getSubscribed(item.getId()); // TODO API: wacht op api implementatie
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
                getAdapter().update(event.subscribed.getContent());
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
