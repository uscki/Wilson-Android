package nl.uscki.appcki.android.fragments.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.generated.roephoek.RoephoekItem;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;

/**
 * Created by peter on 5/16/16.
 */
public class RoephoekItemAdapter extends BaseItemAdapter<RoephoekItemAdapter.ViewHolder, RoephoekItem> {

    public RoephoekItemAdapter(List<RoephoekItem> items) {
        super(items);
    }

    @Override
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.roephoek_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindCustomViewHolder(ViewHolder holder, int position) {
        RoephoekItem item = items.get(position);
        //Log.d("RoephoekAdapter", item.getMessageJSON().toString());
        SpannableStringBuilder text = Parser.parse(item.getMessage(), true, holder.message);
        holder.nickname.setText(item.getNickname());
        //holder.message.setText(item.getMessage());
        holder.message.setText(trim(text));
        holder.time.setText(Utils.timestampConversion(item.getTimestamp().getMillis()));
    }

    private SpannableStringBuilder trim(SpannableStringBuilder str) {
        int i;
        for(i = str.length()-1; i > 0; i--) {
            if(str.charAt(i) != '\n') {
                break;
            }
            Log.e("itemadapter", "newline found");
        }
        return new SpannableStringBuilder(str.subSequence(0, i+1));
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
