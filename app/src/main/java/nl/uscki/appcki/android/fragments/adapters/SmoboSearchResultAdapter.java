package nl.uscki.appcki.android.fragments.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

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
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_person_list_item, parent, false);
        return new ViewHolder(view);
    }

    private void unsetViews(ViewHolder holder) {
        holder.mItem = null;
        holder.name.setText("");
        holder.note.setText("");
        holder.profile.setImageURI("");
    }

    private void resetViews(ViewHolder holder, final PersonName person) {
        holder.mItem = person;
        holder.name.setText(person.getPostalname());
        holder.note.setText("");
        if(person.getPhotomediaid() != null) {
            holder.profile.setImageURI(MediaAPI.getMediaUri(person.getPhotomediaid(), MediaAPI.MediaSize.SMALL));
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getContext() instanceof BasicActivity) {
                    BasicActivity o = (BasicActivity) v.getContext();
                    o.openSmoboFor(person);
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final SimpleDraweeView profile;
        public final TextView name;
        public final TextView note;
        public PersonName mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            profile = (SimpleDraweeView) view.findViewById(R.id.person_list_item_profile);

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
