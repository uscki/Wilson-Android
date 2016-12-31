package nl.uscki.appcki.android.helpers.bbtoviewgroup.elements;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.UnderlineSpan;

import java.util.ArrayList;

import nl.uscki.appcki.android.helpers.bbtoviewgroup.Parser;

/**
 * This class describes a underlined-text element
 *
 * @author Ty Mees
 * @version 1.1
 * @since 0.10
 */
public class U extends GenericElement {

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public U(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = true;
        this.replaceEmoji = true;
        this.type = "U";
    }

    @Override
    public SpannableStringBuilder getSpannedText() {
        SpannableStringBuilder str = Parser.parse(getContent(), true);
        str.setSpan(new UnderlineSpan(), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }
}
