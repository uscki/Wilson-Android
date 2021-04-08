package nl.uscki.appcki.android.fragments.meeting.adapter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import nl.uscki.appcki.android.activities.MeetingActivity;
import nl.uscki.appcki.android.fragments.meeting.MeetingDetailFragment;
import nl.uscki.appcki.android.fragments.meeting.MeetingParticipantsFragment;
import nl.uscki.appcki.android.fragments.meeting.MeetingPlannerFragment;

/**
 * Created by peter on 5/29/16.
 */
public class MeetingDetailAdapter extends FragmentStatePagerAdapter {

    public static final int ITEM = 0;
    public static final int AANWEZIG = 1;
    public static final int AFWEZIG = 2;

    private MeetingActivity.PlannerView activeView;

    public MeetingDetailAdapter(FragmentManager fm, MeetingActivity.PlannerView activeView) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.activeView = activeView;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        Fragment fragment;
        switch (position) {
            case ITEM:
                if (activeView.equals(MeetingActivity.PlannerView.PLANNED)) {
                    fragment = new MeetingDetailFragment();
                } else {
                    fragment = new MeetingPlannerFragment();
                }
                fragment.setArguments(bundle);
                return fragment;
            case AANWEZIG:
                bundle.putBoolean("aanwezig", true);
                fragment = new MeetingParticipantsFragment();
                fragment.setArguments(bundle);
                return fragment;
            case AFWEZIG:
                bundle.putBoolean("aanwezig", false);
                fragment = new MeetingParticipantsFragment();
                fragment.setArguments(bundle);
                return fragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}