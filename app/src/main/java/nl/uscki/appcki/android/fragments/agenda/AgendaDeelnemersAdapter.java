package nl.uscki.appcki.android.fragments.agenda;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_person_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(final ViewHolder holder, int position) {
        unsetViews(holder);
        resetViews(holder, items.get(position));
    }

    private void resetViews(final ViewHolder holder, final AgendaParticipant item) {
        holder.mItem = item;
        holder.name.setText(item.getPerson().getPostalname());
        holder.note.setText(item.getNote());

        Integer profile = holder.mItem.getPerson().getPhotomediaid();
        if(profile != null) {
            Glide.with(holder.mView)
                    .load(MediaAPI.getMediaUri(profile, MediaAPI.MediaSize.SMALL))
                    .fitCenter()
                    .optionalCircleCrop()
                    .placeholder(R.drawable.account)
                    .into(holder.profile);
        }

        final AgendaActivity act = (AgendaActivity)holder.mView.getContext();
        if(act != null) {
            holder.mView.setOnClickListener(view -> act.openSmoboFor(item.getPerson()));
        } else {
            Log.e(getClass().getSimpleName(),
                    "Could not obtain AgendaActivity from viewholder view. " +
                            "Can't register onClick listener for smobo picture");
        }
    }

    private void unsetViews(ViewHolder vh) {
        vh.mItem = null;
        vh.name.setText("");
        vh.note.setText("");
        Glide.with(vh.mView).clear(vh.profile);
    }

    @Override
    public int getItemCount() {
        return items.size();
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
