package me.blackwolf12333.appcki;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.events.LinkClickedEvent;
import me.blackwolf12333.appcki.events.OpenFragmentEvent;
import me.blackwolf12333.appcki.events.ServerErrorEvent;
import me.blackwolf12333.appcki.events.UserLoggedInEvent;
import me.blackwolf12333.appcki.fragments.LoginFragment;
import me.blackwolf12333.appcki.fragments.PageableFragment;
import me.blackwolf12333.appcki.fragments.adapters.HomeViewPagerAdapter;
import me.blackwolf12333.appcki.helpers.UserHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    NavigationView navigationView;

    LoginFragment loginFragment = new LoginFragment();

    public enum Screen {
        LOGIN,
        NEWS,
        AGENDA,
        POLL,
        ROEPHOEK,
        VERGADERPLANNER
    }

    public static Screen currentScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        UserHelper.getInstance().setPreferences(getPreferences(MODE_PRIVATE));
        UserHelper.getInstance().load();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.home_tabs);
        tabLayout.setupWithViewPager(viewPager);

        if (UserHelper.getInstance().isLoggedIn()) {
            loadState();
        } else {
            openFragment(loginFragment, null);
        }
    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        saveState();
        UserHelper.getInstance().save();
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        saveState();
        UserHelper.getInstance().save();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (UserHelper.getInstance().isLoggedIn()) {
            if (id == R.id.nav_news) {
                openTab(PageableFragment.NEWS);
            } else if (id == R.id.nav_agenda) {
                openTab(PageableFragment.AGENDA);
            } else if (id == R.id.nav_poll) {
// TODO: 5/22/16 poll
            } else if (id == R.id.nav_roephoek) {
                openTab(PageableFragment.ROEPHOEK);
            } else if (id == R.id.nav_meeting) {
// TODO: 5/22/16 meetings 
            } else if (id == R.id.nav_login) {
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
        if (tabLayout.getVisibility() == View.GONE) {
            tabLayout.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);
            findViewById(R.id.fragment_container).setVisibility(View.GONE);
        }
        viewPager.setCurrentItem(index);
        tabLayout.setScrollPosition(index, 0f, false);
    }

    private void openFragment(Fragment fragment, Bundle arguments) {
        if (tabLayout.getVisibility() == View.VISIBLE) {
            tabLayout.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
        }
        if (arguments != null) {
            fragment.setArguments(arguments);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void openFragmentWithBack(Fragment fragment, Bundle arguments) {
        if (tabLayout.getVisibility() == View.VISIBLE) {
            tabLayout.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
        }
        if (arguments != null) {
            fragment.setArguments(arguments);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(fragment.getTag())
                .commit();
    }

    private void initLoggedInUI() {
        hideKeyboard(findViewById(R.id.drawer_layout));

        openTab(PageableFragment.NEWS);

        navigationView.getMenu().findItem(R.id.nav_login).setTitle(getString(R.string.logout));
        TextView name = (TextView) navigationView.findViewById(R.id.nav_header_name);
        name.setText(UserHelper.getInstance().getUser().getPerson().getName());
        // TODO: 5/22/16 profile pic
    }

    private void initLoggedOutUI() {
        openFragment(new LoginFragment(), null);
        navigationView.getMenu().findItem(R.id.nav_login).setTitle(getString(R.string.login));

        TextView name = (TextView) navigationView.findViewById(R.id.nav_header_name);
        name.setText("");

        // TODO: 5/22/16 profile pic
    }

    private void saveState() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferences.edit().putString("last_screen", currentScreen.name()).apply();
    }

    private void loadState() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        Screen screen = Screen.valueOf(preferences.getString("last_screen", "NEWS"));
        switch (screen) {
            case LOGIN:
                openFragment(loginFragment, null);
                break;
            case NEWS:
                openTab(PageableFragment.NEWS);
                break;
            case AGENDA:
                openTab(PageableFragment.AGENDA);
                break;
            case POLL:
                break;
            case ROEPHOEK:
                openTab(PageableFragment.ROEPHOEK);
                break;
            case VERGADERPLANNER:
                break;
        }
    }

    public static void hideKeyboard(View someView) {
        InputMethodManager imm = (InputMethodManager) someView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(someView.getWindowToken(), 0);
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setAdapter(new HomeViewPagerAdapter(getSupportFragmentManager()));
    }

    // EVENT HANDLING
    public void onEventMainThread(UserLoggedInEvent event) {
        initLoggedInUI();
    }

    public void onEventMainThread(OpenFragmentEvent event) {
        openFragmentWithBack(event.screen, event.arguments);
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
