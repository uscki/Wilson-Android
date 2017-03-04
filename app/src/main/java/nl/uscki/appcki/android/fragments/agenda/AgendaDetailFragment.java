package nl.uscki.appcki.android.fragments.agenda;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.uscki.appcki.android.R;
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
    private TextView title;
    private TextView when;
    private BBTextView longText;

    private TextView summaryCommissie;
    private TextView summaryTitle;
    private TextView summaryWaar;
    private TextView summaryWhen;
    private TextView summaryCost;
    private View root;

    public static AgendaItem item;

    public AgendaDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_agenda_detail, container, false);

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
                    findViews(view);
                    setupViews(view);
                }
            });
        }

        findViews(view);
        root.setVisibility(View.INVISIBLE);

        return view;
    }

    private void setupViews(View view) {
        title.setText(item.getTitle());
        if (item.getEnd() != null) {
            String whenStr = item.getStart().toString("EEEE dd MMMM YYYY HH:mm") + " - " + item.getEnd().toString("EEEE dd MMMM YYYY HH:mm");
            when.setText(whenStr);
        } else {
            when.setText(item.getStart().toString("EEEE dd MMMM YYYY HH:mm"));
        }
        longText.setText(Parser.parse(item.getDescriptionJSON(), true, longText));

        setTextView(view, item.getWho(), R.id.agenda_summary_commissie_text);
        setTextView(view, item.getWhat(), R.id.agenda_summary_title_text);
        setTextView(view, item.getLocation(), R.id.agenda_summary_waar_text);

        if (item.getEnd() != null) {
            summaryWhen.setText(item.getWhen());
        } else {
            summaryWhen.setText(item.getStart().toString("EEEE dd MMMM YYYY HH:mm"));
        }
        setTextView(view, item.getCosts(), R.id.agenda_summary_cost_text);
    }

    private void findViews(View view) {
        root = view.findViewById(R.id.agenda_detail_root);

        title = (TextView) view.findViewById(R.id.agenda_detail_title);
        when = (TextView) view.findViewById(R.id.agenda_detail_when);
        longText = (BBTextView) view.findViewById(R.id.agenda_detail_longtext);

        summaryCommissie = (TextView) view.findViewById(R.id.agenda_summary_commissie_text);
        summaryTitle = (TextView) view.findViewById(R.id.agenda_summary_title_text);
        summaryWaar = (TextView) view.findViewById(R.id.agenda_summary_waar_text);
        summaryWhen = (TextView) view.findViewById(R.id.agenda_summary_when_text);
        summaryCost = (TextView) view.findViewById(R.id.agenda_summary_cost_text);
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
}
