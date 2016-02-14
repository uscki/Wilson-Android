package me.blackwolf12333.appcki.fragments.roephoek;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.generated.NewsItem;
import me.blackwolf12333.appcki.generated.RoephoekItem;

/**
 * Created by Jorik on 14/02/16.
 */
public class RoephoekItemAdapter extends RecyclerView.Adapter<RoephoekItemAdapter.ViewHolder> {
    private final List<RoephoekItem> mValues;


    public RoephoekItemAdapter(List<RoephoekItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_roephoekitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mMessageView.setText(mValues.get(position).getMessage());
        holder.mNicknameView.setText(mValues.get(position).getNickname());

        //holder.mView.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        Bundle args = new Bundle();
        //        args.putInt("id", holder.mItem.getId());
        //        EventBus.getDefault().post(new OpenFragmentEvent(MainActivity.Screen.ROEPHOEKDETAIL, args));
        //    }
        //});
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mMessageView;
        public final TextView mNicknameView;
        public RoephoekItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mMessageView = (TextView) view.findViewById(R.id.message);
            mNicknameView = (TextView) view.findViewById(R.id.nickname);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mMessageView.getText() + "'";
        }
    }
}
