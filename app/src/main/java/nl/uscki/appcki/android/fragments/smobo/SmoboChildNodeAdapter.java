package nl.uscki.appcki.android.fragments.smobo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.fragments.adapters.SmoboSearchResultAdapter;
import nl.uscki.appcki.android.generated.organisation.Person;
import nl.uscki.appcki.android.generated.smobo.SmoboMentorNode;

public class SmoboChildNodeAdapter extends BaseItemAdapter<SmoboChildNodeAdapter.ViewHolder, SmoboMentorNode> {

    private Person person;

    protected SmoboChildNodeAdapter(Person person, List<SmoboMentorNode> items) {
        super(items);
        this.person = person;
    }

    @Override
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_smobo_mentor_tree_child_node_layout, parent, false);
        return new ViewHolder(view, this.person);
    }

    @Override
    public void onBindCustomViewHolder(ViewHolder holder, int position) {
        holder.setupItem(this.items.get(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private SmoboMentorNode node;
        private Person person;

        private TextView groupName;
        private TextView wasParentDescription;
        private TextView parentsHeader;
        private RecyclerView parents;
        private RecyclerView children;

        private SmoboSearchResultAdapter parentAdapter;
        private SmoboSearchResultAdapter childAdapter;

        public ViewHolder(@NonNull View itemView, Person person) {
            super(itemView);
            this.person = person;

            this.groupName = itemView.findViewById(R.id.smobo_mentor_tree_child_node_mentor_group_name);
            this.wasParentDescription = itemView.findViewById(R.id.smobo_mentor_tree_child_node_was_parent_description);
            this.parentsHeader = itemView.findViewById(R.id.smobo_mentor_tree_child_node_parents_header);
            this.parents = itemView.findViewById(R.id.smobo_mentor_tree_child_node_parents);
            this.children = itemView.findViewById(R.id.smobo_mentor_tree_child_node_children);
            this.parents.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            this.children.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }

        public void setupItem(SmoboMentorNode node) {
            this.node = node;

            this.groupName.setText(concatenateParents());
            this.wasParentDescription.setText(
                    itemView.getContext().getString(
                        R.string.smobo_mentor_tree_was_parent_description,
                        this.person.getPostalname()
                    )
            );
            this.parentsHeader.setText(itemView.getContext().getString(
                    this.node.getParents().size() == 2 ? // excluding current person
                    R.string.smobo_mentor_tree_partner_singular :
                    R.string.smobo_mentor_tree_partner_plural
            ));

            // Do not display the person who's profle we are viewing as a partner of themselves
            // Honestly, the fact that this worked on the one person I tested on was magic. May break on others :')
            this.node.getParents().remove(this.person);

            this.parents.setAdapter( new SmoboSearchResultAdapter(node.getParents()));
            this.children.setAdapter(new SmoboSearchResultAdapter(node.getChildren()));
        }

        private String concatenateParents() {
            StringBuilder groupName = new StringBuilder();
            for(int i = 0; i < this.node.getParents().size(); i++) {
                groupName.append(this.node.getParents().get(i).getPostalname());
                if(i < this.node.getParents().size() - 2) {
                    groupName.append(", ");
                } else
                if (i < this.node.getParents().size() - 1) {
                    groupName.append(" & ");
                }
            }
            return groupName.toString();
        }
    }
}
