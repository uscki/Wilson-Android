package nl.uscki.appcki.android.helpers.bbtoviewgroup.elements;

import android.text.SpannableStringBuilder;

import java.util.ArrayList;

/**
 * This class describes a tex element. Please note that we do not support it at this time
 *
 * @author Ty Mees
 * @version 1
 * @since 0.10
 */
public class Tex extends GenericElement {

    /**
     * Static string containing the 'element' name, only usefull because it is returned in the JSON output
     */
    protected String type = "Tex";

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public Tex(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = false;
        this.replaceEmoji = false;
        this.type = "Tex";
    }

    @Override
    public SpannableStringBuilder getSpannedText() {
        return null;
    }
}
