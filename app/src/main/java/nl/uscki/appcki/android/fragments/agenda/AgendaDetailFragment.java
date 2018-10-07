package nl.uscki.appcki.android.fragments.agenda;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.AgendaActivity;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.RefreshableFragment;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;
import retrofit2.Response;

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

    public static AgendaItem item;

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

        if (getArguments() != null) {
            int id = getArguments().getInt("id");
            swipeContainer.setRefreshing(true);
            Services.getInstance().agendaService.get(id).enqueue(new Callback<AgendaItem>() {
                @Override
                public void onSucces(Response<AgendaItem> response) {
                    swipeContainer.setRefreshing(false);
                    item = response.body();
                    root.setVisibility(View.VISIBLE);
                    setupViews(view);
                }
            });
        }

        root.setVisibility(View.INVISIBLE);

        return view;
    }

    private void setupViews(View view) {
        // the title is set in AgendaActivity

        String format = "EEEE, dd MMMM, HH:mm";
        if (item.getEnd() != null) {
            if (item.getEnd().toLocalDate().equals(item.getStart().toLocalDate())) {
                format = "EEEE, dd MMMM \n HH:mm - ";
                startTime.setText(item.getStart().toString(format) + item.getEnd().toString("HH:mm"));
            } else {
                startTime.setText(item.getStart().toString(format) + "\n" + item.getEnd().toString(format));
            }
        } else {
            startTime.setText(item.getStart().toString(format));
        }
        longText.setText(Parser.parse(item.getDescriptionJSON(), true, longText));

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
        Services.getInstance().agendaService.get(item.getId()).enqueue(new Callback<AgendaItem>() {
            @Override
            public void onSucces(Response<AgendaItem> response) {
                item = response.body();
                getView().invalidate();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof AgendaActivity) {
            activity = (AgendaActivity) context;
        }
        super.onAttach(context);
    }
}
