package me.blackwolf12333.appcki.api;

import android.util.Log;

import com.google.gson.Gson;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.UserHelper;
import me.blackwolf12333.appcki.events.JSONReadyEvent;
import me.blackwolf12333.appcki.events.NewsItemEvent;
import me.blackwolf12333.appcki.events.NewsOverviewEvent;
import me.blackwolf12333.appcki.events.NewsTypesEvent;
import me.blackwolf12333.appcki.events.ShowProgressEvent;
import me.blackwolf12333.appcki.generated.NewsItem;
import me.blackwolf12333.appcki.generated.NewsOverview;
import me.blackwolf12333.appcki.generated.NewsType;
import me.blackwolf12333.appcki.generated.ServerError;

/**
 * Created by peter on 12/9/15.
 */
public class NewsAPI {
    User user;
    Gson gson;

    public NewsAPI() {
        this.user = UserHelper.getInstance().getUser();
        this.gson = new Gson();
        EventBus.getDefault().register(this);
    }

    public void getNewsItem(int id) {
        EventBus.getDefault().post(new ShowProgressEvent(true));
        new APICall(user, "news/get").execute("id=" + id);
    }

    public void getOverview() {
        EventBus.getDefault().post(new ShowProgressEvent(true));
        new APICall(user, "news/overview").execute();
    }

    public void getNewsTypes() {
        new APICall(user, "news/types/overview").execute();
    }

    public void onEventMainThread(JSONReadyEvent event) {
        ServerError error = gson.fromJson(event.json, ServerError.class);

        // status is nooit null bij een server error, dus hiermee kunnen we checken of dit het goede
        // object was voor deserialization.
        if(error != null && error.getStatus() != null) {
            Log.e("APIClass", "error getting data from BadWolf with call: " + event.call + "\nserver error: " + error.toString());
            //TODO handle errors
        } else {
            if(event.call.startsWith("news")) {
                jsonReadyHandler(event);
            }
        }
    }

    public void jsonReadyHandler(JSONReadyEvent event) {
        switch(event.call) {
            case "news/overview":
                NewsOverview newsItem = gson.fromJson(event.json, NewsOverview.class);
                EventBus.getDefault().post(new NewsOverviewEvent(newsItem));
                break;
            case "news/get":
                NewsItem news = gson.fromJson(event.json, NewsItem.class);
                EventBus.getDefault().post(new NewsItemEvent(news));
                break;
            case "news/types/overview":
                Log.i("NewsAPI", event.json.toString());
                NewsType[] newsTypes = gson.fromJson(event.json, NewsType[].class);
                EventBus.getDefault().post(new NewsTypesEvent(newsTypes));
            default:
                Log.i("NewsAPI", "Unhandled api call: " + event.call);
        }
    }
}
