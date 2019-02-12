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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
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
import nl.uscki.appcki.android.helpers.ContactHelper;
import nl.uscki.appcki.android.helpers.DateRangeHelper;
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
    private SmoboItem p;

    private Callback<Pageable<MediaFileMetaData>> photosCallback = new Callback<Pageable<MediaFileMetaData>>() {
        @Override
        public void onSucces(Response<Pageable<MediaFileMetaData>> response) {
            noMoreContent = response.body().getLast();
            scrollLoad = false;
            ((BaseItemAdapter) mediaGrid.getAdapter()).addItems(response.body().getContent());
        }

        @Override
        public void onError(Response<Pageable<MediaFileMetaData>> response) {
            super.onError(response);
            noMoreContent = true;
            scrollLoad = false;
        }
    };

    private Timer timer;
    private TimerTask timerTask;

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
    @BindView(R.id.smobo_homepage_info)
    FrameLayout homepageInfo;
    @BindView(R.id.smobo_groups)
    RecyclerView smoboGroups;
    @BindView(R.id.smobo_media_gridview)
    HorizontalGridView mediaGrid;
    @BindView(R.id.smobo_swiperefresh)
    SwipeRefreshLayout swipeContainer;
    @BindView(R.id.datable_range_info)
    RelativeLayout datableRangeInfo;
    @BindView(R.id.datable_range_icon)
    ImageView datableRangeIcon;
    @BindView(R.id.datable_range_love_status)
    TextView loveStatus;
    @BindView(R.id.datable_range_countdown)
    TextView countdownText;

    private final Callback<SmoboItem> smoboCallback = new Callback<SmoboItem>() {
        @Override
        public void onSucces(Response<SmoboItem> response) {
            p = response.body();
            swipeContainer.setRefreshing(false);

            createAddressInfoWidget(p);
            createEmailInfoWidget(p);
            createPhoneInfoWidget(p);
            createMobileInfoWidget(p);
            createBirthdayInfoWidget(p);
            createWebsiteInfoWidget(p);
            createCountdown();

            ((BaseItemAdapter) smoboGroups.getAdapter()).update(p.getGroups());
        }

        @Override
        public void onError(Response<SmoboItem> response) {
            super.onError(response);
            swipeContainer.setRefreshing(false);
        }
    };

    private boolean stringVisible(String s) {
        return s != null && s.trim().length() > 0;
    }

    private void createAddressInfoWidget(SmoboItem p) {
        String address = p.getPerson().getFullAddres(true);

        if(stringVisible(address)) {
            Bundle bundle = new Bundle();
            bundle.putString("maintext", address);
            bundle.putString("subtext", "Home");
            bundle.putInt("infotype", SmoboInfoWidget.InfoType.ADRESS.ordinal());

            SmoboInfoWidget widget = new SmoboInfoWidget();
            widget.setArguments(bundle);
            context.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.smobo_address_info, widget)
                    .commitAllowingStateLoss();
        } else {
            addressInfo.setPadding(0,0,0,0);
        }
    }

    private void createEmailInfoWidget(SmoboItem p) {
        if(stringVisible(p.getPerson().getEmailaddress())) {
            Bundle bundle = new Bundle();
            bundle.putString("maintext", p.getPerson().getEmailaddress());
            bundle.putString("subtext", "Home");
            bundle.putInt("infotype", SmoboInfoWidget.InfoType.EMAIL.ordinal());

            SmoboInfoWidget widget = new SmoboInfoWidget();
            widget.setArguments(bundle);
            context.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.smobo_email_info, widget)
                    .commitAllowingStateLoss();
        } else {
            emailInfo.setPadding(0,0,0,0);
        }
    }

    private void createPhoneInfoWidget(SmoboItem p) {
        if(stringVisible(p.getPerson().getPhonenumber())) {
            Bundle bundle = new Bundle();
            bundle.putString("maintext", p.getPerson().getPhonenumber());
            bundle.putString("subtext", "Home");
            bundle.putInt("infotype", SmoboInfoWidget.InfoType.PHONE.ordinal());

            SmoboInfoWidget widget = new SmoboInfoWidget();
            widget.setArguments(bundle);
            context.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.smobo_phone_info, widget)
                    .commitAllowingStateLoss();
        } else {
            phoneInfo.setPadding(0,0,0,0);
        }
    }

    private void createMobileInfoWidget(SmoboItem p) {
        if (stringVisible(p.getPerson().getMobilenumber())) {
            Bundle bundle = new Bundle();
            bundle.putString("maintext", p.getPerson().getMobilenumber());
            bundle.putString("subtext", "Mobile");
            bundle.putInt("infotype", SmoboInfoWidget.InfoType.PHONE.ordinal());

            SmoboInfoWidget widget = new SmoboInfoWidget();
            widget.setArguments(bundle);
            context.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.smobo_mobile_info, widget)
                    .commitAllowingStateLoss();
        } else {
            mobileInfo.setPadding(0,0,0,0);
        }
    }

    private void createBirthdayInfoWidget(SmoboItem p) {
        if (p.getPerson().getBirthdate() != null) {
            Bundle bundle = new Bundle();
            String birthday = p.getPerson().getBirthdate().toString("dd-MM-yyyy");
            birthday += String.format(Locale.getDefault(), " (%d)", p.getPerson().getAge());
            bundle.putString("maintext", birthday);
            bundle.putString("subtext", "Verjaardag");
            bundle.putInt("infotype", SmoboInfoWidget.InfoType.BIRTHDAY.ordinal());

            SmoboInfoWidget widget = new SmoboInfoWidget();
            widget.setArguments(bundle);
            context.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.smobo_birthday_info, widget)
                    .commitAllowingStateLoss();
        } else {
            birthdayInfo.setPadding(0,0,0,0);
        }
    }

    private void createWebsiteInfoWidget(SmoboItem p) {
        if(stringVisible(p.getPerson().getHomepage())) {
            Bundle bundle = new Bundle();
            String homepage = p.getPerson().getHomepage();
            bundle.putString("maintext", homepage);
            bundle.putString("subtext", "homepage");
            bundle.putInt("infotype", SmoboInfoWidget.InfoType.HOMEPAGE.ordinal());

            SmoboInfoWidget widget = new SmoboInfoWidget();
            widget.setArguments(bundle);
            context.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.smobo_homepage_info, widget)
                    .commitAllowingStateLoss();
        } else {
            homepageInfo.setPadding(0,0,0,0);
        }
    }

    private void createCountdown() {
        final DateRangeHelper drh = new DateRangeHelper(getContext(), p.getPerson());
        if(!drh.isSuccess()) {
            this.datableRangeInfo.setVisibility(View.GONE);
            return;
        }

        this.timerTask = new TimerTask() {
            @Override
            public void run() {

                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String countdownString = drh.getFullCountdownString();

                        String loveStatusString;
                        int heartIcon = R.drawable.ic_outline_broken_heart_24px;
                        if(drh.getLoveStatus().equals(DateRangeHelper.DateRange.IN_RANGE)) {
                            loveStatusString = getString(R.string.hyap7_verdict_dating_allowed, p.getPerson().getFirstname());
                            heartIcon = R.drawable.ic_outline_favorite_24px;
                        } else if(drh.getLoveStatus().equals(DateRangeHelper.DateRange.OTHER_TOO_YOUNG)) {
                            loveStatusString = getString(R.string.hyap7_verdict_dating_other_too_young, p.getPerson().getFirstname());
                        } else {
                            loveStatusString = getString(R.string.hyap7_verdict_dating_me_too_young, p.getPerson().getFirstname());
                        }

                        SmoboPersonFragment.this.datableRangeIcon.setImageResource(heartIcon);
                        SmoboPersonFragment.this.countdownText.setText(countdownString);
                        SmoboPersonFragment.this.loveStatus.setText(loveStatusString);
                        datableRangeInfo.setVisibility(View.VISIBLE);
                    }
                });
            }
        };

        this.timer = new Timer();
        this.timer.schedule(timerTask, 0, 1000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_smobo_person, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(this.timer != null) {
            this.timer.cancel();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.menu_smobo, menu);

        menu.findItem(R.id.action_save_contact)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                exportContact();
                return true;
            }

        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void exportContact() {
        if(p == null)
            return;

        // Initiate export via intent using contact helper
        ContactHelper.getInstance().exportContactViaIntent(p.getPerson());
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
