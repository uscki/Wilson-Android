package nl.uscki.appcki.android.helpers.bbparser;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.helpers.bbparser.elements.GenericElement;
import nl.uscki.appcki.android.views.BBTextView;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

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
            if(object.getClass().getSimpleName().equals("String")) {
                // We've established that this is a String object, so we can safely make it a string
                String string = (String) object;

                // Parse newlines if we have to
                if (parseNewLines) {
                    //string = escapeHtml4(string);
                    string = string.replaceAll("<br />", "\n");
                }

                if (string.contains("CKI")) {
                    SpannableStringBuilder str = new SpannableStringBuilder(string);
                    int start = string.indexOf("CKI");
                    int end = start+3;

                    str.replace(start, end, "a");
                    CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(TypefaceUtils.load(App.getContext().getAssets(), "fonts/ckilogos.ttf"));
                    str.setSpan(typefaceSpan, start, start+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    output.append(str);
                } else {
                    // Add the string to the StringBuilder
                    output.append(string);
                }
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

    public static String parseToHTML(List<Object> toParse, boolean parseNewLines) {
        StringBuilder builder = new StringBuilder();

        // For every element in the suppplied content
        for (Object object: toParse) {
            // Check if the element is a String object
            if (object.getClass().getSimpleName().equals("String")) {
                // We've established that this is a String object, so we can safely make it a string
                String string = (String) object;
                if (parseNewLines) {
                    //string = escapeHtml4(string);
                    string = string.replaceAll("<br />", "\n");
                }
                builder.append(string);
            } else {
                GenericElement element = GenericElement.fromLinkedTreeUnit((LinkedTreeMap) object);
                if (element != null) {
                    builder.append(element.getHtmlText());
                }
            }
        }
        return builder.toString();
    }
}
