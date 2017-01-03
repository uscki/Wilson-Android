package nl.uscki.appcki.android.views;

import android.content.Context;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import nl.uscki.appcki.android.helpers.bbparser.spans.DefensiveURLSpan;

/**
 * Created by peter on 2/7/16.
 */
public class BBTextView extends TextView {
    public boolean visibilityOfBBUnit = false;

    public BBTextView(Context context) {
        super(context);
    }

    public BBTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BBTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        this.setMovementMethod(LinkMovementMethod.getInstance());
        super.setText(text, type);
        //setupImageViews(); NO CAN DO
        //fixTextView();
    }

    private void fixTextView() {
        SpannableString current = (SpannableString) this.getText();
        URLSpan[] spans = current.getSpans(0, current.length(), URLSpan.class);

        for (URLSpan span : spans) {
            int start = current.getSpanStart(span);
            int end = current.getSpanEnd(span);

            current.removeSpan(span);
            current.setSpan(new DefensiveURLSpan(span.getURL()), start, end, 0);
        }
    }
}
