package nl.uscki.appcki.android.fragments.comments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;

import butterknife.BindView;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.adapters.CommentsAdapter;
import nl.uscki.appcki.android.generated.comments.Comment;
import nl.uscki.appcki.android.generated.comments.CommentPage;
import nl.uscki.appcki.android.views.NewSimplePageableItem;
import retrofit2.Call;
import retrofit2.Response;

public abstract class CommentsFragment extends PageableFragment<CommentPage> {
    private static final int COMMENTS_PAGE_SIZE = 10;

    protected int commentOnTopicId;

    private boolean scrollToEnd = false;
    private Integer scrollToState;

    private CommentsAdapter addNextCallbackToAdapter;

    @BindView(R.id.comment_edit_text)
    EditText commentText;

    @BindView(R.id.place_comment_button)
    ImageButton postCommentButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            commentOnTopicId = getArguments().getInt("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create an adapter for this fragment
        CommentsAdapter adapter = new CommentsAdapter(new ArrayList<Comment>());
        adapter.setCommentsFragment(this);
        setAdapter(adapter);

        // Load initial data
        onSwipeRefresh();

        // TODO restore post comment funcionality
//        postCommentButton.setOnClickListener(postCommentListener);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setEditBoxPosition(NEW_ITEM_EDIT_BOX_POSITION_BOTTOM);
        setScrollDirection(FIRST_ON_TOP);
        NewCommentWidget newCommentWidget = new NewCommentWidget();
        newCommentWidget.setCommentsFragment(this);

        // TODO add this fragment propertly
//        this.addNewPageableItemWidget(newCommentWidget);
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.comments_empty_text);
    }

    @Override
    protected int getPageSize() {
        return COMMENTS_PAGE_SIZE;
    }

    @Override
    public void onSwipeRefresh() { clearText(commentText); }

    /**
     * Post a new comment, as typed in the given edit box
     *
     * @param commentBox      Edit box containing comment text
     * @param parentId        ID of comment this comment is a reply to, or null if this comment
     *                        is not a comment
     */
    public void postComment(EditText commentBox, final Integer parentId) {
        String comment = commentBox.getText().toString();
        clearText(commentBox);
        sendCommentToServer(parentId, comment);
    }

    /**
     * Post a new comment, as typed in the given edit box as a reply to another comment
     *
     * @param commentBox            Edit box containing comment text
     * @param commentViewHolder     View holder for the comment this comment is a reply to.
     */
    public void postComment(EditText commentBox, CommentsAdapter.ViewHolder commentViewHolder) {
        addNextCallbackToAdapter = commentViewHolder.adapter;
        postComment(commentBox, commentViewHolder.comment.id);
    }

    /**
     * This method should post a new comment to the server. Use the field `commentOnTopicId`
     *
     * @param parentId              (Optional) the ID of the comment this is a reply to (if any)
     * @param comment               The comment content
     * @return                      Comment object as returned by the server
     */
    protected abstract Call<Comment> sendCommentToServer(Integer parentId, String comment);

    /**
     * Clear the text of an edit box and remove focus.
     * @param textbox   Edit box to clear
     */
    protected void clearText(EditText textbox) {
        if(textbox != null) {
            Utils.toggleKeyboardForEditBox(getContext(), textbox, false);
            textbox.setText("");
        }
    }

    /**
     * onClick listener for posting a comment that is not a reply
     */
    private View.OnClickListener postCommentListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            postComment(commentText, (Integer) null);
        }
    };

    /**
     * Callback used for posting a new comment. This callback automatically
     * processes the server reply and adds the result to the comment fragment
     */
    protected Callback<Comment> commentPostedCallback = new Callback<Comment>() {
        @Override
        public void onSucces(Response<Comment> response) {
            if(response != null && response.body() != null) {
                Comment c = response.body();
                if(addNextCallbackToAdapter != null) {
                    addNextCallbackToAdapter.add(c);
                    addNextCallbackToAdapter = null;
                } else {
                    getAdapter().add(c);
                }
            }
        }
    };
}
