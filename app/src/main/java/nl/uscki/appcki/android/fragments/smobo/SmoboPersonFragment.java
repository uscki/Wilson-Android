package nl.uscki.appcki.android.fragments.smobo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.SmoboActivity;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.DetailItemUpdatedEvent;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.fragments.adapters.SmoboCommissieAdapter;
import nl.uscki.appcki.android.fragments.adapters.SmoboMediaAdapter;
import nl.uscki.appcki.android.fragments.media.MediaCollectionFragment;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.media.MediaFileMetaData;
import nl.uscki.appcki.android.generated.organisation.Person;
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
    private SmoboActivity activity;

    private Callback<Pageable<MediaFileMetaData>> photosCallback = new Callback<Pageable<MediaFileMetaData>>() {
        @Override
        public void onSucces(Response<Pageable<MediaFileMetaData>> response) {
            if(response.body() != null) {
                mediaGrid.setVisibility(View.VISIBLE);
                if(getContext() != null) {
                    SpannableString viewPhotosText = new SpannableString(getString(R.string.smobo_photos_view_n_all, activity.getP().getNumOfPhotos()));
                    viewPhotosText.setSpan(new UnderlineSpan(), 0, viewPhotosText.length(), 0);
                    showAllFotosButton.setText(viewPhotosText);
                    showAllFotosButton.setVisibility(View.VISIBLE);
                    showAllFotosButton.setOnClickListener(v -> {
                        Intent intent = new MediaCollectionFragment.IntentBuilder(
                                activity.getP().getPerson().getId())
                                .setIsSmobo(activity.getP().getPerson().getPostalname(), activity.getP().getPerson().getId(), activity.getP().getNumOfPhotos())
                                .build(getContext());
                        startActivity(intent);
                    });
                }
                mediaGridAdapter.clear();
                mediaGridAdapter.addItems(response.body().getContent());
            } else {
                mediaGrid.setVisibility(View.GONE);
                if(getContext() != null) {
                    mediaGridHeader.setText(getContext().getString(R.string.smobo_photos_empty));
                }
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
    TextView mediaGridHeader;
    LinearLayoutManager mediaGridLayoutManager;
    SmoboMediaAdapter mediaGridAdapter;
    TextView showAllFotosButton;
    SwipeRefreshLayout swipeContainer;
    RelativeLayout datableRangeInfo;
    ImageView datableRangeIcon;
    TextView loveStatus;
    TextView countdownText;

    private SmoboInfoWidget addressWidget;
    private SmoboInfoWidget emailWidget;
    private SmoboInfoWidget phoneWidget;
    private SmoboInfoWidget mobileWidget;
    private SmoboInfoWidget birthdayWidget;
    private SmoboInfoWidget homepageWidget;

    public void onEventMainThread(DetailItemUpdatedEvent<SmoboItem> item) {
        swipeContainer.setRefreshing(false);
        setupItem(item.getUpdatedItem());
    }

    private void setupItem(SmoboItem p) {
        this.mediaGridAdapter.setPersonId(p.getId());

        setupWidgets();
        createCountdown();

        ((BaseItemAdapter) smoboGroups.getAdapter()).update(p.getGroups());

        if(p.getNumOfPhotos() > 0) {
            Services.getInstance().smoboService.photos(p.getId(), 0, p.getNumOfPhotos()).enqueue(photosCallback);
        } else {
            mediaGrid.setVisibility(View.GONE);
            mediaGridHeader.setText(getText(R.string.smobo_photos_empty));
        }
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

    private boolean stringVisible(String s) {
        return s != null && s.trim().length() > 0;
    }

    private void setupWidgets() {
        Person p = this.activity.getP().getPerson();

        this.addressWidget = createOrUpdateWidget(
                p.getFullAddres(true), R.string.smobo_info_home_address,
                SmoboInfoWidget.InfoType.ADRESS,
                this.addressInfo, this.addressWidget, R.id.smobo_address_info);
        this.emailWidget = createOrUpdateWidget(
                p.getEmailaddress(), R.string.smobo_info_email_address,
                SmoboInfoWidget.InfoType.EMAIL,
                this.emailInfo, this.emailWidget, R.id.smobo_email_info);
        this.phoneWidget = createOrUpdateWidget(
                p.getPhonenumber(), R.string.smobo_info_phone_residential,
                SmoboInfoWidget.InfoType.PHONE,
                this.phoneInfo, this.phoneWidget, R.id.smobo_phone_info);
        this.mobileWidget = createOrUpdateWidget(
                p.getMobilenumber(), R.string.smobo_info_phone_mobile,
                SmoboInfoWidget.InfoType.PHONE,
                this.mobileInfo, this.mobileWidget, R.id.smobo_mobile_info);
        this.birthdayWidget = createOrUpdateWidget(
                p.getBirthdayWidthAge(), R.string.smobo_info_birthday,
                SmoboInfoWidget.InfoType.BIRTHDAY,
                this.birthdayInfo, this.birthdayWidget, R.id.smobo_birthday_info);
        this.homepageWidget = createOrUpdateWidget(
                this.activity.getP().getPerson().getHomepage(), R.string.smobo_info_homepage,
                SmoboInfoWidget.InfoType.HOMEPAGE,
                this.homepageInfo, this.homepageWidget, R.id.smobo_homepage_info);
    }

    private SmoboInfoWidget createOrUpdateWidget(
            String stringContent,
            int widgetLabel,
            SmoboInfoWidget.InfoType type,
            FrameLayout frame,
            SmoboInfoWidget widget,
            int id
    ) {
        if(widget == null) {
            widget = new SmoboInfoWidget(getString(widgetLabel), type);
            context.getSupportFragmentManager().beginTransaction()
                    .replace(id, widget)
                    .commitAllowingStateLoss();
        }

        if(stringVisible(stringContent)) {
            frame.setVisibility(View.VISIBLE);
            widget.updateMaintext(stringContent);
        } else {
            frame.setVisibility(View.GONE);
        }

        return widget;
    }

    private void createCountdown() {
        if(this.dateRangeHelper == null) {
            this.dateRangeHelper = new DateRangeHelper(getContext(), this.activity.getP().getPerson());
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
                            loveStatusString = context.getString(
                                    R.string.hyap7_verdict_dating_allowed,
                                    activity.getP().getPerson().getFirstname());
                            heartIcon = R.drawable.ic_outline_favorite_24px;
                        } else if (SmoboPersonFragment.this.dateRangeHelper.getLoveStatus()
                                .equals(DateRangeHelper.DateRange.OTHER_TOO_YOUNG)) {
                            loveStatusString = context.getString(
                                    R.string.hyap7_verdict_dating_other_too_young,
                                    activity.getP().getPerson().getFirstname());
                        } else {
                            loveStatusString = context.getString(
                                    R.string.hyap7_verdict_dating_me_too_young,
                                    activity.getP().getPerson().getFirstname());
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
        mediaGridHeader = view.findViewById(R.id.smobo_media_text);
        showAllFotosButton = view.findViewById(R.id.smobo_media_viewmore_button);
        swipeContainer = view.findViewById(R.id.smobo_swiperefresh);
        datableRangeInfo = view.findViewById(R.id.datable_range_info);
        datableRangeIcon = view.findViewById(R.id.datable_range_icon);
        loveStatus = view.findViewById(R.id.datable_range_love_status);
        countdownText = view.findViewById(R.id.datable_range_countdown);

        setupMediaGrid();
        setupSwipeContainer();
        smoboGroups.setAdapter(new SmoboCommissieAdapter(new ArrayList<>()));

        if(this.activity != null && this.activity.getP() != null) {
            this.setupItem(this.activity.getP());
        }

        setHasOptionsMenu(true);


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
        this.activity = (SmoboActivity)getActivity();
        if(this.activity != null) {
            boolean registered = this.activity.registerSharedElementCallback(this);
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
                .setOnMenuItemClickListener(item -> {
                    exportContact();
                    return true;
                });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void exportContact() {
        if(activity.getP() == null)
            return;

        // Initiate export via intent using contact helper
        ContactHelper.getInstance().exportContactViaIntent(activity.getP().getPerson());
    }

    protected void setupSwipeContainer() {
        swipeContainer.setOnRefreshListener(() -> this.activity.refreshSmoboItem());

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setRefreshing(false);
    }

    private void setupMediaGrid() {
        this.mediaGridLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mediaGrid.setLayoutManager(this.mediaGridLayoutManager);
        this.mediaGridAdapter = new SmoboMediaAdapter((SmoboActivity) getActivity());
        mediaGrid.setAdapter(this.mediaGridAdapter);
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
