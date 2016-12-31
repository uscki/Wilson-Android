package nl.uscki.appcki.android.helpers.bbtoviewgroup.elements;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

import java.util.ArrayList;

import nl.uscki.appcki.android.helpers.bbtoviewgroup.Parser;
import nl.uscki.appcki.android.helpers.bbtoviewgroup.spans.DefensiveURLSpan;

/**
 * This class describes a link element
 *
 * @author Ty Mees
 * @version 1.3
 * @since 0.6
 */
public class Link extends GenericElement {

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public Link(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = true;
        this.replaceEmoji = false;
        this.type = "Link";
    }

    @Override
    public SpannableStringBuilder getSpannedText() {
        SpannableStringBuilder str = Parser.parse(getContent(), true);
        str.setSpan(new DefensiveURLSpan(this.getParameter()), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }
}
