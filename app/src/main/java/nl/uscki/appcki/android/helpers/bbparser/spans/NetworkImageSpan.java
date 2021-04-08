package nl.uscki.appcki.android.helpers.bbparser.spans;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.views.BBTextView;

/**
 * Created by peter on 12/20/16.
 */
public class NetworkImageSpan extends ImageSpan {
//    String source;
//    int mediaId = -1;
    BBTextView view;
    private LevelListDrawable target;

    public NetworkImageSpan(LevelListDrawable target, int mediaId, BBTextView view) {
        super(target);
        this.view = view;
        this.target = target;
        loadUsckiMedia(mediaId);
    }

    public NetworkImageSpan(LevelListDrawable target,String source, BBTextView view) {
        super(target);
        this.view = view;
        this.target = target;
        loadExternalMedia(source);
    }

    private void loadUsckiMedia(int mediaId) {
        Glide.with(view)
                .load(MediaAPI.getMediaUri(mediaId, MediaAPI.MediaSize.LARGE))
                .thumbnail(Glide.with(view.getContext()).load(MediaAPI.getMediaUri(mediaId, MediaAPI.MediaSize.SMALL)))
                .fitCenter()
                .into(bbMediaTarget);
    }

    private void loadExternalMedia(String resourceUrl) {
        Glide.with(view)
                .load(resourceUrl)
                .into(bbMediaTarget);
    }

    private CustomTarget<Drawable> bbMediaTarget = new CustomTarget<Drawable>() {
        @Override
        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
            target.setLevel(1);
            target.addLevel(1,1, resource);

            float scale = view.getMeasuredWidth() / (float) resource.getIntrinsicWidth();
            Rect b;
            if(scale < 1) {
                b = new Rect(0, 0, Math.round(resource.getIntrinsicWidth() * scale), Math.round(resource.getIntrinsicHeight() * scale));
            } else {
                b = new Rect(0,0, resource.getIntrinsicWidth(), resource.getIntrinsicHeight());
            }
            target.setBounds(b);
            target.setLevel(1);

            // i don't know yet a better way to refresh TextView
            // mTv.invalidate() doesn't work as expected
            CharSequence t = view.getText();
            view.setText(t);
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {

        }
    };
}
