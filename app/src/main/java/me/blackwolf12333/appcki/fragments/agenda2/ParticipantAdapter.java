package me.blackwolf12333.appcki.fragments.agenda2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.generated.agenda.AgendaParticipant;

/**
 * Created by peter on 4/26/16.
 */
public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder> {

    private final List<AgendaParticipant> mValues;

    public ParticipantAdapter(List<AgendaParticipant> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_participant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getPerson().getName());
        holder.mContentView.setText(mValues.get(position).getNote());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO open een PersonFragment
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public AgendaParticipant mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public void clear() {
        mValues.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<AgendaParticipant> list) {
        mValues.addAll(list);
        notifyDataSetChanged();
    }
}
