package nl.uscki.appcki.android.helpers.bbparser.elements;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

import java.util.ArrayList;

import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.helpers.bbparser.spans.DefensiveURLSpan;
import nl.uscki.appcki.android.views.BBTextView;

/**
 * This class describes an email link element
 *
 * @author Ty Mees
 * @version 1.1
 * @since 0.10
 */
public class Email extends GenericElement {

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public Email(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = false;
        this.replaceEmoji = false;
        this.type = "Email";
    }

    @Override
    public SpannableStringBuilder getSpannedText(BBTextView view) {
        SpannableStringBuilder str = Parser.parse(getContent(), true, view);
        str.setSpan(new DefensiveURLSpan("mailto:" + this.getParameter()), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }
}
