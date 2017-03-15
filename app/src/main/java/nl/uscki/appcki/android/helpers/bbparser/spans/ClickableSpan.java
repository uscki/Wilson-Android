package nl.uscki.appcki.android.helpers.bbparser.spans;

import android.text.TextPaint;
import android.view.View;

/**
 * Created by peter on 1/1/17.
 */

public abstract class ClickableSpan extends android.text.style.ClickableSpan {

    /**
     * Performs the click action associated with this span.
     */
    public abstract void onClick(View widget);

    /**
     * Makes the text underlined and in the link color.
     */
    @Override
    public void updateDrawState(TextPaint ds) {
        // don't actually change anything
    }
}
