package nl.uscki.appcki.android.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.ContentLoadedEvent;
import nl.uscki.appcki.android.events.OpenFragmentEvent;
import nl.uscki.appcki.android.events.SwitchTabEvent;
import nl.uscki.appcki.android.events.UserLoggedInEvent;
import nl.uscki.appcki.android.fragments.LoginFragment;
import nl.uscki.appcki.android.fragments.agenda.AgendaDetailTabsFragment;
import nl.uscki.appcki.android.fragments.home.HomeFragment;
import nl.uscki.appcki.android.fragments.home.HomeNewsTab;
import nl.uscki.appcki.android.fragments.meeting.MeetingDetailTabsFragment;
import nl.uscki.appcki.android.fragments.meeting.MeetingOverviewFragment;
import nl.uscki.appcki.android.fragments.poll.PollOverviewFragment;
import nl.uscki.appcki.android.fragments.poll.PollResultFragment;
import nl.uscki.appcki.android.fragments.quotes.QuoteFragment;
import nl.uscki.appcki.android.fragments.search.SmoboSearch;
import nl.uscki.appcki.android.fragments.shop.StoreFragment;
import nl.uscki.appcki.android.fragments.shop.StoreSelectionFragment;
import nl.uscki.appcki.android.generated.organisation.PersonSimple;
import nl.uscki.appcki.android.helpers.ShopPreferenceHelper;
import nl.uscki.appcki.android.helpers.UserHelper;
import nl.uscki.appcki.android.services.LoadFullUserInfoService;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends BasicActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    public static final String ACTION_NEWS_OVERVIEW = "nl.uscki.appcki.android.actions.MainActivity.ACTION_NEWS_OVERVIEW";
    public static final String ACTION_AGENDA_OVERVIEW = "nl.uscki.appcki.android.actions.MainActivity.ACTION_AGENDA_OVERVIEW";
    public static final String ACTION_SHOUTBOX_OVERVIEW = "nl.uscki.appcki.android.actions.MainActivity.ACTION_SHOUTBOX_OVERVIEW";
    public static final String ACTION_MEETING_OVERVIEW = "nl.uscki.appcki.android.actions.MainActivity.ACTION_MEETING_OVERVIEW";
    public static final String ACTION_POLL_OVERVIEW = "nl.uscki.appcki.android.actions.MainActivity.ACTION_POLL_OVERVIEW";

    public static final String ACTION_VIEW_NEWSITEM
            = "nl.uscki.appcki.android.activities.action.ACTION_VIEW_NEWSITEM";

    public static final String PARAM_NEWS_ID
            = "nl.uscki.appcki.android.activities.param.PARAM_NEWS_ID";

    private int focusNewsId = -1;
    private int focusTriesSoFar = 0;

    private static boolean homeScreenExists = false;

    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawer;

    LoginFragment loginFragment = new LoginFragment();

    public enum Screen {
        LOGIN,
        NEWS,
        AGENDA,
        POLL_OVERVIEW,
        ROEPHOEK,
        AGENDA_DETAIL,
        MEETING_OVERVIEW,
        MEETING_PLANNER,
        MEETING_DETAIL,
        QUOTE_OVERVIEW,
        POLL_DETAIL,
        POLL_ACTIVE,
        SMOBO_SEARCH,
        STORE_SELECTION,
        STORE_BUY
    }

    public static Screen currentScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (!UserHelper.getInstance().isLoggedIn()) {
            initLoggedOutUI();
        } else {
            initLoggedInUI();

            try {
                UserHelper.getInstance().getFullPersonInfo(MainActivity.this);
            } catch(NullPointerException e) {
                Intent intent = new Intent(this, LoadFullUserInfoService.class);
                intent.setAction(LoadFullUserInfoService.ACTION_LOAD_USER);
                startService(intent);
            }

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
                openFragment(new MeetingOverviewFragment(), null);
                currentScreen = Screen.MEETING_OVERVIEW;
            } else if (ACTION_POLL_OVERVIEW.equals(intent.getAction())) {
                openFragment(new PollOverviewFragment(), null);
            } else {
                openTab(HomeFragment.NEWS);
            }
        }
    }

    private void handleAgendaItemIntent(Intent intent) {
        Bundle args = new Bundle();
        args.putString("item", getIntent().getStringExtra("item"));
        openFragment(new AgendaDetailTabsFragment(), args);
    }

    private void handleNewsItemIntent(Intent intent) {
        focusNewsId = intent.getIntExtra(PARAM_NEWS_ID, -1);
        focusTriesSoFar = 0;
        openTab(HomeFragment.NEWS, focusNewsId);
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
            if(handleChildFragmentStack()) {
                // Stop here, as a back action has been performed
                return;
            }

            if (currentScreen == Screen.AGENDA_DETAIL) {
                openTab(HomeFragment.AGENDA);
            } else if (currentScreen == Screen.MEETING_PLANNER || currentScreen == Screen.MEETING_DETAIL) {
                openFragment(new MeetingOverviewFragment(), null);
                currentScreen = Screen.MEETING_OVERVIEW;
            } else if(currentScreen == Screen.POLL_ACTIVE) {
                openTab(HomeFragment.NEWS);
            } else if (currentScreen == Screen.POLL_DETAIL) {
                openFragment(new PollOverviewFragment(), null);
            } else if (currentScreen == Screen.POLL_OVERVIEW) {
                openFragment(new PollResultFragment(), null);
            } else if (currentScreen == Screen.STORE_BUY) {
                openFragment(new StoreSelectionFragment(), null);
            } else if (currentScreen != Screen.NEWS) {
                openTab(HomeFragment.NEWS);
            }
            else {
                super.onBackPressed();
            }
        }
    }

    private boolean handleChildFragmentStack() {
        FragmentManager fm = getSupportFragmentManager();
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
            }
        }

        // No fragment found
        return false;
    }

    public static void setHomescreenDestroyed() {
        homeScreenExists = false;
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
        } else if (id == R.id.action_poll_archive) {
            openFragment(new PollOverviewFragment(), null);
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
                    openFragment(new StoreSelectionFragment(), null);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", shopPreferenceHelper.getShop());
                    openFragment(new StoreFragment(), bundle);
                }
            } else if (id == R.id.nav_quotes) {
                openFragment(new QuoteFragment(), null);
            } else if (id == R.id.nav_poll) {
                openFragment(new PollResultFragment(), null);
            } else if (id == R.id.nav_roephoek) {
                openTab(HomeFragment.ROEPHOEK);
            } else if (id == R.id.nav_meeting) {
                openFragment(new MeetingOverviewFragment(), null);
                currentScreen = Screen.MEETING_OVERVIEW;
            } else if (id == R.id.nav_search) {
                openFragment(new SmoboSearch(), null);
                currentScreen = Screen.SMOBO_SEARCH;
            } else if (id == R.id.nav_logout) {
                UserHelper.getInstance().logout();
                initLoggedOutUI();
                currentScreen = Screen.LOGIN;
            }
        } else {
            if (id == R.id.nav_login) {
                openFragment(loginFragment, null);
                currentScreen = Screen.LOGIN;
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openTab(int index) {
        openTab(index, -1);
    }

    private void openTab(int index, int scrollToId) {

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
            openFragment(new HomeFragment(), bundle);
            homeScreenExists = true;
        }

        setMenuToTab(index);
    }

    private void setMenuToTab(int homeFragmentTabIndex) {
        if(homeFragmentTabIndex == HomeFragment.NEWS) {
            changeDrawerMenuSelection(R.id.nav_news);
        } else if(homeFragmentTabIndex == HomeFragment.AGENDA) {
            changeDrawerMenuSelection(R.id.nav_agenda);
        } else if(homeFragmentTabIndex == HomeFragment.ROEPHOEK) {
            changeDrawerMenuSelection(R.id.nav_roephoek);
        }
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

    private void openFragment(Fragment fragment, Bundle arguments) {
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

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void initLoggedInUI() {
        toolbar.setVisibility(View.VISIBLE);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        hideKeyboard(findViewById(R.id.drawer_layout));

        navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);

        TextView name = navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        name.setText(UserHelper.getInstance().getPerson().getPostalname());

        final SimpleDraweeView profile = navigationView.getHeaderView(0).findViewById(R.id.nav_header_profilepic);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSmoboFor(UserHelper.getInstance().getPerson());
            }
        });
        // load the users profile picture
        Services.getInstance().userService.currentUser().enqueue(new Callback<PersonSimple>() {
            @Override
            public void onSucces(Response<PersonSimple> response) {
                Log.e(TAG, response.body().toString());
                UserHelper.getInstance().setPerson(response.body());
                if(UserHelper.getInstance().getPerson().getPhotomediaid() != null) {
                    profile.setImageURI(MediaAPI.getMediaUri(UserHelper.getInstance().getPerson().getPhotomediaid(), MediaAPI.MediaSize.SMALL));
                }
            }
        });
    }

    private void initLoggedOutUI() {
        toolbar.setVisibility(View.GONE);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        openFragment(new LoginFragment(), null);
        currentScreen = Screen.LOGIN;
        navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);

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
            agenda.putExtra("item", event.arguments);
            startActivity(agenda);
            return;
        } else if(event.screen instanceof MeetingDetailTabsFragment) {
            Intent meeting = new Intent(this, MeetingActivity.class);
            meeting.putExtra("item", event.arguments);
            startActivity(meeting);
            return;
        }
        openFragment(event.screen, event.arguments);
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
