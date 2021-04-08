package nl.uscki.appcki.android.fragments.adapters;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import nl.uscki.appcki.android.activities.SmoboActivity;
import nl.uscki.appcki.android.fragments.smobo.SmoboPersonFragment;
import nl.uscki.appcki.android.fragments.smobo.SmoboWickiFragment;

/**
 * Created by peter on 4/5/17.
 */

public class SmoboViewPagerAdapter extends FragmentStatePagerAdapter {
    int id;
    private boolean hasWicki = true;

    public SmoboViewPagerAdapter(FragmentManager fm, int id) {
        super(fm);
        this.id = id;
    }

    private SmoboPersonFragment smoboPersonFragment;
    private SmoboWickiFragment smoboWickiFragment;

    public void setHasWicki(boolean hasWicki) {
        this.hasWicki = hasWicki;
        notifyDataSetChanged();
    }

    public SmoboPersonFragment getSmoboPersonFragment() {
        return smoboPersonFragment;
    }

    public SmoboWickiFragment getSmoboWickiFragment() {
        return smoboWickiFragment;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        switch (position) {
            case SmoboActivity.PERSON:
                if(this.smoboPersonFragment == null) {
                    this.smoboPersonFragment = new SmoboPersonFragment();
                }
                this.smoboPersonFragment.setArguments(bundle);
                if(this.smoboPersonFragment.getP() != null && this.smoboPersonFragment.getP().getWickiPage() == null) {
                    this.setHasWicki(false);
                }
                return this.smoboPersonFragment;
            case SmoboActivity.WICKI:
                if(this.smoboWickiFragment == null) {
                    this.smoboWickiFragment = new SmoboWickiFragment();
                }
                this.smoboWickiFragment.setArguments(bundle);
                return this.smoboWickiFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return hasWicki ? 2 : 1;
    }
}
