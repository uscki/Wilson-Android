package nl.uscki.appcki.android.helpers.bbparser.elements;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.view.View;

import java.util.ArrayList;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.helpers.bbparser.spans.ClickableSpan;
import nl.uscki.appcki.android.views.BBTextView;

/**
 * This class describes the Spoiler BB tag
 *
 * @author Ty Mees
 * @version 1.3
 * @since 0.1
 */
public class Spoiler extends GenericElement {

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public Spoiler(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = true;
        this.replaceEmoji = true;
        this.type = "Spoiler";
    }

    @Override
    public SpannableStringBuilder getSpannedText(BBTextView view) {
        SpannableStringBuilder str = Parser.parse(getContent(), true, view);
        final String prefix = "Spoiler:\n";

        // insert "code" header
        str.insert(0, prefix);
        str.setSpan(new StyleSpan(Typeface.BOLD), 0, prefix.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // set Spoiler spans
        str.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                BBTextView view = (BBTextView) widget;
                CharSequence text = view.getText();
                SpannableStringBuilder str = new SpannableStringBuilder(text);

                if(view.visibilityOfBBUnit) {
                    str.setSpan(new TextAppearanceSpan(App.getContext(), R.style.SpoilerTextInvisible), prefix.length(), str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    view.visibilityOfBBUnit = false;
                } else {
                    str.setSpan(new TextAppearanceSpan(App.getContext(), R.style.SpoilerTextVisible), prefix.length(), str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    view.visibilityOfBBUnit = true;
                }

                view.setText(str);
            }
        }, 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new TextAppearanceSpan(App.getContext(), R.style.SpoilerTextInvisible), prefix.length(), str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }
}
