package nl.uscki.appcki.android.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.joda.time.DateTime;

import java.util.Locale;

import nl.uscki.appcki.android.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppInfoFragment extends Fragment {

    private ImageView instagramButton;
    private ImageView facebookButton;

    private TextView versionCodeTextView;
    private TextView reportBugGithubTextView;
    private TextView reportBugUsckiTextView;

    private TextView copyrightYearEnd;
    private TextView privacyPolicyText;
    private TextView dataIndexationText;

    public AppInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AppInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AppInfoFragment newInstance(String param1, String param2) {
        return new AppInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_app_info, container, false);

        this.instagramButton = view.findViewById(R.id.clickable_instagram_button);
        this.facebookButton = view.findViewById(R.id.clickable_facebook_button);
        this.versionCodeTextView = view.findViewById(R.id.app_info_version_code);
        this.reportBugGithubTextView = view.findViewById(R.id.app_info_report_bug_github);
        this.reportBugUsckiTextView = view.findViewById(R.id.app_info_report_bug_uscki);
        this.copyrightYearEnd = view.findViewById(R.id.app_info_copyright_current);
        this.privacyPolicyText = view.findViewById(R.id.app_info_privacy_policy_link);
        this.dataIndexationText = view.findViewById(R.id.app_info_data_indexation_link);

        setVersionAndCopyright();
        createUrlListeners();

        return view;
    }

    private void setVersionAndCopyright() {
        try {
            Context c = getContext();
            PackageInfo pi = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            this.versionCodeTextView.setText(pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            this.versionCodeTextView.setVisibility(View.GONE);
            e.printStackTrace();
        }
        this.copyrightYearEnd.setText(String.format(Locale.getDefault()," %d", DateTime.now().getYear() + 1));
    }

    private void createUrlListeners() {
//        this.reportBugGithubTextView.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://git.dev.uscki.nl/AppCKI/appcki-native-android/issues"))));
        this.reportBugGithubTextView.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/uscki/Wilson-Android/issues"))));
        this.reportBugUsckiTextView.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.uscki.nl/?pagina=Bugtracker/Edit"))));
        this.instagramButton.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/uscki_incognito"))));
        this.facebookButton.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/uscki/"))));
        this.dataIndexationText.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.uscki.nl/?pagina=Wicki/WPublic&subject=dataindexatie"))));

        // TODO, ideally there would be a link to the USCKI Privacy Policy here, which opens the PrivacyPolicyModalFragment.
        //  - However, thanks to the settings page, we are now working with multiple incompatible versions of the Fragment class
        //  - So this will require a refactor of settings page & PrivacyPolicyModalFragment
    }

}