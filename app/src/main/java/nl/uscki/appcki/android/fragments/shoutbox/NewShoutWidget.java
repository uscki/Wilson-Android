package nl.uscki.appcki.android.fragments.shoutbox;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.generated.roephoek.RoephoekItem;
import nl.uscki.appcki.android.helpers.UserHelper;
import nl.uscki.appcki.android.views.BBEditView;
import nl.uscki.appcki.android.views.NewPageableItem;
import retrofit2.Call;

public class NewShoutWidget extends NewPageableItem<ActionResponse<RoephoekItem>> implements BBEditView.BBEditViewCreatedListener {

    EditText nickname;
    BBEditView bbEditView;
    ImageButton confirmShout;
    TextView remainingChars;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFocusOnCreateView(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roephoek_new_widget, container, false);

        nickname = view.findViewById(R.id.new_shout_nickname);
        confirmShout = view.findViewById(R.id.new_shout_confirm_button);
        remainingChars = view.findViewById(R.id.remaining_chars);

        Bundle arg = new Bundle();
        arg.putInt(BBEditView.ARG_ALLOWED_TAGS, R.array.tag_collection_inline_tags);

        this.bbEditView = new BBEditView();
        this.bbEditView.setArguments(arg);
        this.bbEditView.registerViewListener(this);
        this.bbEditView.setEditBoxLabel(R.string.roephoek_dialog_content_hint);

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.new_shout_content_placeholder, this.bbEditView).commitNow();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nickname.setText(UserHelper.getInstance().getCurrentUser().getNickname());
    }

    @Override
    public void onBBEditViewCreated(BBEditView editView, View view) {
        this.bbEditView.getEditBox().addTextChangedListener(contentLengthWatcher);
        this.bbEditView.getEditBox().setMinLines(1);
    }

    @Override
    public void onBBEditViewDestroy(BBEditView editView) {
        if(editView != null)
            editView.deregisterViewListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(this.bbEditView != null) {
            this.bbEditView.deregisterViewListener(this);
        }
    }

    @Override
    protected EditText getMainTextInput() {
        return bbEditView.getEditBox();
    }

    @Override
    protected ImageButton getConfirmButton() {
        return confirmShout;
    }

    @Override
    protected Call postNewItem() {
        return Services.getInstance()
                .shoutboxService
                .shout(nickname.getText().toString(), bbEditView.getPostContent());
    }

    @Override
    protected @NonNull List<View> getIncorrectFields() {
        List<View> incorrect = new ArrayList();
        if(!isFieldNotEmpty(nickname))
            incorrect.add(nickname);
        if(!isFieldNotEmpty(this.bbEditView.getEditBox()))
            incorrect.add(this.bbEditView.getEditBox());
        return incorrect;
    }

    private TextWatcher contentLengthWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {  }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
            int l = charSequence.length();
            if(bbEditView.getEditBox().getLineCount() > 1) {
                int c = getResources().getColor(l <= 160 ? android.R.color.primary_text_light : R.color.colorRed);
                remainingChars.setText(String.format(Locale.getDefault(), "%d", 160-l));
                remainingChars.setTextColor(c);
                remainingChars.setVisibility(View.VISIBLE);
            } else {
                remainingChars.setVisibility(View.GONE);
            }
            confirmShout.setEnabled(l <= 160);
        }

        @Override
        public void afterTextChanged(Editable editable) { }
    };
}
