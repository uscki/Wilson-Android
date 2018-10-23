package nl.uscki.appcki.android.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.NotificationUtil;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.helpers.PermissionHelper;
import nl.uscki.appcki.android.helpers.UserHelper;

/**
 * A fragment that displays the privacy policy and allows the user to agree or disagree
 */
public class PrivacyPolicyModalFragment extends DialogFragment {

    @BindView(R.id.checkboxPrivacyPolicyGeneral)
    CheckBox agreeGeneralPolicy;

    @BindView(R.id.checkboxPrivacyPolicyAppSpecific)
    CheckBox agreeAppPolicy;

    @BindView(R.id.checkboxPrivacyPolicyNotificationToken)
    CheckBox agreeNotifications;

    @BindView(R.id.privacyPolicyButtonAgree)
    Button agreeButton;

    @BindView(R.id.privacyPolicyRejectButton)
    Button rejectButton;

    private SharedPreferences prefs;
    private boolean generalPolicyAgreed;
    private boolean appPolicyAgreed;
    private boolean notificationPolicyAgreed;

    public PrivacyPolicyModalFragment() {
        // Required empty public constructor
        setCancelable(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Check current status of user agreement
        generalPolicyAgreed = PermissionHelper.getPreferenceBoolean(
                getContext(), PermissionHelper.AGREE_GENERAL_POLICY_KEY);
        appPolicyAgreed = PermissionHelper.getPreferenceBoolean(
                getContext(), PermissionHelper.AGREE_APP_POLICY_KEY);
        notificationPolicyAgreed = PermissionHelper.getPreferenceBoolean(
                getContext(), PermissionHelper.AGREE_NOTIFICATION_POLICY_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_privacy_policy_modal, container, false);
        ButterKnife.bind(this, view);

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

    private CompoundButton.OnCheckedChangeListener onCheckChangeListener =
            new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            switch (compoundButton.getId()) {
                case(R.id.checkboxPrivacyPolicyGeneral):
                    generalPolicyAgreed = true;
                    break;
                case(R.id.checkboxPrivacyPolicyAppSpecific):
                    appPolicyAgreed = true;
                    break;
                case(R.id.checkboxPrivacyPolicyNotificationToken):
                    notificationPolicyAgreed = true;
                    break;
            }

            agreeButton.setEnabled(generalPolicyAgreed && appPolicyAgreed);
        }
    };

    /**
     * If the user agrees with at least the two basic policies, they can continue
     */
    private View.OnClickListener acceptPolicyListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(generalPolicyAgreed && appPolicyAgreed) {

                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(PermissionHelper.AGREE_GENERAL_POLICY_KEY, generalPolicyAgreed);
                editor.putBoolean(PermissionHelper.AGREE_APP_POLICY_KEY, appPolicyAgreed);
                editor.putBoolean(PermissionHelper.AGREE_NOTIFICATION_POLICY_KEY, notificationPolicyAgreed);
                editor.apply();

                if(notificationPolicyAgreed) {
                    // Create notification channels
                    new NotificationUtil(getContext());
                }

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
        @Override
        public void onClick(View view) {

            // Store disagree choice for all policies
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PermissionHelper.AGREE_GENERAL_POLICY_KEY, false);
            editor.putBoolean(PermissionHelper.AGREE_APP_POLICY_KEY, false);
            editor.putBoolean(PermissionHelper.AGREE_NOTIFICATION_POLICY_KEY, false);

            // Reset permissions from setting menu if applicable
            editor.putBoolean(PermissionHelper.USE_CALENDAR_EXPORT_KEY, false);
            editor.putBoolean(PermissionHelper.CALENDAR_EXPORT_EVENT_AUTO_KEY, false);
            editor.putBoolean(PermissionHelper.CALENDAR_EXPORT_MEETING_AUTO_KEY, false);
            editor.putBoolean(PermissionHelper.USE_PEOPLE_EXPORT_KEY, false);

            editor.apply();

            // Remove notification channels
            new NotificationUtil(getContext()).removeExistingChannels();

            // Logout (which also removes notification token)
            UserHelper.getInstance().logout();

            // Close application. Nothing to do here anymore
            FragmentActivity activity = getActivity();
            if(activity != null) {
                activity.finishAffinity();
            }
            System.exit(0);
        }
    };
}
