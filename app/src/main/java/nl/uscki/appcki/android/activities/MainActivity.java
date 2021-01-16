package nl.uscki.appcki.android.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.ContentLoadedEvent;
import nl.uscki.appcki.android.events.CurrentUserUpdateRequiredDirectiveEvent;
import nl.uscki.appcki.android.events.OpenFragmentEvent;
import nl.uscki.appcki.android.events.SwitchTabEvent;
import nl.uscki.appcki.android.events.UserLoggedInEvent;
import nl.uscki.appcki.android.fragments.AppInfoFragment;
import nl.uscki.appcki.android.fragments.LoginFragment;
import nl.uscki.appcki.android.fragments.agenda.AgendaDetailTabsFragment;
import nl.uscki.appcki.android.fragments.forum.ForumOverviewFragment;
import nl.uscki.appcki.android.fragments.forum.ForumPostOverviewFragment;
import nl.uscki.appcki.android.fragments.home.HomeFragment;
import nl.uscki.appcki.android.fragments.home.HomeNewsTab;
import nl.uscki.appcki.android.fragments.media.MediaCaptionContestSharedFragment;
import nl.uscki.appcki.android.fragments.media.MediaCollectionFragment;
import nl.uscki.appcki.android.fragments.meeting.MeetingDetailTabsFragment;
import nl.uscki.appcki.android.fragments.meeting.MeetingOverviewFragment;
import nl.uscki.appcki.android.fragments.poll.PollOverviewFragment;
import nl.uscki.appcki.android.fragments.poll.PollResultFragment;
import nl.uscki.appcki.android.fragments.quotes.QuoteFragment;
import nl.uscki.appcki.android.fragments.search.SmoboSearch;
import nl.uscki.appcki.android.fragments.shop.StoreFragment;
import nl.uscki.appcki.android.fragments.shop.StoreSelectionFragment;
import nl.uscki.appcki.android.generated.organisation.CurrentUser;
import nl.uscki.appcki.android.helpers.ShopPreferenceHelper;
import nl.uscki.appcki.android.helpers.UserHelper;
import retrofit2.Response;

public class MainActivity extends BasicActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    public static final String ACTION_NEWS_OVERVIEW = "nl.uscki.appcki.android.actions.MainActivity.ACTION_NEWS_OVERVIEW";
    public static final String ACTION_AGENDA_OVERVIEW = "nl.uscki.appcki.android.actions.MainActivity.ACTION_AGENDA_OVERVIEW";
    public static final String ACTION_SHOUTBOX_OVERVIEW = "nl.uscki.appcki.android.actions.MainActivity.ACTION_SHOUTBOX_OVERVIEW";
    public static final String ACTION_MEETING_OVERVIEW = "nl.uscki.appcki.android.actions.MainActivity.ACTION_MEETING_OVERVIEW";
    public static final String ACTION_POLL_OVERVIEW = "nl.uscki.appcki.android.actions.MainActivity.ACTION_POLL_OVERVIEW";
    public static final String ACTION_VIEW_STORE = "nl.uscki.appcki.android.actions.MainActivity.ACTION_VIEW_STORE";
    public static final String ACTION_VIEW_COLLECTION = "nl.uscki.appcki.android.actions.MainActivity.ACTION_VIEW_COLLECTION";
    public static final String ACTION_VIEW_FORUM_TOPIC = "nl.uscki.appcki.android.actions.MainActivity.ACTION_VIEW_FORUM_TOPIC";

    public static final String ACTION_VIEW_NEWSITEM
            = "nl.uscki.appcki.android.activities.action.ACTION_VIEW_NEWSITEM";

    public static final String PARAM_NEWS_ID
            = "nl.uscki.appcki.android.activities.param.PARAM_NEWS_ID";
    public static final String PARAM_POLL_ID
            = "nl.uscki.appcki.android.activities.param.PARAM_POLL_ID";

    private int focusNewsId = -1;
    private int focusTriesSoFar = 0;

    private static boolean homeScreenExists = false;

    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawer;
    TextView logout;

    LoginFragment loginFragment = new LoginFragment();

    public enum Screen {
        LOGIN(-1),
        NEWS(R.id.nav_news),
        AGENDA(R.id.nav_agenda),
        POLL_OVERVIEW(R.id.nav_poll),
        ROEPHOEK(R.id.nav_roephoek),
        AGENDA_DETAIL(R.id.nav_agenda),
        MEETING_OVERVIEW(R.id.nav_meeting),
        MEETING_PLANNER(R.id.nav_meeting),
        MEETING_DETAIL(R.id.nav_meeting),
        QUOTE_OVERVIEW(R.id.nav_quotes),
        POLL_DETAIL(R.id.nav_poll),
        POLL_ACTIVE(R.id.nav_poll),
        SMOBO_SEARCH(R.id.nav_search),
        STORE_SELECTION(R.id.nav_shop),
        STORE_BUY(R.id.nav_shop),
        MEDIA_COLLECTION_OVERVIEW(R.id.nav_media),
        MEDIA_LANDING_PAGE(R.id.nav_media),
        FORUM(R.id.nav_forum);

        private int menuItemId;

        Screen(int menuItemId) {
            this.menuItemId = menuItemId;
        }

        public int getMenuItemId() {
            return this.menuItemId;
        }
    }

    public static Screen currentScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);
        logout = findViewById(R.id.menu_logout);

        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        if (!UserHelper.getInstance().isLoggedIn()) {
            initLoggedOutUI();
        } else {
            initLoggedInUI();

            logout.setClickable(true);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserHelper.getInstance().logout();
                    initLoggedOutUI();
                    currentScreen = Screen.LOGIN;
                }
            });

            // Ensure a full user info object is loaded
            UserHelper.getInstance().getCurrentUser(MainActivity.this);

            // Get the intent, verify the action and get the query
            handleIntention(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntention(intent);
    }

    private void handleIntention(Intent intent) {
        if(intent != null) {
            if (Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getStringExtra("item") != null) {
                handleAgendaItemIntent(intent);
            } else if(ACTION_VIEW_NEWSITEM.equals(intent.getAction())) {
                handleNewsItemIntent(intent);
            } else if (ACTION_NEWS_OVERVIEW.equals(intent.getAction())) {
                openTab(HomeFragment.NEWS);
            } else if (ACTION_AGENDA_OVERVIEW.equals(intent.getAction())) {
                openTab(HomeFragment.AGENDA);
            } else if (ACTION_SHOUTBOX_OVERVIEW.equals(intent.getAction())) {
                openTab(HomeFragment.ROEPHOEK);
            } else if (ACTION_MEETING_OVERVIEW.equals(intent.getAction())) {
                openFragment(new MeetingOverviewFragment(), null, false);
                currentScreen = Screen.MEETING_OVERVIEW;
            } else if (ACTION_POLL_OVERVIEW.equals(intent.getAction())) {
                openFragment(new PollOverviewFragment(), null, false);
            } else if (ACTION_VIEW_STORE.equals(intent.getAction())) {
                Bundle args = new Bundle();
                args.putInt("id", intent.getIntExtra(StoreFragment.PARAM_STORE_ID, -1));
                openFragment(new StoreFragment(), args, false);
            } else if (ACTION_VIEW_COLLECTION.equals(intent.getAction())) {
                openFragment(new MediaCollectionFragment(), intent.getExtras(), false);
            } else if (ACTION_VIEW_FORUM_TOPIC.equals(intent.getAction())) {
                openFragment(new ForumPostOverviewFragment(), intent.getExtras(), true);
            } else {
                openTab(HomeFragment.NEWS, false);
            }

            // TODO add forum from notification
        }
    }

    private void handleAgendaItemIntent(Intent intent) {
        Bundle args = new Bundle();
        args.putString("item", getIntent().getStringExtra("item"));
        openFragment(new AgendaDetailTabsFragment(), args, false);
    }

    private void handleNewsItemIntent(Intent intent) {
        focusNewsId = intent.getIntExtra(PARAM_NEWS_ID, -1);
        focusTriesSoFar = 0;
        openTab(HomeFragment.NEWS, focusNewsId, false);
    }

    @Override
    public void onLowMemory() {
        Log.e("Main", "Low memory! onLow");
        UserHelper.getInstance().save(); // save before the app gets removed from memory(?)
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        Log.e("Main", "Low memory! onTrim");
        UserHelper.getInstance().save(); // save before the app gets removed from memory(?)
        super.onTrimMemory(level);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager sfm = getSupportFragmentManager();
            if(handleChildFragmentStack(sfm)) {
                // Stop here, as a back action has been performed
               return;
            } else if (sfm.getBackStackEntryCount() > 0) {
                FragmentManager.BackStackEntry entry = sfm.getBackStackEntryAt(sfm.getBackStackEntryCount() - 1);
                String name = entry.getBreadCrumbTitle() == null ? null : entry.getBreadCrumbTitle().toString();
                getSupportFragmentManager().popBackStack();
                currentScreen = Screen.valueOf(name);
            } else {
                super.onBackPressed();
            }
            changeDrawerMenuSelection();
        }
    }

    private boolean handleChildFragmentStack(FragmentManager fm) {
        Class currentFragmentClass = Utils.getClassForScreen(currentScreen);
        if(currentFragmentClass == null) return false;

        for(Fragment f : fm.getFragments()) {
            if(f.getClass() == currentFragmentClass) {
                FragmentManager cfm = f.getChildFragmentManager();
                if(cfm.getBackStackEntryCount() > 0) {
                    cfm.popBackStack();
                    return true;
                }

                // Nothing to do here
                return false;
            } else {
                if(handleChildFragmentStack(f.getChildFragmentManager()))
                    return true;
            }
        }

        // No fragment found
        return false;
    }

    public static void setHomescreenDestroyed() {
        homeScreenExists = false;
    }

    public static void setHomeScreenExists() {
        homeScreenExists = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_about) {
            openFragment(new AppInfoFragment(), null, true);
        } else if (id == R.id.action_poll_archive) {
            openFragment(new PollOverviewFragment(), null, true);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (UserHelper.getInstance().isLoggedIn()) {
            if (id == R.id.nav_news) {
                openTab(HomeFragment.NEWS);
            } else if (id == R.id.nav_agenda) {
                openTab(HomeFragment.AGENDA);
            } else if (id == R.id.nav_shop) {
                ShopPreferenceHelper shopPreferenceHelper = new ShopPreferenceHelper(this);
                if(shopPreferenceHelper.getShop() < 0) {
                    openFragment(new StoreSelectionFragment(), null, true);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", shopPreferenceHelper.getShop());
                    openFragment(new StoreFragment(), bundle, true);
                }
            } else if (id == R.id.nav_quotes) {
                openFragment(new QuoteFragment(), null, true);
            } else if (id == R.id.nav_poll) {
                openFragment(new PollResultFragment(), null, true);
            } else if (id == R.id.nav_roephoek) {
                openTab(HomeFragment.ROEPHOEK);
            } else if (id == R.id.nav_meeting) {
                openFragment(new MeetingOverviewFragment(), null, true);
                currentScreen = Screen.MEETING_OVERVIEW;
            } else if (id == R.id.nav_search) {
                openFragment(new SmoboSearch(), null, true);
                currentScreen = Screen.SMOBO_SEARCH;
            } else if (id == R.id.nav_media) {
                openFragment(new MediaCaptionContestSharedFragment(), null, true);
                currentScreen = Screen.MEDIA_LANDING_PAGE;
            } else if (id == R.id.nav_forum) {
                openFragment(new ForumOverviewFragment(), null, true);
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void openTab(int index) {
        openTab(index, -1, true);
    }

    private void openTab(int index, boolean addToBackStack) {
        openTab(index, -1, addToBackStack);
    }

    private void openTab(int index, int scrollToId, boolean addToBackStack) {

        if (
                homeScreenExists &&
                        (currentScreen == Screen.ROEPHOEK ||
                                currentScreen == Screen.NEWS ||
                                currentScreen == Screen.AGENDA
                        )
        ) {
            // HomeFragment luistert naar dit event om daarin de tab te switchen
            EventBus.getDefault().post(new SwitchTabEvent(index, scrollToId));
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt("index", index);
            openFragment(new HomeFragment(), bundle, addToBackStack);
            homeScreenExists = true;
        }

        setMenuToTab(index);
    }

    private void setMenuToTab(int homeFragmentTabIndex) {
        changeDrawerMenuSelection();
    }

    public void changeDrawerMenuSelection(int menuItemId) {
        Menu navMenu = navigationView.getMenu();
        for(int i = 0; i < navMenu.size(); i++) {
            if(navMenu.getItem(i).getItemId() == menuItemId) {
                navMenu.getItem(i).setChecked(true);
                return;
            }
        }
    }

    public void changeDrawerMenuSelection() {
        if(currentScreen != null)
            changeDrawerMenuSelection(currentScreen.getMenuItemId());
    }

    private void openFragment(Fragment fragment, Bundle arguments, boolean addToBackStack) {
        if (fragment instanceof LoginFragment) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        } else {
            // TODO: 5/28/16 currently keyboard overlaps in agenda detail, but this needs a new
            // TODO implementation. Check if it's still the case with the new one
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }

        if (arguments != null) {
            fragment.setArguments(arguments);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment);

        if(addToBackStack && currentScreen != null) {
            transaction.addToBackStack(null)
                    .setBreadCrumbTitle(currentScreen.name());
        }

        transaction.commit();

        changeDrawerMenuSelection();
    }

    private void initLoggedInUI() {
        toolbar.setVisibility(View.VISIBLE);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        hideKeyboard(findViewById(R.id.drawer_layout));

        logout.setVisibility(View.VISIBLE);

        CurrentUser user = UserHelper.getInstance().getCurrentUser();
        if(user != null) {
            setUserDependentFeatures(user);
        } else {
            Services.getInstance().userService.currentUser().enqueue(new Callback<CurrentUser>() {
                @Override
                public void onSucces(Response<CurrentUser> response) {
                    UserHelper.getInstance().setCurrentUser(response.body());
                    setUserDependentFeatures(response.body());
                }
            });
        }
    }

    private void setUserDependentFeatures(final CurrentUser user) {
        TextView name = navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        name.setText(user.getPostalname());

        final ImageView profile = navigationView.getHeaderView(0).findViewById(R.id.nav_header_profilepic);

        profile.setOnClickListener(v -> {
            openSmoboFor(user);
            EventBus.getDefault().post(new CurrentUserUpdateRequiredDirectiveEvent());
        });

        if (user.getPhotomediaid() != null) {
            Glide.with(this)
                    .load(MediaAPI.getMediaUri(user.getPhotomediaid(), MediaAPI.MediaSize.SMALL))
                    .fitCenter()
                    .optionalCircleCrop()
                    .placeholder(R.drawable.account)
                    .into(profile);
        }
    }

    private void initLoggedOutUI() {
        toolbar.setVisibility(View.GONE);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        openFragment(new LoginFragment(), null, false);
        currentScreen = Screen.LOGIN;
        logout.setVisibility(View.GONE);

        TextView name = navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        name.setText("");

        ImageView profile = navigationView.getHeaderView(0).findViewById(R.id.nav_header_profilepic);
        profile.setImageResource(R.drawable.account);
    }

    public void resizeOnKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public static void hideKeyboard(View someView) {
        InputMethodManager imm = (InputMethodManager) someView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null)
            imm.hideSoftInputFromWindow(someView.getWindowToken(), 0);
    }

    // EVENT HANDLING

    public void onEventMainThread(UserLoggedInEvent event) {
        if (event.loggedIn) {
            initLoggedInUI();
            openTab(HomeFragment.NEWS);
        } else {
            initLoggedOutUI();
        }
    }

    public void onEventMainThread(OpenFragmentEvent event) {
        //TODO refactor this
        if(event.screen instanceof AgendaDetailTabsFragment) {
            Intent agenda = new Intent(this, AgendaActivity.class);
            agenda.putExtras(event.arguments);
            startActivity(agenda);
            return;
        } else if(event.screen instanceof MeetingDetailTabsFragment) {
            Intent meeting = new Intent(this, MeetingActivity.class);
            meeting.putExtras(event.arguments);
            startActivity(meeting);
            return;
        }
        openFragment(event.screen, event.arguments, true);
    }

    public void onEventMainThread(ContentLoadedEvent event) {
        if (focusNewsId > 0 && event.updatedPageableFragment instanceof HomeNewsTab) {
            if (((HomeNewsTab) event.updatedPageableFragment)
                    .scrollToItemWithId(focusNewsId, focusTriesSoFar >= 3) ||
                    focusTriesSoFar >= 3) {

                focusNewsId = -1;
            }
            focusTriesSoFar++;
        }
    }

    public void onEventMainThread(SwitchTabEvent event) {
        setMenuToTab(event.index);
    }


}
