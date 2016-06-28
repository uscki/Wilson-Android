package me.blackwolf12333.appcki.fragments.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.Years;

import java.util.List;

import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.generated.roephoek.RoephoekItem;

/**
 * Created by peter on 5/16/16.
 */
public class RoephoekItemAdapter extends BaseItemAdapter<RoephoekItemAdapter.ViewHolder, RoephoekItem> {

    public RoephoekItemAdapter(List<RoephoekItem> items) {
        super(items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.roephoek_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RoephoekItem item = items.get(position);
        holder.nickname.setText(item.getNickname());
        holder.message.setText(item.getMessage());
        holder.time.setText(timestampConversion(item.getTimestamp()));
    }

    private String timestampConversion(String timestamp) {
        Long time = Long.valueOf(timestamp);
        DateTime now = DateTime.now();
        DateTime other = new DateTime(time);
        int hours = Hours.hoursBetween(other, now).getHours();
        if(hours > 24) {
            int days = Days.daysBetween(other, now).getDays();
            if(days > 7) {
                int weeks = Weeks.weeksBetween(other, now).getWeeks();
                if(weeks > 3) {
                    int months = Months.monthsBetween(other, now).getMonths();
                    if(months > 12) {
                        int years = Years.yearsBetween(other, now).getYears();
                        return "(± " + years + " jaren geleden)";
                    } else {
                        return "(± " + months + " maanden geleden)";
                    }
                } else {
                    return "(± " + weeks + " weken geleden)";
                }
            } else {
                return "(± " + days + " dagen geleden)";
            }
        } else {
            return "(± " + hours + " uur geleden)";
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView nickname;
        public final TextView message;
        public final TextView time;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            nickname = (TextView) view.findViewById(R.id.roephoek_item_nickname);
            message = (TextView) view.findViewById(R.id.roephoek_item_message);
            time = (TextView) view.findViewById(R.id.roephoek_item_time);
        }

        @Override
        public String toString() {
            return super.toString() + "";
        }
    }
}
