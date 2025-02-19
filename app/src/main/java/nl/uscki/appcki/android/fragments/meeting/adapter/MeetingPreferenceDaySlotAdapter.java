package nl.uscki.appcki.android.fragments.meeting.adapter;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import com.google.gson.Gson;
import org.joda.time.DateTime;
import java.util.List;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.activities.MeetingActivity;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.ServiceGenerator;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.meeting.SlotPreferenceDialog;
import nl.uscki.appcki.android.generated.meeting.Preference;
import nl.uscki.appcki.android.generated.meeting.Slot;
import nl.uscki.appcki.android.helpers.UserHelper;
import retrofit2.Response;

/**
 * Created by peter on 7/16/16.
 */
public class MeetingPreferenceDaySlotAdapter extends RecyclerView.Adapter<MeetingPreferenceDaySlotAdapter.ViewHolder> {
    List<Slot> slots;
    AppCompatActivity context;

    public MeetingPreferenceDaySlotAdapter(AppCompatActivity context, List<Slot> slots) {
        this.slots = slots;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meeting_slot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (!payloads.isEmpty()) {
            Slot slot = (Slot) payloads.get(0);
            holder.slot = slot;
            holder.canAttend.setChecked(getChecked(slot));
            holder.colorcode.setColorFilter(getColorForSlot(slot), PorterDuff.Mode.MULTIPLY);
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Integer meetingId = ((MeetingActivity)this.context).getMeetingItem().getId();
        final Slot slot = slots.get(position);
        holder.slot = slot;
        DateTime dateTime = new DateTime(slot.getStarttime());
        holder.canAttend.setText(dateTime.toString("HH:mm"));
        holder.canAttend.setChecked(getChecked(slot));
        holder.colorcode.setColorFilter(getColorForSlot(slot), PorterDuff.Mode.MULTIPLY);


        holder.canAttend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Services.getInstance().meetingService.setSlot(meetingId, slot.getId(), holder.note.getText().toString(), isChecked).enqueue(new Callback<Slot>() {
                        @Override
                        public void onSucces(Response<Slot> response) {
                            notifyItemChanged(holder.getAdapterPosition(), response.body());
                        }
                    });
            }
        });

        holder.note.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    Boolean checked = holder.canAttend.isChecked();
                    Services.getInstance().meetingService.setSlot(meetingId, slot.getId(), holder.note.getText().toString(), checked).enqueue(new Callback<Slot>() {
                        @Override
                        public void onSucces(Response<Slot> response) {
                            notifyItemChanged(holder.getAdapterPosition(), response.body());
                        }
                    });
                }
            }
        });

        holder.note.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Boolean checked = holder.canAttend.isChecked();
                    MainActivity.hideKeyboard(v);
                    Services.getInstance().meetingService.setSlot(meetingId, slot.getId(), holder.note.getText().toString(), checked).enqueue(new Callback<Slot>() {
                        @Override
                        public void onSucces(Response<Slot> response) {
                            notifyItemChanged(holder.getAdapterPosition(), response.body());
                        }
                    });
                }
                return true;
            }
        });


        holder.note.setText(getNoteText(slot));

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                displayPreferences(holder.slot);
                return false;
            }
        });
    }

    private String getNoteText(Slot slot) {
        for(Preference p : slot.getPreferences()) {
            if (p.getPerson().getId().equals(UserHelper.getInstance().getCurrentUser().getId())) {
                return p.getNotes();
            }
        }
        return "";
    }

    private void displayPreferences(Slot slot) {
        DialogFragment newFragment = new SlotPreferenceDialog();
        Bundle args = new Bundle();
        Gson gson = ServiceGenerator.getGson();
        String json = gson.toJson(slot);
        args.putString("slot", json);
        newFragment.setArguments(args);
        newFragment.show(context.getSupportFragmentManager(), "slot_preferences");
    }

    private boolean getChecked(Slot slot) {
        for(Preference p : slot.getPreferences()) {
            //noinspection EqualsBetweenInconvertibleTypes
            if (p.getPerson().equals(UserHelper.getInstance().getCurrentUser())) {
                return p.getCanattend();
            }
        }

        return true; // als we nog niet hebben gereageert hierop is de default waarde true
    }

    private int getColorForSlot(Slot slot) {
        float total = slot.getPreferences().size();
        float canAttend = 0;
        for(Preference p : slot.getPreferences()) {
            if (p.getCanattend()) {
                canAttend++;
            }
        }

        float ps = (canAttend / total) * 100;
        int red = 0;
        int green = 0;

        if (ps <= 50) {
            red = 255;
        }
        if (ps > 50) {
            red = (int) (256-(ps-50)*5.12);
        }
        if (ps < 50) {
            green = (int) (ps * 5.12);
        }
        if (ps >= 50) {
            green = 255;
        }

        return Color.argb(255, red, green, 0);
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Slot slot;
        public final View mView;
        public final TextView note;
        public final ImageView colorcode;
        public final Switch canAttend;

        public ViewHolder(View view) {
            super(view);
            this.mView = view;
            this.note = (TextView) view.findViewById(R.id.meeting_slot_note);
            this.colorcode = (ImageView) view.findViewById(R.id.meeting_slot_color);
            this.canAttend = (Switch) view.findViewById(R.id.meeting_slot_switch);
        }
    }
}
