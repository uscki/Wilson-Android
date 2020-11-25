package nl.uscki.appcki.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;

/**
 * Created by peter on 12/30/16.
 */

public abstract class RefreshableFragment extends Fragment {
    private BaseItemAdapter adapter;
    protected RecyclerView recyclerView;
    protected SwipeRefreshLayout swipeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pageable, container, false);
        setupSwipeContainer(view);
        setupRecyclerView(view);
        return view;
    }

    protected void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
    }

    protected void setupSwipeContainer(View view) {
        swipeContainer = view.findViewById(R.id.refreshContainer);
        swipeContainer.setOnRefreshListener(this::onSwipeRefresh);

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        swipeContainer.setRefreshing(false);
    }

    public BaseItemAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(BaseItemAdapter adapter) {
        this.adapter = adapter;
    }

    public abstract void onSwipeRefresh();
}
