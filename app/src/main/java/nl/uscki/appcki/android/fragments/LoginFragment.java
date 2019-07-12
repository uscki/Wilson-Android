package nl.uscki.appcki.android.fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.BuildConfig;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.UserLoggedInEvent;
import nl.uscki.appcki.android.generated.organisation.CurrentUser;
import nl.uscki.appcki.android.helpers.PermissionHelper;
import nl.uscki.appcki.android.helpers.UserHelper;
import nl.uscki.appcki.android.services.NotificationReceiver;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    EditText passwordView;
    AutoCompleteTextView userView;
    ImageView logoTop;
    ObjectAnimator animation;

    public static final String AUTH_HEADER = "Authorization";

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        userView = view.findViewById(R.id.username);

        passwordView = view.findViewById(R.id.password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        logoTop = view.findViewById(R.id.login_logo_top);

        Button signIn = view.findViewById(R.id.sign_in_button);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
        // No idea why, but it doesn't work otherwise
        //EventBus.getDefault().post(new ShowProgressEvent(false));
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        userView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        String userName = userView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_incorrect_password));
            focusView = passwordView;
            cancel = true;
        }

        // Check for a valid username address.
        if (TextUtils.isEmpty(userName)) {
            userView.setError(getString(R.string.error_field_required));
            focusView = userView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            MainActivity.hideKeyboard(passwordView);

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            animation = ObjectAnimator.ofFloat(logoTop, "rotationY", 0.0f, 360f);
            animation.setDuration(3600);
            animation.setRepeatCount(ObjectAnimator.INFINITE);
            animation.setInterpolator(new LinearInterpolator());
            animation.start();

            Services.getInstance().userService.login(userName, password).enqueue(new Callback<Void>() {
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    showError(R.string.connection_error);
                    Log.e("LoginFragment", t.getLocalizedMessage());
                }

                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()) {
                        Headers headers = response.headers();
                        final String token = headers.get(AUTH_HEADER);

                        if(BuildConfig.DEBUG) {
                            Log.i("LoginActivity: ", "token: " + token);
                        }

                        // Let's do one request before login to find the current user, which also verifies the token works
                        Services.getInstance().userService.currentUser(token).enqueue(new nl.uscki.appcki.android.api.Callback<CurrentUser>() {
                            @Override
                            public void onSucces(Response<CurrentUser> response) {
                                CurrentUser currentUser = response.body();
                                if (currentUser != null) {
                                    // Force load the current user at least once before login is propegated through app
                                    UserHelper.getInstance().setCurrentUser(currentUser);
                                    UserHelper.getInstance().login(token);
                                    if (PermissionHelper.hasAgreedToNotificationPolicy(getContext())) {
                                        // Force firebase to generate a new notification token by invalidating the current token
                                        NotificationReceiver.invalidateFirebaseInstanceId(true);
                                    }

                                    EventBus.getDefault().post(new UserLoggedInEvent(true));
                                } else {
                                    showError(R.string.error_user_not_found);
                                }
                            }

                            @Override
                            public void onError(Response<CurrentUser> response) {
//                                super.onError(response);
                                showError(R.string.error_user_not_found);
                            }
                        });

                    } else {
                        if(response.code() == 401) {
                            showError(R.string.error_incorrect_password);
                        }
                        else {
                            showError(R.string.error_login_failed);
                        }
                    }
                }
            });
        }
    }

    private void showError(int error) {
        animation.end();
        passwordView.setError(getResources().getString(error));
        passwordView.requestFocus();
    }
}