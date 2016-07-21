package me.blackwolf12333.appcki.fragments;

import android.animation.ObjectAnimator;
import android.os.AsyncTask;
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

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.MainActivity;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.events.ShowProgressEvent;
import me.blackwolf12333.appcki.events.UserLoggedInEvent;
import me.blackwolf12333.appcki.generated.organisation.Person;
import me.blackwolf12333.appcki.helpers.UserHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask authTask = null;

    EditText passwordView;
    AutoCompleteTextView userView;
    ImageView logoTop;

    public LoginFragment() {
        // Required empty public constructor
        MainActivity.currentScreen = MainActivity.Screen.LOGIN;
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
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
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
        EventBus.getDefault().post(new ShowProgressEvent(false));
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (authTask != null) {
            return;
        }

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
            ObjectAnimator animation = ObjectAnimator.ofFloat(logoTop, "rotationY", 0.0f, 360f);
            animation.setDuration(3600);
            animation.setRepeatCount(ObjectAnimator.INFINITE);
            animation.setInterpolator(new LinearInterpolator());
            animation.start();
            EventBus.getDefault().post(new ShowProgressEvent(true));

            try {
                authTask = new UserLoginTask(userName, password);
                authTask.execute();
            } catch (java.io.UnsupportedEncodingException e) {
                passwordView.setError("Username contains invalid characters");
            }
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) throws java.io.UnsupportedEncodingException {
            mEmail = URLEncoder.encode(email, "UTF-8");;
            mPassword = password;
        }

        public String MD5(String md5) {
            try {
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
                byte[] array = md.digest(md5.getBytes());
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < array.length; ++i) {
                    sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
                }
                return sb.toString();
            } catch (java.security.NoSuchAlgorithmException e) {
            }
            return null;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            URL api = null;
            HttpURLConnection connection = null;
            Gson gson = new Gson();

            try {
                String password = MD5(mPassword);
                api = new URL(getString(R.string.apiurl) + "login?username=" + mEmail + "&password=" + password); //TODO API: beslissing maken over hash of niet
                connection = (HttpURLConnection) api.openConnection();
                connection.setRequestMethod("GET");

                String token = connection.getHeaderField("X-AUTH-TOKEN");

                if (token == null) {
                    //TODO: handle error
                    Log.i("LoginActivity", "Error logging in!");
                    connection.disconnect();
                    return false;
                }

                Log.i("LoginActivity: ", "token: " + token);
                Log.i("LoginActivity: ", "decoded: " + new String(Base64.decode(token.split("\\.")[1], Base64.DEFAULT), "UTF-8"));
                Person person = gson.fromJson(new String(Base64.decode(token.split("\\.")[1], Base64.DEFAULT), "UTF-8"), Person.class);
                UserHelper.getInstance().login(token, person);

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            authTask = null;

            if (success) {
                logoTop.setAnimation(null); // stop de animatie
                EventBus.getDefault().post(new UserLoggedInEvent());
            } else {
                passwordView.setError(getString(R.string.error_incorrect_password));
                passwordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            authTask = null;
        }
    }
}