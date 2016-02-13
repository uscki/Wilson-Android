package me.blackwolf12333.appcki.fragments.agenda;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import android.support.v7.app.ActionBar;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.App;
import me.blackwolf12333.appcki.MainActivity;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.helpers.CalendarHelper;
import me.blackwolf12333.appcki.helpers.UserHelper;
import me.blackwolf12333.appcki.events.AgendaItemEvent;
import me.blackwolf12333.appcki.events.AgendaItemSubscribedEvent;
import me.blackwolf12333.appcki.events.MediaBitmapEvent;
import me.blackwolf12333.appcki.events.OpenFragmentEvent;
import me.blackwolf12333.appcki.events.ShowProgressEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;
import me.blackwolf12333.appcki.generated.AgendaItem;
import me.blackwolf12333.appcki.generated.Participant;
import me.blackwolf12333.appcki.generated.Person;
import me.blackwolf12333.appcki.api.media.ImageLoader;
import me.blackwolf12333.appcki.api.MediaAPI;
import me.blackwolf12333.appcki.api.media.NetworkImageView;
import me.blackwolf12333.appcki.api.APISingleton;
import me.blackwolf12333.appcki.api.VolleyAgenda;

/**
 * A simple {@link APIFragment} subclass.
 */
public class AgendaItemDetailFragment extends APIFragment {
    VolleyAgenda agendaAPI = new VolleyAgenda();
    AgendaItem currentItem;

    private TextView itemWhen;
    private TextView itemWhere;
    private TextView itemDeelnemers;
    private NetworkImageView itemPoster;
    private TextView itemLongDescription;
    private CheckBox itemInschrijven;
    private EditText itemNote;
    private Button participantsDetail;
    private AppCompatActivity act;
    private ActionBar actionBar;

    public AgendaItemDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Integer id = getArguments().getInt("id");
        agendaAPI.getAgendaItem(id);
        View view = inflater.inflate(R.layout.fragment_agenda_item_detail, container, false);
        itemWhen = (TextView) view.findViewById(R.id.agendaitem_when);
        itemWhere = (TextView) view.findViewById(R.id.agendaitem_waar);
        itemDeelnemers = (TextView) view.findViewById(R.id.agendaitem_deelnemers);
        itemPoster = (NetworkImageView) view.findViewById(R.id.agendaitem_poster);
        itemLongDescription = (TextView) view.findViewById(R.id.agendaitem_longdescription);
        itemNote = (EditText) view.findViewById(R.id.agendaitem_note);

        itemInschrijven = (CheckBox) view.findViewById(R.id.agendaitem_inschrijven);

        participantsDetail = (Button) view.findViewById(R.id.agendaitem_participants);
        participantsDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putInt("id", currentItem.getId());
                EventBus.getDefault().post(new OpenFragmentEvent(MainActivity.Screen.AGENDAPARTICIPANTS, args));
            }
        });

        act = (AppCompatActivity) getActivity();
        actionBar = act.getSupportActionBar();

        // Inflate the layout for this fragment
        return view;
    }

    private void updateView(final AgendaItem item) {
        itemWhen.setText(item.getWhen());
        itemWhere.setText(item.getWhere());
        itemDeelnemers.setText(item.getParticipants().size()+"");
        itemPoster.setImageDrawable(getResources().getDrawable(R.drawable.default_poster));
        if(item.getPosterid() != null) {
            ImageLoader loader = APISingleton.getInstance(App.getContext()).getImageLoader();
            itemPoster.setImageIdAndType(item.getPosterid().getId(), MediaAPI.getFiletypeFromMime(item.getPosterid().getMimetype()), loader);
        }
        itemLongDescription.setText(item.getLongdescription());

        if(ingeschreven(item)) {
            itemInschrijven.setChecked(true);
            itemInschrijven.setText(getString(R.string.agenda_uitschrijven));
            itemInschrijven.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    agendaAPI.unsubscribe(item.getId());
                    CalendarHelper.getInstance(getContext()).removeItemFromCalendar(currentItem);
                }
            });

            String note = getNote(item);
            itemNote.setText(note);
        } else {
            itemInschrijven.setChecked(false);
            itemInschrijven.setText(getString(R.string.agenda_inschrijven));
            itemInschrijven.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    agendaAPI.subscribe(item.getId(), itemNote.getText().toString());
                    CalendarHelper.getInstance(getContext()).addItemToCalendar(currentItem);
                }
            });
        }
    }

    private String getNote(AgendaItem item) {
        for(Participant p : item.getParticipants()) {
            Person user = UserHelper.getInstance().getUser().getPerson();
            if(p.getPerson().getUsername().equals(user.getUsername())) {
                return p.getNote();
            }
        }
        return "";
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
        currentItem = event.agendaItem;
        actionBar.setTitle(event.agendaItem.getShortdescription());
        updateView(event.agendaItem);
    }

    public void onEventMainThread(AgendaItemSubscribedEvent event) {
        itemInschrijven.setChecked(event.subscribed);
        if(event.subscribed) {
            itemInschrijven.setText(getString(R.string.agenda_uitschrijven));
            itemInschrijven.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    agendaAPI.unsubscribe(currentItem.getId());
                    CalendarHelper.getInstance(getContext()).removeItemFromCalendar(currentItem);
                }
            });

            itemDeelnemers.setText((currentItem.getParticipants().size()+1) +"");
        } else {
            itemInschrijven.setText(getString(R.string.agenda_inschrijven));
            itemInschrijven.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    agendaAPI.subscribe(currentItem.getId(), itemNote.getText().toString());
                    CalendarHelper.getInstance(getContext()).addItemToCalendar(currentItem);
                }
            });
            itemDeelnemers.setText((currentItem.getParticipants().size()-1) +"");
        }
    }

    public void onEventMainThread(MediaBitmapEvent event) {
        itemPoster.setImageBitmap(event.bitmap);
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
