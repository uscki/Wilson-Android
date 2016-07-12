package me.blackwolf12333.appcki.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.Services;
import me.blackwolf12333.appcki.events.NewsItemEvent;
import me.blackwolf12333.appcki.generated.news.NewsItem;
import me.blackwolf12333.appcki.views.BBTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsDetailFragment extends Fragment {
    BBTextView content;

    public NewsDetailFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Services.getInstance().newsService.get(getArguments().getInt("id")).enqueue(new Callback<NewsItem>() {
            @Override
            public void onResponse(Call<NewsItem> call, Response<NewsItem> response) {
                content.setText(response.body().getShorttext());
            }

            @Override
            public void onFailure(Call<NewsItem> call, Throwable t) {
                //TODO
            }
        });
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_detail, container, false);
        content = (BBTextView) view.findViewById(R.id.news_item_content);
        return view;
    }

    public void onEventMainThread(NewsItemEvent event) {
        content.setText(event.newsItem.getShorttext());
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
