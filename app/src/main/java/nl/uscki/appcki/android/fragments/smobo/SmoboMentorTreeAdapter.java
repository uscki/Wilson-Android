package nl.uscki.appcki.android.fragments.smobo;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.BasicActivity;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.generated.organisation.PersonSimpleName;

public class SmoboMentorTreeAdapter extends BaseItemAdapter<SmoboMentorTreeAdapter.ViewHolder, PersonSimpleName> {

    protected SmoboMentorTreeAdapter(List<PersonSimpleName> items) {
        super(items);
    }

    @Override
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_person_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(ViewHolder holder, int position) {
        final PersonSimpleName person = items.get(position);

        holder.name.setText(person.getPostalname());

        Integer photoId = person.getPhotomediaid();
        if(photoId != null) {
            holder.photo.setImageURI(MediaAPI.getMediaUri(photoId, MediaAPI.MediaSize.SMALL));
        } else {
            // Explicitly set photo empty, so it is not inherited from other items
            holder.photo.setImageURI("");
        }

        final BasicActivity activity = (BasicActivity)holder.view.getContext();
        if(activity != null) {
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.openSmoboFor(person);
                }
            });
        } else {
            Log.e(getClass().getSimpleName(),
                    "Could not obtain basic activity from viewholder. Can't register click listener for photo");
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;

        @BindView(R.id.person_list_item_profile)
        SimpleDraweeView photo;

        @BindView(R.id.person_list_item_name)
        TextView name;

        @BindView(R.id.person_list_item_note)
        TextView note;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);

            note.setVisibility(View.GONE);
        }
    }
}
