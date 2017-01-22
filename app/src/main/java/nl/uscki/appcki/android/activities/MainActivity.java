package nl.uscki.appcki.android.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
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
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

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
import nl.uscki.appcki.android.fragments.meeting.MeetingOverviewFragment;
import nl.uscki.appcki.android.generated.organisation.PersonSimple;
import nl.uscki.appcki.android.helpers.UserHelper;
import nl.uscki.appcki.android.views.NetworkImageView;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawer;

    LoginFragment loginFragment = new LoginFragment();

    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;

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
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        UserHelper.getInstance().setPreferences(getPreferences(MODE_PRIVATE));

        if(savedInstanceState != null) {
            Log.e("Main", "Loading with saved instance");
            int ord = savedInstanceState.getInt("screen");
            Screen screen = Screen.values()[ord];
            currentScreen = screen;
            UserHelper.getInstance().load(savedInstanceState.getString("token"));
            loadState(screen);
        } else {
            Log.e("Main", "Loading without saved instance");
            UserHelper.getInstance().load();
            loadState(Screen.NEWS); // load News if there is no known last screen
        }

        if(getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            Bundle args = new Bundle();
            args.putString("item", getIntent().getStringExtra("item"));
            openFragment(new AgendaDetailTabsFragment(), args);
        }
    }

    @Override
    protected void onStart() {
        Log.e("Main", "Loading onStart");
        UserHelper.getInstance().setPreferences(getPreferences(MODE_PRIVATE));
        UserHelper.getInstance().load();
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onPause() {
        UserHelper.getInstance().save();
        super.onPause();
    }

    @Override
    public void onStop() {
        UserHelper.getInstance().save();
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            } else if (id == R.id.nav_poll) {
// TODO: 5/22/16 poll
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("screen", currentScreen.ordinal());
        outState.putString("token", UserHelper.getInstance().TOKEN);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outPersistentState.putString("token", UserHelper.getInstance().TOKEN);
        super.onSaveInstanceState(outState, outPersistentState);
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
        name.setText(UserHelper.getInstance().getPerson().getPostalname());

        final NetworkImageView profile = (NetworkImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_profilepic);
        profile.setDefaultImageResId(R.drawable.account);

        // load the users profile picture
        Services.getInstance().userService.currentUser().enqueue(new Callback<PersonSimple>() {
            @Override
            public void onSucces(Response<PersonSimple> response) {
                Log.e(TAG, response.body().toString());
                UserHelper.getInstance().setPerson(response.body());
                profile.setImageMediaId(UserHelper.getInstance().getPerson().getPhotomediaid(), MediaAPI.MediaSize.SMALL);
            }
        });
    }

    private void initLoggedOutUI() {
        toolbar.setVisibility(View.GONE);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        openFragment(new LoginFragment(), null);
        navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);

        TextView name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        name.setText("");

        NetworkImageView profile = (NetworkImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_profilepic);
        profile.setDefaultImageResId(R.drawable.account);
    }

    public void resizeOnKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void loadState(Screen screen) {
        if (!UserHelper.getInstance().isLoggedIn()) {
            openFragment(loginFragment, null);
            initLoggedOutUI();
            return;
        }
        initLoggedInUI();

        Log.d("MainActivity", "load: " + screen.name());
        openLastScreen(screen);
    }

    private void openLastScreen(Screen screen) {
        switch (screen) {
            case LOGIN:
                initLoggedOutUI();
                openFragment(loginFragment, null);
                break;
            case NEWS:
                openTab(HomeFragment.NEWS);
                break;
            case AGENDA:
                openTab(HomeFragment.AGENDA);
                break;
            case POLL:
                break;
            case ROEPHOEK:
                openTab(HomeFragment.ROEPHOEK);
                break;
            case MEETING_OVERVIEW:
                openFragment(new MeetingOverviewFragment(), null);
                break;
            default: // UNHANDLED SCREENS eg AGENDA_DETAIL
                openTab(HomeFragment.NEWS);
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
        openTab(HomeFragment.NEWS);
    }

    public void onEventMainThread(OpenFragmentEvent event) {
        if(event.screen instanceof AgendaDetailTabsFragment) {
            Intent agenda = new Intent(this, AgendaActivity.class);
            agenda.putExtra("item", event.arguments);
            startActivity(agenda);
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

    public void onEventMainThread(ImageZoomEvent event) {
        zoomImageFromThumb(event.startBounds, event.id);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void zoomImageFromThumb(final Rect startBounds, Integer id) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final NetworkImageView expandedImageView = (NetworkImageView) findViewById(R.id.image_zoomed);
        expandedImageView.setDefaultImageResId(R.drawable.account);
        expandedImageView.setImageMediaId(id, MediaAPI.MediaSize.NORMAL);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        //final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        //thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.fragment_container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        //thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        //thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}
