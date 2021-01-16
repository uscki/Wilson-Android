package nl.uscki.appcki.android.fragments.smobo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.SmoboActivity;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.fragments.adapters.SmoboCommissieAdapter;
import nl.uscki.appcki.android.fragments.adapters.SmoboMediaAdapter;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.media.MediaFileMetaData;
import nl.uscki.appcki.android.generated.smobo.SmoboItem;
import nl.uscki.appcki.android.helpers.ContactHelper;
import nl.uscki.appcki.android.helpers.DateRangeHelper;
import nl.uscki.appcki.android.helpers.ISharedElementViewContainer;
import nl.uscki.appcki.android.views.SmoboInfoWidget;
import retrofit2.Response;

/**
 * Created by peter on 4/5/17.
 */

public class SmoboPersonFragment extends Fragment implements ISharedElementViewContainer {
    private final String TAG = getClass().getSimpleName();
    AppCompatActivity context;

    private Integer id;
    private SmoboItem p;

    public SmoboItem getP() {
        return p;
    }

    private Callback<Pageable<MediaFileMetaData>> photosCallback = new Callback<Pageable<MediaFileMetaData>>() {
        @Override
        public void onSucces(Response<Pageable<MediaFileMetaData>> response) {
            if(response.body() != null) {
                mediaGridAdapter.clear();
                mediaGridAdapter.addItems(response.body().getContent());
            }
        }

        @Override
        public void onError(Response<Pageable<MediaFileMetaData>> response) {
            super.onError(response);
        }
    };

    private Timer timer;
    private TimerTask timerTask;
    private DateRangeHelper dateRangeHelper;

    FrameLayout addressInfo;
    FrameLayout emailInfo;
    FrameLayout phoneInfo;
    FrameLayout mobileInfo;
    FrameLayout birthdayInfo;
    FrameLayout homepageInfo;
    RecyclerView smoboGroups;
    RecyclerView mediaGrid;
    LinearLayoutManager mediaGridLayoutManager;
    SmoboMediaAdapter mediaGridAdapter;
    SwipeRefreshLayout swipeContainer;
    RelativeLayout datableRangeInfo;
    ImageView datableRangeIcon;
    TextView loveStatus;
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

            Services.getInstance().smoboService.photos(id, 0, p.getNumOfPhotos()).enqueue(photosCallback);

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
        if(this.dateRangeHelper == null) {
            this.dateRangeHelper = new DateRangeHelper(getContext(), p.getPerson());
        }
        if(!this.dateRangeHelper.isSuccess()) {
            this.datableRangeInfo.setVisibility(View.GONE);
            return;
        }

        this.timerTask = new TimerTask() {
            @Override
            public void run() {
                if (context != null) {
                    context.runOnUiThread(() -> {
                        String countdownString = SmoboPersonFragment.this
                                .dateRangeHelper.getFullCountdownString();

                        String loveStatusString;
                        int heartIcon = R.drawable.ic_outline_broken_heart_24px;
                        if (SmoboPersonFragment.this.dateRangeHelper.getLoveStatus()
                                .equals(DateRangeHelper.DateRange.IN_RANGE)) {
                            loveStatusString = getString(
                                    R.string.hyap7_verdict_dating_allowed,
                                    p.getPerson().getFirstname());
                            heartIcon = R.drawable.ic_outline_favorite_24px;
                        } else if (SmoboPersonFragment.this.dateRangeHelper.getLoveStatus()
                                .equals(DateRangeHelper.DateRange.OTHER_TOO_YOUNG)) {
                            loveStatusString = getString(
                                    R.string.hyap7_verdict_dating_other_too_young,
                                    p.getPerson().getFirstname());
                        } else {
                            loveStatusString = getString(
                                    R.string.hyap7_verdict_dating_me_too_young,
                                    p.getPerson().getFirstname());
                        }

                        SmoboPersonFragment.this.datableRangeIcon.setImageResource(heartIcon);
                        SmoboPersonFragment.this.countdownText.setText(countdownString);
                        SmoboPersonFragment.this.loveStatus.setText(loveStatusString);
                        datableRangeInfo.setVisibility(View.VISIBLE);
                    });
                }
            }
        };

        this.timer = new Timer();
        this.timer.schedule(timerTask, 0, 1000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_smobo_person, container, false);

        addressInfo = view.findViewById(R.id.smobo_address_info);
        emailInfo = view.findViewById(R.id.smobo_email_info);
        phoneInfo = view.findViewById(R.id.smobo_phone_info);
        mobileInfo = view.findViewById(R.id.smobo_mobile_info);
        birthdayInfo = view.findViewById(R.id.smobo_birthday_info);
        homepageInfo = view.findViewById(R.id.smobo_homepage_info);
        smoboGroups = view.findViewById(R.id.smobo_groups);
        mediaGrid = view.findViewById(R.id.smobo_media_gridview);
        swipeContainer = view.findViewById(R.id.smobo_swiperefresh);
        datableRangeInfo = view.findViewById(R.id.datable_range_info);
        datableRangeIcon = view.findViewById(R.id.datable_range_icon);
        loveStatus = view.findViewById(R.id.datable_range_love_status);
        countdownText = view.findViewById(R.id.datable_range_countdown);

        setHasOptionsMenu(true);

        if (getArguments() != null) {
            this.id = getArguments().getInt("id");

            setupMediaGrid();
            setupSwipeContainer();
            smoboGroups.setAdapter(new SmoboCommissieAdapter(new ArrayList<>()));

            swipeContainer.setRefreshing(true);
            Services.getInstance().smoboService.get(id).enqueue(smoboCallback);
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SmoboActivity smoboActivity = (SmoboActivity)getActivity();
        if(smoboActivity != null) {
            boolean registered = smoboActivity.registerSharedElementCallback(this);
            Log.e(getTag(), "Registered " + getClass() + " for shared element stuff. Result was " + Boolean.toString(registered));
        } else {
            Log.e(getTag(), "SmoboActivity was null! Not registering callbacks on " + getClass());
        }
    }

    @Override
    public void onDestroy() {
        SmoboActivity smoboActivity = (SmoboActivity)getActivity();
        if(smoboActivity != null) {
            boolean registered = smoboActivity.deregisterSharedElementCallback(this);
            Log.e(getTag(), "Deregistered " + getClass() + " for shared element stuff. Result was " + Boolean.toString(registered));
        } else {
            Log.e(getTag(), "SmoboActivity was null! Not deregistering callbacks on " + getClass());
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        if(this.timer != null) this.timer.cancel();
        super.onPause();
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
        swipeContainer.setOnRefreshListener(this::onSwipeRefresh);

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setRefreshing(false);
    }

    private void setupMediaGrid() {
        this.mediaGridLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mediaGrid.setLayoutManager(this.mediaGridLayoutManager);
        this.mediaGridAdapter = new SmoboMediaAdapter((SmoboActivity)getActivity(), this.id, new ArrayList<>());
        mediaGrid.setAdapter(this.mediaGridAdapter);
    }

    public void onSwipeRefresh() {
        Services.getInstance().smoboService.get(id).enqueue(smoboCallback);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        try{
            // Return the fragment manager
            this.context = (AppCompatActivity) context;

            // If using the Support lib.
            // return activity.getSupportFragmentManager();

            if(this.dateRangeHelper != null) {
                this.dateRangeHelper.setContext(getContext());
            }
            if(this.timer != null) {
                this.timer.schedule(this.timerTask, 0, 1000);
            }

        } catch (ClassCastException e) {
            Log.d(TAG, "Can't get the fragment manager with this");
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(this.timer != null) this.timer.cancel();
    }

    @Override
    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
        RecyclerView.LayoutManager layoutManager = this.mediaGrid.getLayoutManager();
        if(!names.isEmpty() && names.get(0) != null && layoutManager != null && this.mediaGrid.getAdapter() != null) {
            String transitionName = names.get(0);
            for(int i = 0; i < this.mediaGrid.getAdapter().getItemCount(); i++) {
                View view = layoutManager.findViewByPosition(i);
                if(view != null) {
                    View imageView = view.findViewById(R.id.photo);
                    if(imageView != null && transitionName.equals(imageView.getTransitionName())) {
                        sharedElements.put(transitionName, imageView);
                        if(getActivity() != null) {
                            getActivity().startPostponedEnterTransition();
                        }
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void setExitSharedElementCallback(@Nullable SharedElementCallback callback) {
        super.setExitSharedElementCallback(callback);
    }

    private View getViewAt(int position) {
        RecyclerView.LayoutManager manager = this.mediaGrid.getLayoutManager();
        View containerView = null;
        if(manager != null) {
            containerView = manager.findViewByPosition(position);
        }
        if(containerView != null) {
            return containerView.findViewById(R.id.photo);
        }
        return null;
    }

    @Override
    public int activityReentering(int code, Intent data) {
        code--;
        if(getViewAt(code) == null) {
            if(getActivity() != null) {
                getActivity().postponeEnterTransition();
            }
            this.mediaGrid.getViewTreeObserver().addOnGlobalLayoutListener(startPostponedIfEverythingElseFails);
        }
        ((LinearLayoutManager)this.mediaGrid.getLayoutManager()).scrollToPositionWithOffset(code, 0);
        return code;
    }

    private ViewTreeObserver.OnGlobalLayoutListener startPostponedIfEverythingElseFails = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            // Force the layout to continue in the rare case the view was null, but the sharedElementsMap was not updated
            mediaGrid.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            if(getActivity() != null) {
                getActivity().startPostponedEnterTransition();
            }
        }
    };
}
