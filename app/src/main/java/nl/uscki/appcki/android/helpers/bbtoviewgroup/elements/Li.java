package nl.uscki.appcki.android.helpers.bbtoviewgroup.elements;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;

import java.util.ArrayList;

import nl.uscki.appcki.android.helpers.bbtoviewgroup.Parser;

/**
 * This class describes a table container element
 *
 * @author Ty Mees
 * @version 1.1
 * @since 0.14
 */
public class Li extends GenericElement {

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public Li(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = true;
        this.replaceEmoji = true;
        this.type = "Li";
    }

    @Override
    public SpannableStringBuilder getSpannedText() {
        SpannableStringBuilder str = Parser.parse(getContent(), true);
        str.setSpan(new BulletSpan(24), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.append('\n');
        return str;
    }
}
