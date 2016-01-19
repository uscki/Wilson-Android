package me.blackwolf12333.appcki.fragments.news;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.api.NewsAPI;
import me.blackwolf12333.appcki.events.NewNewsItemEvent;
import me.blackwolf12333.appcki.events.NewNewsOverviewEvent;
import me.blackwolf12333.appcki.events.NewNewsTypesEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class NewsItemFragment extends APIFragment {

    private static final String ARG_USER = "user";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private User user;
    private NewsAPI newsAPI;
    private NewsAPI.NewsOverview newsOverview = null;
    private NewsAPI.NewsItem newsItem = null;

    private RecyclerView recyclerView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newsitem_list, container, false);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

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
                recyclerView.setAdapter(new MyNewsItemRecyclerViewAdapter(newsOverview.getContent(), mListener));
            }
            this.content = recyclerView;
        }

        showProgress(true);
        newsAPI.getOverview();

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onEventMainThread(NewNewsOverviewEvent event) {
        showProgress(false);
        if(event.newsOverview != null) {
            recyclerView.setAdapter(new MyNewsItemRecyclerViewAdapter(event.newsOverview.getContent(), mListener));
        }
    }

    public void onEventMainThread(NewNewsItemEvent event) {
        showProgress(false);
        if(event.newsItem != null) {
            // TODO
            //recyclerView.setAdapter(new MyNewsItemRecyclerViewAdapter(event.newsOverview.getContent(), mListener));
        }
    }

    public void onEventMainThread(NewNewsTypesEvent event) {
        showProgress(false);
        if(event.newsTypes != null) {
            //TODO whatever hiermee gedaan moet worden...
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(NewsAPI.NewsItem item);
    }
}
