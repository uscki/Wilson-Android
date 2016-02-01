package me.blackwolf12333.appcki;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.api.RoephoekAPI;
import me.blackwolf12333.appcki.events.OpenFragmentEvent;
import me.blackwolf12333.appcki.events.ShowProgressEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;
import me.blackwolf12333.appcki.fragments.ProgressActivity;
import me.blackwolf12333.appcki.fragments.agenda.AgendaFragment;
import me.blackwolf12333.appcki.fragments.agenda.AgendaItemDetailFragment;
import me.blackwolf12333.appcki.fragments.news.NewsItemDetailFragment;
import me.blackwolf12333.appcki.fragments.news.NewsItemFragment;
import me.blackwolf12333.appcki.fragments.poll.PollFragment;
import me.blackwolf12333.appcki.generated.Person;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,ProgressActivity {

    public static final String PREFS_NAME = "preferences";
    public static final int LOGIN_REQUEST = 1;

    private APIFragment fragment;
    private String title;
    private Toolbar toolBar;
    private DrawerLayout drawer;
    private ProgressBar progressBar;
    private View content;

    public enum Screen {
        NEWS(NewsItemFragment.class),
        AGENDA(AgendaFragment.class),
        POLL(PollFragment.class),
        AGENDADETAIL(AgendaItemDetailFragment.class),
        NEWSDETAIL(NewsItemDetailFragment.class),

        ;

        final Class<? extends APIFragment> type;

        Screen(Class<? extends APIFragment> type) {
            this.type = type;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!initUser()) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(loginIntent, LOGIN_REQUEST);
        }

        setContentView(R.layout.activity_main);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                RoephoekAPI api = new RoephoekAPI();
                api.getRoephoek();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        content = (View) findViewById(R.id.fragment_container);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        openScreen(Screen.NEWS);

        // user should be logged in by now
        //initLoggedInUserUI();
    }

    public boolean initUser() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        if(preferences.contains("last_user")) {
            String lastUser = preferences.getString("last_user", "");
            Gson gson = new Gson();
            Person p = gson.fromJson(lastUser, Person.class);
            preferences = getSharedPreferences(p.getUsername(), MODE_PRIVATE);
            String token = preferences.getString("TOKEN", "");
            UserHelper.getInstance().login(token, p);
            return true;
        } else if(UserHelper.getInstance().isLoggedIn()) {
            return true;
        }
        return false;
    }

    private void initLoggedInUserUI() {
        User user = UserHelper.getInstance().getUser();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_login).setTitle(getString(R.string.logout));

        TextView view = (TextView) findViewById(R.id.login_status);
        view.setText("Je bent nu ingelogt als " + user.getPerson().getFirstname());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(fragment instanceof NewsItemDetailFragment) {
                openScreen(Screen.NEWS);
            } else if(fragment instanceof AgendaItemDetailFragment) {
                openScreen(Screen.AGENDA);
            } else {
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        if(UserHelper.getInstance().isLoggedIn()) {
            Gson gson = new Gson();
            User user = UserHelper.getInstance().getUser();
            String person = gson.toJson(user.getPerson());
            Log.i("onPause: ", person);
            getPreferences(MODE_PRIVATE).edit().putString("last_user", person).commit();
            UserHelper.getInstance().save(getSharedPreferences(user.person.getUsername(), MODE_PRIVATE));
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(UserHelper.getInstance().isLoggedIn()) {
            Gson gson = new Gson();
            User user = UserHelper.getInstance().getUser();
            String person = gson.toJson(user.getPerson());
            Log.i("onPause: ", person);
            getPreferences(MODE_PRIVATE).edit().putString("last_user", person).commit();
            UserHelper.getInstance().save(getSharedPreferences(user.person.getUsername(), MODE_PRIVATE));
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOGIN_REQUEST) {
            if(resultCode == RESULT_OK) {
                User user = UserHelper.getInstance().getUser();
                //initLoggedInUserUI();

                UserHelper.getInstance().save(getSharedPreferences(user.getPerson().getUsername(), MODE_PRIVATE));

                openScreen(Screen.NEWS);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        User user = UserHelper.getInstance().getUser();

        if(user != null && user.loggedIn) {
            if (id == R.id.nav_news) {
                openScreen(Screen.NEWS);
            } else if (id == R.id.nav_agenda) {
                openScreen(Screen.AGENDA);
            } else if (id == R.id.nav_poll) {
                openScreen(Screen.POLL);
            } else if (id == R.id.nav_login) {
                item.setTitle(getString(R.string.login));
                UserHelper.getInstance().logout();
            }
        } else {
            System.out.println("user not logged in");
            if (id == R.id.nav_login) {
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivityForResult(loginIntent, LOGIN_REQUEST);
                item.setTitle(getString(R.string.logout));
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onEventMainThread(OpenFragmentEvent event) {
        if(event.arguments != null) {
            openScreen(event.screen, event.arguments);
        } else {
            openScreen(event.screen);
        }
    }

    public void onEventMainThread(ShowProgressEvent event) {
        this.showProgress(event.showProgress);
    }

    private void openScreen(Screen screen) {
        String title = getResources().getStringArray(R.array.screen_titles)[screen.ordinal()];
        openScreen(screen, null, title);
    }

    private void openScreen(Screen screen, Bundle args) {
        String title = getResources().getStringArray(R.array.screen_titles)[screen.ordinal()];
        openScreen(screen, args, title);
    }

    private void openScreen(Screen screen, Bundle args, String newTitle) {
        drawer.closeDrawers();
        showProgress(true);

        Class<? extends APIFragment> type = screen.type;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (newTitle != null) {
            title = newTitle;
            toolBar.setTitle(title);
        }
        try {
            fragment = type.newInstance();

            if (currentFragment == fragment) {
                return;
            }

            if (args != null) {
                fragment.setArguments(args);
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commitAllowingStateLoss();
            invalidateOptionsMenu();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            content.setVisibility(show ? View.GONE : View.VISIBLE);
            content.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    content.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            content.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
