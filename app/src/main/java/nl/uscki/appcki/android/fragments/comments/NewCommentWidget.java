package nl.uscki.appcki.android.fragments.comments;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.generated.comments.Comment;
import nl.uscki.appcki.android.views.NewSimplePageableItem;
import retrofit2.Call;
import retrofit2.Response;

public class NewCommentWidget extends NewSimplePageableItem<Comment>{

    // This fragment handles the posting of the comment
    private CommentsFragment commentsFragment;

    public void setCommentsFragment(CommentsFragment commentsFragment) {
        this.commentsFragment = commentsFragment;
    }

    @Override
    protected Callback<ActionResponse<Comment>> getPostNewItemCallback() {
        return new Callback<ActionResponse<Comment>>() {
            @Override
            public void onSucces(Response<ActionResponse<Comment>> response) {
                if(response != null && response.body() != null) {
                    cleanupAfterPost();
                    commentsFragment.commentPostedCallback.onSucces(response);
                    commentsFragment.scrollToEnd();
                }
            }
        };
    }

    @Override
    protected int getHint() {
        return R.string.comment_hint;
    }

    @Override
    protected Call<ActionResponse<Comment>> postNewItem() {
        return commentsFragment.sendCommentToServer(-1, getMainTextInput().getText().toString());
    }

    @Override
    protected int getTagCollection() {
        return R.array.tag_collection_inline_tags;
    }
}
