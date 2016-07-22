package me.blackwolf12333.appcki.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import java.net.ConnectException;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.Services;
import me.blackwolf12333.appcki.error.ConnectionError;
import me.blackwolf12333.appcki.events.RoephoekEvent;
import me.blackwolf12333.appcki.generated.roephoek.Roephoek;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoephoekDialogFragment extends DialogFragment {


    public RoephoekDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.roephoek_add_dialog, null);
        final EditText name = (EditText) view.findViewById(R.id.roephoek_dialog_name);
        final EditText content = (EditText) view.findViewById(R.id.roephoek_dialog_content);

        builder.setTitle("Nieuwe roep plaatsen").setView(view).setPositiveButton(R.string.roephoek_dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Services.getInstance().shoutboxService.shout(name.getText().toString(), content.getText().toString()).enqueue(new Callback<Roephoek>() {
                            @Override
                            public void onResponse(Call<Roephoek> call, Response<Roephoek> response) {
                                EventBus.getDefault().post(new RoephoekEvent(response.body()));
                            }

                            @Override
                            public void onFailure(Call<Roephoek> call, Throwable t) {
                                if (t instanceof ConnectException) {
                                    new ConnectionError(t); // handle connection error in MainActivity
                                } else {
                                    throw new RuntimeException(t);
                                }
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
