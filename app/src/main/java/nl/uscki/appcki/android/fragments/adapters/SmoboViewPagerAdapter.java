package nl.uscki.appcki.android.fragments.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import nl.uscki.appcki.android.activities.SmoboActivity;
import nl.uscki.appcki.android.fragments.smobo.SmoboPersonFragment;
import nl.uscki.appcki.android.fragments.smobo.SmoboWickiFragment;

/**
 * Created by peter on 4/5/17.
 */

public class SmoboViewPagerAdapter extends FragmentStateAdapter {
    int id;
    private boolean hasWicki = false;

    public SmoboViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, int id) {
        super(fragmentActivity);
        this.id = id;
    }

    public SmoboViewPagerAdapter(@NonNull Fragment fragment, int id) {
        super(fragment);
        this.id = id;
    }

    public SmoboViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, int id) {
        super(fragmentManager, lifecycle);
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

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        switch (position) {
            default:
            case SmoboActivity.PERSON:
                if(this.smoboPersonFragment == null) {
                    this.smoboPersonFragment = new SmoboPersonFragment();
                }
                this.smoboPersonFragment.setArguments(bundle);
                return this.smoboPersonFragment;
            case SmoboActivity.WICKI:
                if(this.smoboWickiFragment == null) {
                    this.smoboWickiFragment = new SmoboWickiFragment();
                }
                this.smoboWickiFragment.setArguments(bundle);
                return this.smoboWickiFragment;
        }
    }

    @Override
    public int getItemCount() {
        return hasWicki ? 2 : 1;
    }
}
