package nl.uscki.appcki.android.fragments.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.home.HomeAgendaTab;
import nl.uscki.appcki.android.fragments.home.HomeFragment;
import nl.uscki.appcki.android.fragments.home.HomeNewsTab;
import nl.uscki.appcki.android.fragments.home.HomeRoephoekTab;

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
            case HomeFragment.NEWS:
                PageableFragment newsFragment = new HomeNewsTab();
                return newsFragment;
            case HomeFragment.AGENDA:
                PageableFragment agendaFragment = new HomeAgendaTab();
                return agendaFragment;
            case HomeFragment.ROEPHOEK:
                PageableFragment roephoekFragment = new HomeRoephoekTab();
                return roephoekFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}