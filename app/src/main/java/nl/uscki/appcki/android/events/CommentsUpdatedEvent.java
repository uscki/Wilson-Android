package nl.uscki.appcki.android.events;

import androidx.annotation.Nullable;

import nl.uscki.appcki.android.fragments.comments.CommentsFragment;
import nl.uscki.appcki.android.generated.comments.Comment;

public class CommentsUpdatedEvent {

    public final CommentsFragment fragment;
    public final @Nullable Comment comment;
    public final boolean added;
    public final int numberOfComments;

    public CommentsUpdatedEvent(CommentsFragment fragment, @Nullable Comment comment, boolean added, int numberOfComments) {
        this.fragment = fragment;
        this.comment = comment;
        this.added = added;
        this.numberOfComments = numberOfComments;
    }
}
