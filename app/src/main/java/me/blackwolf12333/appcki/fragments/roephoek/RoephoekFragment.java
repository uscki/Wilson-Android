package me.blackwolf12333.appcki.fragments.roephoek;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.VolleyRoephoek;
import me.blackwolf12333.appcki.events.RoephoekEvent;
import me.blackwolf12333.appcki.events.ShowProgressEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;
import me.blackwolf12333.appcki.generated.Roephoek;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by Jorik on 13/02/16.
 */
public class RoephoekFragment extends APIFragment {
    private VolleyRoephoek roephoekAPI = new VolleyRoephoek();
    private RecyclerView recyclerView;
    private Roephoek roephoek = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roephoekAPI.getOlder(25515);

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roephoekitem_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.roephoek_item_list);

        return view;
    }

    public void onEventMainThread(RoephoekEvent event) {
        EventBus.getDefault().post(new ShowProgressEvent(false));
        if(event.roephoek != null) {
            recyclerView.setAdapter(new RoephoekItemAdapter(event.roephoek.getContent()));
        }
    }

}
