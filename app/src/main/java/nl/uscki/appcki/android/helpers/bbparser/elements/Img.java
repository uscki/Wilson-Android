package nl.uscki.appcki.android.helpers.bbparser.elements;

import android.content.Intent;
import android.graphics.drawable.LevelListDrawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;

import java.util.ArrayList;

import nl.uscki.appcki.android.activities.FullScreenMediaActivity;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.helpers.bbparser.spans.ClickableSpan;
import nl.uscki.appcki.android.helpers.bbparser.spans.NetworkImageSpan;
import nl.uscki.appcki.android.views.BBTextView;

/**
 * This class describes an image element
 *
 * @author Ty Mees
 * @version 1.1
 * @since 0.10
 */
public class Img extends GenericElement {

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public Img(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = false;
        this.replaceEmoji = false;
        this.type = "Img";
    }

    @Override
    public SpannableStringBuilder getSpannedText(BBTextView view) {
        SpannableStringBuilder str = Parser.parse(getContent(), false, view);
        final String url = str.toString();
        NetworkImageSpan imageSpan = new NetworkImageSpan(new LevelListDrawable(), url, view);
        str.setSpan(imageSpan, 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Unfortunately, I do not think (Image)Spans support shared element transitions. Still, better than nothing
                Intent intent = new FullScreenMediaActivity.SingleImageIntentBuilder("", "")
                        .url(url)
                        .build(view.getContext());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            }
        }, 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }
}
