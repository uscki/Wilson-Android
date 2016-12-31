package nl.uscki.appcki.android.helpers.bbtoviewgroup.elements;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;

import java.util.ArrayList;

import nl.uscki.appcki.android.helpers.bbtoviewgroup.Parser;

/**
 * This class describes a strike-through-text element
 *
 * @author Ty Mees
 * @version 1.1
 * @since 0.10
 */
public class S extends GenericElement {

    public S(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = true;
        this.replaceEmoji = true;
        this.type = "S";
    }

    @Override
    public SpannableStringBuilder getSpannedText() {
        Log.e("element.S", getContent().toString());
        SpannableStringBuilder str = Parser.parse(getContent(), true);
        str.setSpan(new StrikethroughSpan(), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }
}
