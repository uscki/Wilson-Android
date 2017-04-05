package nl.uscki.appcki.android.fragments.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final NewsItem item = items.get(position);
        holder.mItem = item;
        holder.title.setText(item.getTitle());
        holder.content.setText(Parser.parse(item.getShorttextJSON(), true, holder.content));

        String iconUrl = "https://www.uscki.nl/modules/News/images/" + item.getType().getIcon();
        //holder.category.setImageUrl(iconUrl);

        holder.metadata.setText("(" + item.getPerson().getPostalname() + " / " + Utils.timestampConversion(item.getTimestamp()));
        holder.metadata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getContext() instanceof BasicActivity) {
                    BasicActivity c = (BasicActivity) v.getContext();
                    c.openSmoboFor(item.getPerson());
                }
            }
        });

        if (item.getLongtextJSON() != null) {
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
        @BindView(R.id.news_item_title)
        public TextView title;
        @BindView(R.id.news_item_content)
        public BBTextView content;
        @BindView(R.id.news_item_metadata)
        public TextView metadata;
        @BindView(R.id.news_item_category)
        public ImageView category;
        @BindView(R.id.news_item_readmore)
        public TextView readmore;
        public NewsItem mItem;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + title.getText() + "'";
        }
    }
}
