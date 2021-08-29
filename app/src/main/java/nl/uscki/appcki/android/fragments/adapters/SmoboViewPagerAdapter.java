package nl.uscki.appcki.android.fragments.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import nl.uscki.appcki.android.activities.SmoboActivity;
import nl.uscki.appcki.android.fragments.smobo.SmoboMentorTreeFragment;
import nl.uscki.appcki.android.fragments.smobo.SmoboPersonFragment;
import nl.uscki.appcki.android.fragments.smobo.SmoboWickiFragment;

/**
 * Created by peter on 4/5/17.
 */

public class SmoboViewPagerAdapter extends FragmentStateAdapter {
    private boolean hasWicki = false;

    public SmoboViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public SmoboViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public SmoboViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    private SmoboPersonFragment smoboPersonFragment;
    private SmoboMentorTreeFragment mentorTreeFragment;
    private SmoboWickiFragment smoboWickiFragment;

    public void setHasWicki(boolean hasWicki) {
        this.hasWicki = hasWicki;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            default:
            case SmoboActivity.PERSON:
                if(this.smoboPersonFragment == null) {
                    this.smoboPersonFragment = new SmoboPersonFragment();
                }
                return this.smoboPersonFragment;
            case SmoboActivity.MENTOR_TREE:
                if(this.mentorTreeFragment == null) {
                    this.mentorTreeFragment = new SmoboMentorTreeFragment();
                }
                return this.mentorTreeFragment;
            case SmoboActivity.WICKI:
                if(this.smoboWickiFragment == null) {
                    this.smoboWickiFragment = new SmoboWickiFragment();
                }
                return this.smoboWickiFragment;
        }
    }

    @Override
    public int getItemCount() {
        return hasWicki ? 3 : 2;
    }
}
