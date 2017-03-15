package nl.uscki.appcki.android.fragments.meeting.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.gson.Gson;

import nl.uscki.appcki.android.fragments.meeting.MeetingDetailFragment;
import nl.uscki.appcki.android.fragments.meeting.MeetingDetailTabsFragment;
import nl.uscki.appcki.android.fragments.meeting.MeetingParticipantsFragment;
import nl.uscki.appcki.android.fragments.meeting.MeetingPlannerFragment;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;

/**
 * Created by peter on 5/29/16.
 */
public class MeetingDetailAdapter extends FragmentStatePagerAdapter {
    MeetingItem item;

    public MeetingDetailAdapter(FragmentManager fm, MeetingItem item) {
        super(fm);
        this.item = item;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        Fragment fragment;
        Gson gson = new Gson();
        String json = gson.toJson(item, MeetingItem.class);
        bundle.putString("item", json);
        switch (position) {
            case MeetingDetailTabsFragment.ITEM:
                if (item.getMeeting().getStartdate() != null) {
                    fragment = new MeetingDetailFragment();
                } else {
                    fragment = new MeetingPlannerFragment();
                }
                fragment.setArguments(bundle);
                return fragment;
            case MeetingDetailTabsFragment.AANWEZIG:
                bundle.putBoolean("aanwezig", true);
                fragment = new MeetingParticipantsFragment();
                fragment.setArguments(bundle);
                return fragment;
            case MeetingDetailTabsFragment.AFWEZIG:
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