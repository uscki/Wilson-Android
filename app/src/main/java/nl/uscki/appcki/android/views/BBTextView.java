package nl.uscki.appcki.android.views;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.List;

import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.helpers.bbparser.spans.DefensiveURLSpan;

/**
 * Created by peter on 2/7/16.
 */
public class BBTextView extends AppCompatTextView {
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
    }

    //TODO start using this, cause it's easier you dumb bitch
    public void setText(List<Object> textJson, boolean parseNewLines) {
        this.setText(Parser.parse(textJson, parseNewLines, this));
    }
}
