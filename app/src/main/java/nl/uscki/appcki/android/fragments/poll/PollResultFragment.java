package nl.uscki.appcki.android.fragments.poll;


import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import nl.uscki.appcki.android.fragments.RefreshableFragment;
import nl.uscki.appcki.android.generated.poll.PollItem;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PollResultFragment extends RefreshableFragment {
    @BindView(R.id.poll_result_question)
    TextView question;
    @BindView(R.id.poll_result_options)
    RecyclerView options;

    PollItem item;

    private ItemTouchHelper attachedSwipeCallback;

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

    /**
     * Is this fragment displaying the active poll?
     * @return  Boolean indicating if the poll currently being displayed is the active poll
     */
    public boolean isActive() {
        return item != null && item.getPoll().getActive();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_poll_result, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        setupSwipeContainer(view);

        if (getArguments() != null) {
            MainActivity.currentScreen = MainActivity.Screen.POLL_DETAIL;
            item = new Gson().fromJson(getArguments().getString("item"), PollItem.class);
            setupViews();
        } else {
            MainActivity.currentScreen = MainActivity.Screen.POLL_ACTIVE;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        inflater.inflate(R.menu.poll_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private boolean canVote() {
        return item != null && item.getPoll().getActive() && (item.getMyVote() == null || item.getMyVote() < 0);
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().pollService.get(item.getId()).enqueue(new Callback<PollItem>() {
            @Override
            public void onSucces(Response<PollItem> response) {
                if(response != null && response.body() != null) {
                    item = response.body();
                    swipeContainer.setRefreshing(false);
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
        swipeContainer.setRefreshing(true);
        PollResultAdapter adapter = (PollResultAdapter) options.getAdapter();
        int optionId = adapter.getItems().get(adapterPosition).getId();

        Services.getInstance().pollService.vote(optionId).enqueue(new Callback<PollItem>() {
            @Override
            public void onSucces(Response<PollItem> response) {
                if(response != null && response.body() != null) {
                    item = response.body();
                    swipeContainer.setRefreshing(false);
                    setupViews();
                }
            }
        });
    }
}
