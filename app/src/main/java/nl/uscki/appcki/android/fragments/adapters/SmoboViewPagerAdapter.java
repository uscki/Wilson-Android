package nl.uscki.appcki.android.fragments.adapters;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import nl.uscki.appcki.android.activities.SmoboActivity;
import nl.uscki.appcki.android.fragments.smobo.SmoboMentorTreeFragment;
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
            case SmoboActivity.MENTOR_TREE:
                SmoboMentorTreeFragment fragment3 = new SmoboMentorTreeFragment();
                fragment3.setArguments(bundle);
                return fragment3;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
