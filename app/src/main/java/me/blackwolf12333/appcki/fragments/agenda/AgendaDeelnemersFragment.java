package me.blackwolf12333.appcki.fragments.agenda;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.List;

import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.generated.agenda.AgendaItem;
import me.blackwolf12333.appcki.generated.agenda.AgendaParticipant;

/**
 * A fragment representing a list of AgendaParticipants.
 */
public class AgendaDeelnemersFragment extends Fragment {
    List<AgendaParticipant> deelnemers;
    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Gson gson = new Gson();
            deelnemers = gson.fromJson(getArguments().getString("item"), AgendaItem.class).getParticipants();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agendadeelnemers_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            recyclerView = (RecyclerView) view;
            recyclerView.setAdapter(new AgendaDeelnemersAdapter(deelnemers));
        }
        return view;
    }

    // EVENT HANDLING

    @Override
    public void onStart() {
        //EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        //EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
