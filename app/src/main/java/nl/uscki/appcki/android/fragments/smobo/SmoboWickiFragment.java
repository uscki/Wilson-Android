package nl.uscki.appcki.android.fragments.smobo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.SmoboActivity;
import nl.uscki.appcki.android.events.DetailItemUpdatedEvent;
import nl.uscki.appcki.android.generated.smobo.SmoboItem;
import nl.uscki.appcki.android.views.BBTextView;

/**
 * Created by peter on 4/5/17.
 */

public class SmoboWickiFragment extends Fragment {
    private SmoboActivity activity;

    BBTextView wickiText;
    SwipeRefreshLayout swipeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_smobo_wicki, container, false);
        wickiText = view.findViewById(R.id.smobo_wicki_text);
        swipeContainer = view.findViewById(R.id.smobo_wicki_swiperefresh);

        if(getActivity() instanceof SmoboActivity) {
            this.activity = (SmoboActivity) getActivity();
        }

        if (this.activity != null) {
            setupSwipeContainer();
            if(this.activity.getP() != null) {
                updateItem(this.activity.getP());
            }
        }

        return view;
    }

    private void updateItem(SmoboItem p) {
        if (p.getWickiPage() != null)
            wickiText.setText(p.getWickiPage(), true);
    }

    public void onEventMainThread(DetailItemUpdatedEvent<SmoboItem> item) {
        swipeContainer.setRefreshing(false);
        updateItem(item.getUpdatedItem());
    }

    protected void setupSwipeContainer() {
        swipeContainer.setOnRefreshListener(() -> this.activity.refreshSmoboItem());

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setRefreshing(false);
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
