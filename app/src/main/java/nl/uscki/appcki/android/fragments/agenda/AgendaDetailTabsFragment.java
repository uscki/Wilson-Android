package nl.uscki.appcki.android.fragments.agenda;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgendaDetailTabsFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private AgendaItem item;
    private Menu menu;

    private boolean foundUser = false;

    public static final int AGENDA = 0;
    public static final int DEELNEMERS = 1;
    public static final int COMMENTS = 2;

    public AgendaDetailTabsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tabs, container, false);
    }

}
