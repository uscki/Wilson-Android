package me.blackwolf12333.appcki.fragments.agenda;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.Services;
import me.blackwolf12333.appcki.events.AgendaItemSubscribedEvent;
import me.blackwolf12333.appcki.generated.agenda.Subscribers;
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
        final int agendaId = 1455;

        builder.setTitle("Inschrijven").setView(view).setPositiveButton(R.string.agenda_dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Services.getInstance().agendaService.subscribe(agendaId, note.getText().toString()).enqueue(new Callback<Subscribers>() {
                    @Override
                    public void onResponse(Call<Subscribers> call, Response<Subscribers> response) {
                        EventBus.getDefault().post(new AgendaItemSubscribedEvent(response.body()));
                    }

                    @Override
                    public void onFailure(Call<Subscribers> call, Throwable t) {

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
