package nl.uscki.appcki.android.fragments.agenda;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.net.ConnectException;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.error.ConnectionError;
import nl.uscki.appcki.android.events.AgendaItemSubscribedEvent;
import nl.uscki.appcki.android.generated.agenda.Subscribers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by peter on 6/28/16.
 */
public class SubscribeDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.agenda_subscribe_dialog, null);
        final EditText note = (EditText) view.findViewById(R.id.agenda_dialog_note);
        final int agendaId = getArguments().getInt("id");

        builder.setTitle("Inschrijven").setView(view).setPositiveButton(R.string.agenda_dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Services.getInstance().agendaService.subscribe(agendaId, note.getText().toString()).enqueue(new Callback<Subscribers>() {
                    @Override
                    public void onResponse(Call<Subscribers> call, Response<Subscribers> response) {
                        EventBus.getDefault().post(new AgendaItemSubscribedEvent(response.body(), false));
                        Log.d("Subscribe", response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<Subscribers> call, Throwable t) {
                        if (t instanceof ConnectException) {
                            new ConnectionError(t); // handle connection error in MainActivity
                        } else {
                            throw new RuntimeException(t);
                        }
                    }
                });
            }
        }).setNegativeButton(R.string.agenda_dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                SubscribeDialogFragment.this.getDialog().cancel();
            }
        });

        return builder.create();
    }
}
