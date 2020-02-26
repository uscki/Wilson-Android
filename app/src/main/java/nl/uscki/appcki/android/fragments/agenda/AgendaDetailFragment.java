package nl.uscki.appcki.android.fragments.agenda;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.AgendaActivity;
import nl.uscki.appcki.android.events.DetailItemUpdatedEvent;
import nl.uscki.appcki.android.fragments.RefreshableFragment;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.helpers.AgendaSubscribedHelper;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgendaDetailFragment extends RefreshableFragment {
    private TextView startTime;
    private TextView participants;
    private TextView registrationRequired;
    private BBTextView longText;
    private TextView summaryCommissie;
    private TextView summaryTitle;
    private TextView summaryWaar;
    private TextView summaryWhen;
    private TextView summaryCost;
    private View root;

    private AgendaActivity activity;

    public AgendaDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_agenda_detail, container, false);

        this.startTime = view.findViewById(R.id.agenda_detail_time);
        this.participants = view.findViewById(R.id.agenda_detail_participants);
        this.registrationRequired = view.findViewById(R.id.agenda_registration_required);
        this.longText = view.findViewById(R.id.agenda_detail_longtext);
        this.summaryCommissie = view.findViewById(R.id.agenda_summary_commissie_text);
        this.summaryTitle = view.findViewById(R.id.agenda_summary_title_text);
        this.summaryWaar = view.findViewById(R.id.agenda_summary_waar_text);
        this.summaryWhen = view.findViewById(R.id.agenda_summary_when_text);
        this.summaryCost = view.findViewById(R.id.agenda_summary_cost_text);
        this.root = view.findViewById(R.id.agenda_detail_root);
        setupSwipeContainer(view);

        if(activity.getAgendaItem() != null) {
            setupViews(view, activity.getAgendaItem());
        }

        return view;
    }

    public void onEventMainThread(DetailItemUpdatedEvent<AgendaItem> event) {
        swipeContainer.setRefreshing(false);
        if(getView() != null) {
            setupViews(getView(), event.getUpdatedItem());
        } else {
            Log.e(getClass().getSimpleName(), "Trying to update agenda item, but view is null");
        }
    }

    private void setupViews(View view, AgendaItem item) {
        longText.setText(Parser.parse(item.getDescription(), true, longText));

        String when = AgendaSubscribedHelper.getWhen(item);
        this.startTime.setText(when);

        String participantsText;
        if(item.getMaxregistrations() == null) {
            participantsText = getString(R.string.agenda_n_participants, item.getParticipants().size());
        } else if (item.getMaxregistrations() <= 0) {
            participantsText = getString(R.string.agenda_prepublished_event_registration_opens_later);
        } else if (item.getBackupList().isEmpty()) {
            participantsText = getString(R.string.agenda_n_participants_max,
                    item.getParticipants().size(), item.getMaxregistrations());
        } else {
            participantsText = getString(R.string.agenda_n_participants_backup,
                    item.getParticipants().size(), item.getMaxregistrations(),
                    item.getBackupList().size());
        }
        this.participants.setText(participantsText);

        if(item.getRegistrationrequired()) {
            if(item.getHasDeadline()) {
                // registrationRequired and hasDeadline can be two different things, so technically
                // the following line should be within the first if-clause, not the second. However,
                // max n participants (i.e. prepublishing) can only be set when registration is
                // required, meaning 'registration required' does not mean too much on pre-published
                // events.
                registrationRequired.setVisibility(View.VISIBLE);
                int registrationRequiredText = item.getDeadline().isBeforeNow() ?
                        R.string.agenda_event_register_deadline_passed_date :
                        R.string.agenda_registration_required_before;

                registrationRequired.setText(getString(
                        registrationRequiredText,
                        item.getDeadline().toString("EEEE, dd MMM YYYY, HH:mm")
                    )
                );
            }
        }

        setTextView(view, item.getWho(), R.id.agenda_summary_commissie_text);
        setTextView(view, item.getWhat(), R.id.agenda_summary_title_text);
        setTextView(view, item.getLocation(), R.id.agenda_summary_waar_text);

        /*if (item.getWhen() != null) {
            summaryWhen.setText(item.getWhen());
        } else {
            summaryWhen.setText(when);
        }*/
        setTextView(view, item.getCosts(), R.id.agenda_summary_cost_text);
    }

    private void setTextView(View v, String str, int id) {
        if (str != null && !str.isEmpty()) {
            ((TextView) v.findViewById(id)).setText(str);
        } else {
            v.findViewById(id).setVisibility(View.GONE);
        }
    }

    @Override
    public void onSwipeRefresh() {
        activity.refreshAgendaItem();
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof AgendaActivity) {
            activity = (AgendaActivity) context;
        }
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
