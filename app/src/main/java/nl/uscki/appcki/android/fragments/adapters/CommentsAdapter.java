package nl.uscki.appcki.android.fragments.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.activities.AgendaActivity;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.fragments.comments.CommentsFragment;
import nl.uscki.appcki.android.generated.comments.Comment;
import nl.uscki.appcki.android.helpers.UserHelper;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;

public class CommentsAdapter extends BaseItemAdapter<CommentsAdapter.ViewHolder, Comment>{
    private boolean isNested = false;
    private CommentsFragment commentsFragment;

    private ViewHolder hasVisibleReplyRowViewHolder = null;

    public CommentsAdapter(CommentsFragment commentsFragment, List<Comment> items) {
        super(items);
        this.commentsFragment = commentsFragment;
    }

    @Override
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_comment_item, parent, false);
        return new ViewHolder(view);
    }

    public boolean hideReplyBox() {
        if(hasVisibleReplyRowViewHolder != null) {
            hasVisibleReplyRowViewHolder.replyRow.setVisibility(View.GONE);
            hasVisibleReplyRowViewHolder.actualCommentText.setText("");
            hasVisibleReplyRowViewHolder = null;
            return true;
        }
        return false;
    }

    /**
     * NOTE: Explicitly set all properties. MediaCollectionViewHolder may be overwritten with a different comment.
     * Not all viewholders are built from scratch!
     *
     * If properties are not explicitly set, they can take the value of the wrong comment
     * 
     * @param holder        MediaCollectionViewHolder to populate
     * @param position      Position of the item in the adapter, which contains the data to populate
     *                      the viewholder with
     */
    @Override
    public void onBindCustomViewHolder(@NonNull final CommentsAdapter.ViewHolder holder, int position) {
        holder.actualCommentText.setText("");
        holder.comment = items.get(position);

        // Set the photo of the commenter
        Integer profilePictureId = holder.comment.person.getPhotomediaid();
        if(profilePictureId != null) {
            Glide.with(holder.mView)
                    .load(MediaAPI.getMediaUri(profilePictureId, MediaAPI.MediaSize.SMALL))
                    .fitCenter()
                    .optionalCircleCrop()
                    .placeholder(R.drawable.account)
                    .into(holder.commenterPhoto);
        } else {
            Glide.with(holder.mView)
                    .clear(holder.commenterPhoto);
        }

        final AgendaActivity act = (AgendaActivity)holder.mView.getContext();
        if(act != null) {
            holder.commenterPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    act.openSmoboFor(holder.comment.person);
                }
            });
        } else {
            Log.e(getClass().getSimpleName(),
                    "Could not obtain AgendaActivity from viewholder view. " +
                            "Can't register onClick listener for smobo picture");
        }

        // Set name of commenter
        SpannableString commmenterName = new SpannableString(holder.comment.person.getPostalname());
        commmenterName.setSpan(new StyleSpan(Typeface.BOLD), 0, commmenterName.length(), 0);
        holder.commenterName.setText(commmenterName);

        // Set the content of the comment
        holder.commentContent.setText(Parser.parse(holder.comment.comment, true, holder.commentContent));

        // Clear the view holder every time it is bound. Otherwise, for some reason, replies can
        // appear on the wrong comments, and are never removed again
        holder.adapter.clear();

        // Replies to comments, if any, are added recursively. As for now, only one level of
        // recursion is allowed
        if(holder.comment.reactions != null && !holder.comment.reactions.isEmpty() && !isNested) {
            // Adapter can have trace replies from previous load. Clear first
            holder.adapter.addItems(holder.comment.reactions);
        }

        // Set visibilities depending on level of recursion
        holder.replyRow.setVisibility(View.GONE);
        if(isNested) {
            holder.itemView.findViewById(R.id.comment_list_divider).setVisibility(View.GONE);
            holder.replyButton.setVisibility(View.INVISIBLE);
        } else {
            holder.itemView.findViewById(R.id.comment_list_divider).setVisibility(View.VISIBLE);
            holder.replyButton.setVisibility(View.VISIBLE);
            holder.replyButton.setOnClickListener(view -> {
                // Allows intercepting onBackPressed later
                hasVisibleReplyRowViewHolder = holder;
                commentsFragment.getChildFragmentManager()
                        .beginTransaction()
                        .addToBackStack("new_comment_reply")
                        .commit();
                holder.replyRow.setVisibility(View.VISIBLE);
                Utils.toggleKeyboardForEditBox(
                        holder.mView.getContext(),
                        holder.actualCommentText,
                        true);
            });
            holder.activateReplyCommentButton();
        }

        // Is this comment an announcement?
        if(holder.comment.announcement) {
            holder.showIsAnnouncementView.setVisibility(View.VISIBLE);
        } else {
            // Set view to gone explicitly, because sometimes the view holder is shortly bound
            // with the wrong comment. In that case, it is set to visible, and never to invisible
            // again
            holder.showIsAnnouncementView.setVisibility(View.GONE);
        }

        // Set comment delete button behavior
        if(canCommentDelete(holder.comment)) {
            holder.comment_delete_view.setVisibility(View.VISIBLE);
            holder.activateDeleteCommentButton();
        } else {
            holder.comment_delete_view.setOnClickListener(null);
            holder.comment_delete_view.setVisibility(View.GONE);
        }
    }

    private boolean canCommentDelete(Comment comment) {
        return comment.person.equals(UserHelper.getInstance().getCurrentUser()) &&
                comment.reactions.isEmpty() && commentsFragment.canDelete();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final View mView;
        public Comment comment;
        public CommentsAdapter adapter;

        ImageView commenterPhoto;
        TextView commenterName;
        ImageView showIsAnnouncementView;
        ImageButton comment_delete_view;
        ImageButton replyButton;
        BBTextView commentContent;
        RecyclerView replies;
        TableRow replyRow;
        ImageButton postCommentButton;
        EditText actualCommentText;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            actualCommentText = itemView.findViewById(R.id.comment_edit_text);
            postCommentButton = itemView.findViewById(R.id.place_comment_button);
            replyRow = itemView.findViewById(R.id.comment_reply_row);
            replies = itemView.findViewById(R.id.comment_replies);
            commentContent = itemView.findViewById(R.id.comment_content);
            replyButton = itemView.findViewById(R.id.comment_reply_button);
            showIsAnnouncementView = itemView.findViewById(R.id.verified_announcement_view);
            comment_delete_view = itemView.findViewById(R.id.comment_delete_view);
            commenterName = itemView.findViewById(R.id.comment_placer_name);
            commenterPhoto = itemView.findViewById(R.id.comment_person_photo);

            if(adapter != null) {
                adapter.clear();
            }
            adapter = new CommentsAdapter(CommentsAdapter.this.commentsFragment, new ArrayList<Comment>());
            adapter.isNested = true;
            replies.setAdapter(adapter);
        }

        // Add a listener to the post comment button to process the comment
        void activateReplyCommentButton() {
            postCommentButton.setOnClickListener(view -> {
                replyRow.setVisibility(View.GONE);
                commentsFragment.postComment(actualCommentText, ViewHolder.this);
                hasVisibleReplyRowViewHolder = null;
                commentsFragment.getChildFragmentManager().popBackStack("new_comment_reply", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            });
        }

        void activateDeleteCommentButton() {
            comment_delete_view.setOnClickListener(view -> new AlertDialog.Builder(mView.getContext())
                    .setTitle(R.string.comment_delete_header)
                    .setMessage(R.string.comment_delete_message)
                    .setIcon(R.drawable.ic_delete_24px)
                    .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            commentsFragment.deleteComment(comment.id);
                        }
                    })
                    .setNegativeButton(R.string.dialog_no, null)
                    .show());
        }
    }
}
