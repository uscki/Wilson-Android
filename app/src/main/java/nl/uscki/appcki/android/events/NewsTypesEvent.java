package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.generated.news.NewsType;

/**
 * Created by peter on 12/30/15.
 */
public class NewsTypesEvent {
    public NewsType[] newsTypes;

    public NewsTypesEvent(NewsType[] newsTypes) {
        this.newsTypes = newsTypes;
    }
}
