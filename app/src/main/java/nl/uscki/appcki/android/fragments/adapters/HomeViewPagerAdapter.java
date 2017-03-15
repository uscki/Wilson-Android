package nl.uscki.appcki.android.fragments.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

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
        switch (position) {
            case HomeFragment.NEWS:
                return new HomeNewsTab();
            case HomeFragment.AGENDA:
                return new HomeAgendaTab();
            case HomeFragment.ROEPHOEK:
                return new HomeRoephoekTab();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}