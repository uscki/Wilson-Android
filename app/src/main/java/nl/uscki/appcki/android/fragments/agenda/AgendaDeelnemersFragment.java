package nl.uscki.appcki.android.fragments.agenda;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.AgendaActivity;
import nl.uscki.appcki.android.events.AgendaItemUpdatedEvent;
import nl.uscki.appcki.android.fragments.RefreshableFragment;
import nl.uscki.appcki.android.generated.ListSectionHeader;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.generated.agenda.AgendaParticipant;

/**
 * A fragment representing a list of AgendaParticipants.
 */
public class AgendaDeelnemersFragment extends RefreshableFragment {
    @BindView(R.id.empty_text)
    TextView emptyText;

    @BindView(R.id.recyclerView)
    RecyclerView participantList;

    private AgendaActivity activity;

    private void setupParticipantList(AgendaItem item) {
        if (getAdapter() instanceof AgendaDeelnemersAdapter) {
            getAdapter().update(item.getParticipants());
            if(item.getBackupList().size() > 0) {
                ListSectionHeader seperatingHeader = new ListSectionHeader(String.format(
                        Locale.getDefault(),
                        "%s (%d)",
                        getString(R.string.agenda_participants_backup_list),
                        item.getBackupList().size()));
                seperatingHeader.setHelpText(getString(R.string.agenda_participants_backup_list_help));
                getAdapter().add(seperatingHeader);
                getAdapter().addItems(item.getBackupList());
            }
        }
        if(emptyText != null && participantList != null) {
            if (item.getMaxregistrations() != null && item.getMaxregistrations() == 0) {
                emptyText.setText(getString(R.string.agenda_prepublished_event_registration_opens_later));
                emptyText.setVisibility(View.VISIBLE);
                participantList.setVisibility(View.GONE);
            } else if (item.getParticipants().isEmpty()) {
                emptyText.setText(getString(R.string.agenda_participants_list_empty));
                emptyText.setVisibility(View.VISIBLE);
                participantList.setVisibility(View.GONE);
            } else {
                emptyText.setVisibility(View.GONE);
                participantList.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onSwipeRefresh() {
        activity.refreshAgendaItem();
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof AgendaActivity) {
            this.activity = (AgendaActivity) context;
            AgendaDeelnemersAdapter adapter = new AgendaDeelnemersAdapter(new ArrayList<AgendaParticipant>());
            adapter.activity = this.activity;
            setAdapter(adapter);
        }
        super.onAttach(context);
    }

    // EVENT HANDLING
    public void onEventMainThread(AgendaItemUpdatedEvent event) {
        swipeContainer.setRefreshing(false);
        if(getAdapter() instanceof AgendaDeelnemersAdapter) {
            setupParticipantList(activity.getAgendaItem());
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
