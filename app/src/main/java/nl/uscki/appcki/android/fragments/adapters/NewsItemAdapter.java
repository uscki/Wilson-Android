package nl.uscki.appcki.android.fragments.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.generated.news.NewsItem;
import nl.uscki.appcki.android.helpers.bbtoviewgroup.Parser;
import nl.uscki.appcki.android.views.BBTextView;
import nl.uscki.appcki.android.views.NetworkImageView;

/**
 */
public class NewsItemAdapter extends BaseItemAdapter<NewsItemAdapter.ViewHolder, NewsItem> {
    public NewsItemAdapter(List<NewsItem> items) {
        super(items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        NewsItem item = items.get(position);
        holder.mItem = item;
        holder.title.setText(item.getTitle());
        holder.content.setText(Parser.parse(item.getShorttextJSON(), true));

        String iconUrl = "https://www.uscki.nl/modules/News/images/" + item.getType().getIcon();
        holder.category.setImageUrl(iconUrl);

        holder.metadata.setText("(" + item.getPerson().getName() + " / " + Utils.timestampConversion(item.getTimestamp()));
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
        public final TextView title;
        public final BBTextView content;
        public final TextView metadata;
        public final NetworkImageView category;
        public NewsItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.news_item_title);
            content = (BBTextView) view.findViewById(R.id.news_item_content);
            metadata = (TextView) view.findViewById(R.id.news_item_metadata);
            category = (NetworkImageView) view.findViewById(R.id.news_item_category);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + title.getText() + "'";
        }
    }
}
