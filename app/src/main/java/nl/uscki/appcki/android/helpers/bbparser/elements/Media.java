package nl.uscki.appcki.android.helpers.bbparser.elements;

import android.content.Intent;
import android.graphics.drawable.LevelListDrawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import nl.uscki.appcki.android.activities.FullScreenMediaActivity;
import nl.uscki.appcki.android.helpers.bbparser.spans.ClickableSpan;
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
        int mediaId = Integer.parseInt(content);

        SpannableStringBuilder str = new SpannableStringBuilder(content);
        Log.e("elements.Media", mediaId+"");

        NetworkImageSpan imageSpan = new NetworkImageSpan(new LevelListDrawable(), mediaId, view);
        str.setSpan(imageSpan, 0, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Unfortunately, I do not think (Image)Spans support shared element transitions. Still, better than nothing
                Intent intent = new FullScreenMediaActivity.SingleImageIntentBuilder("", null)
                        .media(mediaId)
                        .build(view.getContext());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            }
        }, 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }

}
