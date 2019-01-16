package nl.uscki.appcki.android.fragments.agenda;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import nl.uscki.appcki.android.fragments.comments.CommentsFragment;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;

/**
 * Created by peter on 5/29/16.
 */
public class AgendaDetailAdapter extends FragmentStatePagerAdapter {

    private AgendaDetailFragment details;
    private AgendaDeelnemersFragment participants;
    private AgendaCommentsFragment comments;

    public AgendaDetailAdapter(FragmentManager fm, int agendaID) {
        super(fm);

        details = new AgendaDetailFragment();
        participants = new AgendaDeelnemersFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(CommentsFragment.ARGUMENT_COMMENTS_TOPIC_ID, agendaID);
        comments = new AgendaCommentsFragment();
        comments.setArguments(bundle);
    }

    @Override
    public Fragment getItem(int position) {
        // TODO probably do some state check with resumed and stuff
        switch (position) {
            case AgendaDetailTabsFragment.AGENDA:
                return details;
            case AgendaDetailTabsFragment.DEELNEMERS:
                return participants;
            case AgendaDetailTabsFragment.COMMENTS:
                return comments;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}