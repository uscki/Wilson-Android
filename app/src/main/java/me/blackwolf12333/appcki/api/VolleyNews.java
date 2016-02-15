package me.blackwolf12333.appcki.api;

import com.android.volley.Response;

import java.util.HashMap;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.api.common.VolleyAPI;
import me.blackwolf12333.appcki.events.NewsItemEvent;
import me.blackwolf12333.appcki.events.NewsOverviewEvent;
import me.blackwolf12333.appcki.generated.news.NewsItem;
import me.blackwolf12333.appcki.generated.news.NewsOverview;

/**
 * Created by peter on 2/7/16.
 */
public class VolleyNews extends VolleyAPI {
    public void getNewsOverview() {
        this.apiCall(new NewsOverviewCall());
    }

    public void getNewsItem(Integer id) {
        this.apiCall(new NewsItemCall(id));
    }

    public class NewsOverviewCall extends Call<NewsOverview> {
        public NewsOverviewCall() {
            this.url = "news/overview";
            this.arguments = new HashMap<>();
            this.arguments.put("sort", "posteddate,desc");
            this.type = NewsOverview.class;
            this.responseListener = new Response.Listener<NewsOverview>() {
                @Override
                public void onResponse(NewsOverview response) {
                    EventBus.getDefault().post(new NewsOverviewEvent(response));
                }
            };
        }
    }

    public class NewsItemCall extends Call<NewsItem> {
        public NewsItemCall(Integer id) {
            this.url = "news/get";
            this.arguments = new HashMap<>();
            this.arguments.put("id", id);
            this.type = NewsItem.class;
            this.responseListener = new Response.Listener<NewsItem>() {
                @Override
                public void onResponse(NewsItem response) {
                    EventBus.getDefault().post(new NewsItemEvent(response));
                }
            };
        }
    }
}
