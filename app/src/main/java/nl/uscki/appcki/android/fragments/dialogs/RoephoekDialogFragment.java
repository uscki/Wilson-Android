package nl.uscki.appcki.android.fragments.dialogs;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.RoephoekEvent;
import nl.uscki.appcki.android.generated.roephoek.RoephoekItem;
import nl.uscki.appcki.android.helpers.UserHelper;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoephoekDialogFragment extends DialogFragment {


    public RoephoekDialogFragment() {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.roephoek_add_dialog, null);
        final EditText name = (EditText) view.findViewById(R.id.roephoek_dialog_name);
        final EditText content = (EditText) view.findViewById(R.id.roephoek_dialog_content);

        name.setText(UserHelper.getInstance().getPerson().getNickname());
        content.requestFocus();

        builder.setTitle("Nieuwe roep plaatsen").setView(view).setPositiveButton(R.string.roephoek_dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Services.getInstance().shoutboxService.shout(name.getText().toString(), content.getText().toString()).enqueue(new Callback<RoephoekItem>() {
                            @Override
                            public void onSucces(Response<RoephoekItem> response) {
                                EventBus.getDefault().post(new RoephoekEvent(response.body()));
                            }
                        });
                    }
                }).setNegativeButton(R.string.roephoek_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        RoephoekDialogFragment.this.getDialog().cancel();
                    }
        });

        return builder.create();
    }
}
