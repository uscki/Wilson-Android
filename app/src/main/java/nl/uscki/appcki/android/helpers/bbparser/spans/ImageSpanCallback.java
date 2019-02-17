package nl.uscki.appcki.android.helpers.bbparser.spans;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.view.ViewTreeObserver;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.views.BBTextView;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by peter on 1/3/17.
 */

public class ImageSpanCallback extends Callback<ResponseBody> {
    private BBTextView view;
    private LevelListDrawable mDrawable;
    private Bitmap bitmap;

    public ImageSpanCallback(BBTextView view, LevelListDrawable drawable) {
        this.view = view;
        this.mDrawable = drawable;
    }

    @Override
    public void onSucces(Response<ResponseBody> response) {
        this.bitmap = BitmapFactory.decodeStream(response.body().byteStream());
        if (this.bitmap != null) {
            final BitmapDrawable d =
                    new BitmapDrawable(this.view.getContext().getResources(), this.bitmap);
            mDrawable.addLevel(1, 1, d);

            // When we're still parsing the BBTextView, the initial width and height are 0. Wait
            // until the BBTextView is drawn and measured to start fitting the image
            view.getViewTreeObserver()
                    .addOnGlobalLayoutListener(this.ImageSpanGlobalLayoutChangeListener);

            // Initially draw image in original dimensions. Above observer will resize if necessary
            // as soon as the view size is measured
            mDrawable.setBounds(0, 0, this.bitmap.getWidth(), this.bitmap.getHeight());
            mDrawable.setLevel(1);

            // i don't know yet a better way to refresh TextView
            // mTv.invalidate() doesn't work as expected
            CharSequence t = view.getText();
            view.setText(t);
        }
    }

    private ViewTreeObserver.OnGlobalLayoutListener ImageSpanGlobalLayoutChangeListener =
            new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            // We're resizing the view, so remove this listener to avoid endless recursion
            view.getViewTreeObserver()
                    .removeOnGlobalLayoutListener(ImageSpanGlobalLayoutChangeListener);

            // Store some nice stuff
            int bitmapWidth = ImageSpanCallback.this.bitmap.getWidth();
            int bitmapHeight = ImageSpanCallback.this.bitmap.getHeight();

            // Determine how to scale the image so it fits in either dimension
            float scaleFactorX = view.getMeasuredWidth() / (float) bitmapWidth;
            float scaleFactorY = view.getMeasuredHeight() / (float) bitmapHeight;

            // Use scale factor that scales image the most, so we no it fits both
            // horizontally and vertically (note, Float.min() requires higher API).
            float scaleFactor = scaleFactorX < scaleFactorY ? scaleFactorX : scaleFactorY;

            if(scaleFactor <= 1) {
                // Only rescale if the image does not fit. If it's already small, we do not
                // want to scale up
                bitmapWidth = Math.round(scaleFactor * bitmapWidth);
                bitmapHeight = Math.round(scaleFactor * bitmapHeight);

                mDrawable.setBounds(0,0, bitmapWidth, bitmapHeight);
                CharSequence t = view.getText();
                view.setText(t);
            }
        }
    };
}
