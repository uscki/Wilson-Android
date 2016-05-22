package me.blackwolf12333.appcki.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.App;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.MediaAPI;
import me.blackwolf12333.appcki.api.VolleyAgenda;
import me.blackwolf12333.appcki.api.common.APISingleton;
import me.blackwolf12333.appcki.api.media.ImageLoader;
import me.blackwolf12333.appcki.api.media.NetworkImageView;
import me.blackwolf12333.appcki.events.AgendaItemEvent;
import me.blackwolf12333.appcki.events.AgendaItemSubscribedEvent;
import me.blackwolf12333.appcki.generated.agenda.AgendaItem;
import me.blackwolf12333.appcki.generated.agenda.AgendaParticipant;
import me.blackwolf12333.appcki.generated.organisation.Person;
import me.blackwolf12333.appcki.helpers.UserHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgendaDetailFragment extends Fragment {
    AgendaItem currentItem;
    private View view;
    private TextView itemTitle;
    private TextView itemWhen;
    private TextView itemWhere;
    private TextView itemDeelnemers;
    private NetworkImageView itemPoster;
    private TextView itemLongDescription;
    private CheckBox itemInschrijven;
    private EditText itemNote;
    private Button participantsDetail;
    private ActionBar actionBar;

    public AgendaDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Integer id = getArguments().getInt("id");
        VolleyAgenda.getInstance().getAgendaItem(id);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_agenda_detail, container, false);
        view.setVisibility(View.GONE);
        itemTitle = (TextView) view.findViewById(R.id.agenda_item_title);
        itemWhen = (TextView) view.findViewById(R.id.agenda_item_when);
        itemWhere = (TextView) view.findViewById(R.id.agenda_item_waar);
        itemDeelnemers = (TextView) view.findViewById(R.id.agenda_item_deelnemers);
        itemPoster = (NetworkImageView) view.findViewById(R.id.agenda_item_poster);
        itemLongDescription = (TextView) view.findViewById(R.id.agenda_item_longdescription);
        itemNote = (EditText) view.findViewById(R.id.agenda_item_note);

        itemInschrijven = (CheckBox) view.findViewById(R.id.agenda_item_inschrijven);

        participantsDetail = (Button) view.findViewById(R.id.agenda_item_participants);
        participantsDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putInt("id", currentItem.getId());
                // TODO: 5/16/16 agenda participants
                //EventBus.getDefault().post(new OpenFragmentEvent(MainActivity.Screen.AGENDAPARTICIPANTS, args));
            }
        });

        AppCompatActivity act = (AppCompatActivity) getActivity();
        actionBar = act.getSupportActionBar();

        // Inflate the layout for this fragment
        return view;
    }

    private void updateView(final AgendaItem item) {
        itemTitle.setText(item.getShortdescription());
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
                    VolleyAgenda.getInstance().unsubscribe(item.getId());
                    //CalendarHelper.getInstance(getContext()).removeItemFromCalendar(currentItem);
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
                    VolleyAgenda.getInstance().subscribe(item.getId(), itemNote.getText().toString());
                    //CalendarHelper.getInstance(getContext()).addItemToCalendar(currentItem);
                }
            });
        }
    }

    private String getNote(AgendaItem item) {
        for(AgendaParticipant p : item.getParticipants()) {
            Person user = UserHelper.getInstance().getUser().getPerson();
            if(p.getPerson().getUsername().equals(user.getUsername())) {
                return p.getNote();
            }
        }
        return "";
    }

    private boolean ingeschreven(AgendaItem item) {
        for(AgendaParticipant p : item.getParticipants()) {
            Person user = UserHelper.getInstance().getUser().getPerson();
            if(p.getPerson().getUsername().equals(user.getUsername())) {
                return true;
            }
        }
        return false;
    }

    public void onEventMainThread(AgendaItemEvent event) {
        currentItem = event.agendaItem;
        actionBar.setTitle(event.agendaItem.getShortdescription());
        updateView(event.agendaItem);
        view.setVisibility(View.VISIBLE);
    }

    public void onEventMainThread(AgendaItemSubscribedEvent event) {
        itemInschrijven.setChecked(event.subscribed);
        if(event.subscribed) {
            itemInschrijven.setText(getString(R.string.agenda_uitschrijven));
            itemInschrijven.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VolleyAgenda.getInstance().unsubscribe(currentItem.getId());
                    //CalendarHelper.getInstance(getContext()).removeItemFromCalendar(currentItem);
                }
            });

            itemDeelnemers.setText((currentItem.getParticipants().size()+1) +"");
        } else {
            itemInschrijven.setText(getString(R.string.agenda_inschrijven));
            itemInschrijven.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VolleyAgenda.getInstance().subscribe(currentItem.getId(), itemNote.getText().toString());
                    //CalendarHelper.getInstance(getContext()).addItemToCalendar(currentItem);
                }
            });
            itemDeelnemers.setText((currentItem.getParticipants().size()-1) +"");
        }
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
