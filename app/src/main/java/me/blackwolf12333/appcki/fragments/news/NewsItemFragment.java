package me.blackwolf12333.appcki.fragments.news;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.VolleyNews;
import me.blackwolf12333.appcki.events.NewsOverviewEvent;
import me.blackwolf12333.appcki.events.ShowProgressEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;
import me.blackwolf12333.appcki.generated.news.NewsItem;
import me.blackwolf12333.appcki.generated.news.NewsOverview;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class NewsItemFragment extends APIFragment {
    private VolleyNews newsAPI = new VolleyNews();
    private NewsOverview newsOverview = null;
    private NewsItem newsItem = null;

    private RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NewsItemFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        newsAPI.getNewsOverview();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newsitem_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.news_item_list);

        return view;
    }

    @Override
    public void refresh() {
        newsAPI.getNewsOverview();
    }

    public void onEventMainThread(NewsOverviewEvent event) {
        EventBus.getDefault().post(new ShowProgressEvent(false));
        if(event.newsOverview != null) {
            recyclerView.setAdapter(new NewsItemAdapter(event.newsOverview.getContent()));
        }
    }
}
