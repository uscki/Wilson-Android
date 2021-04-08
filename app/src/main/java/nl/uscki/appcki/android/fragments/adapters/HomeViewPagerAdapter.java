package nl.uscki.appcki.android.fragments.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.fragments.home.HomeAgendaTab;
import nl.uscki.appcki.android.fragments.home.HomeFragment;
import nl.uscki.appcki.android.fragments.home.HomeNewsTab;
import nl.uscki.appcki.android.fragments.home.HomeRoephoekTab;


/**
 * Created by peter on 5/22/16.
 */
public class HomeViewPagerAdapter extends FragmentStatePagerAdapter {

    private Context context;

    public HomeViewPagerAdapter(Context context, @NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.context = context;
    }

    private int[] headers = new int[] {
            R.string.home_fragment_tab_header_news,
            R.string.home_fragment_tab_header_agenda,
            R.string.home_fragment_tab_header_shoutbox
    };

    private HomeNewsTab homeNewsTab;
    private HomeAgendaTab homeAgendaTab;
    private HomeRoephoekTab homeRoephoekTab;

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case HomeFragment.NEWS:
                if(this.homeNewsTab == null)
                    this.homeNewsTab = new HomeNewsTab();
                return this.homeNewsTab;
            case HomeFragment.AGENDA:
                if(this.homeAgendaTab == null)
                    this.homeAgendaTab = new HomeAgendaTab();
                return this.homeAgendaTab;
            case HomeFragment.ROEPHOEK:
                if(this.homeRoephoekTab == null)
                    this.homeRoephoekTab = new HomeRoephoekTab();
                return this.homeRoephoekTab;
        }

        return null;
    }



    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return this.context.getString(this.headers[position]);
    }

    @Override
    public int getCount() {
        return 3;
    }
}