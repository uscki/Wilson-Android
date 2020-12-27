package nl.uscki.appcki.android.helpers;

import android.content.Intent;
import android.view.View;

import java.util.List;
import java.util.Map;

public interface ISharedElementViewContainer {

    void onMapSharedElements(List<String> names, Map<String, View> sharedElements);

    int activityReentering(int code, Intent data);
}
