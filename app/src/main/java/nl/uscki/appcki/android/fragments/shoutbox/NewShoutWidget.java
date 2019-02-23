package nl.uscki.appcki.android.fragments.shoutbox;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.helpers.UserHelper;
import nl.uscki.appcki.android.views.NewPageableItem;
import retrofit2.Call;

public class NewShoutWidget extends NewPageableItem {

    @BindView(R.id.new_shout_nickname)
    EditText nickname;

    @BindView(R.id.new_shout_content)
    EditText content;

    @BindView(R.id.new_shout_confirm_button)
    ImageButton confirmShout;

    @BindView(R.id.remaining_chars)
    TextView remainingChars;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roephoek_new_widget, container, false);
        ButterKnife.bind(this, view);
        content.addTextChangedListener(contentLengthWatcher);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nickname.setText(UserHelper.getInstance().getCurrentUser().getNickname());
    }

    @Override
    protected EditText getMainTextInput() {
        return content;
    }

    @Override
    protected ImageButton getConfirmButton() {
        return confirmShout;
    }

    @Override
    protected Call postNewItem() {
        return Services.getInstance()
                .shoutboxService
                .shout(nickname.getText().toString(), content.getText().toString());
    }

    @Override
    protected @NonNull List<View> getIncorrectFields() {
        List<View> incorrect = new ArrayList();
        if(!isFieldNotEmpty(nickname))
            incorrect.add(nickname);
        if(!isFieldNotEmpty(content))
            incorrect.add(content);
        return incorrect;
    }

    private TextWatcher contentLengthWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {  }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
            int l = charSequence.length();
            if(content.getLineCount() > 1) {
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
