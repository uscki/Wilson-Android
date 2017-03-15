package nl.uscki.appcki.android.helpers.bbparser.elements;

import android.text.SpannableStringBuilder;

import java.util.ArrayList;

import nl.uscki.appcki.android.views.BBTextView;

/**
 * This class describes a ticket element
 *
 * @author Ty Mees
 * @version 1
 * @since 1.2
 */
public class Ticket extends GenericElement {

    /**
     * Static string containing the 'element' name, only usefull because it is returned in the JSON output
     */
    protected String type = "Ticket";

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public Ticket(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = false;
        this.replaceEmoji = false;
        this.type = "Ticket";
    }

    @Override
    public SpannableStringBuilder getSpannedText(BBTextView view) {
        return null;
    }
}
