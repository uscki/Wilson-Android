package nl.uscki.appcki.android.helpers.bbparser.elements;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;

import java.util.ArrayList;

import nl.uscki.appcki.android.helpers.bbparser.spans.NetworkImageSpan;
import nl.uscki.appcki.android.views.BBTextView;

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
    public SpannableStringBuilder getSpannedText(BBTextView view) {
        String content = getContent().get(0).toString();
        Integer mediaId = Integer.parseInt(content);

        SpannableStringBuilder str = new SpannableStringBuilder(content);
        Log.e("elements.Media", mediaId+"");
        str.setSpan(new NetworkImageSpan(mediaId, null, view), 0, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }
}
