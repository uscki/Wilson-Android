package nl.uscki.appcki.android.helpers;

import android.animation.TypeEvaluator;
import android.graphics.drawable.Drawable;

public class DrawableTypeEvaluator implements TypeEvaluator<Drawable> {
    @Override
    public Drawable evaluate(float fraction, Drawable startDrawable, Drawable endDrawable) {
        return (fraction > .2) ? endDrawable : startDrawable;
    }
}
