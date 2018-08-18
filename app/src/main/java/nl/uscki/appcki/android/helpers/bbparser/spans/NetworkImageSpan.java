package nl.uscki.appcki.android.helpers.bbparser.spans;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.text.style.DynamicDrawableSpan;

import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.views.BBTextView;

/**
 * Created by peter on 12/20/16.
 */

public class NetworkImageSpan extends DynamicDrawableSpan {
    String source;
    Integer mediaId;
    BBTextView view;

    public NetworkImageSpan(Integer mediaId, String source, BBTextView view) {
        this.source = source;
        this.view = view;
        this.mediaId = mediaId;
    }

    @Override
    public Drawable getDrawable() {
        LevelListDrawable d = new LevelListDrawable();
        Drawable empty = new ColorDrawable(Color.TRANSPARENT);
        d.addLevel(0, 0, empty);
        d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

        if(source != null) {
            Services.getInstance().imageService.getImage(source).enqueue(new ImageSpanCallback(view, d));
        } else if(mediaId != null) {
            Services.getInstance().mediaService.file(mediaId, MediaAPI.MediaSize.SMALL.toString()).enqueue(new ImageSpanCallback(view, d));
        }

        return d;
    }
}
