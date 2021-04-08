package nl.uscki.appcki.android.fragments.forum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import nl.uscki.appcki.android.R;

public class ForumLandingFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_caption_contest_shared, container, false);
        this.tabLayout = view.findViewById(R.id.media_landing_tab_layout);
        this.viewPager = view.findViewById(R.id.media_landing_viewpager);
        this.viewPager.setAdapter(new ForumLandingPageFragmentAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                default:
                case 0:
                    tab.setText(getText(R.string.wilson_media_forum_landing_page_recent_header));
                    break;
                case 1:
                    tab.setText(getString(R.string.wilson_media_forum_landing_page_fora_header));
                    break;
            }
        }).attach();

        return view;
    }

    static class ForumLandingPageFragmentAdapter extends FragmentStateAdapter {

        public ForumLandingPageFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        public ForumLandingPageFragmentAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        public ForumLandingPageFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        private ForumRecentPostsFragment forumRecentPostFragment;
        private ForumOverviewFragment forumOverviewFragment;

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                default:
                    if (forumRecentPostFragment == null) {
                        forumRecentPostFragment = new ForumRecentPostsFragment();
                    }
                    return forumRecentPostFragment;
                case 1:
                    if (forumOverviewFragment == null) {
                        forumOverviewFragment = new ForumOverviewFragment();
                    }
                    return forumOverviewFragment;
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
