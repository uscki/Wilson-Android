package nl.uscki.appcki.android.fragments.agenda;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.AgendaActivity;
import nl.uscki.appcki.android.activities.SmoboActivity;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.api.Services;
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
                Intent smoboIntent = new Intent(activity, SmoboActivity.class);
                smoboIntent.putExtra("id", holder.mItem.getPerson().getId());
                smoboIntent.putExtra("name", holder.mItem.getPerson().getPostalname());
                /*ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(activity, holder.profile, "profile");
                Log.e("partadapter", "id: " + holder.mItem.getPerson().getId());
                // Check if we're running on Android 5.0 or higher
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Call some material design APIs here
                    activity.startActivity(smoboIntent, options.toBundle());
                } else {
                    // Implement this feature without material design
                    activity.startActivity(smoboIntent);
                }*/
                activity.startActivity(smoboIntent);

            }
        });
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
            profile = (ImageView) view.findViewById(R.id.person_list_item_profile);

            name = (TextView) view.findViewById(R.id.person_list_item_name);
            note = (TextView) view.findViewById(R.id.person_list_item_note);
            note.setVisibility(View.VISIBLE);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + name.getText() + "'";
        }
    }
}
