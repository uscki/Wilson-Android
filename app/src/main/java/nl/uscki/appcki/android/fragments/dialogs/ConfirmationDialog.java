package nl.uscki.appcki.android.fragments.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import nl.uscki.appcki.android.R;

/**
 * Created by peter on 12/14/17.
 */

public class ConfirmationDialog extends DialogFragment {
    ConfirmationDialogListener listener;

    int messageStringId;
    int possitiveStringId;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        messageStringId = getArguments().getInt("messageId");
        possitiveStringId = getArguments().getInt("positiveId");
        Log.e("Confirmation", "" + messageStringId);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(messageStringId)
                .setPositiveButton(possitiveStringId, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onPositive();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public interface ConfirmationDialogListener {
        void onPositive();
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (ConfirmationDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ConfirmationDialogListener");
        }
    }
}
