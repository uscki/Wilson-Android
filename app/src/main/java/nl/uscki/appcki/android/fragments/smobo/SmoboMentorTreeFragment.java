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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private SwipeRefreshLayout swipeContainer;
    private TextView startYear;
    private TextView startedYearsAgo;
    private RecyclerView parents;
    private RecyclerView siblings;
    private RecyclerView childrenContainer;

    private SmoboSearchResultAdapter parentAdapter;
    private SmoboSearchResultAdapter siblingAdapter;
    private SmoboChildNodeAdapter childNodeAdapter;

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
        this.swipeContainer = view.findViewById(R.id.smobo_mentor_tree_swipe_container);
        this.startYear = view.findViewById(R.id.smobo_mentor_tree_startyear);
        this.startedYearsAgo = view.findViewById(R.id.smobo_mentor_tree_started_years_ago);
        this.parents = view.findViewById(R.id.smobo_mentor_tree_parents_recycler_view);
        this.siblings = view.findViewById(R.id.smobo_mentor_tree_siblings_recycler_view);
        this.childrenContainer = view.findViewById(R.id.smobo_mentor_tree_children_recycler_view);

        this.parents.setLayoutManager(new LinearLayoutManager(getContext()));
        this.siblings.setLayoutManager(new LinearLayoutManager(getContext()));
        this.parentAdapter = new SmoboSearchResultAdapter(new ArrayList<>());
        this.siblingAdapter = new SmoboSearchResultAdapter(new ArrayList<>());
        this.parents.setAdapter(this.parentAdapter);
        this.siblings.setAdapter(this.siblingAdapter);

        setupSwipeContainer();

        if(this.activity != null && this.activity.getP() != null) {
            setupItem(this.activity.getP());
        }

        return view;
    }

    private void setupItem(SmoboItem person) {
        this.swipeContainer.setRefreshing(false);
        setupStartYear(person.getGroups());
        setupMentorTree(person.getMentorNode().getSubnodes());
        this.parentAdapter.clear();
        this.parentAdapter.addItems(person.getMentorNode().getParents());
        this.siblingAdapter.clear();
        this.siblingAdapter.addItems(person.getMentorNode().getChildren());
    }

    private void setupMentorTree(List<SmoboMentorNode> subNodes) {
        if(subNodes != null && subNodes.size() > 0) {
            this.childNodeAdapter = new SmoboChildNodeAdapter(this.activity.getP().getPerson(), subNodes);
            this.childrenContainer.setAdapter(this.childNodeAdapter);
            this.childrenContainer.setLayoutManager(new LinearLayoutManager(getContext()));
            this.childrenContainer.setVisibility(View.VISIBLE);
        } else {
            this.childrenContainer.setVisibility(View.GONE);
        }
    }

    protected void setupSwipeContainer() {
        swipeContainer.setOnRefreshListener(() -> this.activity.refreshSmoboItem());

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setRefreshing(false);
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
            this.startedYearsAgo.setText(
                    getString(
                            R.string.smobo_mentortree_formatted_years_ago,
                            new DateTime().getYear() - firstYear.getYear()
                    )
            );
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