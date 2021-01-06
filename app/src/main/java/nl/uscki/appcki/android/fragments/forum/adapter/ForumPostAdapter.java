package nl.uscki.appcki.android.fragments.forum.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.BasicActivity;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.fragments.forum.ForumPostOverviewFragment;
import nl.uscki.appcki.android.generated.forum.Post;
import nl.uscki.appcki.android.generated.forum.Topic;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;

public class ForumPostAdapter extends BaseItemAdapter<ForumPostAdapter.ForumPostViewHolder, Post> {

    private BasicActivity activity;
    private ForumPostOverviewFragment overviewFragment;
    private Topic topic;

    public ForumPostAdapter(List<Post> items, ForumPostOverviewFragment overviewFragment, BasicActivity activity, Topic topic) {
        super(items);
        this.overviewFragment = overviewFragment;
        this.activity = activity;
        this.topic = topic;
    }

    @Override
    public ForumPostViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        return new ForumPostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_post_layout, parent, false));
    }

    @Override
    public void onBindCustomViewHolder(ForumPostViewHolder holder, int position) {
        holder.populateFromPost(items.get(position));
    }

    public class ForumPostViewHolder extends RecyclerView.ViewHolder {
        private Post post;

        private ImageView authorImage;
        private TextView authorName;
        private TextView postedDate;

        private ImageView linkButton;
        private ImageView quoteButton;

        private BBTextView content;

        private ImageView signatureDivider;
        private TextView signature;

        public ForumPostViewHolder(@NonNull View itemView) {
            super(itemView);
            this.authorImage = itemView.findViewById(R.id.forum_post_author_image);
            this.authorName = itemView.findViewById(R.id.forum_post_author_name);
            this.postedDate = itemView.findViewById(R.id.forum_post_posted_date);
            this.linkButton = itemView.findViewById(R.id.forum_post_link_button);
            this.quoteButton = itemView.findViewById(R.id.forum_post_quote_text_button);
            this.content = itemView.findViewById(R.id.forum_post_content);
            this.signatureDivider = itemView.findViewById(R.id.forum_post_signature_divider);
            this.signature = itemView.findViewById(R.id.forum_post_signature);
        }

        public void populateFromPost(Post post) {
            this.post = post;
            Context c = itemView.getContext();
            if(post.getPerson() != null) {
                loadAuthorPhoto(post);
            }
            this.authorName.setText(post.getPosterName());

            this.postedDate.setText(post.getPost_time().toString(c.getString(R.string.joda_datetime_format_year_month_day_time_with_day_names)));
            Drawable editedDrawable = null;
            if(post.getPost_time().getMillis() - post.getOriginal_post_time().getMillis() < -3000) {
                // Equals goes on millisecond basis, but parsing is less accurate than that
                // Let's assume if people edit within 3 seconds it should be considered the same post :)
                editedDrawable = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_outline_edit_24px_black);
            }
            this.postedDate.setCompoundDrawablesRelativeWithIntrinsicBounds(editedDrawable, null, null, null);

            this.content.setText(Parser.parse(post.getPost(), true, content));

            if(post.getSignature() != null && !post.getSignature().isEmpty()) {
                SpannableString s = new SpannableString(post.getSignature());
                s.setSpan(new StyleSpan(Typeface.ITALIC),0, s.length(), 0);
                signature.setText(s);
                signature.setVisibility(View.VISIBLE);
                signatureDivider.setVisibility(View.VISIBLE);
            } else {
                signatureDivider.setVisibility(View.GONE);
                signature.setVisibility(View.GONE);
            }

            quoteButton.setOnClickListener(v -> {
                String quotedText = this.content.getText().toString(); // R.I.P. formatting. Raw BB code would be useful here, but then we get issues with indices
                if(content.getSelectionStart() >= 0 && content.getSelectionEnd() - content.getSelectionStart() > 0) {
                    quotedText = quotedText.substring(content.getSelectionStart(), content.getSelectionEnd());
                }
                String author = post.getPosterName();
                String quote = String.format("[quote author=\"%s\"]%s[/quote]", author, quotedText);
                overviewFragment.showNewPostDialog(quote);
            });

            linkButton.setOnClickListener(v -> {
                String url = itemView.getResources().getString(
                        R.string.incognito_website_forum_post,
                        topic.getId(), post.getId()
                );
                String shareText = itemView.getResources().getString(
                        R.string.wilson_media_forum_post_share_intent_text_extra,
                        post.getPosterName(),
                        topic.getTitle(),
                        url
                );

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TITLE, shareText);
                intent.putExtra(Intent.EXTRA_TEXT, shareText);
                intent.setType("text/*");
                itemView.getContext().startActivity(Intent.createChooser(intent,
                        itemView.getContext().getString(R.string.app_general_action_share_intent_text)));
            });
        }

        private void loadAuthorPhoto(Post post) {
            if(post.getPerson() != null && post.getPerson().getPhotomediaid() != null) {
                Glide.with(itemView.getContext())
                        .load(MediaAPI.getMediaUri(post.getPerson().getPhotomediaid(), MediaAPI.MediaSize.SMALL))
                        .circleCrop()
                        .error(R.drawable.account)
                        .into(authorImage);
            } else {
                // User not logged in, or external
                authorImage.setImageDrawable(null);
            }

            if(post.getPerson() != null) {
                authorImage.setOnClickListener(v -> {
                    ForumPostAdapter.this.activity.openSmoboFor(post.getPerson());
                });
            }
        }
    }
}
