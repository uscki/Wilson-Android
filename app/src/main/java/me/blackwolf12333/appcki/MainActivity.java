package me.blackwolf12333.appcki;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.Toast;

import com.google.gson.Gson;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.api.MediaAPI;
import me.blackwolf12333.appcki.api.common.APISingleton;
import me.blackwolf12333.appcki.api.media.ImageLoader;
import me.blackwolf12333.appcki.api.media.ImageRequest;
import me.blackwolf12333.appcki.api.media.NetworkImageView;
import me.blackwolf12333.appcki.events.LinkClickedEvent;
import me.blackwolf12333.appcki.events.MediaFileEvent;
import me.blackwolf12333.appcki.events.OpenFragmentEvent;
import me.blackwolf12333.appcki.events.ServerErrorEvent;
import me.blackwolf12333.appcki.events.ShowProgressEvent;
import me.blackwolf12333.appcki.events.UserLoggedInEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;
import me.blackwolf12333.appcki.fragments.LoginFragment;
import me.blackwolf12333.appcki.fragments.agenda2.AgendaFragment;
import me.blackwolf12333.appcki.fragments.agenda.AgendaItemDetailFragment;
import me.blackwolf12333.appcki.fragments.agenda.ParticipantFragment;
import me.blackwolf12333.appcki.fragments.meetings.MeetingFragment;
import me.blackwolf12333.appcki.fragments.news.NewsItemDetailFragment;
import me.blackwolf12333.appcki.fragments.news2.NewsFragment;
import me.blackwolf12333.appcki.fragments.poll.PollFragment;
import me.blackwolf12333.appcki.fragments.roephoek.RoephoekFragment;
import me.blackwolf12333.appcki.generated.organisation.Person;
import me.blackwolf12333.appcki.helpers.UserHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private APIFragment fragment;
    private String title;
    private Toolbar toolBar;
    private DrawerLayout drawer;
    private ProgressBar progressBar;
    private View content;
    private NetworkImageView userProfilePic;

    public enum Screen {
        NEWS(NewsFragment.class),
        AGENDA(AgendaFragment.class),
        POLL(PollFragment.class),
        AGENDADETAIL(AgendaItemDetailFragment.class),
        NEWSDETAIL(NewsItemDetailFragment.class),
        LOGIN(LoginFragment.class),
        AGENDAPARTICIPANTS(ParticipantFragment.class),
        ROEPHOEK(RoephoekFragment.class),
        MEETINGS(MeetingFragment.class),
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

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        //openScreen(Screen.ROEPHOEKPOST);
        //    }
        //});

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        content = (View) findViewById(R.id.fragment_container);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        userProfilePic = (NetworkImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_picture);
    }

    @Override
    protected void onResume() {
        if(!initUser()) {
            openScreen(Screen.LOGIN);
        } else {
            openScreen(Screen.NEWS);
        }
        super.onResume();
    }

    public boolean initUser() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        if(preferences.contains("last_user")) {
            Log.d("MainActivity", "last_user found");
            loadUser();
            return true;
        } else if(UserHelper.getInstance().isLoggedIn()) {
            //initLoggedInUserUI(UserHelper.getInstance().getUser());
            return true;
        }
        return false;
    }

    private void initLoggedInUserUI(User user) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_login).setTitle(getString(R.string.logout));

        new MediaAPI().getMediaFile(UserHelper.getInstance().getUser().getPerson().getPhotomediaid());

        TextView view = (TextView) navigationView.getHeaderView(0).findViewById(R.id.login_status);
        view.setText("Je bent nu ingelogt als " + user.getPerson().getFirstname());
    }

    private void initLoggedOutUserUI() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_login).setTitle(getString(R.string.login));

        TextView view = (TextView) findViewById(R.id.login_status);
        view.setText("Je bent uitgelogd.");
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
            } else if(fragment instanceof ParticipantFragment) {
                openScreen(Screen.AGENDA); //TODO go back to viewed item
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
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        saveUser();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        saveUser();
        super.onDestroy();
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
            } else if (id == R.id.nav_roephoek) {
                openScreen(Screen.ROEPHOEK);
            } else if(id == R.id.nav_meetings) {
               openScreen(Screen.MEETINGS);
            } else if (id == R.id.nav_login) {
                UserHelper.getInstance().logout(getSharedPreferences(user.getPerson().getUsername(), MODE_PRIVATE));
                initLoggedOutUserUI();
                openScreen(Screen.LOGIN);
            }
        } else {
            if (id == R.id.nav_login) {
                openScreen(Screen.LOGIN);
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

    public void onEventMainThread(UserLoggedInEvent event) {
        User user = UserHelper.getInstance().getUser();
        initLoggedInUserUI(user);
        openScreen(Screen.NEWS);
    }

    public void onEventMainThread(MediaFileEvent event) {
        ImageLoader loader = APISingleton.getInstance(App.getContext()).getImageLoader();
        String url = String.format(ImageRequest.URL, MediaAPI.getFiletypeFromMime(event.file.getMimetype()), event.file.getId());
        Log.d("MainActivity", "image request url: " + url);
        userProfilePic.setImageMediaFile(event.file, loader);
    }

    public void onEventMainThread(LinkClickedEvent event) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(event.url.replace('\"',' ').trim()));
        startActivity(intent);
    }

    public void onEventMainThread(ServerErrorEvent event) {
        Toast toast;
        switch (event.error.getStatus()) {
            case 401:
            case 403:
                toast = Toast.makeText(getApplicationContext(), getString(R.string.notloggedin), Toast.LENGTH_SHORT);
                toast.show();
                openScreen(Screen.LOGIN);
                break;
            case 404:
                toast = Toast.makeText(getApplicationContext(), getString(R.string.content_loading_error), Toast.LENGTH_SHORT);
                toast.show();
                break;
            case 500:
                toast = Toast.makeText(getApplicationContext(), getString(R.string.content_loading_error), Toast.LENGTH_SHORT);
                toast.show();
                //TODO stuur een bericht naar de AppCKI over een interne server error
        }
    }

    private void saveUser() {
        if(UserHelper.getInstance().isLoggedIn()) {
            Gson gson = new Gson();
            User user = UserHelper.getInstance().getUser();
            String person = gson.toJson(user.getPerson());
            Log.i("saveUser: ", person);
            getPreferences(MODE_PRIVATE).edit().putString("last_user", person).commit();
            UserHelper.getInstance().save(getSharedPreferences(user.person.getUsername(), MODE_PRIVATE));
        }
    }

    private void loadUser() {
        if(getPreferences(MODE_PRIVATE).contains("last_user")) {
            Gson gson = new Gson();
            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            Person person = gson.fromJson(preferences.getString("last_user",""), Person.class);
            preferences = getSharedPreferences(person.getUsername(), MODE_PRIVATE);
            String token = preferences.getString("TOKEN", "");
            if(!token.isEmpty()) {
                Log.i("loadUser", token + " with person: " + person.toString());
                UserHelper.getInstance().login(token, person);
            }
        }
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
        //refreshLayout.setRefreshing(show);
        //content.setVisibility(show ? View.GONE : View.VISIBLE);
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
            //refreshLayout.setRefreshing(show);
            content.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
