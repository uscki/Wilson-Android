package me.blackwolf12333.appcki.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.blackwolf12333.appcki.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageableFragment extends APIFragment {


    public PageableFragment() {
        // Required empty public constructor
    }

    @Override
    public void refresh() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pageable, container, false);
    }

}