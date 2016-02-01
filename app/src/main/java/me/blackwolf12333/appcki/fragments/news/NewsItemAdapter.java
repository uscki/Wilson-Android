package me.blackwolf12333.appcki.fragments.news;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.MainActivity;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.events.OpenFragmentEvent;
import me.blackwolf12333.appcki.generated.NewsItem;

/**
 * {@link RecyclerView.Adapter} that can display a {@link NewsItem}.
 */
public class NewsItemAdapter extends RecyclerView.Adapter<NewsItemAdapter.ViewHolder> {

    private final List<NewsItem> mValues;

    public NewsItemAdapter(List<NewsItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_newsitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).getTitle());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putInt("id", holder.mItem.getId());
                EventBus.getDefault().post(new OpenFragmentEvent(MainActivity.Screen.NEWSDETAIL, args));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public NewsItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
