package nl.uscki.appcki.android.helpers.bbparser.elements;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;

import java.util.ArrayList;

import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;

/**
 * This class describes a subscript-text element
 *
 * @author Ty Mees
 * @version 1
 * @since 2.3.1
 */
public class Sub extends GenericElement {

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public Sub(ArrayList<Object> content, String parameter) {
        super(content, parameter);
        this.parseContents = true;
        this.replaceEmoji = true;
        this.type = "Sub";
    }

    @Override
    public SpannableStringBuilder getSpannedText(BBTextView view) {
        SpannableStringBuilder str = Parser.parse(getContent(), false, view);

        str.setSpan(new RelativeSizeSpan(0.75f), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return str;
    }
}