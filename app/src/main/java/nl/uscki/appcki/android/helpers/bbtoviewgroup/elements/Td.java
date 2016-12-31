package nl.uscki.appcki.android.helpers.bbtoviewgroup.elements;

import android.text.SpannableStringBuilder;

import java.util.ArrayList;

/**
 * This class describes a td element
 *
 * @author Ty Mees
 * @version 1.1
 * @since 0.13
 */
public class Td extends GenericElement {

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public Td(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = true;
        this.replaceEmoji = true;
        this.type = "Td";
    }

    @Override
    public SpannableStringBuilder getSpannedText() {
        return null;
    }
}
