package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.generated.news.NewsOverview;

/**
 * Created by peter on 12/30/15.
 */
public class NewsOverviewEvent {
    public NewsOverview newsOverview;

    public NewsOverviewEvent(NewsOverview newsOverview) {
        this.newsOverview = newsOverview;
    }
}
