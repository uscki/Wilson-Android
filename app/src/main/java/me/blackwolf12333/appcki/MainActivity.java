package me.blackwolf12333.appcki;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.events.ErrorEvent;
import me.blackwolf12333.appcki.events.LinkClickedEvent;
import me.blackwolf12333.appcki.events.OpenFragmentEvent;
import me.blackwolf12333.appcki.events.ServerErrorEvent;
import me.blackwolf12333.appcki.events.SwitchTabEvent;
import me.blackwolf12333.appcki.events.UserLoggedInEvent;
import me.blackwolf12333.appcki.fragments.HomeFragment;
import me.blackwolf12333.appcki.fragments.HomeSubFragments;
import me.blackwolf12333.appcki.fragments.LoginFragment;
import me.blackwolf12333.appcki.fragments.RoephoekDialogFragment;
import me.blackwolf12333.appcki.fragments.meeting.MeetingOverviewFragment;
import me.blackwolf12333.appcki.helpers.UserHelper;
import me.blackwolf12333.appcki.views.NetworkImageView;

public class MainActivity extends AppCompatActivity
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
        POLL,
        ROEPHOEK,
        AGENDA_DETAIL,
        MEETING_OVERVIEW,
        MEETING_PLANNER,
        MEETING_DETAIL
    }

    public static Screen currentScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        UserHelper.getInstance().setPreferences(getPreferences(MODE_PRIVATE));
        UserHelper.getInstance().load();

        loadState();
    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        saveState();
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        saveState();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Log.d(TAG, "back: " + currentScreen.name());
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentScreen == Screen.AGENDA_DETAIL) {
                openTab(HomeSubFragments.AGENDA);
            } else if (currentScreen == Screen.MEETING_PLANNER || currentScreen == Screen.MEETING_DETAIL) {
                openFragment(new MeetingOverviewFragment(), null);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (UserHelper.getInstance().isLoggedIn()) {
            if (id == R.id.nav_news) {
                openTab(HomeSubFragments.NEWS);
            } else if (id == R.id.nav_agenda) {
                openTab(HomeSubFragments.AGENDA);
            } else if (id == R.id.nav_poll) {
// TODO: 5/22/16 poll
            } else if (id == R.id.nav_roephoek) {
                openTab(HomeSubFragments.ROEPHOEK);
            } else if (id == R.id.nav_meeting) {
                openFragment(new MeetingOverviewFragment(), null);
            } else if (id == R.id.nav_logout) {
                UserHelper.getInstance().logout();
                initLoggedOutUI();
            }
        } else {
            if (id == R.id.nav_login) {
                openFragment(loginFragment, null);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openTab(int index) {
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

        TextView name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        name.setText(UserHelper.getInstance().getPerson().getName());
        // TODO API: 5/22/16 profile pic

        NetworkImageView profile = (NetworkImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_profilepic);
        //profile.setImageMediaFile(UserHelper.getInstance().getPerson().getPhotomediaid());
    }

    private void initLoggedOutUI() {
        toolbar.setVisibility(View.GONE);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        openFragment(new LoginFragment(), null);
        navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);

        TextView name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        name.setText("");

        // TODO API: 5/22/16 profile pic
        NetworkImageView profile = (NetworkImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_profilepic);
        profile.setDefaultImageResId(android.R.drawable.sym_def_app_icon);
    }

    public void resizeOnKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void saveState() {
        UserHelper.getInstance().save();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferences.edit().putString("last_screen", currentScreen.name()).apply();
        Log.d("MainActivity", "save: " + currentScreen.name());
    }

    private void loadState() {
        if (!UserHelper.getInstance().isLoggedIn()) {
            openFragment(loginFragment, null);
            initLoggedOutUI();
            return;
        }
        initLoggedInUI();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        Screen screen = Screen.valueOf(preferences.getString("last_screen", "NEWS"));
        Log.d("MainActivity", "load: " + screen.name());
        switch (screen) {
            case LOGIN:
                initLoggedOutUI();
                openFragment(loginFragment, null);
                break;
            case NEWS:
                openTab(HomeSubFragments.NEWS);
                break;
            case AGENDA:
                openTab(HomeSubFragments.AGENDA);
                break;
            case POLL:
                break;
            case ROEPHOEK:
                openTab(HomeSubFragments.ROEPHOEK);
                break;
            case MEETING_OVERVIEW:
                openFragment(new MeetingOverviewFragment(), null);
                break;
            default: // UNHANDLED SCREENS eg AGENDA_DETAIL
                openTab(HomeSubFragments.NEWS);
                break;
        }
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
        openTab(HomeSubFragments.NEWS);
        saveState(); // save user
    }

    public void onEventMainThread(OpenFragmentEvent event) {
        openFragment(event.screen, event.arguments);
    }

    public void onEventMainThread(ServerErrorEvent event) {
        Toast toast;
        switch (event.error.getStatus()) {
            case 401: // Unauthorized
            case 403: // Forbidden
                toast = Toast.makeText(getApplicationContext(), getString(R.string.notloggedin), Toast.LENGTH_SHORT);
                toast.show();
                openFragment(new LoginFragment(), null);
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
                //TODO stuur een bericht naar de AppCKI over een interne server error
        }
    }

    public void onEventMainThread(LinkClickedEvent event) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(event.url.replace('\"',' ').trim()));
        startActivity(intent);
    }
}
