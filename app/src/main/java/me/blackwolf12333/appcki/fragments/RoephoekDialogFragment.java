package me.blackwolf12333.appcki.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.VolleyRoephoek;

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
                        // TODO: 6/28/16 post content
                        VolleyRoephoek.getInstance().addShout(name.getText().toString(), content.getText().toString());
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
