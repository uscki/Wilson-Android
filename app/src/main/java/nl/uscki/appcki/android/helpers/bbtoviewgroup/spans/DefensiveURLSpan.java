package nl.uscki.appcki.android.helpers.bbtoviewgroup.spans;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.style.URLSpan;
import android.view.View;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.events.LinkClickedEvent;

/**
 * Created by peter on 12/20/16.
 */

public class DefensiveURLSpan extends URLSpan {
    public DefensiveURLSpan(String url) {
        super(url);
    }

    public Parcelable.Creator CREATOR = new Parcelable.Creator<DefensiveURLSpan>() {

        @Override
        public DefensiveURLSpan createFromParcel(Parcel source) {
            return new DefensiveURLSpan(source.readString());
        }

        @Override
        public DefensiveURLSpan[] newArray(int size) {
            return new DefensiveURLSpan[size];
        }
    };

    @Override
    public void onClick(View widget) {
        EventBus.getDefault().post(new LinkClickedEvent(getURL()));
        super.onClick(widget);
    }
}
