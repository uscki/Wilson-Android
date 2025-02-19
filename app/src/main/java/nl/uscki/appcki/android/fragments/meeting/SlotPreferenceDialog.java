package nl.uscki.appcki.android.fragments.meeting;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.ServiceGenerator;
import nl.uscki.appcki.android.fragments.meeting.adapter.MeetingParticipantAdapter;
import nl.uscki.appcki.android.generated.meeting.Slot;
import nl.uscki.appcki.android.generated.organisation.PersonWithNote;

/**
 * Created by peter on 8/4/16.
 */
public class SlotPreferenceDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Gson gson = ServiceGenerator.getGson();
        Slot slot = gson.fromJson(getArguments().getString("slot"), Slot.class);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.meeting_slot_preferences_dialog, null);
        RecyclerView available = view.findViewById(R.id.meeting_slot_preferences_available);
        RecyclerView unavailable = view.findViewById(R.id.meeting_slot_preferences_unavailable);

        available.setAdapter(new MeetingParticipantAdapter(PersonWithNote.fromSlotPreferencesAvailable(slot)));
        available.setHasFixedSize(true);
        unavailable.setAdapter(new MeetingParticipantAdapter(PersonWithNote.fromSlotPreferencesUnavailable(slot)));
        unavailable.setHasFixedSize(true);

        builder.setTitle(getText(R.string.action_preferences)).setView(view).setNeutralButton(getText(R.string.action_close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        return builder.create();
    }
}
