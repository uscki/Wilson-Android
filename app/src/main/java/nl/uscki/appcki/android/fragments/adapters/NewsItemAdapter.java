package nl.uscki.appcki.android.fragments.adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.activities.BasicActivity;
import nl.uscki.appcki.android.activities.NewsActivity;
import nl.uscki.appcki.android.generated.news.NewsItem;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;

/**
 */
public class NewsItemAdapter extends BaseItemAdapter<NewsItemAdapter.ViewHolder, NewsItem> {
    public NewsItemAdapter(List<NewsItem> items) {
        super(items);
    }

    @Override
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(ViewHolder holder, int position) {
        final NewsItem item = items.get(position);
        holder.mItem = item;
        holder.title.setText(item.getTitle());
        holder.content.setText(Parser.parse(item.getShorttext(), true, holder.content));

        String iconUrl = "https://www.uscki.nl/modules/News/images/" + item.getType().getIcon();
        //holder.category.setImageUrl(iconUrl);

        holder.metadata.setText("(" + item.getPerson().getPostalname() + " / " + Utils.timestampConversion(item.getTimestamp().getMillis(), false) + ")");
        holder.metadata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getContext() instanceof BasicActivity) {
                    BasicActivity c = (BasicActivity) v.getContext();
                    c.openSmoboFor(item.getPerson());
                }
            }
        });

        if (item.getLongtext() != null) {
            holder.readmore.setVisibility(View.VISIBLE);
            holder.readmore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), NewsActivity.class);
                    intent.putExtra("item", new Gson().toJson(item));
                    v.getContext().startActivity(intent);
                }
            });
        } else {
            holder.readmore.setVisibility(View.GONE);
        }

        if (item.getLink() != null) {
            holder.readmore.setVisibility(View.VISIBLE);
            holder.readmore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getLink()));
                    v.getContext().startActivity(intent);
                }
            });
        } else {
            holder.readmore.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public TextView title;
        public BBTextView content;
        public TextView metadata;
        public ImageView category;
        public TextView readmore;
        public NewsItem mItem;

        public ViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.news_item_title);
            content = view.findViewById(R.id.news_item_content);
            metadata = view.findViewById(R.id.news_item_metadata);
            category = view.findViewById(R.id.news_item_category);
            readmore = view.findViewById(R.id.news_item_readmore);

            mView = view;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + title.getText() + "'";
        }
    }
}
