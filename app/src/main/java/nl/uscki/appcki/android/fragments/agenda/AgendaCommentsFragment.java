package nl.uscki.appcki.android.fragments.agenda;

import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.comments.CommentsFragment;
import nl.uscki.appcki.android.generated.comments.Comment;
import retrofit2.Call;

public class AgendaCommentsFragment extends CommentsFragment{

    public AgendaCommentsFragment() {
        // Required empty public constructor
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
    public Call<Comment> sendCommentToServer(Integer replyToComment, String comment) {
        return Services.getInstance().agendaService.replyToComment(commentOnTopicId, replyToComment, comment);
    }
}
