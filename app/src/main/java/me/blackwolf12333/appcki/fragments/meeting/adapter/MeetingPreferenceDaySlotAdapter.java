package me.blackwolf12333.appcki.fragments.meeting.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.net.ConnectException;
import java.util.List;

import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.Services;
import me.blackwolf12333.appcki.error.ConnectionError;
import me.blackwolf12333.appcki.fragments.meeting.SlotPreferenceDialog;
import me.blackwolf12333.appcki.generated.meeting.Preference;
import me.blackwolf12333.appcki.generated.meeting.Slot;
import me.blackwolf12333.appcki.helpers.UserHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by peter on 7/16/16.
 */
public class MeetingPreferenceDaySlotAdapter extends RecyclerView.Adapter<MeetingPreferenceDaySlotAdapter.ViewHolder> {
    List<Slot> slots;

    private Callback<Boolean> callback = new Callback<Boolean>() {
        @Override
        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
            Log.d("SlotAdapter", "succesfully set slot");
        }

        @Override
        public void onFailure(Call<Boolean> call, Throwable t) {
            if (t instanceof ConnectException) {
                new ConnectionError(t); // handle connection error in MainActivity
            } else {
                throw new RuntimeException(t);
            }
        }
    };

    private Context context;

    public MeetingPreferenceDaySlotAdapter(Context context, List<Slot> slots) {
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Slot slot = slots.get(position);
        holder.slot = slot;
        DateTime dateTime = new DateTime(slot.getStarttime());
        holder.canAttend.setText(dateTime.toString("HH:mm"));
        holder.canAttend.setChecked(getChecked(slot));
        holder.colorcode.setColorFilter(getColorForSlot(slot), PorterDuff.Mode.MULTIPLY);

        holder.canAttend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Services.getInstance().meetingService.setSlot(slot.getId(), holder.note.getText().toString(), isChecked).enqueue(callback);
            }
        });

        holder.note.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Boolean checked = holder.canAttend.isChecked();
                    Services.getInstance().meetingService.setSlot(slot.getId(), holder.note.getText().toString(), checked).enqueue(callback);
                }
                return true;
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                displayPreferences(slot);
                return false;
            }
        });
    }

    private void displayPreferences(Slot slot) {
        DialogFragment newFragment = new SlotPreferenceDialog();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        String json = gson.toJson(slot);
        args.putString("slot", json);
        newFragment.setArguments(args);
        newFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "slot_preferences");
    }

    private boolean getChecked(Slot slot) {
        for(Preference p : slot.getPreferences()) {
            if (p.getPerson() == UserHelper.getInstance().getPerson()) {
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
