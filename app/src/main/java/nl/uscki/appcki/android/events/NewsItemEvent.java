package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.generated.news.NewsItem;

/**
 * Created by peter on 12/30/15.
 */
public class NewsItemEvent {
    public NewsItem newsItem;

    public NewsItemEvent(NewsItem newsItem) {
        this.newsItem = newsItem;
    }
}
