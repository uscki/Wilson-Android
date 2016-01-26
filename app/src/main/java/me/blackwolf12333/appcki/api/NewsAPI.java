package me.blackwolf12333.appcki.api;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.events.JSONReadyEvent;
import me.blackwolf12333.appcki.events.NewNewsItemEvent;
import me.blackwolf12333.appcki.events.NewNewsOverviewEvent;
import me.blackwolf12333.appcki.events.NewNewsTypesEvent;
import me.blackwolf12333.appcki.generated.NewsItem;
import me.blackwolf12333.appcki.generated.NewsOverview;
import me.blackwolf12333.appcki.generated.NewsType;

/**
 * Created by peter on 12/9/15.
 */
public class NewsAPI extends APIClass {
    public NewsItem item;
    public NewsOverview overview;

    public NewsAPI(User user) {
        super(user);
    }

    public void getNewsItem(int id) {
        new APICall(user, "news/get").execute("id=" + id);
    }

    public void getOverview() {
        new APICall(user, "news/overview").execute();
    }

    public void getNewsTypes() {
        new APICall(user, "news/types/overview").execute();
    }

    public void jsonReadyHandler(JSONReadyEvent event) {
        NewsOverview newsItem = null;
        newsItem = gson.fromJson(event.json, NewsOverview.class);
        if(newsItem == null) {
            NewsItem news = null;
            news = gson.fromJson(event.json, NewsItem.class);
            if(news == null) {
                NewsType[] newsTypes = null;
                newsTypes = gson.fromJson(event.json, NewsType[].class);
                if(newsTypes == null) {
                    return; // TODO handle this as error
                } else {
                    EventBus.getDefault().post(new NewNewsTypesEvent(newsTypes));
                }
            } else {
                EventBus.getDefault().post(new NewNewsItemEvent(news));
            }
        } else {
            EventBus.getDefault().post(new NewNewsOverviewEvent(newsItem));
        }
    }
}
