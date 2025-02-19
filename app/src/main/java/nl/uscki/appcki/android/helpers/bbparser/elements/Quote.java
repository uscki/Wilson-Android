package nl.uscki.appcki.android.helpers.bbparser.elements;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.text.style.StyleSpan;

import java.util.ArrayList;

import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.helpers.bbparser.spans.QuoteSpan;
import nl.uscki.appcki.android.views.BBTextView;

/**
 * This class describes a Quote element
 *
 * @author Ty Mees
 * @version 1.1
 * @since 0.10
 */
public class Quote extends GenericElement {
    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public Quote(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = true;
        this.replaceEmoji = true;
        this.type = "Quote";
    }

    MetricAffectingSpan offset = new MetricAffectingSpan() {
        @Override
        public void updateMeasureState(TextPaint p) {
            p.baselineShift += 10;
        }

        @Override
        public void updateDrawState(TextPaint tp) {

        }
    };

    @Override
    public SpannableStringBuilder getSpannedText(BBTextView view) {
        SpannableStringBuilder str = Parser.parse(getContent(), true, view);
        String name;
        if(getParameter() != null && !getParameter().isEmpty()) {
            name = getParameter() + " schreef als volgt:\n";
        } else {
            name = "Quote:\n";
        }
        str.insert(0, name);
        str.setSpan(new StyleSpan(Typeface.BOLD), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(offset, 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new QuoteSpan(), name.length(), str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }
}
