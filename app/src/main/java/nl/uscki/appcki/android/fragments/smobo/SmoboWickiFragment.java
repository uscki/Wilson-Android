package nl.uscki.appcki.android.fragments.smobo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.generated.smobo.SmoboItem;
import nl.uscki.appcki.android.views.BBTextView;
import retrofit2.Response;

/**
 * Created by peter on 4/5/17.
 */

public class SmoboWickiFragment extends Fragment {
    int id;

    private Callback<SmoboItem> smoboCallback = new Callback<SmoboItem>() {
        @Override
        public void onSucces(Response<SmoboItem> response) {
            SmoboItem p = response.body();
            swipeContainer.setRefreshing(false);

            if (p.getWickiPage() != null)
                wickiText.setText(p.getWickiPage(), true);
        }
    };

    @BindView(R.id.smobo_wicki_text)
    BBTextView wickiText;
    @BindView(R.id.smobo_wicki_swiperefresh)
    SwipeRefreshLayout swipeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_smobo_wicki, container, false);
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            this.id = getArguments().getInt("id");

            setupSwipeContainer();

            Services.getInstance().smoboService.get(id).enqueue(smoboCallback);
        }

        return view;
    }

    protected void setupSwipeContainer() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onSwipeRefresh();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setRefreshing(false);
    }

    public void onSwipeRefresh() {
        Services.getInstance().smoboService.get(id).enqueue(smoboCallback);
    }
}
