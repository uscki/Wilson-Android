package nl.uscki.appcki.android.fragments.agenda;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.adapters.AgendaCommentsAdapter;
import nl.uscki.appcki.android.generated.comments.Comment;
import nl.uscki.appcki.android.generated.comments.CommentPage;
import retrofit2.Response;

public class AgendaCommentsFragment extends Fragment{

    private int agendaId;
    private SwipeRefreshLayout refreshContainer;

    private boolean scrollToEnd = false;
    private Integer scrollToState;

    @BindView(R.id.comment_recycler_view)
    RecyclerView commentRecyclerView;

    @BindView(R.id.comment_edit_text)
    EditText commentText;

    @BindView(R.id.place_comment_button)
    ImageButton postCommentButton;

    public AgendaCommentsFragment() {
        // Required empty public constructor
    }

    private Callback<CommentPage> callback = new Callback<CommentPage>() {
        @Override
        public void onSucces(Response<CommentPage> response) {
            if(response != null && response.body() != null) {
                AgendaCommentsAdapter adapter = new AgendaCommentsAdapter(response.body().getContent());
                adapter.setCommentsFragment(AgendaCommentsFragment.this);
                commentRecyclerView.setAdapter(adapter);
                refreshContainer.setRefreshing(false);
                if(scrollToEnd) {
                    Log.e(AgendaCommentsFragment.this.getClass().getSimpleName(), "Scrolling to end?");
                    scrollToEnd = false;
                    commentRecyclerView.scrollToPosition(commentRecyclerView.getAdapter().getItemCount() - 1);
                } else if(scrollToState != null) {
                    Log.e(AgendaCommentsFragment.this.getClass().getSimpleName(), "Scrolling to position " + scrollToState);
                    commentRecyclerView.scrollToPosition(scrollToState);
                    scrollToState = null;
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            agendaId = getArguments().getInt("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_agenda_comments, container, false);
        ButterKnife.bind(this, view);

        commentRecyclerView.setAdapter(new AgendaCommentsAdapter(new ArrayList<Comment>()));
        postCommentButton.setOnClickListener(postCommentListener);

        refreshContainer = view.findViewById(R.id.commentRefreshContainer);
        if(refreshContainer != null) {
            refreshContainer.setOnRefreshListener(onSwipeRefreshListener);
        }

        Services.getInstance().agendaService.getComments(agendaId).enqueue(callback);
        return view;
    }

    public void onSwipeRefresh() {
        clearText(commentText);
        Services.getInstance().agendaService.getComments(agendaId).enqueue(callback);
    }

    View.OnClickListener postCommentListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            postComment(commentText, null);
        }
    };

    public void postComment(EditText commentBox, Integer parentid) {
        refreshContainer.setRefreshing(true);
        String comment = commentBox.getText().toString();
        clearText(commentBox);
        if(parentid == null) {
            scrollToEnd = true;
        } else {
            scrollToState = commentRecyclerView.getScrollState();
        }
        Services.getInstance().agendaService.replyToComment(agendaId, parentid, comment).enqueue(new Callback<CommentPage>() {
            @Override
            public void onSucces(Response<CommentPage> response) {
                onSwipeRefresh();
            }
        });
    }

    private void clearText(EditText textbox) {
        if(textbox != null) {
            textbox.clearFocus();
            textbox.setText("");
        }
    }

    SwipeRefreshLayout.OnRefreshListener onSwipeRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            onSwipeRefresh();
        }
    };
}
