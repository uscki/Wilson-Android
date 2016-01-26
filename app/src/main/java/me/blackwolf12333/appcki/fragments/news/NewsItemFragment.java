package me.blackwolf12333.appcki.fragments.news;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.MainActivity;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.api.NewsAPI;
import me.blackwolf12333.appcki.events.NewNewsItemEvent;
import me.blackwolf12333.appcki.events.NewNewsOverviewEvent;
import me.blackwolf12333.appcki.events.NewNewsTypesEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;
import me.blackwolf12333.appcki.fragments.ProgressActivity;
import me.blackwolf12333.appcki.generated.NewsItem;
import me.blackwolf12333.appcki.generated.NewsOverview;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class NewsItemFragment extends APIFragment {

    private static final String ARG_USER = "user";

    private int mColumnCount = 1;
    private User user;
    private NewsAPI newsAPI;
    private NewsOverview newsOverview = null;
    private NewsItem newsItem = null;

    private RecyclerView recyclerView;

    private ProgressActivity activity;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NewsItemFragment() {

    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static NewsItemFragment newInstance(User user) {
        NewsItemFragment fragment = new NewsItemFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(ARG_USER);
            newsAPI = new NewsAPI(user);
        }
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ProgressActivity)) {
            throw new IllegalArgumentException("This fragments acitivity must implement interface ProgressActivity");
        }
        this.activity = (ProgressActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newsitem_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            if(newsOverview != null) {
                //recyclerView.setAdapter(new NewsItemAdapter(newsOverview.getContent()));
            }
        }

        getNewsAPI().getOverview();

        return view;
    }

    private NewsAPI getNewsAPI() {
        if(this.newsAPI == null) {
            this.newsAPI = new NewsAPI(MainActivity.user);
        }
        return this.newsAPI;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onEventMainThread(NewNewsOverviewEvent event) {
        activity.showProgress(false);
        if(event.newsOverview != null) {
            System.out.println(event.newsOverview.getContent().size());
            recyclerView.setAdapter(new NewsItemAdapter(event.newsOverview.getContent()));
        }
    }

    public void onEventMainThread(NewNewsItemEvent event) {
        activity.showProgress(false);
        if(event.newsItem != null) {
            // TODO
            //recyclerView.setAdapter(new MyNewsItemRecyclerViewAdapter(event.newsOverview.getContent(), mListener));
        }
    }

    public void onEventMainThread(NewNewsTypesEvent event) {
        if(event.newsTypes != null) {
            //TODO whatever hiermee gedaan moet worden...
        }
    }

    public void setUser(User user) {
        this.user = user;
        newsAPI = new NewsAPI(user);
    }
}
