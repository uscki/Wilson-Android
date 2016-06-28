package me.blackwolf12333.appcki.fragments.agenda;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.blackwolf12333.appcki.App;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.common.APISingleton;
import me.blackwolf12333.appcki.api.media.ImageLoader;
import me.blackwolf12333.appcki.api.media.NetworkImageView;
import me.blackwolf12333.appcki.generated.agenda.AgendaParticipant;

/**
 * {@link RecyclerView.Adapter} that can display a {@link AgendaParticipant}
 */
public class AgendaDeelnemersAdapter extends RecyclerView.Adapter<AgendaDeelnemersAdapter.ViewHolder> {

    private final List<AgendaParticipant> mValues;

    public AgendaDeelnemersAdapter(List<AgendaParticipant> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_person_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.name.setText(mValues.get(position).getPerson().getName());
        holder.note.setText(mValues.get(position).getNote());

        if(holder.mItem.getPerson().getPhotomediaid() != null) {
            ImageLoader loader = APISingleton.getInstance(App.getContext()).getImageLoader();
            // TODO: 5/29/16 fix this shit in the api
            //MediaFile file = holder.mItem.getPerson().getPhotomediaid();
            //holder.profile.setImageIdAndType(file.getId(), MediaAPI.getFiletypeFromMime(file.getMimetype()), loader);
        }

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
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final NetworkImageView profile;
        public final TextView name;
        public final TextView note;
        public AgendaParticipant mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            profile = (NetworkImageView) view.findViewById(R.id.person_list_item_profile);
            profile.setDefaultImageResId(R.drawable.account);

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
