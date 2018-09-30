package nl.uscki.appcki.android.fragments.agenda;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.generated.SingleValueWilsonItem;

public class PossibleAnswersAdapter extends BaseItemAdapter<PossibleAnswersAdapter.ViewHolder, SingleValueWilsonItem<String>> {

    public PossibleAnswersAdapter(List<SingleValueWilsonItem<String>> items) {super(items); }

    private RecyclerView parentRecyclerView;
    private SubscribeDialogFragment dialogFragment;
    private String selectedValue = null;

    public String getSelectedValue() {
        return selectedValue;
    }

    public void setParentElements(RecyclerView parentRecyclerView, SubscribeDialogFragment dialogFragment) {
        this.parentRecyclerView = parentRecyclerView;
        this.dialogFragment = dialogFragment;
    }

    @Override
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_agenda_possible_answer, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(final ViewHolder holder, int position) {
        holder.possibleAnswerText.setText(items.get(position).getValue().trim());
        holder.possibleAnswerValue = items.get(position).getValue();
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetCheckmarks();
                holder.checkmark.setVisibility(View.VISIBLE);
                selectedValue = holder.possibleAnswerValue;
                dialogFragment.notifySelectionMade();
            }
        });
    }

    private void resetCheckmarks() {
        for(int childCount = parentRecyclerView.getChildCount(), i = 0; i < childCount; i++) {
            final ViewHolder holder = (ViewHolder) parentRecyclerView.getChildViewHolder(parentRecyclerView.getChildAt(i));
            if(holder != null) {
                holder.checkmark.setVisibility(View.INVISIBLE);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private String possibleAnswerValue;

        @BindView(R.id.possible_answer_checkmark)
        ImageView checkmark;

        @BindView(R.id.possible_answer_value)
        TextView possibleAnswerText;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.mView = view;
        }

        public void setValue(String possibleAnswerValue) {
            this.possibleAnswerValue = possibleAnswerValue;
        }
    }
}