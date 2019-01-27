package nl.uscki.appcki.android.fragments.smobo;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.RefreshableFragment;
import nl.uscki.appcki.android.generated.ListSectionHeader;
import nl.uscki.appcki.android.generated.organisation.PersonSimpleName;
import nl.uscki.appcki.android.generated.smobo.SmoboItem;
import nl.uscki.appcki.android.generated.smobo.SmoboMentorNode;
import retrofit2.Response;

public class SmoboMentorTreeFragment extends RefreshableFragment {

    private int id;

    private Callback<SmoboItem> smoboItemCallback = new Callback<SmoboItem>() {
        @Override
        public void onSucces(Response<SmoboItem> response) {
            SmoboItem item = response.body();
            if(getAdapter() != null) {
                getAdapter().clear();
                addSublist(item.getMentorNode().getParents(), "Ouders");
                addSublist(item.getMentorNode().getChildren(), "Broertjes & Zusjes");

                for(SmoboMentorNode node: item.getMentorNode().getSubnodes()) {
                    for(PersonSimpleName n : node.getParents()) {
                        if(n.getId().equals(id)) {
                            node.getParents().remove(n);
                            break;
                        }
                    }

                    node.getParents().addAll(node.getChildren());
                    addSublist(node.getParents(), "Mentorgroepje");
                }

            }
            swipeContainer.setRefreshing(false);
        }
    };

    private void addSublist(List<PersonSimpleName> list, String header) {
        if(list.size() > 0) {
            getAdapter().add(new ListSectionHeader(header));
            getAdapter().addItems(list);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setAdapter(new SmoboMentorTreeAdapter(new ArrayList<PersonSimpleName>()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if(getArguments() != null) {
            this.id = getArguments().getInt("id");
            setupSwipeContainer(view);
            onSwipeRefresh();
        }



        return view;
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().smoboService.get(this.id).enqueue(smoboItemCallback);
    }
}
