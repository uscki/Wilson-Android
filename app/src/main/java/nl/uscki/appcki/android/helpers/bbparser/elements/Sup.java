package nl.uscki.appcki.android.helpers.bbparser.elements;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;

import java.util.ArrayList;

import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;

/**
 * This class describes a superscript-text element
 *
 * @author Ty Mees
 * @version 1
 * @since 2.3.1
 */
public class Sup extends GenericElement {

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public Sup(ArrayList<Object> content, String parameter) {
        super(content,parameter);
        this.parseContents = true;
        this.replaceEmoji = true;
        this.type = "Sup";
    }

    @Override
    public SpannableStringBuilder getSpannedText(BBTextView view) {
        SpannableStringBuilder str = Parser.parse(content, false, view);

        str.setSpan(new SuperscriptSpan(), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return str;
    }
}