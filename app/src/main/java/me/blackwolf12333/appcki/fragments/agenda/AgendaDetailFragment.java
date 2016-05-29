package me.blackwolf12333.appcki.fragments.agenda;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.generated.agenda.AgendaItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgendaDetailFragment extends Fragment {
    TextView title;
    TextView when;
    TextView longText;

    AgendaItem item;

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
        longText = (TextView) view.findViewById(R.id.agenda_detail_longtext);

        title.setText(item.getShortdescription());
        when.setText(item.getWhen());
        longText.setText(item.getLongdescription());

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
