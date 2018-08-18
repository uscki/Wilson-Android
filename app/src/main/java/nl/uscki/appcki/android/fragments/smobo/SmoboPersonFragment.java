package nl.uscki.appcki.android.fragments.smobo;

import android.content.Context;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.fragments.adapters.SmoboCommissieAdapter;
import nl.uscki.appcki.android.fragments.adapters.SmoboMediaAdapter;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.media.MediaFileMetaData;
import nl.uscki.appcki.android.generated.organisation.Committee;
import nl.uscki.appcki.android.generated.smobo.SmoboItem;
import nl.uscki.appcki.android.views.SmoboInfoWidget;
import retrofit2.Response;

/**
 * Created by peter on 4/5/17.
 */

public class SmoboPersonFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();
    AppCompatActivity context;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private Integer id;
    private int visibleThreshold = 9;
    private int lastVisibleItem, totalItemCount;
    protected Integer page = 0;
    private int pageSize = 20;
    private boolean scrollLoad;
    private boolean noMoreContent;

    private Callback<Pageable<MediaFileMetaData>> photosCallback = new Callback<Pageable<MediaFileMetaData>>() {
        @Override
        public void onSucces(Response<Pageable<MediaFileMetaData>> response) {
            noMoreContent = response.body().getLast();
            scrollLoad = false;
            ((BaseItemAdapter) mediaGrid.getAdapter()).addItems(response.body().getContent());
        }
    };

    @BindView(R.id.smobo_address_info)
    FrameLayout addressInfo;
    @BindView(R.id.smobo_email_info)
    FrameLayout emailInfo;
    @BindView(R.id.smobo_phone_info)
    FrameLayout phoneInfo;
    @BindView(R.id.smobo_mobile_info)
    FrameLayout mobileInfo;
    @BindView(R.id.smobo_birthday_info)
    FrameLayout birthdayInfo;
    @BindView(R.id.smobo_groups)
    RecyclerView smoboGroups;
    @BindView(R.id.smobo_media_gridview)
    HorizontalGridView mediaGrid;
    @BindView(R.id.smobo_swiperefresh)
    SwipeRefreshLayout swipeContainer;

    private Callback<SmoboItem> smoboCallback = new Callback<SmoboItem>() {
        @Override
        public void onSucces(Response<SmoboItem> response) {
            SmoboItem p = response.body();
            swipeContainer.setRefreshing(false);
            //scrollView.setVisibility(View.VISIBLE);

            createAddressInfoWidget(p);
            createEmailInfoWidget(p);
            createPhoneInfoWidget(p);
            createMobileInfoWidget(p);
            createBirthdayInfoWidget(p);

            ((BaseItemAdapter) smoboGroups.getAdapter()).update(p.getGroups());
        }
    };

    private void createAddressInfoWidget(SmoboItem p) {
        Bundle bundle = new Bundle();
        bundle.putString("maintext", p.getPerson().getAddress1() + "\n" + p.getPerson().getZipcode() + ", " + p.getPerson().getCity());
        bundle.putString("subtext", "Home");
        bundle.putInt("infotype", SmoboInfoWidget.InfoType.ADRESS.ordinal());

        SmoboInfoWidget widget = new SmoboInfoWidget();
        widget.setArguments(bundle);
        context.getSupportFragmentManager().beginTransaction()
                .replace(R.id.smobo_address_info, widget)
                .commit();
    }

    private void createEmailInfoWidget(SmoboItem p) {
        Bundle bundle = new Bundle();
        bundle.putString("maintext", p.getPerson().getEmailaddress());
        bundle.putString("subtext", "Home");
        bundle.putInt("infotype", SmoboInfoWidget.InfoType.EMAIL.ordinal());

        SmoboInfoWidget widget = new SmoboInfoWidget();
        widget.setArguments(bundle);
        context.getSupportFragmentManager().beginTransaction()
                .replace(R.id.smobo_email_info, widget)
                .commit();
    }

    private void createPhoneInfoWidget(SmoboItem p) {
        if(p.getPerson().getPhonenumber() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("maintext", p.getPerson().getPhonenumber());
            bundle.putString("subtext", "Home");
            bundle.putInt("infotype", SmoboInfoWidget.InfoType.PHONE.ordinal());

            SmoboInfoWidget widget = new SmoboInfoWidget();
            widget.setArguments(bundle);
            context.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.smobo_phone_info, widget)
                    .commit();
        } else {
            phoneInfo.setPadding(0,0,0,0);
        }
    }

    private void createMobileInfoWidget(SmoboItem p) {
        if (p.getPerson().getMobilenumber() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("maintext", p.getPerson().getMobilenumber());
            bundle.putString("subtext", "Mobile");
            bundle.putInt("infotype", SmoboInfoWidget.InfoType.PHONE.ordinal());

            SmoboInfoWidget widget = new SmoboInfoWidget();
            widget.setArguments(bundle);
            context.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.smobo_mobile_info, widget)
                    .commit();
        } else {
            mobileInfo.setPadding(0,0,0,0);
        }
    }

    private void createBirthdayInfoWidget(SmoboItem p) {
        if (p.getPerson().getMobilenumber() != null) {
            Bundle bundle = new Bundle();
            String birthday = p.getPerson().getBirthdate().toString("dd-MM-yyyy");
            bundle.putString("maintext", birthday);
            bundle.putString("subtext", "Verjaardag");
            bundle.putInt("infotype", SmoboInfoWidget.InfoType.BIRTHDAY.ordinal());

            SmoboInfoWidget widget = new SmoboInfoWidget();
            widget.setArguments(bundle);
            context.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.smobo_birthday_info, widget)
                    .commit();
        } else {
            mobileInfo.setPadding(0,0,0,0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_smobo_person, container, false);
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            this.id = getArguments().getInt("id");

            setupMediaGrid();
            setupSwipeContainer();
            smoboGroups.setAdapter(new SmoboCommissieAdapter(new ArrayList<Committee>()));

            swipeContainer.setRefreshing(true);
            Services.getInstance().smoboService.get(id).enqueue(smoboCallback);
            Services.getInstance().smoboService.photos(id, page, pageSize).enqueue(photosCallback);
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

    private void setupMediaGrid() {
        HorizontalGridView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mediaGrid.setLayoutManager(layoutManager);

        mediaGrid.setAdapter(new SmoboMediaAdapter(new ArrayList<MediaFileMetaData>()));
        mediaGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) mediaGrid.getLayoutManager();
                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!scrollLoad // we are not already loading a new page
                        && totalItemCount <= (lastVisibleItem + visibleThreshold) // we should be loading a new page
                        && !noMoreContent) { // there is still content to load
                    // End has been reached
                    scrollLoad = true;
                    Log.e("mediagrid", "Loading page: " + page);
                    page++; // update pageE
                    Services.getInstance().smoboService.photos(id, page, pageSize).enqueue(photosCallback);
                }
            }
        });
    }

    public void onSwipeRefresh() {
        Services.getInstance().smoboService.get(id).enqueue(smoboCallback);
        Services.getInstance().smoboService.photos(id, page, pageSize).enqueue(photosCallback);
    }

    @Override
    public void onAttach(Context context) {
        try{
            final AppCompatActivity activity = (AppCompatActivity) context;

            // Return the fragment manager
            this.context = activity;

            // If using the Support lib.
            // return activity.getSupportFragmentManager();

        } catch (ClassCastException e) {
            Log.d(TAG, "Can't get the fragment manager with this");
        }
        super.onAttach(context);
    }
}
