package nl.uscki.appcki.android.fragments.adapters;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.generated.organisation.Committee;

/**
 * Created by peter on 3/8/17.
 */

public class SmoboCommissieAdapter extends BaseItemAdapter<SmoboCommissieAdapter.ViewHolder, Committee> {
    public SmoboCommissieAdapter(List<Committee> items) {
        super(items);
    }

    @Override
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.smobo_commissie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(ViewHolder holder, int position) {
        Committee item = items.get(position);

        String duration;
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMM yyyy");
        if (items.get(position).getEnd().isBeforeNow()) {
            duration = item.getStart().toString(fmt) + " - " + item.getEnd().toString(fmt);
            holder.duration.setText(duration);
            holder.name.setText(item.getName());
        } else {
            duration = item.getStart().toString(fmt) + " - (heden)";

            holder.name.setText(item.getName());
            holder.duration.setText(duration);
            holder.name.setTypeface(Typeface.DEFAULT_BOLD);
            holder.duration.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.smobo_commissie_name)
        TextView name;
        @BindView(R.id.smobo_commissie_duration)
        TextView duration;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
