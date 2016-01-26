package me.blackwolf12333.appcki;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import me.blackwolf12333.appcki.fragments.APIFragment;
import me.blackwolf12333.appcki.fragments.NotLoggedInFragment;
import me.blackwolf12333.appcki.fragments.ProgressActivity;
import me.blackwolf12333.appcki.fragments.agenda.AgendaFragment;
import me.blackwolf12333.appcki.fragments.news.NewsItemFragment;
import me.blackwolf12333.appcki.fragments.poll.PollFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,ProgressActivity {

    public static final String PREFS_NAME = "preferences";
    public static final int LOGIN_REQUEST = 1;
    public static User user = new User(null);

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
        NOLOGIN(NotLoggedInFragment.class),

        ;

        final Class<? extends APIFragment> type;

        Screen(Class<? extends APIFragment> type) {
            this.type = type;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                //RoephoekAPI api = new RoephoekAPI(user);
                //api.getRoephoek();
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

        if(!user.loggedIn) {
            openScreen(Screen.NOLOGIN);
        } else {
            openScreen(Screen.NEWS);
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOGIN_REQUEST) {
            if(resultCode == RESULT_OK) {
                user = (User) data.getSerializableExtra("me.blackwolf12333.appcki.LOGIN_REQUEST");

                TextView view = (TextView) findViewById(R.id.textView);
                view.setText("Je bent nu ingelogt als " + user.getFirstName());

                user.loggedIn = true;

                openScreen(Screen.NEWS);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(user != null && user.loggedIn) {
            if (id == R.id.nav_news) {
                openScreen(Screen.NEWS);
            } else if (id == R.id.nav_agenda) {
                openScreen(Screen.AGENDA);
            } else if (id == R.id.nav_poll) {
                openScreen(Screen.POLL);
            } else if (id == R.id.nav_login) {
                item.setTitle(getString(R.string.login));
                user = null; // just forget about the user to log out
            }
        } else {
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

    private void openScreen(Screen screen) {
        String title = getResources().getStringArray(R.array.screen_titles)[screen.ordinal()];
        openScreen(screen, null, title);
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
            fragment.setUser(user);

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
        System.out.println("show: " + show);
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
