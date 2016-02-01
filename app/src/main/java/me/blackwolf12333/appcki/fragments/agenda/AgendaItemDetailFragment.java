package me.blackwolf12333.appcki.fragments.agenda;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.UserHelper;
import me.blackwolf12333.appcki.api.AgendaAPI;
import me.blackwolf12333.appcki.events.AgendaItemEvent;
import me.blackwolf12333.appcki.events.AgendaItemSubscribedEvent;
import me.blackwolf12333.appcki.events.ShowProgressEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;
import me.blackwolf12333.appcki.generated.AgendaItem;
import me.blackwolf12333.appcki.generated.Participant;
import me.blackwolf12333.appcki.generated.Person;

/**
 * A simple {@link APIFragment} subclass.
 */
public class AgendaItemDetailFragment extends APIFragment {
    AgendaAPI agendaAPI = new AgendaAPI();

    private TextView itemTitle;
    private TextView itemWhen;
    private TextView itemWhere;
    private TextView itemDeelnemers;
    private ImageView itemPoster;
    private TextView itemLongDescription;
    private CheckBox itemInschrijven;
    private EditText itemNote;

    public AgendaItemDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Integer id = getArguments().getInt("id");
        agendaAPI.getAgendaItem(id);
        View view = inflater.inflate(R.layout.fragment_agenda_item_detail, container, false);
        itemTitle = (TextView) view.findViewById(R.id.agendaitem_title);
        itemWhen = (TextView) view.findViewById(R.id.agendaitem_when);
        itemWhere = (TextView) view.findViewById(R.id.agendaitem_waar);
        itemDeelnemers = (TextView) view.findViewById(R.id.agendaitem_deelnemers);
        itemPoster = (ImageView) view.findViewById(R.id.agendaitem_poster);
        itemLongDescription = (TextView) view.findViewById(R.id.agendaitem_longdescription);
        itemNote = (EditText) view.findViewById(R.id.agendaitem_note);

        itemInschrijven = (CheckBox) view.findViewById(R.id.agendaitem_inschrijven);

        // Inflate the layout for this fragment
        return view;
    }

    private void updateView(final AgendaItem item) {
        itemTitle.setText(item.getWhat());
        itemWhen.setText(item.getWhen());
        itemWhere.setText(item.getWhere());
        itemDeelnemers.setText(item.getParticipants().size()+"");
        //TODO set holder.itemPoster to an actual picture from AgendaItem.mediaCollection
        itemPoster.setImageDrawable(getResources().getDrawable(R.drawable.poster));
        itemLongDescription.setText(item.getLongdescription());

        if(ingeschreven(item)) {
            itemInschrijven.setChecked(true);
            itemInschrijven.setText(getString(R.string.agenda_uitschrijven));
            itemInschrijven.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    agendaAPI.unsubscribe(item);
                }
            });
        } else {
            itemInschrijven.setChecked(false);
            itemInschrijven.setText(getString(R.string.agenda_inschrijven));
            itemInschrijven.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    agendaAPI.subscribe(item, itemNote.getText().toString());
                }
            });
        }
    }

    private boolean ingeschreven(AgendaItem item) {
        for(Participant p : item.getParticipants()) {
            Person user = UserHelper.getInstance().getUser().getPerson();
            if(p.getPerson().getUsername().equals(user.getUsername())) {
                return true;
            }
        }
        return false;
    }

    public void onEventMainThread(AgendaItemEvent event) {
        EventBus.getDefault().post(new ShowProgressEvent(false));
        updateView(event.agendaItem);
    }

    public void onEventMainThread(AgendaItemSubscribedEvent event) {
        itemInschrijven.setChecked(true);
        itemInschrijven.setText(getString(R.string.agenda_uitschrijven));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
