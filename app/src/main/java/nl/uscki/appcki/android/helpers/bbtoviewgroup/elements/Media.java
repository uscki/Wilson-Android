package nl.uscki.appcki.android.helpers.bbtoviewgroup.elements;

import android.text.SpannableStringBuilder;

import java.util.ArrayList;

/**
 * This class describes a medai element. This is different from the Img tag, as this applies only to imagery from USCKI
 *
 * NOTE: This tag currently cannot provide the same functionality as Z.E.B.R.A. as the Media module is not implemented
 * in it's entirety
 *
 * @author Ty Mees
 * @version 1.1
 * @since 0.10
 */
public class Media extends GenericElement {

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public Media(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = false;
        this.replaceEmoji = false;
        this.type = "Media";
    }

    @Override
    public SpannableStringBuilder getSpannedText() {
        return null;
    }

    private static boolean isNumeric(String str)
    {
        return str.matches("\\d+");
    }
}
