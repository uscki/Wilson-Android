package nl.uscki.appcki.android.fragments.agenda;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.gson.Gson;

import nl.uscki.appcki.android.generated.agenda.AgendaItem;

/**
 * Created by peter on 5/29/16.
 */
public class AgendaDetailAdapter extends FragmentStatePagerAdapter {
    AgendaItem item;

    public AgendaDetailAdapter(FragmentManager fm, AgendaItem item) {
        super(fm);
        this.item = item;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String json = gson.toJson(item, AgendaItem.class);
        bundle.putString("item", json);
        switch (position) {
            case AgendaDetailTabsFragment.AGENDA:
                AgendaDetailFragment fragment = new AgendaDetailFragment();
                fragment.setArguments(bundle);
                return fragment;
            case AgendaDetailTabsFragment.DEELNEMERS:
                AgendaDeelnemersFragment fragment1 = new AgendaDeelnemersFragment();
                fragment1.setArguments(bundle);
                return fragment1;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}