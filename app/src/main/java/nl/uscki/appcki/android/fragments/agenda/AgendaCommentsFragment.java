package nl.uscki.appcki.android.fragments.agenda;

import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.comments.CommentsFragment;

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
    protected void sendCommentToServer(Integer parentId, String comment) {
        Services.getInstance().agendaService.replyToComment(commentOnTopicId, parentId, comment).enqueue(commentPostedCallback);
    }
}
