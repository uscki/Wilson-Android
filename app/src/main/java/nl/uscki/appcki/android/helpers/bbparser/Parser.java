package nl.uscki.appcki.android.helpers.bbparser;

import android.text.SpannableStringBuilder;

import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

import nl.uscki.appcki.android.helpers.bbparser.elements.GenericElement;
import nl.uscki.appcki.android.views.BBTextView;

/**
 * Created by peter on 12/20/16.
 */

public class Parser {
    public static SpannableStringBuilder parse(List<Object> toParse, boolean parseNewLines, BBTextView view)
    {
        // Use a stringbuilder to make the string that will be the output
        SpannableStringBuilder output = new SpannableStringBuilder();

        // For every element in the suppplied content
        for (Object object: toParse) {
            // Check if the element is a String object
            if(object.getClass().getSimpleName().equals("String"))
            {
                // We've established that this is a String object, so we can safely make it a string
                String string = (String) object;

                // Parse newlines if we have to
                if(parseNewLines) {
                    //string = escapeHtml4(string);
                    string = string.replaceAll("<br />", "\n");
                }

                // Add the string to the StringBuilder
                output.append(string);
            }
            else
            {
                // As it is not a string we assume it's a BB Element. This can go wrong, if someone fucks up my parser
                GenericElement element = GenericElement.fromLinkedTreeUnit((LinkedTreeMap) object);

                // Add the result of the element's toHtml function to the Stringbuilder
                if(element != null && view != null)
                    output.append(element.getSpannedText(view));
            }
        }

        // Return the builded string
        return output;
    }
}
