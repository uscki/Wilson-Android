package nl.uscki.appcki.android.fragments.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.BasicActivity;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.generated.organisation.PersonName;

/**
 * Created by peter on 4/20/17.
 */

public class SmoboSearchResultAdapter extends BaseItemAdapter<SmoboSearchResultAdapter.ViewHolder, PersonName> {

    public SmoboSearchResultAdapter(List<PersonName> items) {
        super(items);
    }

    @Override
    public void onBindCustomViewHolder(ViewHolder holder, int position) {
        unsetViews(holder);
        resetViews(holder, items.get(position));
    }

    @Override
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_person_list_item, parent, false);
        return new ViewHolder(view);
    }

    private void unsetViews(ViewHolder holder) {
        holder.mItem = null;
        holder.name.setText("");
        holder.note.setText("");
        Glide.with(holder.mView)
                .clear(holder.profile);
    }

    private void resetViews(ViewHolder holder, final PersonName person) {
        holder.mItem = person;
        holder.name.setText(person.getPostalname());
        holder.note.setText("");
        if(person.getPhotomediaid() != null) {
            Glide.with(holder.mView)
                    .load(MediaAPI.getMediaUri(person.getPhotomediaid(), MediaAPI.MediaSize.SMALL))
                    .placeholder(R.drawable.account)
                    .fitCenter()
                    .optionalCircleCrop()
                    .into(holder.profile);
        }
        holder.mView.setOnClickListener(v -> {
            if (v.getContext() instanceof BasicActivity) {
                BasicActivity o = (BasicActivity) v.getContext();
                o.openSmoboFor(person);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView profile;
        public final TextView name;
        public final TextView note;
        public PersonName mItem;

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
