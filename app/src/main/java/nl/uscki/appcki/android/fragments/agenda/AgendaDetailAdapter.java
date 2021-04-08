package nl.uscki.appcki.android.fragments.agenda;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import nl.uscki.appcki.android.fragments.comments.CommentsFragment;

/**
 * Created by peter on 5/29/16.
 */
public class AgendaDetailAdapter extends FragmentStatePagerAdapter {

    public static final int AGENDA_DETAILS_TAB_POSITION = 0;
    public static final int AGENDA_PARTICIPANTS_TAB_POSITION = 1;
    public static final int AGENDA_COMMENTS_TAB_POSITION = 2;

    private AgendaDetailFragment details;
    private AgendaDeelnemersFragment participants;
    private AgendaCommentsFragment comments;

    public AgendaDetailAdapter(FragmentManager fm, int agendaID) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

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
            case AgendaDetailAdapter.AGENDA_DETAILS_TAB_POSITION:
                return details;
            case AgendaDetailAdapter.AGENDA_PARTICIPANTS_TAB_POSITION:
                return participants;
            case AgendaDetailAdapter.AGENDA_COMMENTS_TAB_POSITION:
                return comments;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}