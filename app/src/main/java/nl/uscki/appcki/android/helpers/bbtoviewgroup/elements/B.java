package nl.uscki.appcki.android.helpers.bbtoviewgroup.elements;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;

import java.util.ArrayList;

import nl.uscki.appcki.android.helpers.bbtoviewgroup.Parser;

/**
 * This class describes a bold-text element
 *
 * @author Ty Mees
 * @version 1.1
 * @since 0.10
 */
public class B extends GenericElement {

    public B(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = true;
        this.replaceEmoji = true;
        this.type = "B";
    }

    @Override
    public SpannableStringBuilder getSpannedText() {
        SpannableStringBuilder str = Parser.parse(getContent(), true);
        str.setSpan(new StyleSpan(Typeface.BOLD), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }
}
