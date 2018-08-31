package nl.uscki.appcki.android.fragments.agenda;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;

import butterknife.BindView;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.adapters.AgendaCommentsAdapter;
import nl.uscki.appcki.android.generated.comments.Comment;
import nl.uscki.appcki.android.generated.comments.CommentPage;
import retrofit2.Response;

public class AgendaCommentsFragment extends PageableFragment<CommentPage>{

    private final int COMMENTS_PAGE_SIZE = 10;

    private int agendaId;

    private boolean scrollToEnd = false;
    private Integer scrollToState;

    @BindView(R.id.comment_edit_text)
    EditText commentText;

    @BindView(R.id.place_comment_button)
    ImageButton postCommentButton;

    public AgendaCommentsFragment() {
        // Required empty public constructor
    }

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

        AgendaCommentsAdapter adapter = new AgendaCommentsAdapter(new ArrayList<Comment>());
        adapter.setCommentsFragment(this);
        setAdapter(adapter);
        Services.getInstance().agendaService.getComments(agendaId, page, getPageSize()).enqueue(callback);

        // TODO restore post comment funcionality
//        postCommentButton.setOnClickListener(postCommentListener);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSwipeRefresh() {
        clearText(commentText);
        Services.getInstance().agendaService.getComments(agendaId, page, getPageSize()).enqueue(callback);
    }

    @Override
    public void onScrollRefresh() {
        Services.getInstance().agendaService.getComments(agendaId, page, getPageSize()).enqueue(callback);
    }

    @Override
    public String getEmptyText() {
        return "Geen reacties";
    }

    @Override
    protected int getPageSize() {
        return COMMENTS_PAGE_SIZE;
    }

    View.OnClickListener postCommentListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            postComment(commentText, null);
        }
    };

    public void postComment(EditText commentBox, final Integer parentid) {
        String comment = commentBox.getText().toString();
        clearText(commentBox);
        // TODO restore scroll functionality after placing comment
//        if(parentid == null) {
//            scrollToEnd = true;
//        } else {
//            scrollToState = commentRecyclerView.getScrollState();
//        }
        Services.getInstance().agendaService.replyToComment(agendaId, parentid, comment).enqueue(new Callback<CommentPage>() {
            @Override
            public void onSucces(Response<CommentPage> response) {
                if(parentid == null) {
                    noMoreContent = false;
                    onScrollRefresh();
                } else {
                    // We don't know what page this will be on
                    onSwipeRefresh();
                }
            }
        });
    }

    private void clearText(EditText textbox) {
        if(textbox != null) {
            textbox.clearFocus();
            textbox.setText("");
        }
    }
}
