package nl.uscki.appcki.android.fragments.adapters;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.SmoboActivity;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.fragments.comments.CommentsFragment;
import nl.uscki.appcki.android.generated.comments.Comment;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;

public class CommentsAdapter extends BaseItemAdapter<CommentsAdapter.ViewHolder, Comment>{
    private boolean isNested = false;
    private CommentsFragment commentsFragment;

    public CommentsAdapter(List<Comment> items) {
        super(items);
    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_comment_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Set a reference to the CommentsFragment that uses this adapter, so replies can be posted
     * @param fragment  CommentsFragment that uses this adapter
     */
    public void setCommentsFragment(CommentsFragment fragment) {
        this.commentsFragment = fragment;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentsAdapter.ViewHolder holder, int position) {
        holder.comment = items.get(position);

        // Set the photo of the commenter
        Integer profilePictureId = holder.comment.person.getPhotomediaid();
        if(profilePictureId != null) {
            holder.commenterPhoto.setImageURI(MediaAPI.getMediaUri(profilePictureId, MediaAPI.MediaSize.SMALL));
        }

        // If privacy allows it, link the photo to the smobo
        if(holder.comment.person.getDisplayonline()) {
            holder.commenterPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent smoboIntent = new Intent(view.getContext(), SmoboActivity.class);
                    smoboIntent.putExtra("id", holder.comment.person.getId());
                    smoboIntent.putExtra("name", holder.comment.person.getPostalname());
                    smoboIntent.putExtra("photo", holder.comment.person.getPhotomediaid());
                    view.getContext().startActivity(smoboIntent);
                }
            });
        }

        // Set name of commenter
        SpannableString commmenterName = new SpannableString(holder.comment.person.getPostalname());
        commmenterName.setSpan(new StyleSpan(Typeface.BOLD), 0, commmenterName.length(), 0);
        holder.commenterName.setText(commmenterName);

        // Set the content of the comment
        holder.commentContent.setText(Parser.parse(holder.comment.comment, true, holder.commentContent));

        // Replies to comments, if any, are added recursively. As for now, only one level of
        // recursion is allowed
        if(holder.comment.reactions != null && !holder.comment.reactions.isEmpty() && !isNested) {
            // Adapter can have trace replies from previous load. Clear first
            holder.adapter.clear();
            holder.adapter.addItems(holder.comment.reactions);
        }

        // Set visibilities depending on level of recursion
        holder.replyRow.setVisibility(View.GONE);
        if(isNested) {
            holder.itemView.findViewById(R.id.comment_list_divider).setVisibility(View.GONE);
            holder.replyButton.setVisibility(View.INVISIBLE);
        } else {
            holder.replyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.replyRow.setVisibility(View.VISIBLE);
                }
            });
            holder.activateReplyCommentButton();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final View mView;
        public Comment comment;
        public CommentsAdapter adapter;

        @BindView(R.id.comment_person_photo)
        public SimpleDraweeView commenterPhoto;

        @BindView(R.id.comment_placer_name)
        public TextView commenterName;

        @BindView(R.id.comment_reply_button)
        public ImageButton replyButton;

        @BindView(R.id.comment_content)
        BBTextView commentContent;

        @BindView(R.id.comment_replies)
        RecyclerView replies;

        @BindView(R.id.comment_reply_row)
        TableRow replyRow;

        @BindView(R.id.place_comment_button)
        ImageButton postCommentButton;

        @BindView(R.id.comment_edit_text)
        EditText actualCommentText;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            ButterKnife.bind(this, itemView);
            if(adapter != null) {
                adapter.clear();
            }
            adapter = new CommentsAdapter(new ArrayList<Comment>());
            adapter.isNested = true;
            replies.setAdapter(adapter);
        }

        // Add a listener to the post comment button to process the comment
        void activateReplyCommentButton() {
            postCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    replyRow.setVisibility(View.GONE);
                    commentsFragment.postComment(actualCommentText, ViewHolder.this);
                }
            });
        }
    }
}
