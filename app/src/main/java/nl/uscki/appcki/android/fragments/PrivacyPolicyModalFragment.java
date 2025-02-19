package nl.uscki.appcki.android.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.NotificationUtil;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.events.PrivacyPolicyPreferenceChangedEvent;
import nl.uscki.appcki.android.helpers.PermissionHelper;
import nl.uscki.appcki.android.helpers.ResourceHelper;
import nl.uscki.appcki.android.helpers.UserHelper;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.services.NotificationReceiver;
import nl.uscki.appcki.android.views.BBTextView;

/**
 * A fragment that displays the privacy policy and allows the user to agree or disagree
 */
public class PrivacyPolicyModalFragment extends DialogFragment {

    TextView updateNoticeText;
    BBTextView mainText;
    CheckBox agreeGeneralPolicy;
    CheckBox agreeAppPolicy;
    CheckBox agreeNotifications;
    Button agreeButton;
    Button rejectButton;

    private SharedPreferences prefs;
    private boolean generalPolicyAgreed;
    private boolean appPolicyAgreed;
    private boolean notificationPolicyAgreed;

    private int versionCode;
    private boolean hasAgreedToPreviousVersion;

    public PrivacyPolicyModalFragment() {
        // Required empty public constructor
        setCancelable(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Check current status of user agreement
        generalPolicyAgreed = PermissionHelper.getAgreeToPolicyLatest(
                getActivity(), PermissionHelper.AGREE_GENERAL_POLICY_KEY);
        appPolicyAgreed = PermissionHelper.getAgreeToPolicyLatest(
                getActivity(), PermissionHelper.AGREE_APP_POLICY_KEY);
        notificationPolicyAgreed = PermissionHelper.getAgreeToPolicyLatest(
                getActivity(), PermissionHelper.AGREE_NOTIFICATION_POLICY_KEY);

        hasAgreedToPreviousVersion = PermissionHelper.getAgreeToOverallPolicy(
                        getActivity(), PermissionHelper.AGREE_APP_POLICY_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_privacy_policy_modal, container, false);

        updateNoticeText = view.findViewById(R.id.privacy_policy_updated_notice_text);
        mainText = view.findViewById(R.id.privacyPolicyMainText);
        agreeGeneralPolicy = view.findViewById(R.id.checkboxPrivacyPolicyGeneral);
        agreeAppPolicy = view.findViewById(R.id.checkboxPrivacyPolicyAppSpecific);
        agreeNotifications = view.findViewById(R.id.checkboxPrivacyPolicyNotificationToken);
        agreeButton = view.findViewById(R.id.privacyPolicyButtonAgree);
        rejectButton = view.findViewById(R.id.privacyPolicyRejectButton);

        if(hasAgreedToPreviousVersion && !appPolicyAgreed) {
            updateNoticeText.setVisibility(View.VISIBLE);
        }

        setPolicyText();

        // Set state of checkboxes based on previous decisions
        agreeGeneralPolicy.setChecked(generalPolicyAgreed);
        agreeAppPolicy.setChecked(appPolicyAgreed);
        agreeNotifications.setChecked(notificationPolicyAgreed);

        // Enable or disable save button based on preferences
        agreeButton.setEnabled(generalPolicyAgreed && appPolicyAgreed);

        // Register check changed listener on all three checkboxes
        agreeGeneralPolicy.setOnCheckedChangeListener(onCheckChangeListener);
        agreeAppPolicy.setOnCheckedChangeListener(onCheckChangeListener);
        agreeNotifications.setOnCheckedChangeListener(onCheckChangeListener);

        // Register click listeners for buttons
        agreeButton.setOnClickListener(acceptPolicyListener);
        rejectButton.setOnClickListener(rejectPolicyListener);

        return view;
    }

    /**
     * Load the JSON containing the latest privacy policy from the resources, and show it
     * in the dialog view
     */
    private void setPolicyText() {
        int currentPrivacyPolicyIndex = getResources().getInteger(R.integer.privacy_policy_current_version_index);
        int[] privacyPolicyVersionNumbers = getResources().getIntArray(R.array.privacy_policy_version_numbers);
        String[] privacyPolicyVersionFiles = getResources().getStringArray(R.array.privacy_policy_versions);

        versionCode = privacyPolicyVersionNumbers[currentPrivacyPolicyIndex];
        String privacyPolicyKey = privacyPolicyVersionFiles[currentPrivacyPolicyIndex];

        int privacyPolicyIdentifier =
                getResources().getIdentifier(privacyPolicyKey, "raw", getActivity().getPackageName());

        InputStream inputStream = getResources().openRawResource(privacyPolicyIdentifier);
        String json = ResourceHelper.getRawStringResourceFromStream(inputStream);

        Type objectListType = new TypeToken<ArrayList<Object>>() { }.getType();
        List<Object> policyText = new Gson().fromJson(json, objectListType);
        mainText.setText(Parser.parse(policyText, true, mainText));
    }

    private CompoundButton.OnCheckedChangeListener onCheckChangeListener =
            new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            switch (compoundButton.getId()) {
                case(R.id.checkboxPrivacyPolicyGeneral):
                    generalPolicyAgreed = compoundButton.isChecked();
                    break;
                case(R.id.checkboxPrivacyPolicyAppSpecific):
                    appPolicyAgreed = compoundButton.isChecked();
                    break;
                case(R.id.checkboxPrivacyPolicyNotificationToken):
                    notificationPolicyAgreed = compoundButton.isChecked();
                    break;
            }

            agreeButton.setEnabled(generalPolicyAgreed && appPolicyAgreed);
        }
    };

    /**
     * If the user agrees with at least the two basic policies, they can continue
     */
    private View.OnClickListener acceptPolicyListener = new View.OnClickListener() {
        @SuppressLint("ApplySharedPref")
        @Override
        public void onClick(View view) {
            if(generalPolicyAgreed && appPolicyAgreed) {

                PermissionHelper.setAgreeToPolicy(
                        view.getContext(),
                        PermissionHelper.AGREE_GENERAL_POLICY_KEY,
                        versionCode,
                        generalPolicyAgreed);

                PermissionHelper.setAgreeToPolicy(
                        view.getContext(),
                        PermissionHelper.AGREE_APP_POLICY_KEY,
                        versionCode,
                        appPolicyAgreed);

                PermissionHelper.setAgreeToPolicy(
                        view.getContext(),
                        PermissionHelper.AGREE_NOTIFICATION_POLICY_KEY,
                        versionCode,
                        notificationPolicyAgreed);

                // Create notification channels
                NotificationUtil nu = new NotificationUtil(getActivity());

                if(!notificationPolicyAgreed) {
                    // Remove notification channels
                    nu.removeExistingChannels();

                    // Remove existing firebase messaging token and prevent generation of new one
                    NotificationReceiver.invalidateFirebaseInstanceId(false);
                } else {
                    NotificationUtil.setFirebaseEnabled(true);
                }

                // Notify listeners of change in policy agreement
                EventBus.getDefault().post(
                        new PrivacyPolicyPreferenceChangedEvent(
                                generalPolicyAgreed,
                                appPolicyAgreed,
                                notificationPolicyAgreed
                        )
                );

                // Close this dialog fragment
                dismiss();
            } else {
                // User has not yet agreed and should not have been able to click the button
                agreeButton.setEnabled(false);
            }
        }
    };

    /**
     * User explicitly disagrees with the privacy and data policies. Store this information, make
     * sure all data gathering activities are blocked and close the application
     */
    private View.OnClickListener rejectPolicyListener = new View.OnClickListener() {
        @SuppressLint("ApplySharedPref")
        @Override
        public void onClick(View view) {

            // Store disagree choice for all policies
            PermissionHelper.setAgreeToPolicy(
                    view.getContext(),
                    PermissionHelper.AGREE_GENERAL_POLICY_KEY,
                    versionCode,
                    false);

            PermissionHelper.setAgreeToPolicy(
                    view.getContext(),
                    PermissionHelper.AGREE_APP_POLICY_KEY,
                    versionCode,
                    false);

            PermissionHelper.setAgreeToPolicy(
                    view.getContext(),
                    PermissionHelper.AGREE_NOTIFICATION_POLICY_KEY,
                    versionCode,
                    false);

            // Reset permissions from setting menu if applicable
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PermissionHelper.USE_CALENDAR_EXPORT_KEY, false);
            editor.putBoolean(PermissionHelper.CALENDAR_EXPORT_EVENT_AUTO_KEY, false);
            editor.putBoolean(PermissionHelper.CALENDAR_EXPORT_MEETING_AUTO_KEY, false);
            editor.putBoolean(PermissionHelper.USE_PEOPLE_EXPORT_KEY, false);

            editor.commit();

            EventBus.getDefault().post(
                    new PrivacyPolicyPreferenceChangedEvent(
                            false,
                            false,
                            false
                    )
            );

            // Remove notification channels
            new NotificationUtil(getActivity()).removeExistingChannels();

            // Logout (which also removes notification token)
            UserHelper.getInstance().logout();

            // Close application. Nothing to do here anymore
            Activity activity = getActivity();
            if(activity != null) {
                activity.finishAffinity();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        // Lots of text, so let's try to use as much screen as we can
        Window window = getDialog().getWindow();
        if(window != null) {
            ViewGroup.LayoutParams params = window.getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            window.setAttributes((android.view.WindowManager.LayoutParams) params);
        }
    }
}
