package me.blackwolf12333.appcki.fragments.agenda;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import java.util.List;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.MediaAPI;
import me.blackwolf12333.appcki.events.ImageZoomEvent;
import me.blackwolf12333.appcki.fragments.adapters.BaseItemAdapter;
import me.blackwolf12333.appcki.generated.agenda.AgendaParticipant;
import me.blackwolf12333.appcki.views.NetworkImageView;

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
        holder.name.setText(items.get(position).getPerson().getName());
        holder.note.setText(items.get(position).getNote());

        if(holder.mItem.getPerson().getPhotomediaid() != null) {
            // TODO API: 5/29/16 fix this shit in the api
            Integer profile = holder.mItem.getPerson().getPhotomediaid();
            holder.profile.setImageMediaId(profile, MediaAPI.MediaSize.SMALL);
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
        return items.size();
    }

    @Override
    public Integer getLastID() {
        return items.get(items.size()-1).getId();
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
                EventBus.getDefault().post(new ImageZoomEvent(startBounds, profile.getMediaId()));
            }
        };

        @Override
        public String toString() {
            return super.toString() + " '" + name.getText() + "'";
        }
    }
}
