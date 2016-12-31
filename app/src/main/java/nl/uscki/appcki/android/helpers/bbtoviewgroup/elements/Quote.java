package nl.uscki.appcki.android.helpers.bbtoviewgroup.elements;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;

import java.util.ArrayList;

import nl.uscki.appcki.android.helpers.bbtoviewgroup.Parser;
import nl.uscki.appcki.android.helpers.bbtoviewgroup.spans.QuoteSpan;

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

    @Override
    public SpannableStringBuilder getSpannedText() {
        SpannableStringBuilder str = Parser.parse(getContent(), true);
        String name;
        if(getParameter() != null && !getParameter().isEmpty()) {
            name = getParameter() + " schreef als volgt:\n";
        } else {
            name = "Quote:\n";
        }
        str.insert(0, name);
        str.setSpan(new StyleSpan(Typeface.BOLD), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new QuoteSpan(), name.length(), str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }
}
