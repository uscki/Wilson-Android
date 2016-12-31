package nl.uscki.appcki.android.helpers.bbtoviewgroup.elements;

import android.text.SpannableStringBuilder;
import android.util.Log;

import java.util.ArrayList;

import nl.uscki.appcki.android.helpers.bbtoviewgroup.Parser;

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
    public SpannableStringBuilder getSpannedText() {
        Log.e("Ul", getContent().toString());
        return Parser.parse(getContent(), true); // skip this one cause we have bullet span in Li
    }
}
