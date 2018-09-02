package nl.uscki.appcki.android.fragments.comments;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
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
    protected Callback<Comment> getPostNewItemCallback() {
        return new Callback<Comment>() {
            @Override
            public void onSucces(Response<Comment> response) {
                if(response != null && response.body() != null) {
                    cleanupAfterPost();
                    commentsFragment.getAdapter().add(response.body());
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
    protected Call<Comment> postNewItem() {
        return commentsFragment.sendCommentToServer(null, getMainTextInput().getText().toString());
    }
}
