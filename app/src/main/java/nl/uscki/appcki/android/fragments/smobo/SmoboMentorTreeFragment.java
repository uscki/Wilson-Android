package nl.uscki.appcki.android.fragments.smobo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.SmoboActivity;
import nl.uscki.appcki.android.events.DetailItemUpdatedEvent;
import nl.uscki.appcki.android.fragments.adapters.SmoboSearchResultAdapter;
import nl.uscki.appcki.android.generated.organisation.Committee;
import nl.uscki.appcki.android.generated.smobo.SmoboItem;
import nl.uscki.appcki.android.generated.smobo.SmoboMentorNode;

public class SmoboMentorTreeFragment extends Fragment {

    private SmoboActivity activity;
    private TextView startYear;
    private RecyclerView parents;
    private RecyclerView siblings;
    private RecyclerView childrenContainer;

    private SmoboSearchResultAdapter parentAdapter;
    private SmoboSearchResultAdapter siblingAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if(getActivity() instanceof SmoboActivity) {
            this.activity = (SmoboActivity) getActivity();
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_smobo_mentor_tree, container, false);
        this.startYear = view.findViewById(R.id.smobo_mentor_tree_startyear);
        this.parents = view.findViewById(R.id.smobo_mentor_tree_parents_recycler_view);
        this.siblings = view.findViewById(R.id.smobo_mentor_tree_siblings_recycler_view);
        this.childrenContainer = view.findViewById(R.id.smobo_mentor_tree_children_recycler_view);

        this.parents.setLayoutManager(new LinearLayoutManager(getContext()));
        this.siblings.setLayoutManager(new LinearLayoutManager(getContext()));
        this.parentAdapter = new SmoboSearchResultAdapter(new ArrayList<>());
        this.siblingAdapter = new SmoboSearchResultAdapter(new ArrayList<>());
        this.parents.setAdapter(this.parentAdapter);
        this.siblings.setAdapter(this.siblingAdapter);

        if(this.activity != null && this.activity.getP() != null) {
            setupItem(this.activity.getP());
        }

        return view;
    }

    private void setupItem(SmoboItem person) {
        setupStartYear(person.getGroups());
        setupMentorTree(person.getMentorNode());
        this.parentAdapter.clear();
        this.parentAdapter.addItems(person.getMentorNode().getParents());
        this.siblingAdapter.clear();
        this.siblingAdapter.addItems(person.getMentorNode().getChildren());
    }

    private void setupMentorTree(SmoboMentorNode person) {

    }

    private void setupStartYear(List<Committee> groups) {
        DateTime firstYear = null;
        if(groups != null) {
            for (Committee committee : groups) {
                DateTime start = committee.getStart();
                if(firstYear == null || firstYear.isAfter(start)) {
                    firstYear = start;
                }
            }
        }
        if(firstYear != null) {
            this.startYear.setText(firstYear.toString("Y"));
        }
    }

    public void onEventMainThread(DetailItemUpdatedEvent<SmoboItem> item) {
       setupItem(item.getUpdatedItem());
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}