package nl.uscki.appcki.android.helpers.bbparser.elements;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;

import java.util.ArrayList;

import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.helpers.bbparser.spans.DefensiveURLSpan;
import nl.uscki.appcki.android.views.BBTextView;

public class EmbeddedVideo extends GenericElement {
    boolean youtube;
    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public EmbeddedVideo(ArrayList<Object> content, String parameter, boolean youtube)
    {
        super(content, parameter);
        this.parseContents = false;
        this.replaceEmoji = false;
        this.type = "EmbeddedVideo";
        this.youtube = youtube;
    }

    @Override
    public SpannableStringBuilder getSpannedText(BBTextView view) {
        String url = "";
        // each time a new page is loaded this gets reevaluated, so we can only set the full url once
        // in the content and afterwards have to use that
        // We have to set the content differently because the Parser.parse line that uses the content
        // to render (what should be) the url, however we only receive the video id initially
        // otherwise we would get a very random string of stuff instead of a nice youtube url
        if(this.getContent().get(0).toString().length() <= 15) {
            // youtube/vimeo video ids are shorter than 15 characters while their full urls are longer
            if (this.youtube)
                url = "https://youtube.com/watch?v=" + this.getContent().get(0);
            else
                url = "https://vimeo.com/" + this.getContent().get(0);
        } else {
            url = (String)this.getContent().get(0);
        }
        this.getContent().set(0, url);
        SpannableStringBuilder str = Parser.parse(getContent(), true, view);
        str.setSpan(new DefensiveURLSpan(url), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }
}
