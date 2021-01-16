package nl.uscki.appcki.android.fragments.adapters;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.activities.BasicActivity;
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
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(ViewHolder holder, int position) {
        final NewsItem item = items.get(position);
        holder.mItem = item;
        holder.title.setText(item.getTitle().replaceAll("CKI", App.USCKI_CKI_CHARACTER));

        if(item.getShorttext() != null) {
            holder.content.setText(Parser.parse(item.getShorttext(), true, holder.content));
            holder.content.setVisibility(View.VISIBLE);
        } else {
            holder.content.setText("");
            holder.content.setVisibility(View.GONE);
        }

        holder.metadata.setText(String.format(Locale.getDefault(), "(%s / %s)", item.getPerson().getPostalname(), Utils.timestampConversion(item.getTimestamp().getMillis(), false)));
        holder.metadata.setOnClickListener(v -> {
            if (v.getContext() instanceof BasicActivity) {
                BasicActivity c = (BasicActivity) v.getContext();
                c.openSmoboFor(item.getPerson());
            }
        });

        if (item.getLink() != null) {
            holder.externalLinkContainer.setVisibility(View.VISIBLE);
            holder.externalLink.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getLink()));
                v.getContext().startActivity(intent);
            });
        } else {
            holder.externalLinkContainer.setVisibility(View.GONE);
        }

        if (item.getLongtext() != null) {
            holder.readmore.setVisibility(View.VISIBLE);
            holder.longtext.setText(Parser.parse(item.getLongtext(), true, holder.longtext));
            holder.longtext.setLongClickable(true);
            holder.longtext.setOnLongClickListener(v -> {
                hideLongtext(holder);
                return true;
            });
            hideLongtext(holder);
        } else {
            holder.readmore.setVisibility(View.GONE);
        }
    }

    private void hideLongtext(ViewHolder holder) {
        holder.longtext.setVisibility(View.GONE);
        holder.readmore.setText(R.string.news_read_more);
        holder.readmore.setOnClickListener(v -> {
            showLongtext(holder);
        });
        setStartDrawableToReadMore(holder, R.drawable.ic_keyboard_arrow_down_black_24dp);
    }

    private void showLongtext(ViewHolder holder) {
        holder.longtext.setVisibility(View.VISIBLE);
        holder.readmore.setText(R.string.news_read_less);
        holder.readmore.setOnClickListener(v -> {
            hideLongtext(holder);
        });
        setStartDrawableToReadMore(holder, R.drawable.ic_keyboard_arrow_up_black_24dp);
    }

    private void setStartDrawableToReadMore(ViewHolder holder, int drawable) {
        Resources.Theme appTheme = holder.mView.getContext().getTheme();
        Drawable drawable1 =  ResourcesCompat.getDrawable(holder.mView.getResources(), drawable, appTheme);
        holder.readmore.setCompoundDrawablesWithIntrinsicBounds(drawable1, null, null, null);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public TextView title;
        public BBTextView content;
        public BBTextView longtext;
        public TextView metadata;
        public TextView externalLink;
        public TextView readmore;
        public NewsItem mItem;
        public LinearLayout externalLinkContainer;

        public ViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.news_item_title);
            content = view.findViewById(R.id.news_item_content);
            longtext = view.findViewById(R.id.news_item_longtext);
            metadata = view.findViewById(R.id.news_item_metadata);
            readmore = view.findViewById(R.id.news_item_readmore);
            externalLink = view.findViewById(R.id.news_item_external_link);
            externalLinkContainer = view.findViewById(R.id.news_item_external_link_container);

            mView = view;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + title.getText() + "'";
        }
    }
}
