package nl.uscki.appcki.android.fragments.agenda;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.AgendaActivity;
import nl.uscki.appcki.android.events.AgendaItemUpdatedEvent;
import nl.uscki.appcki.android.fragments.RefreshableFragment;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgendaDetailFragment extends RefreshableFragment {
    @BindView(R.id.agenda_detail_time)
    TextView startTime;

    @BindView(R.id.agenda_detail_longtext)
    BBTextView longText;

    @BindView(R.id.agenda_summary_commissie_text)
    TextView summaryCommissie;
    @BindView(R.id.agenda_summary_title_text)
    TextView summaryTitle;
    @BindView(R.id.agenda_summary_waar_text)
    TextView summaryWaar;
    @BindView(R.id.agenda_summary_when_text)
    TextView summaryWhen;
    @BindView(R.id.agenda_summary_cost_text)
    TextView summaryCost;
    @BindView(R.id.agenda_detail_root)
    View root;
    @BindView(R.id.registration_required)
    LinearLayout registrationRequiredLayout;
    @BindView(R.id.registration_required_date)
    TextView registrationRequiredDate;
    @BindView(R.id.registration_opens_later)
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
        ButterKnife.bind(this, view);
        setupSwipeContainer(view);

//        if (getArguments() != null) {
//            int id = getArguments().getInt("id");
//            swipeContainer.setRefreshing(true);
//            Services.getInstance().agendaService.get(id).enqueue(new Callback<AgendaItem>() {
//                @Override
//                public void onSucces(Response<AgendaItem> response) {
//                    swipeContainer.setRefreshing(false);
//                    item = response.body();
//                    root.setVisibility(View.VISIBLE);
//                    setupViews(view);
//                }
//            });
//        }
//
//
//
//        root.setVisibility(View.INVISIBLE);

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

        // TODO somehow use string resources
        String format = "EEEE, dd MMMM, HH:mm";
        if (item.getEnd() != null) {
            if (item.getEnd().toLocalDate().equals(item.getStart().toLocalDate())) {
                format = "EEEE, dd MMMM HH:mm - ";
                startTime.setText(item.getStart().toString(format) + item.getEnd().toString("HH:mm"));
            } else {
                startTime.setText(item.getStart().toString(format) + "\n" + item.getEnd().toString(format));
            }
        } else {
            startTime.setText(item.getStart().toString(format));
        }

        longText.setText(Parser.parse(item.getDescription(), true, longText));

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

        if (item.getEnd() != null) {
            summaryWhen.setText(item.getWhen());
        } else {
            summaryWhen.setText(item.getStart().toString(format));
        }
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
//        Services.getInstance().agendaService.get(item.getId()).enqueue(new Callback<AgendaItem>() {
//            @Override
//            public void onSucces(Response<AgendaItem> response) {
//                item = response.body();
//                getView().invalidate();
//                swipeContainer.setRefreshing(false);
//            }
//        });
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
