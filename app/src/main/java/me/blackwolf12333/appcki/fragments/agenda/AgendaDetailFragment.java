package me.blackwolf12333.appcki.fragments.agenda;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.generated.agenda.AgendaItem;
import me.blackwolf12333.appcki.views.BBTextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgendaDetailFragment extends Fragment {
    TextView title;
    TextView when;
    BBTextView longText;

    TextView summaryCommissie;
    ImageView summaryCommissieIcon;
    TextView summaryTitle;
    TextView summaryWaar;
    TextView summaryWhen;
    TextView summaryCost;

    public static AgendaItem item;

    public AgendaDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_agenda_detail, container, false);

        if (getArguments() != null) {
            Gson gson = new Gson();
            item = gson.fromJson(getArguments().getString("item"), AgendaItem.class);
        }

        title = (TextView) view.findViewById(R.id.agenda_detail_title);
        when = (TextView) view.findViewById(R.id.agenda_detail_when);
        longText = (BBTextView) view.findViewById(R.id.agenda_detail_longtext);

        summaryCommissie = (TextView) view.findViewById(R.id.agenda_summary_commissie_text);
        summaryCommissieIcon = (ImageView) view.findViewById(R.id.agenda_summary_commissie_icon);
        summaryTitle = (TextView) view.findViewById(R.id.agenda_summary_title_text);
        summaryWaar = (TextView) view.findViewById(R.id.agenda_summary_waar_text);
        summaryWhen = (TextView) view.findViewById(R.id.agenda_summary_when_text);
        summaryCost = (TextView) view.findViewById(R.id.agenda_summary_cost_text);

        title.setText(item.getShortdescription());
        when.setText(item.getWhen());
        longText.setText(item.getLongdescription());

        if (item.getCommittee() != null) {
            summaryCommissie.setText(item.getCommittee().getGroup().getName());
        } else {
            summaryCommissie.setVisibility(View.GONE);
            summaryCommissieIcon.setVisibility(View.GONE);
        }

        summaryTitle.setText(item.getShortdescription());
        summaryWaar.setText(item.getWhere());
        summaryWhen.setText(item.getWhen());
        summaryCost.setText(item.getCosts());

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
