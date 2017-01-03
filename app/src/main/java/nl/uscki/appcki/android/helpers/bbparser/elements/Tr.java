package nl.uscki.appcki.android.helpers.bbparser.elements;

import android.text.SpannableStringBuilder;

import java.util.ArrayList;

import nl.uscki.appcki.android.views.BBTextView;

/**
 * This class describes a tr container element
 *
 * @author Ty Mees
 * @version 1.1
 * @since 0.13
 */
public class Tr extends GenericElement {

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public Tr(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = true;
        this.replaceEmoji = true;
        this.type = "TR";
    }

    @Override
    public SpannableStringBuilder getSpannedText(BBTextView view) {
        return null;
    }
}
