package nl.uscki.appcki.android.helpers.bbtoviewgroup.elements;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;

import java.util.ArrayList;

/**
 * This class describes a header element, H1 to be exact.
 *
 * @author Ty Mees
 * @version 1.2
 * @since 0.6
 */
public class Code extends GenericElement {

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public Code(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = false;
        this.replaceEmoji = false;
        this.type = "Code";
    }

    @Override
    public SpannableStringBuilder getSpannedText() {
        SpannableStringBuilder str = new SpannableStringBuilder();
        str.insert(0, "Code:\n");
        str.setSpan(new StyleSpan(Typeface.BOLD), 0, "Code:\n".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return null;
    }
}
