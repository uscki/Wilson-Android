package nl.uscki.appcki.android.fragments.poll;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.generated.poll.PollItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class PollResultFragment extends Fragment {
    @BindView(R.id.poll_result_question)
    TextView question;
    @BindView(R.id.poll_result_options)
    RecyclerView options;

    PollItem item;

    public PollResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.currentScreen = MainActivity.Screen.POLL_RESULT;
        View view = inflater.inflate(R.layout.fragment_poll_result, container, false);
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            item = new Gson().fromJson(getArguments().getString("item"), PollItem.class);
            setupViews();
        }

        return view;
    }

    private void setupViews() {
        question.setText(item.getPoll().getTitle());
        options.setAdapter(new PollResultAdapter(item.getOptions()));
    }
}
