package nl.uscki.appcki.android.helpers.bbparser.elements;

import android.text.SpannableStringBuilder;
import android.util.Log;

import java.util.ArrayList;

import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;

/**
 * This class describes a table container element
 *
 * @author Ty Mees
 * @version 1.1
 * @since 0.14
 */
public class Ul extends GenericElement {

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public Ul(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = true;
        this.replaceEmoji = true;
        this.type = "UL";
    }

    @Override
    public SpannableStringBuilder getSpannedText(BBTextView view) {
        Log.e("Ul", getContent().toString());
        return Parser.parse(getContent(), true, view); // skip this one cause we have bullet span in Li
    }
}
