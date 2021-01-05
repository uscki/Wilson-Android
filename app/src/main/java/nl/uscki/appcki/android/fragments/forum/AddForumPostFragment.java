package nl.uscki.appcki.android.fragments.forum;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Locale;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.events.DetailItemUpdatedEvent;
import nl.uscki.appcki.android.generated.forum.Post;
import nl.uscki.appcki.android.generated.organisation.CurrentUser;
import nl.uscki.appcki.android.helpers.UserHelper;
import nl.uscki.appcki.android.views.BBEditView;
import retrofit2.Response;

public class AddForumPostFragment extends DialogFragment {

    public static final String ARG_INITIAL_CONTENT = "nl.uscki.wilson.FORUM.ARG.NEW_POST.INITIAL_CONTENT";
    private static final String SHARED_PREFERENCE_SAVED_POST_TMP_FORMAT = "nl.uscki.wilson.SharedPreferences.SAVED_POST_TOPIC_%d";

    private int topicId;
    private String initialContent;

    private BBEditView content;
    private EditText signature;
    private EditText name;
    private Button cancelButton;
    private Button previewButton;
    private Button confirmButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dialog);

        assert getArguments() != null;
        topicId = getArguments().getInt(ForumTopicOverviewFragment.ARG_TOPIC_ID);
        initialContent = getArguments().getString(ARG_INITIAL_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forum_post_editor, container, false);

        name = view.findViewById(R.id.forum_post_editor_name);
        signature = view.findViewById(R.id.forum_post_editor_signature);
        cancelButton = view.findViewById(R.id.forum_post_editor_post_cancel_btn);
        previewButton = view.findViewById(R.id.forum_post_editor_post_preview_btn);
        confirmButton = view.findViewById(R.id.forum_post_editor_post_confirm_btn);

        cancelButton.setOnClickListener(v -> dismiss());
        confirmButton.setOnClickListener(v -> {
            if(content.getPostContent() == null || content.getPostContent().isEmpty()) {
                Toast.makeText(getContext(), R.string.wilson_media_forum_empty_post_warning, Toast.LENGTH_LONG).show();
            } else if (name.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), R.string.wilson_media_forum_empty_name_warning, Toast.LENGTH_LONG).show();
            }
            Services.getInstance().forumService.addPost(-1, topicId,
                content.getPostContent(), name.getText().toString(),
                signature.getText().toString())

                .enqueue(new Callback<ActionResponse<Post>>() {
                    @Override
                    public void onSucces(Response<ActionResponse<Post>> response) {
                        if(response != null && response.body() != null && response.body().payload != null) {
                            content.setPostContent(""); // Calling this before dismissing removes the stored key
                            EventBus.getDefault().post(new DetailItemUpdatedEvent<>(response.body().payload));
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), R.string.unknown_server_error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
        });

        CurrentUser currentUser = UserHelper.getInstance().getCurrentUser();
        name.setText(currentUser.getNickname());
        signature.setText(currentUser.getSignature());

        setupEditor();
        return view;
    }

    private void setupEditor() {
        String savedPost = null;
        if(getActivity() != null && getContext() != null) {
            SharedPreferences p = getActivity().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE);
            savedPost = p.getString(
                    String.format(Locale.getDefault(), SHARED_PREFERENCE_SAVED_POST_TMP_FORMAT, this.topicId),
                    null
            );
        }

        if(savedPost != null && initialContent != null) {
            savedPost += " " + initialContent;
        } else if (initialContent != null) {
            savedPost = initialContent;
        }

        Bundle arguments = new Bundle();
        arguments.putInt(BBEditView.ARG_ALLOWED_TAGS, R.array.tag_collection_default_tags);

        this.content = new BBEditView();
        this.content.setArguments(arguments);
        this.content.setEditBoxLabel(R.string.wilson_media_forum_new_post_content_label);
        this.content.setPostContent(savedPost);
        this.previewButton.setOnClickListener(content.getRequestPreviewListener());
        FragmentTransaction ft = this.getChildFragmentManager().beginTransaction();
        ft.replace(R.id.forum_post_editor_content_placeholder, this.content);
        ft.commit();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(this.content != null && this.content.getPostContent() != null && getContext() != null &&
                this.getActivity() != null)
        {
            String content = this.content.getPostContent();
            SharedPreferences p = getActivity().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE);
            String postKey = String.format(Locale.getDefault(), SHARED_PREFERENCE_SAVED_POST_TMP_FORMAT, topicId);
            if(this.content.getPostContent().isEmpty()) {
                p.edit().remove(postKey).apply();
            } else {
                p.edit().putString(postKey, content).apply();
            }
        }
    }
}
