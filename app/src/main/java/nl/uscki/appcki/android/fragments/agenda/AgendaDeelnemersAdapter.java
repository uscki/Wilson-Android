package nl.uscki.appcki.android.fragments.agenda;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.generated.agenda.AgendaParticipant;

/**
 * {@link RecyclerView.Adapter} that can display a {@link AgendaParticipant}
 */
public class AgendaDeelnemersAdapter extends BaseItemAdapter<AgendaDeelnemersAdapter.ViewHolder, AgendaParticipant> {

    public AgendaDeelnemersAdapter(List<AgendaParticipant> items) {
        super(items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_person_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = items.get(position);
        holder.name.setText(items.get(position).getPerson().getPostalname());
        holder.note.setText(items.get(position).getNote());

        Integer profile = holder.mItem.getPerson().getPhotomediaid();
        if(profile == null) {
            profile = 0;
        }
        Services.getInstance().picasso
                .load(MediaAPI.getMediaUrl(profile, MediaAPI.MediaSize.SMALL))
                .placeholder(R.drawable.account)
                .into(holder.profile);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 5/29/16 bekijk persoon
                Log.d("AgendaDeelnemersAdapter", "TODO: bekijk persoon");
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public Integer getLastID() {
        return items.get(items.size()-1).getId();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView profile;
        public final TextView name;
        public final TextView note;
        public AgendaParticipant mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            profile = (ImageView) view.findViewById(R.id.person_list_item_profile);
            profile.setOnClickListener(zoomClickListener);

            name = (TextView) view.findViewById(R.id.person_list_item_name);
            note = (TextView) view.findViewById(R.id.person_list_item_note);
            note.setVisibility(View.VISIBLE);
        }

        private View.OnClickListener zoomClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Rect startBounds = new Rect();
                profile.getGlobalVisibleRect(startBounds);
                //TODO implement a nicer big image viewer
                //EventBus.getDefault().post(new ImageZoomEvent(startBounds, profile.getMediaId()));
            }
        };

        @Override
        public String toString() {
            return super.toString() + " '" + name.getText() + "'";
        }
    }
}
