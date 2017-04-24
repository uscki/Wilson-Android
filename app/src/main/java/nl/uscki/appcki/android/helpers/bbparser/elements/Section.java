package nl.uscki.appcki.android.helpers.bbparser.elements;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.View;

import java.util.ArrayList;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.helpers.bbparser.spans.ClickableSpan;
import nl.uscki.appcki.android.views.BBTextView;

/**
 * This class describes a WiCKI Section element
 *
 * @author Ty Mees
 * @version 1.1
 * @since 0.10
 */
public class Section extends GenericElement {

    /**
     * Basic constructor which sets the parsing settings for this element
     */
    public Section(ArrayList<Object> content, String parameter)
    {
        super(content, parameter);
        this.parseContents = true;
        this.replaceEmoji = true;
        this.type = "Section";
    }

    @Override
    public SpannableStringBuilder getSpannedText(BBTextView view) {
        final SpannableStringBuilder str = Parser.parse(getContent(), true, view);
        final String prefix = getParameter();

        // insert "Section" header
        str.insert(0, prefix);
        str.setSpan(new TextAppearanceSpan(App.getContext(), R.style.AppTheme_TextSubHeader), 0, prefix.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // set Spoiler spans
        str.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                BBTextView view = (BBTextView) widget;
                CharSequence text = view.getText();
                SpannableStringBuilder str2 = new SpannableStringBuilder(text);

                // this still won't work when sections have the same title
                int start = str2.toString().indexOf(prefix) + prefix.length();
                int end = str2.toString().indexOf(str.toString()) + str.length();

                if(view.visibilityOfBBUnit) {
                    str2.setSpan(new TextAppearanceSpan(App.getContext(), R.style.SpoilerTextVisible), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    view.visibilityOfBBUnit = false;
                } else {
                    str2.setSpan(new TextAppearanceSpan(App.getContext(), R.style.SpoilerTextInvisible), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    view.visibilityOfBBUnit = true;
                }

                view.setText(str2);
            }
        }, 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new TextAppearanceSpan(App.getContext(), R.style.SpoilerTextVisible), prefix.length(), str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }
}
