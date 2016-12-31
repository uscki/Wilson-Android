package nl.uscki.appcki.android.helpers.bbtoviewgroup.elements;

import android.text.SpannableStringBuilder;

import java.util.ArrayList;

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
    public SpannableStringBuilder getSpannedText() {
        return null;
    }
}
