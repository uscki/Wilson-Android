package nl.uscki.appcki.android.fragments.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.generated.roephoek.RoephoekItem;
import nl.uscki.appcki.android.views.BBTextView;

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
    public Integer getLastID() {
        return items.get(items.size()-1).getId();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RoephoekItem item = items.get(position);
        holder.nickname.setText(item.getNickname());
        holder.message.setText(item.getMessage());
        holder.time.setText(Utils.timestampConversion(item.getTimestamp()));
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView nickname;
        public final BBTextView message;
        public final TextView time;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            nickname = (TextView) view.findViewById(R.id.roephoek_item_nickname);
            message = (BBTextView) view.findViewById(R.id.roephoek_item_message);
            time = (TextView) view.findViewById(R.id.roephoek_item_time);
        }

        @Override
        public String toString() {
            return super.toString() + "";
        }
    }
}
