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
import android.support.v4.app.FragmentManager;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.SmoboActivity;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.fragments.agenda.AgendaCommentsFragment;
import nl.uscki.appcki.android.generated.comments.Comment;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;

public class AgendaCommentsAdapter extends BaseItemAdapter<AgendaCommentsAdapter.ViewHolder, Comment>{
    private boolean isNested = false;
    private AgendaCommentsFragment commentsFragment;

    public AgendaCommentsAdapter(List<Comment> items) {
        super(items);
    }

    @NonNull
    @Override
    public AgendaCommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_comment_item, parent, false);
        return new ViewHolder(view);
    }

    public void setCommentsFragment(AgendaCommentsFragment fragment) {
        this.commentsFragment = fragment;
    }

    @Override
    public void addItems(List<Comment> items) {
        super.addItems(items);
    }

    @Override
    public void onBindViewHolder(@NonNull final AgendaCommentsAdapter.ViewHolder holder, int position) {
        holder.comment = items.get(position);

        Integer profilePictureId = holder.comment.person.getPhotomediaid();
        if(profilePictureId != null) {
            holder.commenterPhoto.setImageURI(MediaAPI.getMediaUri(profilePictureId, MediaAPI.MediaSize.SMALL));
        }
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

        holder.commentContent.setText(Parser.parse(holder.comment.comment, true, holder.commentContent));

        // Wheeee, recursion!
        Log.e(getClass().getSimpleName(), "Adding replies to holder");
        if(holder.comment.reactions != null && !holder.comment.reactions.isEmpty()) {
            // Adapter can have trace replies from previous load. Clear first
            holder.adapter.clear();
            holder.adapter.addItems(holder.comment.reactions);
        }

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

    @Override
    public int getItemViewType(int position) {
        // TODO if it is a List of comments,
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final View mView;
        public Comment comment;
        public AgendaCommentsAdapter adapter;
        private FragmentManager fm;

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
            adapter = new AgendaCommentsAdapter(new ArrayList<Comment>());
            adapter.isNested = true;
            replies.setAdapter(adapter);
        }

        void activateReplyCommentButton() {
            postCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    replyRow.setVisibility(View.GONE);
                    commentsFragment.postComment(actualCommentText, comment.id);
                }
            });
        }
    }
}
