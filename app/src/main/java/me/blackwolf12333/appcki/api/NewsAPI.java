package me.blackwolf12333.appcki.api;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.events.JSONReadyEvent;
import me.blackwolf12333.appcki.events.NewNewsItemEvent;
import me.blackwolf12333.appcki.events.NewNewsOverviewEvent;
import me.blackwolf12333.appcki.events.NewNewsTypesEvent;

/**
 * Created by peter on 12/9/15.
 */
public class NewsAPI {
    private User user;
    private Gson gson;

    public NewsItem item;
    public NewsOverview overview;

    public NewsAPI(User user) {
        this.user = user;
        this.gson = new Gson();
        EventBus.getDefault().register(this);
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

    public void onEventMainThread(JSONReadyEvent event) {
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

    public class NewsOverview {
        @SerializedName("totalElements")
        int totalElements;
        @SerializedName("totalPages")
        int totalPages;
        @SerializedName("last")
        boolean last;
        @SerializedName("first")
        boolean first;
        @SerializedName("sort")
        Object sort;
        @SerializedName("numberOfElements")
        int numberOfElements;
        @SerializedName("size")
        int size;
        @SerializedName("number")
        int number;
        @SerializedName("content")
        NewsItem[] content;

        public List<NewsItem> getContent() {
            return Arrays.asList(content);
        }



        @Override
        public String toString() {
            return "number: " + number;
        }
    }

    public class NewsItem {
        @SerializedName("id")
        public int id;
        @SerializedName("shorttext")
        public String shorttext;
        @SerializedName("longtext")
        public String longtext;
        @SerializedName("title")
        public String title;
        @SerializedName("posteddate")
        public String postedDate;
        @SerializedName("person")
        public PersonAPI.Person person;
        @SerializedName("link")
        public String link;
        @SerializedName("sticky")
        public String sticky;
        @SerializedName("type")
        public NewsType type;

        @Override
        public String toString() {
            return "id: " + id;
        }
    }

    public class NewsType {
        @SerializedName("id")
        int id;
        @SerializedName("name")
        String name;
        @SerializedName("icon")
        String icon;

        @Override
        public String toString() {
            return "id: " + id;
        }
    }
}
