package nl.uscki.appcki.android.fragments.shoutbox;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roephoek_new_widget, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nickname.setText(UserHelper.getInstance().getPerson().getNickname());
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
}
