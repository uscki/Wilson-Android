package nl.uscki.appcki.android.fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
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

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.UserLoggedInEvent;
import nl.uscki.appcki.android.generated.organisation.PersonSimple;
import nl.uscki.appcki.android.helpers.UserHelper;
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

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        userView = (AutoCompleteTextView) view.findViewById(R.id.username);

        passwordView = (EditText) view.findViewById(R.id.password);
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


        logoTop = (ImageView) view.findViewById(R.id.login_logo_top);

        Button signIn = (Button) view.findViewById(R.id.sign_in_button);
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

            password = MD5(password);
            Services.getInstance().userService.login(userName, password).enqueue(new Callback<Void>() {
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    showError("Failed to connect to server!");
                    Log.e("LoginFragment", t.getLocalizedMessage());
                }

                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()) {
                        Headers headers = response.headers();
                        String token = headers.get("X-AUTH-TOKEN");

                        Gson gson = new Gson();

                        try {
                            //TODO REMOVE THIS IN PRODUCTION
                            Log.i("LoginActivity: ", "token: " + token);
                            Log.i("LoginActivity: ", "decoded: " + new String(Base64.decode(token.split("\\.")[1], Base64.DEFAULT), "UTF-8"));
                            PersonSimple person = gson.fromJson(new String(Base64.decode(token.split("\\.")[1], Base64.DEFAULT), "UTF-8"), PersonSimple.class);
                            UserHelper.getInstance().login(token, person);

                            EventBus.getDefault().post(new UserLoggedInEvent());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            showError("Token contains invalid characters, please sent help");
                        }
                    }
                    else
                    {
                        if(response.code() == 401) {
                            showError("Username or password is incorrect!");
                        }
                        else {
                            showError("Unknown error encountered from server");
                        }
                    }
                }
            });
        }
    }

    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException ignored) {
        }
        return null;
    }

    private void showError(String error) {
        animation.end();
        passwordView.setError(error);
        passwordView.requestFocus();
    }
}