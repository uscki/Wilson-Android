package nl.uscki.appcki.android.fragments.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.Years;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.generated.news.NewsItem;
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
        holder.content.setText(item.getShorttext());

        String iconUrl = "https://www.uscki.nl/modules/News/images/" + item.getType().getIcon();
        holder.category.setImageUrl(iconUrl);

        holder.metadata.setText("(" + item.getPerson().getName() + " / " + timestampConversion(item.getTimestamp()));
    }

    private String timestampConversion(Long time) {
        DateTime now = DateTime.now();
        DateTime other = new DateTime(time);
        int hours = Hours.hoursBetween(other, now).getHours();
        if(hours > 24) {
            int days = Days.daysBetween(other, now).getDays();
            if(days > 7) {
                int weeks = Weeks.weeksBetween(other, now).getWeeks();
                if(weeks > 3) {
                    int months = Months.monthsBetween(other, now).getMonths();
                    if(months > 12) {
                        int years = Years.yearsBetween(other, now).getYears();
                        return "± " + years + " jaren geleden)";
                    } else {
                        return "± " + months + " maanden geleden)";
                    }
                } else {
                    return "± " + weeks + " weken geleden)";
                }
            } else {
                return "± " + days + " dagen geleden)";
            }
        } else {
            return "± " + hours + " uur geleden)";
        }
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
