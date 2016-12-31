package nl.uscki.appcki.android.helpers.bbtoviewgroup.elements;

import android.text.SpannableStringBuilder;

import java.util.ArrayList;

/**
 * This class describes the Spoiler BB tag
 *
 * @author Ty Mees
 * @version 1.3
 * @since 0.1
 */
public class Spoiler extends GenericElement {

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public Spoiler(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = true;
        this.replaceEmoji = true;
        this.type = "Spoiler";
    }

    @Override
    public SpannableStringBuilder getSpannedText() {
        return null;
    }
}
