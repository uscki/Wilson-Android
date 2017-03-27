package nl.uscki.appcki.android.activities;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.ErrorEvent;
import nl.uscki.appcki.android.events.ImageZoomEvent;
import nl.uscki.appcki.android.events.LinkClickedEvent;
import nl.uscki.appcki.android.events.OpenFragmentEvent;
import nl.uscki.appcki.android.events.ServerErrorEvent;
import nl.uscki.appcki.android.events.SwitchTabEvent;
import nl.uscki.appcki.android.events.UserLoggedInEvent;
import nl.uscki.appcki.android.fragments.LoginFragment;
import nl.uscki.appcki.android.fragments.agenda.AgendaDetailTabsFragment;
import nl.uscki.appcki.android.fragments.home.HomeFragment;
import nl.uscki.appcki.android.fragments.home.RoephoekDialogFragment;
import nl.uscki.appcki.android.fragments.meeting.MeetingDetailTabsFragment;
import nl.uscki.appcki.android.fragments.meeting.MeetingOverviewFragment;
import nl.uscki.appcki.android.fragments.poll.PollOverviewFragment;
import nl.uscki.appcki.android.fragments.quotes.QuoteFragment;
import nl.uscki.appcki.android.generated.organisation.PersonSimple;
import nl.uscki.appcki.android.helpers.UserHelper;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends BasicActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

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
        POLL_VOTE,
        POLL_RESULT
    }

    public static Screen currentScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseCrash.log("Creating MainActivity");
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (!UserHelper.getInstance().isLoggedIn()) {
            initLoggedOutUI();
        } else {
            initLoggedInUI();
            openTab(HomeFragment.NEWS);
        }

        if(getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            Bundle args = new Bundle();
            args.putString("item", getIntent().getStringExtra("item"));
            openFragment(new AgendaDetailTabsFragment(), args);
        }

        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Call some material design APIs here
            setTheme(R.style.AppThemeLollipop);
        } else {
            // Implement this feature without material design
        }

        // TODO configure shit for this server side
        FirebaseMessaging.getInstance().subscribeToTopic("meetings");
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Log.d(TAG, "back: " + currentScreen.name());
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentScreen == Screen.AGENDA_DETAIL) {
                openTab(HomeFragment.AGENDA);
            } else if (currentScreen == Screen.MEETING_PLANNER || currentScreen == Screen.MEETING_DETAIL) {
                openFragment(new MeetingOverviewFragment(), null);
            } else if (currentScreen == Screen.POLL_VOTE || currentScreen == Screen.POLL_RESULT) {
                openFragment(new PollOverviewFragment(), null);
            }
            else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
            return true;
        } else if(id == R.id.action_roephoek_roep) {
            buildRoephoekAddDialog();
            return true;
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
            } else if (id == R.id.nav_quotes) {
                openFragment(new QuoteFragment(), null);
            } else if (id == R.id.nav_poll) {
                openFragment(new PollOverviewFragment(), null);
            } else if (id == R.id.nav_roephoek) {
                openTab(HomeFragment.ROEPHOEK);
            } else if (id == R.id.nav_meeting) {
                openFragment(new MeetingOverviewFragment(), null);
                currentScreen = Screen.MEETING_OVERVIEW;
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openTab(int index) {
        FirebaseCrash.log("openTab(" + index + ")");
        if (currentScreen == Screen.ROEPHOEK || currentScreen == Screen.NEWS || currentScreen == Screen.AGENDA) {
            // HomeFragment luistert naar dit event om daarin de tab te switchen
            EventBus.getDefault().post(new SwitchTabEvent(index));
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt("index", index);
            openFragment(new HomeFragment(), bundle);
        }
    }

    private void openFragment(Fragment fragment, Bundle arguments) {
        if (fragment instanceof LoginFragment) {
            FirebaseCrash.log("openFragment: setting soft input mode");
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        } else {
            // TODO: 5/28/16 currently keyboard overlaps in agenda detail, but this needs a new
            // TODO implementation. Check if it's still the case with the new one
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }

        if (arguments != null) {
            FirebaseCrash.log("openFragment: adding a bundle to this fragment");
            fragment.setArguments(arguments);
        }

        FirebaseCrash.log("openFragment: beginTransition.replace");
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

        TextView name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        name.setText(UserHelper.getInstance().getPerson().getPostalname());

        final SimpleDraweeView profile = (SimpleDraweeView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_profilepic);

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

        TextView name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        name.setText("");

        ImageView profile = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_profilepic);
        profile.setImageResource(R.drawable.account);
    }

    public void resizeOnKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void buildRoephoekAddDialog() {
        DialogFragment newFragment = new RoephoekDialogFragment();
        newFragment.show(getSupportFragmentManager(), "roephoek_add");
    }

    public static void hideKeyboard(View someView) {
        InputMethodManager imm = (InputMethodManager) someView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(someView.getWindowToken(), 0);
    }

    // EVENT HANDLING
    public void onEventMainThread(ErrorEvent event) {
        Toast toast = Toast.makeText(getApplicationContext(), event.error.getMessage(), Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onEventMainThread(UserLoggedInEvent event) {
        initLoggedInUI();
        openTab(HomeFragment.NEWS);
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

    public void onEventMainThread(ServerErrorEvent event) {
        Toast toast;
        switch (event.error.getStatus()) {
            case 401: // Unauthorized
                // TODO what zijn permissions even?
                break;
            case 403: // Forbidden
                toast = Toast.makeText(getApplicationContext(), getString(R.string.notloggedin), Toast.LENGTH_SHORT);
                toast.show();
                initLoggedOutUI();
                break;
            case 404: // Not found
                toast = Toast.makeText(getApplicationContext(), getString(R.string.content_loading_error), Toast.LENGTH_SHORT);
                toast.show();
                break;
            case 405:
                break;
            case 500: // Internal error
                toast = Toast.makeText(getApplicationContext(), getString(R.string.content_loading_error), Toast.LENGTH_SHORT);
                toast.show();
                Gson gson = new Gson();
                FirebaseCrash.report(new Exception(gson.toJson(event.error))); // just log this server error to firebase
        }
    }

    public void onEventMainThread(LinkClickedEvent event) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(event.url.replace('\"',' ').trim()));
        startActivity(intent);
    }

    public void onEventMainThread(ImageZoomEvent event) {
        //zoomImageFromThumb(event.startBounds, event.id);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
