package nl.uscki.appcki.android.fragments.poll;


import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.generated.poll.PollItem;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PollResultFragment extends Fragment {
    @BindView(R.id.poll_result_question)
    TextView question;
    @BindView(R.id.poll_result_options)
    RecyclerView options;

    PollItem item;

    private ItemTouchHelper attachedSwipeCallback;

    public PollResultFragment() {
        // Required empty public constructor
    }


    /**
     * Handle swipe action on poll options
     *
     * See also https://www.androidhive.info/2017/09/android-recyclerview-swipe-delete-undo-using-itemtouchhelper/
     */
    ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView,
                              RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {

            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            if(viewHolder != null) {
                PollResultAdapter.ViewHolder pollViewHolder = (PollResultAdapter.ViewHolder) viewHolder;
                int itemPosition = pollViewHolder.getAdapterPosition();

                vote(pollViewHolder, itemPosition);
            }
            Log.e(getClass().getSimpleName(), "Swiped item in viewholder in direction " + direction);
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if(viewHolder != null) {
                final View foregroundView = ((PollResultAdapter.ViewHolder) viewHolder).foreground;

                // Make sure the foreground view is the one that moves, keeping the background fixed
                // in place
                getDefaultUIUtil().onSelected(foregroundView);
            }
        }

        @Override
        public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
            final View foregroundView = ((PollResultAdapter.ViewHolder) viewHolder).foreground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            final View foregroundView = ((PollResultAdapter.ViewHolder) viewHolder).foreground;
            getDefaultUIUtil().clearView(foregroundView);
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
            final View foregroundView = ((PollResultAdapter.ViewHolder) viewHolder).foreground;

            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.currentScreen = MainActivity.Screen.POLL_RESULT;
        View view = inflater.inflate(R.layout.fragment_poll_result, container, false);
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            item = new Gson().fromJson(getArguments().getString("item"), PollItem.class);
            setupViews();
        } else {
            Services.getInstance().pollService.active().enqueue(new Callback<PollItem>() {
                @Override
                public void onSucces(Response<PollItem> response) {
                    if(response != null && response.body() != null) {
                        item = response.body();
                        setupViews();
                    }
                }
            });
        }


        return view;
    }

    private boolean canVote() {
        return item != null && item.getPoll().getActive() && (item.getMyVote() == null || item.getMyVote() < 0);
    }

    private void refreshPollItem() {
        Services.getInstance().pollService.get(item.getId()).enqueue(new Callback<PollItem>() {
            @Override
            public void onSucces(Response<PollItem> response) {
                if(response != null && response.body() != null) {
                    item = response.body();
                    item.setMyVote(3);

                    // TODO: Change to if it has a vote or not
                    setupViews();
                }
            }
        });
    }

    private void setupViews() {
        question.setText(item.getPoll().getTitle());
        options.setAdapter(new PollResultAdapter(item.getOptions(), canVote()));
        if(canVote()) {
            attachedSwipeCallback = new ItemTouchHelper(swipeCallback);
            attachedSwipeCallback.attachToRecyclerView(options);
            Log.e(getClass().getSimpleName(), "Enabled");
        } else if(attachedSwipeCallback != null) {
            attachedSwipeCallback.attachToRecyclerView(null);
        }
    }

    void vote(PollResultAdapter.ViewHolder viewHolder, int adapterPosition) {
        Log.e(getClass().getSimpleName(), "Voting for " + adapterPosition + "th element!");
        refreshPollItem();
    }
}
