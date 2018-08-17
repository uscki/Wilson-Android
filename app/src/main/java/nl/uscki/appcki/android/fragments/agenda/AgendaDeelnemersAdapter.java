package nl.uscki.appcki.android.fragments.agenda;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.AgendaActivity;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.generated.agenda.AgendaParticipant;

/**
 * {@link RecyclerView.Adapter} that can display a {@link AgendaParticipant}
 */
public class AgendaDeelnemersAdapter extends BaseItemAdapter<AgendaDeelnemersAdapter.ViewHolder, AgendaParticipant> {

    AgendaActivity activity;

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
        unsetViews(holder);
        resetViews(holder, items.get(position));
    }

    private void resetViews(final ViewHolder holder, AgendaParticipant item) {
        holder.mItem = item;
        holder.name.setText(item.getPerson().getPostalname());
        holder.note.setText(item.getNote());

        Integer profile = holder.mItem.getPerson().getPhotomediaid();
        if(profile != null) {
            holder.profile.setImageURI(MediaAPI.getMediaUri(profile, MediaAPI.MediaSize.SMALL));
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO open smobo
            }
        });
    }

    private void unsetViews(ViewHolder vh) {
        vh.mItem = null;
        vh.name.setText("");
        vh.note.setText("");
        vh.profile.setImageURI("");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final SimpleDraweeView profile;
        public final TextView name;
        public final TextView note;
        public AgendaParticipant mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            profile = view.findViewById(R.id.person_list_item_profile);

            name = view.findViewById(R.id.person_list_item_name);
            note = view.findViewById(R.id.person_list_item_note);
            note.setVisibility(View.VISIBLE);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + name.getText() + "'";
        }
    }
}
