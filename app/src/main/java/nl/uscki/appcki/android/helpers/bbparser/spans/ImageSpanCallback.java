package nl.uscki.appcki.android.helpers.bbparser.spans;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LevelListDrawable;

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

    public ImageSpanCallback(BBTextView view, LevelListDrawable drawable) {
        this.view = view;
        this.mDrawable = drawable;
    }

    @Override
    public void onSucces(Response<ResponseBody> response) {
        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
        if (bitmap != null) {
            BitmapDrawable d = new BitmapDrawable(bitmap);
            mDrawable.addLevel(1, 1, d);
            mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            mDrawable.setLevel(1);
            // i don't know yet a better way to refresh TextView
            // mTv.invalidate() doesn't work as expected
            CharSequence t = view.getText();
            view.setText(t);
        }
    }
}
