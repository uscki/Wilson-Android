package me.blackwolf12333.appcki.fragments.news;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v7.app.ActionBar;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.events.NewsItemEvent;
import me.blackwolf12333.appcki.events.ShowProgressEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;
import me.blackwolf12333.appcki.generated.NewsItem;
import me.blackwolf12333.appcki.api.VolleyNews;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsItemDetailFragment extends APIFragment {
    //NewsAPI newsAPI = new NewsAPI();
    VolleyNews newsAPI = new VolleyNews();

    TextView itemTitle;
    TextView itemContent;

    AppCompatActivity act;
    ActionBar actionBar;

    public NewsItemDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Integer id = getArguments().getInt("id");
        newsAPI.getNewsItem(id);

        act = (AppCompatActivity) getActivity();

        View view = inflater.inflate(R.layout.fragment_news_item_detail, container, false);
        actionBar = act.getSupportActionBar();
        itemContent = (TextView) view.findViewById(R.id.newsitem_content);

        return view;
    }

    private void updateView(NewsItem item) {
        itemContent.setText(item.getShorttext());
    }

    public void onEventMainThread(NewsItemEvent event) {
        EventBus.getDefault().post(new ShowProgressEvent(false));
        actionBar.setTitle(event.newsItem.getTitle());
        updateView(event.newsItem);
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

}
