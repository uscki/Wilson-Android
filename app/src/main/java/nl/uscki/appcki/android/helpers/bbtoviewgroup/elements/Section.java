package nl.uscki.appcki.android.helpers.bbtoviewgroup.elements;

import android.text.SpannableStringBuilder;

import java.util.ArrayList;

/**
 * This class describes a WiCKI Section element
 *
 * @author Ty Mees
 * @version 1.1
 * @since 0.10
 */
public class Section extends GenericElement {

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public Section(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = true;
        this.replaceEmoji = true;
        this.type = "Section";
    }

    @Override
    public SpannableStringBuilder getSpannedText() {
        return null;
    }
}
