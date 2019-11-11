package nl.uscki.appcki.android.fragments.agenda;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.AgendaActivity;
import nl.uscki.appcki.android.events.AgendaItemUpdatedEvent;
import nl.uscki.appcki.android.fragments.RefreshableFragment;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.helpers.AgendaSubscribedHelper;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgendaDetailFragment extends RefreshableFragment {
    TextView startTime;
    TextView participants;
    BBTextView longText;
    LinearLayout registrationRequiredLayout;
    TextView registrationRequiredDate;
    TextView registrationLaterText;

    private AgendaActivity activity;

    public AgendaDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_agenda_detail, container, false);

        startTime = view.findViewById(R.id.agenda_detail_time);
        participants = view.findViewById(R.id.agenda_detail_participants);
        longText = view.findViewById(R.id.agenda_detail_longtext);
        registrationRequiredLayout = view.findViewById(R.id.registration_required);
        registrationLaterText = view.findViewById(R.id.registration_required_text);
        registrationRequiredDate = view.findViewById(R.id.registration_required_date);

        setupSwipeContainer(view);

        if(activity.getAgendaItem() != null) {
            setupViews(view, activity.getAgendaItem());
        }

        return view;
    }

    public void onEventMainThread(AgendaItemUpdatedEvent event) {
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
        this.participants.setText(AgendaSubscribedHelper.getParticipantsSummary(getContext(), item));
        if(item.getRegistrationrequired()) {
            registrationRequiredLayout.setVisibility(View.VISIBLE);
            if(item.getDeadline() != null) {
                // TODO somehow use string resources
                registrationRequiredDate.setText(item.getDeadline().toString("EEEE, dd MMM, HH:mm"));
            }
            if(item.getMaxregistrations() <= 0) {
                registrationLaterText.setVisibility(View.VISIBLE);
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
