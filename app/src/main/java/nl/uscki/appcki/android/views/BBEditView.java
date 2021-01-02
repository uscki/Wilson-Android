package nl.uscki.appcki.android.views;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.generated.ParsedBBCode;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

import static androidx.core.content.ContextCompat.getSystemService;

public class BBEditView extends Fragment {

    public static final String ARG_ALLOWED_TAGS = "nl.uscki.wilson.BB.tags.ALLOWED";

    private String allowedTags = null;

    private Button previewButton;

    private EditText content;
    private BBTextView preview;
    private TextView label;
    private ContentLoadingProgressBar loadingIndicator;

    private Drawable buttonSelectableBackgroundColor;
    private ColorStateList buttonSelectableForegroundColor;
    private int buttonSelectedBackgroundColor;
    private int buttonSelectedForegroundColor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            this.allowedTags = getArguments().getString(ARG_ALLOWED_TAGS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bb_editor_fragment_layout, container, false);

        this.content = view.findViewById(R.id.bb_editor_content);
        this.label = view.findViewById(R.id.bb_editor_content_label);
        this.preview = view.findViewById(R.id.bb_editor_preview);
        this.loadingIndicator = view.findViewById(R.id.bb_editor_loading_indicator);

        for(BB_TAG tag : BB_TAG.values()) {
            View b = view.findViewById(tag.getButtonId());
            if(b != null) {
                b.setTag(tag.toString());
                if (this.allowedTags == null || this.allowedTags.contains(tag.toString())) {
                    b.setVisibility(View.VISIBLE);
                    b.setOnClickListener(markupButtonClickListener);
                } else {
                    b.setOnClickListener(null);
                    b.setVisibility(View.GONE);
                }
            } else {
                Log.v("BB-Edit Textview", "SKipping button setup for BB-tag \"" + tag.toString() + "\"");
            }
        }

        this.content.requestFocus();

        InputMethodManager inputMethodManager = getSystemService(Objects.requireNonNull(getContext()), InputMethodManager.class);
        if(inputMethodManager != null)
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        this.buttonSelectedBackgroundColor = ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getActivity().getTheme());
        this.buttonSelectedForegroundColor = ResourcesCompat.getColor(getResources(), R.color.lb_control_button_text, getActivity().getTheme());

        updateEditBox();
        if(this.contentString != null) {
            this.content.setText(contentString);
            this.content.setSelection(contentString.length(), contentString.length());
        }

        return view;
    }

    View.OnClickListener markupButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (content.getVisibility() != View.VISIBLE) {
                resetPreview();
            } else {
                if (v.getTag() != null) {
                    String replacement;

                    BB_TAG tag = BB_TAG.valueOf((String) v.getTag());

                    if (tag.isDefaultCursorBehavior()) {
                        replacePostContentAtSelection(String.format("[%1$s]%2$s[/%1$s]", tag.toString(), "%1$s"));
                    } else {
                        int start = content.getSelectionStart();
                        int length = content.getSelectionEnd() - start;
                        int startOffset = 0;
                        int endOffset = 0;
                        switch (BB_TAG.valueOf((String) v.getTag())) {
                            case ul:
                                replacement = "[ul]\n\t[li]%1$s[/li]\n\t[li][/li]\n[/ul]";
                                startOffset = length == 0 ?
                                        "[ul]\n\t[li]".length() :
                                        "[ul]\n\t[li][/li]\n\t[li]".length() + length;
                                endOffset = startOffset;
                                break;
                            case link:
                                replacement = "[link url=\"%1$s\"]%1$s[/link]";
                                startOffset = length == 0 ?
                                        "[link url=\"".length() :
                                        "[link url=\"\"]".length() + length;
                                endOffset = length;
                                break;
                            case section:
                                replacement = "[section title=\"%1$s\"][/section]";
                                startOffset = length == 0 ?
                                        "[section title=\"".length() :
                                        "[section title=\"\"]".length() + length;
                                endOffset = startOffset;
                                break;
                            case quote:
                                replacement = "\n\n[quote author=\"\"]%1$s[/quote]\n\n";
                                startOffset = length == 0 ?
                                        "\n\n[quote author=\"\"]".length() :
                                        "\n\n[quote author=\"".length();
                                endOffset = startOffset;
                                break;
                            default:
                                replacement = "%1$s"; // Ignore button (why is that button even enabled)
                                break;
                        }

                        replacePostContentAtSelection(replacement);
                        content.setSelection(startOffset + start, endOffset + start);
                    }
                }
            }
        }
    };

    private String replacePostContentAtSelection(String replacementFormat) {
        int start = content.getSelectionStart();
        int end = content.getSelectionEnd();
        String selectedText = content.getText().subSequence(content.getSelectionStart(), content.getSelectionEnd()).toString();

        this.content.getEditableText().replace(start, end, String.format(replacementFormat, selectedText));

        if(start == end) {
            int newPosition = start + replacementFormat.indexOf("%1$s");
            this.content.setSelection(newPosition, newPosition);
        } // If not empty, cursor is automatically shifted to end of inserted string by Android :)

        return selectedText;
    }

    private View.OnClickListener requestPreviewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(previewButton == null && v instanceof Button) {
                previewButton = (Button) v;
                buttonSelectableBackgroundColor = previewButton.getBackground();
                buttonSelectableForegroundColor = previewButton.getTextColors();
            }

            if(v.getBackground() == buttonSelectableBackgroundColor) {
                if (content.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), R.string.wilson_bb_code_empty_preview_warning, Toast.LENGTH_LONG).show();
                } else {
                    loadingIndicator.show();
                    if(previewButton != null) {
                        v.setBackgroundColor(buttonSelectedBackgroundColor);
                        previewButton.setTextColor(buttonSelectedForegroundColor);
                    }
                    String toParse = content.getText().toString();
                    RequestBody body = RequestBody.create(MediaType.parse("text/plain"), toParse);
                    Services.getInstance().bbService.parse(body).enqueue(previewRequestedCallback);
                }
            } else {
                resetPreview();
            }
        }
    };

    public View.OnClickListener getRequestPreviewListener() {
        return requestPreviewListener;
    }

    private Callback<ActionResponse<ParsedBBCode>> previewRequestedCallback = new Callback<ActionResponse<ParsedBBCode>>() {
        @Override
        public void onSucces(Response<ActionResponse<ParsedBBCode>> response) {
            if(response.body() != null && response.body().payload != null && response.body().payload.rawParsed != null) {
                preview.setText(Parser.parse(response.body().payload.rawParsed, true, preview));
                preview.setVisibility(View.VISIBLE);
                content.setVisibility(View.INVISIBLE);
            } else {
                Toast.makeText(getContext(), R.string.wilson_bb_code_preview_parse_error, Toast.LENGTH_LONG).show();
            }
            loadingIndicator.hide();
        }

        @Override
        public void onError(Response<ActionResponse<ParsedBBCode>> response) {
            loadingIndicator.hide();
            Toast.makeText(getContext(), R.string.wilson_bb_code_preview_parse_error, Toast.LENGTH_LONG).show();
        }
    };

    public void setEditBoxLabel(String label) {
        this.editBoxLabel = label;
        if(label == null)
            this.editBoxLabelResource = -1;
        updateEditBox();
    }

    public void setEditBoxLabel(int stringResourceId) {
        this.editBoxLabelResource = stringResourceId;
        if(stringResourceId < 0)
            this.editBoxLabel = null;
        updateEditBox();
    }

    private void updateEditBox() {
        if(this.label == null) return;

        if(this.editBoxLabel != null) {
            this.label.setText(this.editBoxLabel);
            this.label.setVisibility(View.VISIBLE);
        } else if (this.editBoxLabelResource >= 0) {
            this.label.setText(this.editBoxLabelResource);
            this.label.setVisibility(View.VISIBLE);
        } else {
            this.label.setVisibility(View.GONE);
        }
    }

    private String contentString;

    public String getPostContent() {
        String c = "";
        if(this.content != null) {
            c = this.content.getText().toString();
        }
        return c;
    }

    public void setPostContent(String content) {
        this.contentString = content;
        if(this.content != null)
            this.content.setText(content);
    }

    private void resetPreview() {
        // Reset preview if enabled
        content.setVisibility(View.VISIBLE);
        preview.setVisibility(View.GONE);
        if(this.previewButton != null && this.buttonSelectableBackgroundColor != null) {
            this.previewButton.setBackground(this.buttonSelectableBackgroundColor);
        }
        if(this.previewButton != null && this.buttonSelectableForegroundColor != null) {
            this.previewButton.setTextColor(this.buttonSelectableForegroundColor);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Objects.requireNonNull(getContext()), InputMethodManager.class);
        if(inputMethodManager != null)
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private String editBoxLabel;
    private int editBoxLabelResource = -1;

    enum BB_TAG {
        b(R.id.bb_editor_add_bold_button, true),
        code(R.id.bb_editor_add_code_button, true),
        email(R.id.bb_editor_add_email_button, true),
        h1(R.id.bb_editor_add_header_1_button, true),
        h2(R.id.bb_editor_add_header_2_button, true),
        h3(R.id.bb_editor_add_header_3_button, true),
        i(R.id.bb_editor_add_italic_button, true),
        img(R.id.bb_editor_add_image_button, true),
        link(R.id.bb_editor_add_link_button, false),
        media(-1, true),
        quote(R.id.bb_editor_add_quote_button, false),
        s(R.id.bb_editor_add_strikethrough_button, true),
        section(-1, false),
        spoiler(R.id.bb_editor_add_spoiler_button, true),
        table(-1, false),
        ticket(-1, false),
        twitter(-1, true),
        u(R.id.bb_editor_add_underline_button, true),
        ul(R.id.bb_editor_add_list_button, false),
        vimeo(-1, true),
        wickilink(-1, false),
        sub(R.id.bb_editor_add_subscript_button, true),
        sup(R.id.bb_editor_add_superscript_button, true),
        youtube(-1, true); // Contentious, but it is simple if we ignore width

        BB_TAG(int buttonId, boolean defaultCursorBehavior) {
            this.buttonId = buttonId;
            this.defaultCursorBehavior = defaultCursorBehavior;
        }

        private int buttonId;
        private boolean defaultCursorBehavior;

        public int getButtonId() {
            return buttonId;
        }

        public boolean isDefaultCursorBehavior() {
            return defaultCursorBehavior;
        }
    }
}
