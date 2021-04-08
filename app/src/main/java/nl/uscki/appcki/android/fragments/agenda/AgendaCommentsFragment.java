package nl.uscki.appcki.android.fragments.agenda;

import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.fragments.comments.CommentsFragment;
import nl.uscki.appcki.android.generated.comments.Comment;
import retrofit2.Call;

public class AgendaCommentsFragment extends CommentsFragment{

    public AgendaCommentsFragment() {
        // Required empty public constructor
        super(true);
    }

    @Override
    public void onSwipeRefresh() {
        super.onSwipeRefresh();
        Services.getInstance().agendaService.getComments(commentOnTopicId, page, getPageSize()).enqueue(callback);
    }

    @Override
    public void onScrollRefresh() {
        Services.getInstance().agendaService.getComments(commentOnTopicId, page, getPageSize()).enqueue(callback);
    }

    @Override
    public Call<ActionResponse<Comment>> sendCommentToServer(Integer replyToComment, String comment) {
        return Services.getInstance().agendaService.replyToComment(commentOnTopicId, replyToComment, comment);
    }

    @Override
    protected Call<Boolean> deleteCommentFromServer(Integer commentId) {
        return Services.getInstance().agendaService.deleteComment(commentOnTopicId, commentId);
    }

    @Override
    protected boolean getUseNestedScrolling() {
        return true;
    }
}
