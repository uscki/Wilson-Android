package nl.uscki.appcki.android.fragments.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import nl.uscki.appcki.android.fragments.HomeSubFragments;
import nl.uscki.appcki.android.fragments.PageableFragment;

/**
 * Created by peter on 5/22/16.
 */
public class HomeViewPagerAdapter extends FragmentStatePagerAdapter {

    public HomeViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        switch (position) {
            case HomeSubFragments.NEWS:
                PageableFragment newsFragment = new HomeSubFragments();
                bundle.putInt("type", HomeSubFragments.NEWS);
                newsFragment.setArguments(bundle);
                return newsFragment;
            case HomeSubFragments.AGENDA:
                PageableFragment agendaFragment = new HomeSubFragments();
                bundle.putInt("type", HomeSubFragments.AGENDA);
                agendaFragment.setArguments(bundle);
                return agendaFragment;
            case HomeSubFragments.ROEPHOEK:
                PageableFragment roephoekFragment = new HomeSubFragments();
                bundle.putInt("type", HomeSubFragments.ROEPHOEK);
                roephoekFragment.setArguments(bundle);
                return roephoekFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}