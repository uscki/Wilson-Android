package me.blackwolf12333.appcki.fragments.meeting;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;

import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.fragments.meeting.adapter.MeetingParticipantAdapter;
import me.blackwolf12333.appcki.generated.meeting.Slot;
import me.blackwolf12333.appcki.generated.organisation.PersonWithNote;

/**
 * Created by peter on 8/4/16.
 */
public class SlotPreferenceDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Gson gson = new Gson();
        Slot slot = gson.fromJson(getArguments().getString("slot"), Slot.class);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.meeting_slot_preferences_dialog, null);
        RecyclerView available = (RecyclerView) view.findViewById(R.id.meeting_slot_preferences_available);
        RecyclerView unavailable = (RecyclerView) view.findViewById(R.id.meeting_slot_preferences_unavailable);

        available.setAdapter(new MeetingParticipantAdapter(PersonWithNote.fromSlotPreferencesAvailable(slot)));
        available.setHasFixedSize(true);
        unavailable.setAdapter(new MeetingParticipantAdapter(PersonWithNote.fromSlotPreferencesUnavailable(slot)));
        unavailable.setHasFixedSize(true);

        builder.setTitle("Voorkeuren").setView(view).setNeutralButton("Sluiten", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        return builder.create();
    }
}
