package nl.uscki.appcki.android.helpers.bbparser.elements;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

import java.util.ArrayList;

import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.helpers.bbparser.spans.NetworkImageSpan;
import nl.uscki.appcki.android.views.BBTextView;

/**
 * This class describes an image element
 *
 * @author Ty Mees
 * @version 1.1
 * @since 0.10
 */
public class Img extends GenericElement {

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public Img(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = false;
        this.replaceEmoji = false;
        this.type = "Img";
    }

    @Override
    public SpannableStringBuilder getSpannedText(BBTextView view) {
        SpannableStringBuilder str = Parser.parse(getContent(), false, view);
        final String url = str.toString();

        str.setSpan(new NetworkImageSpan(null, url, view), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }
}
