package nl.uscki.appcki.android.fragments.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import nl.uscki.appcki.android.activities.SmoboActivity;
import nl.uscki.appcki.android.fragments.home.HomeAgendaTab;
import nl.uscki.appcki.android.fragments.home.HomeFragment;
import nl.uscki.appcki.android.fragments.home.HomeNewsTab;
import nl.uscki.appcki.android.fragments.home.HomeRoephoekTab;
import nl.uscki.appcki.android.fragments.smobo.SmoboPersonFragment;
import nl.uscki.appcki.android.fragments.smobo.SmoboWickiFragment;

/**
 * Created by peter on 4/5/17.
 */

public class SmoboViewPagerAdapter extends FragmentStatePagerAdapter {
    int id;

    public SmoboViewPagerAdapter(FragmentManager fm, int id) {
        super(fm);
        this.id = id;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        switch (position) {
            case SmoboActivity.PERSON:
                SmoboPersonFragment fragment = new SmoboPersonFragment();
                fragment.setArguments(bundle);
                return fragment;
            case SmoboActivity.WICKI:
                SmoboWickiFragment fragment2 = new SmoboWickiFragment();
                fragment2.setArguments(bundle);
                return fragment2;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
