package me.blackwolf12333.appcki.fragments.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.blackwolf12333.appcki.fragments.PageableFragment;

/**
 * Created by peter on 5/22/16.
 */
public class HomeViewPagerAdapter extends FragmentPagerAdapter {

    public HomeViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        switch (position) {
            case PageableFragment.NEWS:
                PageableFragment newsFragment = new PageableFragment();
                bundle.putInt("type", PageableFragment.NEWS);
                newsFragment.setArguments(bundle);
                return newsFragment;
            case PageableFragment.AGENDA:
                PageableFragment agendaFragment = new PageableFragment();
                bundle.putInt("type", PageableFragment.AGENDA);
                agendaFragment.setArguments(bundle);
                return agendaFragment;
            case PageableFragment.ROEPHOEK:
                PageableFragment roephoekFragment = new PageableFragment();
                bundle.putInt("type", PageableFragment.ROEPHOEK);
                roephoekFragment.setArguments(bundle);
                return roephoekFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case PageableFragment.NEWS:
                return "Nieuws";
            case PageableFragment.AGENDA:
                return "Agenda";
            case PageableFragment.ROEPHOEK:
                return "Roephoek";
        }
        return "";
    }
}