package me.blackwolf12333.appcki.fragments.meeting;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;

import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.Services;
import me.blackwolf12333.appcki.generated.meeting.Preference;
import me.blackwolf12333.appcki.generated.meeting.Slot;

/**
 * Created by peter on 7/16/16.
 */
public class MeetingPreferenceDaySlotAdapter extends RecyclerView.Adapter<MeetingPreferenceDaySlotAdapter.ViewHolder> {
    List<Slot> slots;

    public MeetingPreferenceDaySlotAdapter(List<Slot> slots) {
        this.slots = slots;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meeting_slot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Slot slot = slots.get(position);
        holder.slot = slot;
        DateTime dateTime = new DateTime(slot.getStarttime());
        holder.canAttend.setText(dateTime.toString("HH:mm"));
        holder.colorcode.setColorFilter(getColorForSlot(slot), PorterDuff.Mode.MULTIPLY);

        holder.canAttend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Services.getInstance().meetingService.setSlot(slot.getId(), holder.note.getText().toString());
                }
            }
        });

        holder.note.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Services.getInstance().meetingService.setSlot(slot.getId(), holder.note.getText().toString());
                }
                return true;
            }
        });
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
