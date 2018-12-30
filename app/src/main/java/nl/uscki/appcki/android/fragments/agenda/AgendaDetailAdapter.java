package nl.uscki.appcki.android.fragments.agenda;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import nl.uscki.appcki.android.generated.agenda.AgendaItem;

/**
 * Created by peter on 5/29/16.
 */
public class AgendaDetailAdapter extends FragmentStatePagerAdapter {

    private AgendaDetailFragment details;
    private AgendaDeelnemersFragment participants;
    private AgendaCommentsFragment comments;

    public AgendaDetailAdapter(FragmentManager fm) {
        super(fm);
        details = new AgendaDetailFragment();
        participants = new AgendaDeelnemersFragment();
        comments = new AgendaCommentsFragment();
    }

    @Override
    public Fragment getItem(int position) {
        // TODO probably do some state check with resumed and stuff
//        Bundle bundle = new Bundle();
//        bundle.putInt("id", item.getId());
        switch (position) {
            case AgendaDetailTabsFragment.AGENDA:
//                AgendaDetailFragment fragment = new AgendaDetailFragment();
//                fragment.setArguments(bundle);
//                return fragment;
                return details;
            case AgendaDetailTabsFragment.DEELNEMERS:
//                AgendaDeelnemersFragment fragment1 = new AgendaDeelnemersFragment();
//                fragment1.setArguments(bundle);
//                return fragment1;
                return participants;
            case AgendaDetailTabsFragment.COMMENTS:
//                AgendaCommentsFragment fragment2 = new AgendaCommentsFragment();
//                fragment2.setArguments(bundle);
//                return fragment2;
                return comments;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}